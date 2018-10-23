package com.fuchsundlowe.macrolife.MonthView;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;

import java.util.ArrayList;
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
    private Map<Short, DoublyHolder> yearBuckets; // Holds Doubly Holders mapped for year...
    private Map<Short, short[]> minuteCounter; // Holds minutes per day for each task...
    private int currentYear = -1;
    private LifecycleOwner lifecycle;
    private Observer<List<TaskObject>> taskObserver;
    private Observer<List<RepeatingEvent>> eventObserver;
    private List<LiveData<List<TaskObject>>> tasks;
    private List<LiveData<List<RepeatingEvent>>> events;


    MonthViewModel(Context context, LifecycleOwner lifecycleOwner) {
        dataMaster = LocalStorage.getInstance(context); // just in case...
        yearBuckets = new HashMap<>();
        minuteCounter = new HashMap<>();
        lifecycle = lifecycleOwner;

        tasks = new ArrayList<>();
        events = new ArrayList<>();
        // TODO: Define Observers

    }

    // Protocol Implementations
    @Override
    public TaskEventHolder findAsTask(int hashID) {
        TaskObject data = dataMaster.findTaskObjectBy(hashID);
        return (data != null) ? new TaskEventHolder(data, null) : null;
    }
    @Override
    public TaskEventHolder findAsEvent(int hashID) {
        RepeatingEvent data = dataMaster.getEventWith(hashID);
        return (data != null) ? new TaskEventHolder(null, data) :  null;
    }
    @Override
    public short[] timeTablesFor(short year) {
        /*
         * Needs to access all of the tasks in specified year.
         * Then it needs to go through all of them ( from where will we get the data? Is it gonna be
         * live data directly or data we sorted for day list ) and count minutes of duration for
         * each day and stores them in designated array for year. We simply return here the pre-def
         * array.
         *
         * How is data stored sir?
         */
        return (minuteCounter.get(year) != null) ? minuteCounter.get(year) : new short[0];

    }
    @Override
    public List<TaskEventHolder> holdersFor(int dayOfYear, short year) {
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
            tasks.get(2).observe(lifecycle, taskObserver);

            events.get(0).removeObservers(lifecycle);
            events.set(0, events.get(1));
            events.set(1, events.get(2));
            events.set(2, dataMaster.eventsForAYear(year +1));
            events.get(2).observe(lifecycle, eventObserver);
        } else if (year == currentYear -1) {
            tasks.get(2).removeObservers(lifecycle);
            tasks.set(2, tasks.get(1));
            tasks.set(1, tasks.get(0));
            tasks.set(0, dataMaster.tasksForAYear(year -1));
            tasks.get(0).observe(lifecycle, taskObserver);

            events.get(2).removeObservers(lifecycle);
            events.set(2, events.get(1));
            events.set(1, events.get(0));
            events.set(0, dataMaster.eventsForAYear(year -1));
            events.get(0).observe(lifecycle, eventObserver);
        } else {
            // completely new Data should be set
            // Unsubscribe potential data
            for (LiveData<List<TaskObject>> liveData: tasks) {
                liveData.removeObservers(lifecycle);
            }
            tasks.clear();
            tasks.set(0, dataMaster.tasksForAYear(year -1));
            tasks.get(0).observe(lifecycle, taskObserver);
            tasks.set(1, dataMaster.tasksForAYear(year));
            tasks.get(1).observe(lifecycle, taskObserver);
            tasks.set(2, dataMaster.tasksForAYear(year +1));
            tasks.get(2).observe(lifecycle, taskObserver);

            for (LiveData<List<RepeatingEvent>> liveData: events) {
                liveData.removeObservers(lifecycle);
            }
            events.clear();
            events.set(0, dataMaster.eventsForAYear(year -1));
            events.get(0).observe(lifecycle, eventObserver);
            events.set(1, dataMaster.eventsForAYear(year));
            events.get(1).observe(lifecycle, eventObserver);
            events.set(2, dataMaster.eventsForAYear(year +1));
            events.get(3).observe(lifecycle, eventObserver);
        }

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

    /*
     * A holder class for TaskHolders and Event Holders.
     */
    private class DoublyHolder {
        List<List<TaskEventHolder>> tasks, events;
    }
}
