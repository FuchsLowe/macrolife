package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Dao
public interface NewDAO {

    // Inserting Data:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(TaskObject ... taskObjects);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertComplexTask(ComplexGoal ... complexGoals);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertListObject(ListObject ... listObjects);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRepeatingEvent(RepeatingEvent ... events);


    // Updating Values:
    @Update
    void saveTask(TaskObject ... taskObjects);

    @Update
    void saveComplexTask(ComplexGoal ... complexGoals);

    @Update
    void saveListObject(ListObject ... listObjects);

    @Update
    void saveRepeatingEvent(RepeatingEvent ... repeatingEvents);


    // Deleting Values:
    @Delete
    void removeTask(TaskObject ... taskObjects);

    @Delete
    void removeComplexGoal(ComplexGoal ... complexGoals);

    @Delete
    void removeListObject(ListObject ... listObjects);

    @Delete
    void removeRepeatingEvent(RepeatingEvent ... repeatingEvents);


    // Queries for in memory search:
    @Query("SELECT * FROM TaskObject")
    LiveData<List<TaskObject>> getAllTaskObjects();

    @Query("SELECT * FROM ComplexGoal")
    LiveData<List<ComplexGoal>> getAllComplexGoals();

    @Query("SELECT * FROM ListObject")
    LiveData<List<ListObject>> getAllListObjects();

    @Query("SELECT * FROM RepeatingEvent")
    LiveData<List<RepeatingEvent>> getAllRepeatingEvents();

    @Query("SELECT * FROM REPEATINGEVENT")
    RepeatingEvent[] TEST_repeatingEvents();

    @Query("SELECT * FROM TASKOBJECT")
    TaskObject[] TEST_taskObjects();

    @Query("SELECT * FROM TASKOBJECT WHERE (hashID == :hashID)")
    LiveData<TaskObject> findTaskObjectWith(int hashID);


    // Specialty Queries:
    @Query("SELECT * FROM TaskObject WHERE (TimeDefined == 2 AND ((taskStartTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(taskEndTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN taskStartTime AND taskEndTime) OR " +
            "(:dayEnd BETWEEN taskStartTime AND taskEndTime)))" )
    LiveData<List<TaskObject>> getTaskThatIntersects(long dayStart, long dayEnd);

    @Query("SELECT * FROM TaskObject WHERE ((taskStartTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(taskEndTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN taskStartTime AND taskEndTime) OR " +
            "(:dayEnd BETWEEN taskStartTime AND taskEndTime))")
    LiveData<List<TaskObject>> getTaskThatIntersectsDayWithAnyTimeValue(long dayStart, long dayEnd);

    @Query("SELECT * FROM RepeatingEvent WHERE (startTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(endTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN startTime AND endTime) OR " +
            "(:dayEnd BETWEEN startTime AND endTime)" )
    LiveData<List<RepeatingEvent>> getEventThatIntersects(long dayStart, long dayEnd);

    @Query("SELECT * FROM TaskObject WHERE (TimeDefined == 1 AND taskStartTime BETWEEN :dayStart AND :dayEnd)")
    LiveData<List<TaskObject>> getReminderTasksForDay(long dayStart, long dayEnd);

    @Query("SELECT * FROM RepeatingEvent WHERE ((startTime BETWEEN :dayStart AND :dayEnd) AND (endTime == NULL OR endTime == 0))")
    LiveData<List<RepeatingEvent>>getReminderEventsForDay(long dayStart, long dayEnd);

    @Query("SELECT * FROM TaskObject WHERE (taskCreatedTimeStamp == :timeStamp)")
    LiveData<TaskObject> getTaskObjectWithCreationTime(long timeStamp);

    @Query("SELECT * FROM TaskObject WHERE (timeDefined == 0 AND isTaskCompleted == 0)")
    LiveData<List<TaskObject>> getTasksForRecommendationFetcher();

    @Query("SELECT * FROM TaskObject WHERE (hashID == :byId)")
    TaskObject findTaskObject(int byId);

}
