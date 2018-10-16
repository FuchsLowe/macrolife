package com.fuchsundlowe.macrolife.ListView;


import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.AsyncSorterCommunication;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

// Intended for wrapping several objects to be used by AsyncSorter
public class Transporter {

    List<TaskObject> virginTasks;
    List<RepeatingEvent> virginEvents;

    Vector<TaskEventHolder> oldUnassigned;
    Vector<TaskEventHolder> oldCompleted;
    Vector<TaskEventHolder> oldUpcoming;
    Vector<TaskEventHolder> oldOverdue;

    Map<Integer, TaskEventHolder> oldUnassignedMap;
    Map<Integer, TaskEventHolder> oldCompletedMap;
    Map<Integer, TaskEventHolder> oldUpcomingMap;
    Map<Integer, TaskEventHolder> oldOverdueMap;

    Map<Integer, TaskEventHolder> nextTaskMap;

    boolean editedUnassigned, editedCompleted, editedUpcoming, editedOverdue;

    AsyncSorterCommunication parent;

    public Transporter(List<TaskObject> tasksToConvert, List<RepeatingEvent> eventsToConvert,
                       Vector<TaskEventHolder> unassigned,
                       Vector<TaskEventHolder> completed,
                       Vector<TaskEventHolder> upcoming,
                       Vector<TaskEventHolder> overdue,
                       AsyncSorterCommunication parent) {

        this.virginTasks = tasksToConvert;
        this.virginEvents = eventsToConvert;
        this.oldUnassigned = unassigned;
        this.oldCompleted = completed;
        this.oldUpcoming = upcoming;
        this.oldOverdue = overdue;
        this.parent = parent;

    }
    // We create maps for easier checks of Buckets back in ListDataController
    public void initiateMaps() {
        // Call this from Asynchronous thread
        oldUnassignedMap = new HashMap<>();
        oldCompletedMap = new HashMap<>();
        oldUpcomingMap = new HashMap<>();
        oldOverdueMap = new HashMap<>();
        nextTaskMap = new HashMap<>();

        // Now we fill them up:
        for (TaskEventHolder holder: oldUnassigned) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            oldUnassignedMap.put(key, holder);
        }
        for (TaskEventHolder holder: oldCompleted) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            oldCompletedMap.put(key, holder);
        }
        for (TaskEventHolder holder: oldUpcoming) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            oldUpcomingMap.put(key, holder);
        }
        for (TaskEventHolder holder: oldOverdue) {
            int key = holder.getActiveID();
            if (!holder.isTask()) {
                key *= -1;
            }
            oldOverdueMap.put(key, holder);
        }
    }

    boolean areTasks() {
        return virginTasks != null;
    }

    int sizeOfList() {
        if (areTasks()) {
            if (virginTasks != null) {
                return virginTasks.size();
            } else {
                return 0;
            }
        } else{
            if (virginEvents != null) {
                return virginEvents.size();
            } else {
                return 0;
            }
        }
    }

}
