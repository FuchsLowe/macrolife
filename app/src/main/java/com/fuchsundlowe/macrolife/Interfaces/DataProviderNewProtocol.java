package com.fuchsundlowe.macrolife.Interfaces;

// This is universal Data Provider Protocol for All

import android.arch.lifecycle.LiveData;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public interface DataProviderNewProtocol {
    LiveData<List<TaskObject>> getTasksFor(Calendar day);
    ComplexGoal findComplexGoal(int byID);
    TaskObject findTaskObjectBy(int ID);
    // tasks either starts, ends or lasts through this day
    LiveData<List<TaskObject>> getTaskThatIntersects(Calendar day);
    LiveData<List<RepeatingEvent>> getEventsThatIntersect(Calendar day);
    ComplexGoal getComplexGoalBy(int masterID);
    void deleteTask(TaskObject objectToDelete);
    void saveListObject(ListObject objectToSave);
    void deleteListObject(ListObject objectToDelete);
    ArrayList<ListObject> findListFor(int taskObjectID);
}
