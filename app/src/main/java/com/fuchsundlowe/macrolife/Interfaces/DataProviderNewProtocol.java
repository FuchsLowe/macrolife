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
import java.util.Set;

public interface DataProviderNewProtocol {
    LiveData<List<TaskObject>> getTasksFor(Calendar day);
    ComplexGoal findComplexGoal(int byID);
    TaskObject findTaskObjectBy(int ID);
    // tasks either starts, ends or lasts through this day
    LiveData<List<TaskObject>> getTaskThatIntersects(Calendar day);
    LiveData<List<RepeatingEvent>> getEventsThatIntersect(Calendar day);
    List<RepeatingEvent> getEventsBy(int masterID, TaskObject.Mods mod);
    RepeatingEvent getEventWith(int hashID);
    ComplexGoal getComplexGoalBy(int masterID);
    void deleteTask(TaskObject objectToDelete);
    void saveListObject(ListObject objectToSave);
    void deleteListObject(ListObject objectToDelete);
    List<ListObject> findListFor(int taskObjectID);
    void deleteRepeatingEvent(RepeatingEvent eventToDelete);
    void deleteAllRepeatingEvents(int forMasterID, TaskObject.Mods withMod);
    void saveRepeatingEvent(RepeatingEvent event);
    void reSaveRepeatingEventsFor(int masterHashID);
    LiveData<List<RepeatingEvent>>getAllEvents();
    void saveTaskObject(TaskObject task);
    ArrayList<TaskObject>getDataForRecommendationBar();
    boolean isDataBaseOpen();
    void closeDataBase();
    LiveData<TaskObject> getTaskObjectWithCreationTime(Calendar creationTime);
    int findNextFreeHashIDForTask();
    int findNextFreeHashIDForList();
    int findNextFreeHashIDForEvent();
}
