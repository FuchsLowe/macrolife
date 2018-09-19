package com.fuchsundlowe.macrolife.ListView;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.P1;
import com.fuchsundlowe.macrolife.Interfaces.P2;
import com.fuchsundlowe.macrolife.Interfaces.P3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Class that manages database calls, filters data and creates LiveData Objects to be used for
 * subsequent distributions to UI Controllers that will use them.
 *
 * Custom class is used instead of ViewModel because this class needs to observe live objects from
 * DB while ViewModel implementations explicitly forbid this.
 *
 * How it works:
 *
 * List Data Controller receives live data objects from DataProvider. It then sorts the data into
 * lists in separate thread due to possibility that we are handling large amounts of data.
 * Whoever is interested in receiving the Lists subscribes to specific list. They will receive either
 * immediately a list if LDC is done sorting or ASAP. Whenever new set is received from DP, LDC
 * sorts it and dispatches it to subscribed ones.
 *
 * Any changes made to the list by subscribers are reported back to LDC to inform subscribers to that
 * list that things have changed. Goal is to avoid unnecessary whole data calls, but to allow the
  * system to just make changes to one element, like insert it or remove it from view and not
  * deal with whole data set and then figuring out which ones are being removed and which ones are
  * staying. Per say, completed task is changed to upcoming so, subscriber removes it from list, reports
  * the change to LDC and then LDC reports this change to all subscribers who should insert the task
  * in upcoming by updating their views.
 */
class ListDataController implements P1, P3 {

    private Context mContext;
    private DataProviderNewProtocol dataMaster;

    private LiveData<List<TaskObject>> allTaskObjects;
    private Observer<List<TaskObject>> observerForTasks;

    private LiveData<List<RepeatingEvent>> allRepeatingEvents;
    private Observer<List<RepeatingEvent>> observerForEvents;

    private List<TaskEventHolder> unassigned;
    private List<TaskEventHolder> completed;
    private List<TaskEventHolder>  upcoming;
    private List<TaskEventHolder>  overdue;

    private currentStatus tasksStatus, eventsStatus;

    private List<P2> unassignedSet;
    private List<P2> completedSet;
    private List<P2> upcomingSet;
    private List<P2> overdueSet;

    private AsyncSorter tasksSorter, eventsSorter;

    private Map<bracketType, Boolean> toFlush;

    private P3 self;


