package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

import java.util.Map;

// A protocol that enables communication back from AsyncSorter to LDC...
public interface AsyncSorterCommunication {

    void markTasksReady();
    void markEventsReady();

    void changedOverdue();
    void changedUpcoming();
    void changedCompleted();
    void changedUnassigned();

    void deliverNewComplexTotals(Map<Integer, Integer> completed, Map<Integer, Integer> incomplete,
                                 Map<Integer, TaskEventHolder> nextTasks);

    void flushChanges();
}
