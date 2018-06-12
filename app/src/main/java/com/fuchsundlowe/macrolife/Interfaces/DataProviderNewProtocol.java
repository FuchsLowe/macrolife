package com.fuchsundlowe.macrolife.Interfaces;

// This is universal Data Provider Protocol for All

import android.arch.lifecycle.LiveData;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public interface DataProviderNewProtocol {
    LiveData<ArrayList<TaskObject>> getTasksFor(Calendar day);
    ComplexGoal findComplexGoal(int byID);
    TaskObject findTaskObjectBy(int ID);
    // tasks either starts, ends or lasts through this day
    LiveData<ArrayList<TaskObject>> getTaskThatIntersects(Calendar day);
    LiveData<ArrayList<RepeatingEvent>> getEventsThatIntersect(Calendar day);
}