    protected ListDataController(Context context) {
        // Initialization phase:
        this.mContext = context;
        dataMaster = LocalStorage.getInstance(mContext);

        // Defining the Observers:
        initiateData();
        // Binding observers to LiveDataObjects
        allTaskObjects = dataMaster.getAllTaskObjects();
        allTaskObjects.observeForever(observerForTasks);

        allRepeatingEvents = dataMaster.getAllRepeatingEvents();
        allRepeatingEvents.observeForever(observerForEvents);

    }
    private void initiateData() {

        unassigned = Collections.synchronizedList(new ArrayList<TaskEventHolder>());
        completed = Collections.synchronizedList(new ArrayList<TaskEventHolder>());
        upcoming = Collections.synchronizedList(new ArrayList<TaskEventHolder>());
        overdue = Collections.synchronizedList(new ArrayList<TaskEventHolder>());

        unassignedSet = new ArrayList<>();
        completedSet = new ArrayList<>();
        upcomingSet = new ArrayList<>();
        overdueSet = new ArrayList<>();

        tasksStatus = currentStatus.notDefined;
        eventsStatus = currentStatus.notDefined;

        toFlush = new ConcurrentHashMap<>();
        toFlush.put(bracketType.undefined, Boolean.FALSE);
        toFlush.put(bracketType.completed, Boolean.FALSE);
        toFlush.put(bracketType.upcoming, Boolean.FALSE);
        toFlush.put(bracketType.overdue, Boolean.FALSE);

        this.self = this;

        // Tasks:
        observerForTasks = new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> objects) {
                if (tasksSorter != null) {
                    tasksSorter.cancel(true);
                }
                tasksSorter = new AsyncSorter();
                Transporter task = new Transporter(objects, null, unassigned,
                        completed, upcoming, overdue, self);
                tasksSorter.execute(task);
            }
        };
        // Events:
        observerForEvents = new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> events) {
                // Send for sorting
                if (eventsSorter != null) {
                    eventsSorter.cancel(true);
                }
                eventsSorter = new AsyncSorter();
                Transporter event = new Transporter(null, events, unassigned,
                        completed, upcoming, overdue, self);
                eventsSorter.execute(event);
            }
        };
    }

    // This method is called from ListView onDestroy class to delete any LiveData observeForever calls
    protected void destroy() {
        self = null;

        if (allTaskObjects != null && observerForTasks != null) {
            allTaskObjects.removeObserver(observerForTasks);
            observerForTasks = null;
        }
        if (allRepeatingEvents != null && observerForEvents != null) {
            allRepeatingEvents.removeObserver(observerForEvents);
            observerForEvents = null;
        }
        if (tasksSorter != null) {
            tasksSorter.cancel(true);
            tasksSorter = null;
        }
        if (eventsSorter != null) {
            eventsSorter.cancel(true);
            eventsSorter = null;
        }

        tasksStatus = currentStatus.notDefined;
        eventsStatus = currentStatus.notDefined;

        unassigned = null;
        completed = null;
        upcoming = null;
        overdue = null;

        unassignedSet = null;
        completedSet = null;
        upcomingSet = null;
        overdueSet = null;

        mContext = null;
        dataMaster = null;

    }

    // Protocol P2 implementation:
    public void reportChange(TaskEventHolder beingChanged) {

    }
    public void subscribeToCompleted(P2 protocol) {
        completedSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverCompleted(completed);
        }
    }
    public void subscribeToOverdue(P2 protocol) {
        overdueSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverOverdue(overdue);
        }
    }
    public void subscribeToUnassigned(P2 protocol) {
        unassignedSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverUnassigned(unassigned);
        }
    }
    public void subscribeToUpcoming(P2 protocol) {
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverUpcoming(upcoming);
        }
    }


    // Protocol P3 implementations:
    @Override
    public void markTasksReady() {
        tasksStatus = currentStatus.ready;
    }
    @Override
    public void markEventsReady() {
        eventsStatus = currentStatus.ready;
    }
    @Override
    public void changedOverdue() {
        toFlush.put(bracketType.overdue, Boolean.TRUE);
    }
    @Override
    public void changedUpcoming() {
        toFlush.put(bracketType.upcoming, Boolean.TRUE);
    }
    @Override
    public void changedCompleted() {
        toFlush.put(bracketType.completed, Boolean.TRUE);
    }
    @Override
    public void changedUnassigned() {
        toFlush.put(bracketType.undefined, Boolean.TRUE);
    }
    @Override
    public void flushChanges() {
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            if (toFlush.get(bracketType.overdue)) {
                for (P2 fragment: overdueSet) {
                    fragment.deliverOverdue(overdue);
                }
                toFlush.put(bracketType.overdue, Boolean.FALSE);
            }
            if (toFlush.get(bracketType.upcoming)) {
                for (P2 fragment: upcomingSet) {
                    fragment.deliverUpcoming(upcoming);
                }
                toFlush.put(bracketType.upcoming, Boolean.FALSE);
            }
            if (toFlush.get(bracketType.completed)) {
                for (P2 fragment: completedSet) {
                    fragment.deliverCompleted(completed);
                }
                toFlush.put(bracketType.completed, Boolean.FALSE);
            }
            if (toFlush.get(bracketType.undefined)) {
                for (P2 fragment: unassignedSet) {
                    fragment.deliverUnassigned(unassigned);
                }
                toFlush.put(bracketType.undefined, Boolean.FALSE);
            }
        }
    }

    // Enums:
    protected enum bracketType {
        completed, overdue, undefined, upcoming
    }
    private enum currentStatus {
        ready, working, notDefined
    }
}
