package com.fuchsundlowe.macrolife.Interfaces;


import android.arch.lifecycle.Observer;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.ListView.ComplexDataProtocol;

import java.util.List;

// Used by fragments to report changes back to parent and sign up for, implemented by LDC...
public interface LDCProtocol {
    // Used to report the change made in one of TaskEvents so it should be re-sifted
    void subscribeToCompleted(LDCToFragmentListView protocol);
    void subscribeToOverdue(LDCToFragmentListView protocol);
    void subscribeToUnassigned(LDCToFragmentListView protocol);
    void subscribeToUpcoming(LDCToFragmentListView protocol);
    void subscribeToComplexGoalsStatistics(LDCToFragmentListView protocol);
    void subscribeToComplexLiveData(ComplexDataProtocol protocol);
    void destroy();

    // Used to connect Observers from LDC with life-cycle aware observables
    Observer<List<TaskObject>> getTaskObserver();
    Observer<List<RepeatingEvent>> getEventObserver();
    Observer<List<ComplexGoal>> getGoalObserver();

    // Standard calls to DB:
    TaskEventHolder searchForTask(int taskID);
    TaskEventHolder searchForEvent(int eventID);
    ComplexGoal searchForComplexGoal(int goalID);

    void saveTaskEventHolder(TaskEventHolder toSave);

    void deleteTask(TaskObject taskToDelete);
    void deleteEvent(RepeatingEvent eventToDelete);
}
