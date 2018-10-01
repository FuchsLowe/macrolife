package com.fuchsundlowe.macrolife.ListView;

import android.arch.lifecycle.LiveData;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;

import java.util.List;

// Simple protocol used to subscribe to live data for ComplexGoals
public interface ComplexLiveDataProtocol {

    void complexGoalLiveData(LiveData<List<ComplexGoal>> data);
}
