package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

import java.util.List;

// Interface implemented by Fragments to receive communication from ListDataController:
public interface P2 {

    void deliverCompleted(List<TaskEventHolder> set);
    void deliverUpcoming(List<TaskEventHolder> set);
    void deliverUnassigned(List<TaskEventHolder> set);
    void deliverOverdue(List<TaskEventHolder> set);

    void addCompleted(List<TaskEventHolder> set);
    void addUpcoming(List<TaskEventHolder> set);
    void addUnassigned(List<TaskEventHolder> set);
    void addOverdue(List<TaskEventHolder> set);
}
