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



    // Task Object calls:
    TaskObject findTaskObjectBy(int ID);
    void saveTaskObject(TaskObject task);
    ArrayList<TaskObject>getDataForRecommendationBar();
    LiveData<List<TaskObject>>getLiveDataForRecommendationBar();
    LiveData<TaskObject> getTaskObjectWithCreationTime(Calendar creationTime);
    LiveData<List<TaskObject>>getTasksForWeekView(Calendar forDay);
    void deleteTask(TaskObject objectToDelete);
    LiveData<List<TaskObject>> getTaskThatIntersects(Calendar day);
    LiveData<List<TaskObject>> getTasksForRemindersView(Calendar forDay);


    // Repeating Events:
    LiveData<List<RepeatingEvent>>getAllEvents();
    LiveData<List<RepeatingEvent>> getEventsThatIntersect(Calendar day);
    RepeatingEvent getEventWith(int hashID);
    void saveRepeatingEvent(RepeatingEvent event);
    void reSaveRepeatingEventsFor(int masterHashID);
    void deleteAllRepeatingEvents(int forMasterID);
    void deleteRepeatingEvent(RepeatingEvent eventToDelete);


    // List Objects:
    List<ListObject> findListFor(int taskObjectID);
    void saveListObject(ListObject objectToSave);
    void deleteListObject(ListObject objectToDelete);


    // Complex Goals:
    ComplexGoal getComplexGoalBy(int masterID);
    ComplexGoal findComplexGoal(int byID);


    // Methods:
    boolean isDataBaseOpen();
    void closeDataBase();
    int findNextFreeHashIDForTask();
    int findNextFreeHashIDForList();
    int findNextFreeHashIDForEvent();
}
