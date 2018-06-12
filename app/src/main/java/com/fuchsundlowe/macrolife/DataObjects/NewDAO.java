package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

@Dao
public interface NewDAO {

    @Query("SELECT * FROM TaskObject WHERE (taskStartTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(taskEndTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN taskStartTime AND taskEndTime) OR " +
            "(:dayEnd BETWEEN taskStartTime AND taskEndTime)" )
    LiveData<ArrayList<TaskObject>> getTaskThatIntersects(long dayStart, long dayEnd);

    @Query("SELECT * FROM RepeatingEvent WHERE (startTime BETWEEN :dayStart AND :dayEnd) OR " +
            "(endTime BETWEEN :dayStart AND :dayEnd ) OR " +
            "(:dayStart BETWEEN startTime AND endTime) OR " +
            "(:dayEnd BETWEEN startTime AND endTime)" )
    LiveData<ArrayList<RepeatingEvent>> getEventThatIntersects(long dayStart, long dayEnd);


}
