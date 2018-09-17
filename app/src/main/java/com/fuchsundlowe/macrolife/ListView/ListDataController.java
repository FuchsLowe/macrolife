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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
class ListDataController implements P1 {

    private Context mContext;
    private DataProviderNewProtocol dataMaster;

    private boolean tasksHaveArrived, eventsHaveArrived = false;


    private LiveData<List<TaskObject>> allTaskObjects;
    private Observer<List<TaskObject>> observerForTasks;

    private LiveData<List<RepeatingEvent>> allRepeatingEvents;
    private Observer<List<RepeatingEvent>> observerForEvents;

    private List<TaskEventHolder> completed;
    private List<TaskEventHolder> upcoming;
    private List<TaskEventHolder> overdue;
    private List<TaskEventHolder> unassigned;

    private boolean setsEvaluated = false;
    private List<P2> completedSet;
    private List<P2> overdueSet;
    private List<P2> upcomingSet;
    private List<P2> unassignedSet;

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

        completed = new ArrayList<>();
        upcoming = new ArrayList<>();
        overdue = new ArrayList<>();
        unassigned = new ArrayList<>();

        completedSet = new ArrayList<>();
        overdueSet = new ArrayList<>();
        upcomingSet = new ArrayList<>();
        unassignedSet = new ArrayList<>();

        final boolean tasksHaveArrived, eventsHaveArrived = false;


