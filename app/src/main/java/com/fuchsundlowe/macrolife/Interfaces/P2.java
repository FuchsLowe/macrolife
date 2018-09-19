package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

import java.util.List;

// Abstract class implemented by Fragments to receive communication from ListDataController:
public abstract class P2 {

    // Mark: Used to deliver whole new set of data
    public void deliverCompleted(List<TaskEventHolder> newHolders) {

    }
    public void deliverOverdue(List<TaskEventHolder> newHolders) {

    }
    public void deliverUnassigned(List<TaskEventHolder> newHolders) {

    }
    public void deliverUpcoming(List<TaskEventHolder> newHolders) {

    }
}
