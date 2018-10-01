package com.fuchsundlowe.macrolife.Interfaces;


import android.arch.lifecycle.LiveData;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.ListView.ComplexLiveDataProtocol;

import java.util.List;

// Used by fragments to report changes back to parent and sign up for, implemented by LDC...
public interface LDCProtocol {
    // Used to report the change made in one of TaskEvents so it should be re-sifted
    void subscribeToCompleted(LDCToFragmentListView protocol);
    void subscribeToOverdue(LDCToFragmentListView protocol);
    void subscribeToUnassigned(LDCToFragmentListView protocol);
    void subscribeToUpcoming(LDCToFragmentListView protocol);
    void subscribeToComplexGoalsStatistics(LDCToFragmentListView protocol);
    LiveData<List<ComplexGoal>> subscribeToComplexLiveData(ComplexLiveDataProtocol protocol);
    void destroy();

    // Standard calls to DB:
    TaskEventHolder searchForTask(int taskID);
    TaskEventHolder searchForEvent(int eventID);
    void deleteTask(TaskObject taskToDelete);
    void deleteEvent(RepeatingEvent eventToDelete);
}
