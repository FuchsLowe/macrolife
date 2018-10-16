package com.fuchsundlowe.macrolife.ListView;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.AsyncSorterCommunication;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.LDCToFragmentListView;
import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.ListView.ListView.bracketType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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
 */
class ListDataController implements LDCProtocol, AsyncSorterCommunication {

    private Context mContext;
    private DataProviderNewProtocol dataMaster;

    private LiveData<List<TaskObject>> allTaskObjects;
    private Observer<List<TaskObject>> observerForTasks;

    private LiveData<List<RepeatingEvent>> allRepeatingEvents;
    private Observer<List<RepeatingEvent>> observerForEvents;

    private Observer<List<ComplexGoal>> observerForGoals;

    private Map<Integer, Integer> completedStatistics, incompleteStatistics;
    private Vector<TaskEventHolder> unassigned;
    private Vector<TaskEventHolder> completed;
    private Vector<TaskEventHolder>  upcoming;
    private Vector<TaskEventHolder>  overdue;
    private List<ComplexGoal> complex;

    private currentStatus tasksStatus, eventsStatus;

    // Holders for subscribers to data for referred categories
    private List<LDCToFragmentListView> complexStatisticsSet;
    private List<LDCToFragmentListView> unassignedSet;
    private List<LDCToFragmentListView> completedSet;
    private List<LDCToFragmentListView> upcomingSet;
    private List<LDCToFragmentListView> overdueSet;
    private List<ComplexDataProtocol> complexSet;

    private AsyncSorter tasksSorter, eventsSorter;

    private Map<bracketType, Boolean> toFlush;
    private Map<Integer, TaskEventHolder> nextTask;

    private AsyncSorterCommunication self;


    ListDataController(Context context) {
        // Initialization phase:
        this.mContext = context;
        dataMaster = LocalStorage.getInstance(mContext);

        // Defining the Observers:
        initiateData();
        // Binding observers to LiveDataObjects
        allTaskObjects = dataMaster.getAllTaskObjects();

        allRepeatingEvents = dataMaster.getAllRepeatingEvents();

    }
    @SuppressLint("UseSparseArrays")
    private void initiateData() {

        unassigned = new Vector<>();
        completed = new Vector<>();
        upcoming = new Vector<>();
        overdue = new Vector<>();

        complexStatisticsSet = new ArrayList<>();
        unassignedSet = new ArrayList<>();
        completedSet = new ArrayList<>();
        upcomingSet = new ArrayList<>();
        overdueSet = new ArrayList<>();
        complexSet = new ArrayList<>();

        nextTask = new HashMap<>();

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
                tasksStatus = currentStatus.working;
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
                eventsStatus = currentStatus.working;
                eventsSorter = new AsyncSorter();
                Transporter event = new Transporter(null, events, unassigned,
                        completed, upcoming, overdue, self);
                eventsSorter.execute(event);
            }
        };
        // Goals:
        observerForGoals = new Observer<List<ComplexGoal>>() {
            @Override
            public void onChanged(@Nullable List<ComplexGoal> complexGoals) {
                complex = complexGoals;
                for (ComplexDataProtocol protocol: complexSet) {
                    protocol.complexGoalLiveData(complexGoals);
                }
            }
        };
    }

    // This method is called from ListView onDestroy class to delete any LiveData observeForever calls
    public void destroy() {
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

    // Protocol LDCToFragmentListView implementation:
    public void subscribeToCompleted(LDCToFragmentListView protocol) {
        completedSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverCompleted(completed);
        }
    }
    public void subscribeToOverdue(LDCToFragmentListView protocol) {
        overdueSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverOverdue(overdue);
        }
    }
    public void subscribeToUnassigned(LDCToFragmentListView protocol) {
        unassignedSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverUnassigned(unassigned);
        }
    }
    public void subscribeToUpcoming(LDCToFragmentListView protocol) {
        upcomingSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverUpcoming(upcoming);
        }
    }
    public void subscribeToComplexGoalsStatistics(LDCToFragmentListView protocol) {
        complexStatisticsSet.add(protocol);
        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            protocol.deliverComplexTasksStatistics(completedStatistics, incompleteStatistics, nextTask);
        }
    }
    public void subscribeToComplexLiveData(ComplexDataProtocol protocol) {
        complexSet.add(protocol);
        if (complex != null) {
            protocol.complexGoalLiveData(complex);
        }
    }


    // Protocol AsyncSorterCommunication implementations:
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
    public void deliverNewComplexTotals(Map<Integer, Integer> completed, Map<Integer, Integer> incomplete, Map<Integer, TaskEventHolder> nextTask) {
        completedStatistics = completed;
        incompleteStatistics = incomplete;
        this.nextTask = nextTask;
    }
    @Override
    public void flushChanges() {

        if (tasksStatus == currentStatus.ready && eventsStatus == currentStatus.ready) {
            if (toFlush.get(bracketType.overdue)) {
                for (LDCToFragmentListView fragment: overdueSet) {
                    fragment.deliverOverdue(overdue);
                }
                toFlush.put(bracketType.overdue, Boolean.FALSE);
            }
            if (toFlush.get(bracketType.upcoming)) {
                for (LDCToFragmentListView fragment: upcomingSet) {
                    fragment.deliverUpcoming(upcoming);
                }
                toFlush.put(bracketType.upcoming, Boolean.FALSE);
            }
            if (toFlush.get(bracketType.completed)) {
                for (LDCToFragmentListView fragment: completedSet) {
                    fragment.deliverCompleted(completed);
                }
                toFlush.put(bracketType.completed, Boolean.FALSE);
            }
            if (toFlush.get(bracketType.undefined)) {
                for (LDCToFragmentListView fragment: unassignedSet) {
                    fragment.deliverUnassigned(unassigned);
                }
                toFlush.put(bracketType.undefined, Boolean.FALSE);
            }
            for (LDCToFragmentListView fragment : complexStatisticsSet) {
                fragment.deliverComplexTasksStatistics(completedStatistics, incompleteStatistics, nextTask);
            }
        }
    }

    @Override
    public Observer<List<TaskObject>> getTaskObserver() {
        return observerForTasks;
    }
    @Override
    public Observer<List<RepeatingEvent>> getEventObserver() {
        return observerForEvents;
    }
    public Observer<List<ComplexGoal>> getGoalObserver() {
        return observerForGoals;
    }


    // Standard calls to database serviced by this protocol

    public TaskEventHolder searchForTask(int taskID) {
        TaskObject object = dataMaster.findTaskObjectBy(taskID);
        if (object != null) {
            return new TaskEventHolder(object, null);
        }
        return null;
    }
    public TaskEventHolder searchForEvent(int eventID) {
        RepeatingEvent event = dataMaster.getEventWith(eventID);
        if (event != null) {
          return new TaskEventHolder(null, event);
        }
        return null;
    }
    public ComplexGoal searchForComplexGoal(int goalID) {
        return dataMaster.findComplexGoal(goalID);
    }
    public void saveTaskEventHolder(TaskEventHolder toSave) {
        if (toSave.isTask()) {
            dataMaster.saveTaskObject(toSave.getTask());
        } else {
            dataMaster.saveRepeatingEvent(toSave.getEvent());
        }
    }
    @Override
    public void deleteTask(TaskObject taskToDelete) {
        dataMaster.deleteTask(taskToDelete);
    }
    @Override
    public void deleteEvent(RepeatingEvent eventToDelete) {
        dataMaster.deleteRepeatingEvent(eventToDelete);
    }

    private enum currentStatus {
        ready, working, notDefined
    }
}
