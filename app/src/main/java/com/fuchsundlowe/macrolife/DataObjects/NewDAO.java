package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NewDAO {

    @Query("SELECT * FROM TaskObject WHERE (TimeDefined == 2 AND ((taskStartTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(taskEndTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN taskStartTime AND taskEndTime) OR " +
            "(:dayEnd BETWEEN taskStartTime AND taskEndTime)))" )
    LiveData<List<TaskObject>> getTaskThatIntersects(long dayStart, long dayEnd);

    @Query("SELECT * FROM RepeatingEvent WHERE (startTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(endTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN startTime AND endTime) OR " +
            "(:dayEnd BETWEEN startTime AND endTime)" )
    LiveData<List<RepeatingEvent>> getEventThatIntersects(long dayStart, long dayEnd);

    @Query("SELECT * FROM TaskObject WHERE (TimeDefined == 1 AND taskStartTime BETWEEN :dayStart AND :dayEnd)")
    LiveData<List<TaskObject>> getReminderTasksForDay(long dayStart, long dayEnd);

}
