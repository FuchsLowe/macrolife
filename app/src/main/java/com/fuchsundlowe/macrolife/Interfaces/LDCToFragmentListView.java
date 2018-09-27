package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

import java.util.List;
import java.util.Map;

// Abstract class implemented by Fragments to receive communication from ListDataController:
public abstract class LDCToFragmentListView {

    // Mark: Used to deliver whole new set of data
    public void deliverCompleted(List<TaskEventHolder> newHolders) {

    }
    public void deliverOverdue(List<TaskEventHolder> newHolders) {

    }
    public void deliverUnassigned(List<TaskEventHolder> newHolders) {

    }
    public void deliverUpcoming(List<TaskEventHolder> newHolders) {

    }

    public void deliverComplexTasksStatistics(Map<Integer, Integer> newCompleted,
                                              Map<Integer, Integer> newIncomplete) {

    }
}
