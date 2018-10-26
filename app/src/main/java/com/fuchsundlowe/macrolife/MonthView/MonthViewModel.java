package com.fuchsundlowe.macrolife.MonthView;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * This class is in charge of managing the data that should be displayed in Month View.
 * Mainly this class takes the data from DataBase and prepares it for more custom data queries
 * required by MonthView and support classes.
 * It also works as a bridge between MonthView and its support classes and DataBase, specifically
 * for servicing the standard lookup queries, save and delete and so on.
 */
public class MonthViewModel implements MonthDataControllerProtocol {

    private MonthViewDataProvider dataMaster;
    private final Map<Short, DoublyHolder> yearBuckets; // Holds Doubly Holders mapped for year...
    private final Map<Short, int[]> taskMinuteCounter; // Holds minutes per day for each task...
    private final Map<Short, int[]> eventMinuteCounter; // -||- but for events
    private int currentYear = -1;
    private LifecycleOwner lifecycle;
    private List<LiveData<List<TaskObject>>> tasks;
    private List<LiveData<List<RepeatingEvent>>> events;

    private final short MAX_DAYS_IN_A_YEAR = 366;
    private final long REMINDER_DURATION = 1000 * 60 * 30; // Calculated 30 min in millis


    MonthViewModel(Context context, LifecycleOwner lifecycleOwner) {
        dataMaster = LocalStorage.getInstance(context); // just in case...
        yearBuckets = new HashMap<>();
        taskMinuteCounter = new HashMap<>();
        eventMinuteCounter = new HashMap<>();
        lifecycle = lifecycleOwner;

        tasks = new ArrayList<>();
        events = new ArrayList<>();
    }

    // Protocol Implementations
    @Override
    public @Nullable TaskEventHolder findAsTask(int hashID) {
        TaskObject data = dataMaster.findTaskObjectBy(hashID);
        return (data != null) ? new TaskEventHolder(data, null) : null;
    }
    @Override
    public @Nullable TaskEventHolder findAsEvent(int hashID) {
        RepeatingEvent data = dataMaster.getEventWith(hashID);
        return (data != null) ? new TaskEventHolder(null, data) :  null;
    }
    @Override
    public void deleteTask(TaskObject task, Calendar date) {
        // Delete from database:
        dataMaster.deleteTask(task);
        // delete from local:
        int year = date.get(Calendar.YEAR);
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
        for (TaskEventHolder temp : yearBuckets.get(year).tasks.get(dayOfYear)) {
            if (temp.getActiveID() == task.getHashID()) {
                yearBuckets.get(year).tasks.get(dayOfYear).remove(temp);
                return;
            }
        }
    }

    @Override
    public void deleteEvent(RepeatingEvent event, Calendar date) {
        // Delete from database:
        dataMaster.deleteRepeatingEvent(event);
        // Delete from local:
        int year = date.get(Calendar.YEAR);
        int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
        for (TaskEventHolder temp : yearBuckets.get(year).events.get(dayOfYear)) {
            if (temp.getActiveID() == event.getHashID()) {
                yearBuckets.get(year).events.get(dayOfYear).remove(temp);
                return;
            }
        }
    }

