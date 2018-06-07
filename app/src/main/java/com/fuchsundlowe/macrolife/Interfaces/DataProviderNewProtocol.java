package com.fuchsundlowe.macrolife.Interfaces;

// This is universal Data Provider Protocol for All

import android.arch.lifecycle.LiveData;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

import java.util.ArrayList;
import java.util.Calendar;

public interface DataProviderNewProtocol {
    LiveData<ArrayList<TaskObject>> getTasksFor(Calendar day);
    ComplexGoal findComplexGoal(int byID);
}
