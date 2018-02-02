package com.fuchsundlowe.macrolife.RoomElementsScapped;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;

/**
 * Created by macbook on 1/31/18.
 */
//@Dao
public interface myTestDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTask(OrdinaryEventMaster task);

    @Query("SELECT * FROM OrdinaryEventMaster")
    public OrdinaryEventMaster[] getAllTasks();

}
