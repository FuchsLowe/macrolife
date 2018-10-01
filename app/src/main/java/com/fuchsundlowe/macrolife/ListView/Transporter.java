package com.fuchsundlowe.macrolife.ListView;


import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.AsyncSorterCommunication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Intended for wrapping several objects to be used by AsyncSorter
public class Transporter {

    List<TaskObject> tasksToConvert;
    List<RepeatingEvent> eventsToConvert;

    List<TaskEventHolder> mUnassigned;
    List<TaskEventHolder> mCompleted;
    List<TaskEventHolder> mUpcoming;
    List<TaskEventHolder> mOverdue;

    Map<Integer, TaskEventHolder> unassigned;
    Map<Integer, TaskEventHolder> completed;
    Map<Integer, TaskEventHolder> upcoming;
    Map<Integer, TaskEventHolder> overdue;

    Map<Integer, TaskEventHolder> nextTask;

    boolean editedUnassigned, editedCompleted, editedUpcoming, editedOverdue;

    AsyncSorterCommunication parent;

    public Transporter(List<TaskObject> tasksToConvert, List<RepeatingEvent> eventsToConvert,
                       List<TaskEventHolder> unassigned,
                       List<TaskEventHolder> completed,
                       List<TaskEventHolder> upcoming,
                       List<TaskEventHolder> overdue,
                       AsyncSorterCommunication parent) {

        this.tasksToConvert = tasksToConvert;
        this.eventsToConvert = eventsToConvert;
        this.mUnassigned = unassigned;
        this.mCompleted = completed;
        this.mUpcoming = upcoming;
        this.mOverdue = overdue;
        this.parent = parent;

    }
    // We create maps for easier checks of Buckets back in ListDataController
    public void initiateMaps() {
        // Call this from Asynchronous thread
        unassigned = new HashMap<>();
        completed = new HashMap<>();
        upcoming = new HashMap<>();
        overdue = new HashMap<>();
        nextTask = new HashMap<>();

        // Now we fill them up:
        for (TaskEventHolder holder: mUnassigned) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            unassigned.put(key, holder);
        }
        for (TaskEventHolder holder: mCompleted) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            completed.put(key, holder);
        }
        for (TaskEventHolder holder: mUpcoming) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            upcoming.put(key, holder);
        }
        for (TaskEventHolder holder: mOverdue) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            overdue.put(key, holder);
        }
    }

    boolean areTasks() {
        return tasksToConvert != null;
    }

    int sizeOfList() {
        if (areTasks()) {
            if (tasksToConvert != null) {
                return tasksToConvert.size();
            } else {
                return 0;
            }
        } else{
            if (eventsToConvert != null) {
                return eventsToConvert.size();
            } else {
                return 0;
            }
        }
    }

}