        // TODO Implement, the meat of class
        // Tasks:
        observerForTasks = new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> objects) {
                // Evaluate and put into right groups
                reportTaskHaveArrived();
                sifter(objects);
            }
        };
        // Events:
        observerForEvents = new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> events) {
                reportEventsHaveArrived();
                // Evaluate and put into right group
                sifter((ArrayList<RepeatingEvent>) events);
            }
        };

    }
    private void reportTaskHaveArrived() {
        tasksHaveArrived = true;
    }
    private void reportEventsHaveArrived() {
        eventsHaveArrived = true;
    }
    // This method evaluates the holder and returns its target bracket
    private bracketType evaluate(TaskEventHolder holder) {
        // TODO : Evaluate the time it takes to calculate this...
        long nan = System.nanoTime();
        Calendar currentTime = Calendar.getInstance();
        long nan2 = System.nanoTime();
        long resForCal = nan2 -nan;

        bracketType reportType = null;
        switch (holder.getCompletionState()) {
            case completed:
                reportType = bracketType.completed;
                break;
            case incomplete:
                /*
                 * if its incomplete... establish if its reminder or t/e...
                 * if reminder determine if day has passed
                 *
                 * if t/e establish end time if has passed
                 */
                switch (holder.getTimeDefined()) {
                    case noTime:
                        reportType = bracketType.unassigned;
                        break;
                    case onlyDate:
                        // meaning its reminder, has the day passed?
                        if (hasDayPassed(holder.getStartTime(), currentTime)) {
                            reportType = bracketType.overdue;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                    case dateAndTime:
                        if (holder.getEndTime().before(currentTime)) {
                            // means that it has passed
                            reportType = bracketType.overdue;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                }
                break;
            case notCheckable:
                switch (holder.getTimeDefined()) {
                    case noTime:
                        reportType = bracketType.unassigned;
                        break;
                    case onlyDate:
                        if (hasDayPassed(holder.getStartTime(), currentTime)) {
                            reportType = bracketType.completed;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                    case dateAndTime:
                        if (holder.getEndTime().before(currentTime)) {
                            reportType = bracketType.completed;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                }
                break;
        }


        long total = System.nanoTime();
        long totalResult = total - nan;

        return reportType;
    }
    private void sifter(List<TaskObject> o) {
        // Remove all task objects from existing brackets
        List<TaskEventHolder> toDelete = new ArrayList<>();
        for (TaskEventHolder holder: completed) {
            if (holder.isTask()) {
                toDelete.add(holder);
            }
        }
        completed.removeAll(toDelete);
        toDelete.clear();
        for (TaskEventHolder holder: overdue) {
            if (holder.isTask()) {
                toDelete.add(holder);
            }
        }
        overdue.removeAll(toDelete);
        toDelete.clear();
        for (TaskEventHolder holder: upcoming) {
            if (holder.isTask()) {
                toDelete.add(holder);
            }
        }
        upcoming.removeAll(toDelete);
        toDelete.clear();
        for (TaskEventHolder holder: unassigned) {
            if (holder.isTask()) {
                toDelete.add(holder);
            }
        }
        unassigned.removeAll(toDelete);
        toDelete.clear();

        // now we need to distribute new ones inside the lot:
        boolean editedCompleted = false;
        boolean editedOverdue = false;
        boolean editedUpcoming = false;
        boolean editedUnassigned = false;
        for (TaskObject object: o) {
            TaskEventHolder holder = new TaskEventHolder(object, null);
            switch (evaluate(holder)) {
                case completed:
                    editedCompleted = true;
                    completed.add(holder);
                    break;
                case overdue:
                    editedOverdue = true;
                    overdue.add(holder);
                    break;
                case upcoming:
                    editedUpcoming = true;
                    upcoming.add(holder);
                    break;
                case unassigned:
                    editedUnassigned = true;
                    unassigned.add(holder);
                    break;
            }
        }
        if (editedCompleted) {
            for (P2 subscriber: completedSet) {
                subscriber.deliverCompleted(completed);
            }
        }
        if (editedOverdue) {
            for (P2 subscriber: overdueSet) {
                subscriber.deliverOverdue(overdue);
            }
        }
        if (editedUnassigned) {
            for (P2 subscriber: unassignedSet) {
                subscriber.deliverUnassigned(unassigned);
            }
        }
        if (editedUpcoming) {
            for (P2 subscriber: upcomingSet) {
                subscriber.deliverUpcoming(upcoming);
            }
        }

    }
    private void sifter(ArrayList<RepeatingEvent> o) {

    }

    // a helper method that determines if reminder has passed or not
    private boolean hasDayPassed(Calendar day, Calendar currentTime) {
        if (currentTime.get(Calendar.YEAR) == day.get(Calendar.YEAR)) {
            return currentTime.get(Calendar.DAY_OF_YEAR) > day.get(Calendar.DAY_OF_YEAR);
        } else {
            return currentTime.get(Calendar.YEAR) > day.get(Calendar.YEAR);
        }
    }

    // MARK Methods for dispensing data to Fragments
    // This method is called from ListView onDestroy class to delete any LiveData observeForever calls
    protected void destroy() {
        if (allTaskObjects != null && observerForTasks != null) {
            allTaskObjects.removeObserver(observerForTasks);
        }
        if (allRepeatingEvents != null && observerForEvents != null) {
            allRepeatingEvents.removeObserver(observerForEvents);
        }

    }

    // Protocol implementation:
    public void reportChange(TaskEventHolder beingChanged) {

    }
    public void subscribeToCompleted(P2 protocol) {
        completedSet.add(protocol);
        if (setsEvaluated) {
            protocol.deliverCompleted(completed);
        } // if not, it will be delivered when completed

    }
    public void subscribeToOverdue(P2 protocol) {
        overdueSet.add(protocol);
        if (setsEvaluated) {
            protocol.deliverOverdue(overdue);
        }
    }
    public void subscribeToUnassigned(P2 protocol) {
        unassignedSet.add(protocol);
        if (setsEvaluated) {
            protocol.deliverUnassigned(unassigned);
        }
    }
    public void subscribeToUpcoming(P2 protocol) {
        upcomingSet.add(protocol);
        if (setsEvaluated) {
            protocol.deliverUpcoming(upcoming);
        }

    }

    private enum bracketType {
        completed, overdue, unassigned, upcoming
    }
}