    @Override
    public int[][] timeTablesFor(short year) {
        // Returns the task and event time tables for specific year if they are calculated...
        int[][] temp = new int[2][MAX_DAYS_IN_A_YEAR];
        if (taskMinuteCounter.containsKey(year) && eventMinuteCounter.containsKey(year)) {
            temp[0] = taskMinuteCounter.get(year);
            temp[1] = eventMinuteCounter.get(year);
        }
        return temp;
    }
    @Override
    public List<TaskEventHolder> holdersFor(int dayOfYear, int year) {
        List<TaskEventHolder> temp = new LinkedList<>();
        temp.addAll(yearBuckets.get(year).tasks.get(dayOfYear));
        temp.addAll(yearBuckets.get(year).events.get(dayOfYear));
        return  temp;
    }
    @Override
    public void newYearSet(int year) {
        // Determine if we have selected year, and if we have +/-1 for that year defined.
        // Then what we have or not we process and what we don't need we discard...
        if (year == currentYear +1) {
            tasks.get(0).removeObservers(lifecycle);
            tasks.set(0, tasks.get(1));
            tasks.set(1, tasks.get(2));
            tasks.set(2, dataMaster.tasksForAYear(year +1));
            tasks.get(2).observe(lifecycle, subscribeTaskForYear(year +1));

            events.get(0).removeObservers(lifecycle);
            events.set(0, events.get(1));
            events.set(1, events.get(2));
            events.set(2, dataMaster.eventsForAYear(year +1));
            events.get(2).observe(lifecycle, subscribeEventForYear(year +1));
        } else if (year == currentYear -1) {
            tasks.get(2).removeObservers(lifecycle);
            tasks.set(2, tasks.get(1));
            tasks.set(1, tasks.get(0));
            tasks.set(0, dataMaster.tasksForAYear(year -1));
            tasks.get(0).observe(lifecycle, subscribeTaskForYear(year -1));

            events.get(2).removeObservers(lifecycle);
            events.set(2, events.get(1));
            events.set(1, events.get(0));
            events.set(0, dataMaster.eventsForAYear(year -1));
            events.get(0).observe(lifecycle, subscribeEventForYear(year -1));
        } else {
            // completely new Data should be set
            // Unsubscribe potential data
            for (LiveData<List<TaskObject>> liveData: tasks) {
                liveData.removeObservers(lifecycle);
            }
            tasks.clear();
            tasks.set(0, dataMaster.tasksForAYear(year -1));
            tasks.get(0).observe(lifecycle, subscribeTaskForYear(year -1));
            tasks.set(1, dataMaster.tasksForAYear(year));
            tasks.get(1).observe(lifecycle, subscribeTaskForYear(year));
            tasks.set(2, dataMaster.tasksForAYear(year +1));
            tasks.get(2).observe(lifecycle, subscribeTaskForYear(year +1));

            for (LiveData<List<RepeatingEvent>> liveData: events) {
                liveData.removeObservers(lifecycle);
            }
            events.clear();
            events.set(0, dataMaster.eventsForAYear(year -1));
            events.get(0).observe(lifecycle, subscribeEventForYear(year -1));
            events.set(1, dataMaster.eventsForAYear(year));
            events.get(1).observe(lifecycle, subscribeEventForYear(year));
            events.set(2, dataMaster.eventsForAYear(year +1));
            events.get(3).observe(lifecycle, subscribeEventForYear(year +1));
        }
        currentYear = year;
    }
    private Observer<List<TaskObject>> subscribeTaskForYear(final int year) {
        Observer<List<TaskObject>> taskObserver = new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable final List<TaskObject> objects) {
                if (objects != null) {
                    Thread highPriority = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<List<TaskEventHolder>> newTaskForYear = new ArrayList<>(MAX_DAYS_IN_A_YEAR);
                            int[] newTaskMinutes = new int[MAX_DAYS_IN_A_YEAR];
                            for (TaskObject task: objects) {
                                // Put the task in correct brackets:
                                Calendar taskStartTime = (Calendar) task.getTaskStartTime().clone();
                                taskStartTime.set(Calendar.HOUR_OF_DAY, 0);
                                taskStartTime.set(Calendar.MINUTE, 0);
                                taskStartTime.set(Calendar.SECOND, 0);
                                taskStartTime.set(Calendar.MILLISECOND, 0);
                                do { // rolls until this object has been assigned to all days it belongs to
                                    switch (task.getTimeDefined()) {
                                        case noTime:
                                            // TODO: Remove this in production
                                            throw new AssertionError("Task without a date defined was found with start/end time set in some way");
                                        case onlyDate:
                                            newTaskMinutes[taskStartTime.get(Calendar.DAY_OF_YEAR)] += REMINDER_DURATION;
                                            break;
                                        case dateAndTime:
                                            long timeToAdd = 0;
                                            // The most complex one:
                                            if (task.getTaskStartTime().get(Calendar.DAY_OF_YEAR) ==
                                                    task.getTaskEndTime().get(Calendar.DAY_OF_YEAR)) {
                                                timeToAdd = task.getTaskEndTime().getTimeInMillis() -
                                                        task.getTaskStartTime().getTimeInMillis();
                                            } else {
                                                if (taskStartTime.get(Calendar.DAY_OF_YEAR) == task.getTaskEndTime().get(Calendar.DAY_OF_YEAR)) {
                                                    timeToAdd = task.getTaskEndTime().getTimeInMillis() -
                                                            taskStartTime.getTimeInMillis();
                                                } else if (taskStartTime.getTimeInMillis() < task.getTaskEndTime().getTimeInMillis()) {
                                                    timeToAdd = Constants.millisInADay;
                                                }
                                            }
                                            newTaskMinutes[taskStartTime.get(Calendar.DAY_OF_YEAR)] += timeToAdd;
                                            break;
                                    }
                                    // This deals with setting task to belong to specific day of year
                                    int dayOfYear =  taskStartTime.get(Calendar.DAY_OF_YEAR);
                                    newTaskForYear.get(dayOfYear).add(new TaskEventHolder(task, null));
                                    // increases the count
                                    taskStartTime.add(Calendar.DAY_OF_YEAR, 1);
                                } while (taskStartTime.getTimeInMillis() < task.getTaskEndTime().getTimeInMillis());
                            }
                            synchronized (yearBuckets) {
                                // Changing the values for tasks for a day
                                yearBuckets.get(year).tasks = newTaskForYear;
                            }
                            synchronized (taskMinuteCounter) {
                                // Changing the values for minutes counted
                                taskMinuteCounter.put((short) year, newTaskMinutes);
                            }
                        }
                    });
                    highPriority.setPriority(Thread.MAX_PRIORITY);
                    highPriority.start();
                }
            }
        };

        return taskObserver;
    }
    private Observer<List<RepeatingEvent>> subscribeEventForYear(final int year) {
        Observer<List<RepeatingEvent>> eventObserver = new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable final List<RepeatingEvent> events) {
                if (events != null) {
                    Thread highPriority = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<List<TaskEventHolder>> newEventsForYear = new ArrayList<>(MAX_DAYS_IN_A_YEAR);
                            int[] newEventMinutes = new int[MAX_DAYS_IN_A_YEAR];
                            for (RepeatingEvent event: events) {
                                // Put the event in correct brackets:
                                Calendar eventStartTime = (Calendar) event.getStartTime().clone();
                                eventStartTime.set(Calendar.HOUR_OF_DAY, 0);
                                eventStartTime.set(Calendar.MINUTE, 0);
                                eventStartTime.set(Calendar.SECOND, 0);
                                eventStartTime.set(Calendar.MILLISECOND, 0);
                                do { // rolls until this object has been assigned to all days it belongs to
                                    long timeToAdd = 0;
                                    if (event.getEndTime() != null && event.getEndTime().getTimeInMillis() > 1) {
                                        // means this is not a reminder
                                        // 3 options:
                                        if (event.getEndTime().get(Calendar.DAY_OF_YEAR) ==
                                                event.getStartTime().get(Calendar.DAY_OF_YEAR)) {
                                            timeToAdd = event.getEndTime().getTimeInMillis() -
                                                    event.getStartTime().getTimeInMillis();
                                        } else {
                                            if (eventStartTime.get(Calendar.DAY_OF_YEAR) ==
                                                    event.getEndTime().get(Calendar.DAY_OF_YEAR)) {
                                                timeToAdd = event.getEndTime().getTimeInMillis() -
                                                        eventStartTime.getTimeInMillis();
                                            } else if (event.getStartTime().getTimeInMillis() <
                                                    event.getEndTime().getTimeInMillis()) {
                                                timeToAdd = Constants.millisInADay;
                                            }
                                        }
                                    } else {
                                        timeToAdd = REMINDER_DURATION;
                                    }
                                    newEventMinutes[eventStartTime.get(Calendar.DAY_OF_YEAR)] += timeToAdd;
                                    // This deals with setting event to belong to specific day of year
                                    int dayOfYear =  eventStartTime.get(Calendar.DAY_OF_YEAR);
                                    newEventsForYear.get(dayOfYear).add(new TaskEventHolder(null, event));
                                    // increases the count
                                    eventStartTime.add(Calendar.DAY_OF_YEAR, 1);
                                } while (eventStartTime.getTimeInMillis() < event.getEndTime().getTimeInMillis());
                            }
                            synchronized (yearBuckets) {
                                // Changing the values for tasks for a day
                                yearBuckets.get(year).tasks = newEventsForYear;
                            }
                            synchronized (taskMinuteCounter) {
                                // Changing the values for minutes counted
                                taskMinuteCounter.put((short) year, newEventMinutes);
                            }
                        }
                    });
                    highPriority.setPriority(Thread.MAX_PRIORITY);
                    highPriority.start();
                }
            }
        };
        return eventObserver;
    }
    @Override
    public int getFreeHashIDForTask() {
        return dataMaster.findNextFreeHashIDForTask();
    }
    @Override
    public void saveTaskEventHolder(TaskEventHolder toSave) {
        if (toSave.isTask()) {
            dataMaster.saveTaskObject(toSave.getTask());
        } else {
            dataMaster.saveRepeatingEvent(toSave.getEvent());
        }
    }

    // A holder class for TaskHolders and Event Holders.
    private class DoublyHolder {
        List<List<TaskEventHolder>> tasks, events;
    }
}
