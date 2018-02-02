package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by macbook on 2/2/18.
 */
@Dao
public interface DAO {

    // Managing ComplexGoal objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(ComplexGoalMaster event);

    @Delete
    void deleteTask(ComplexGoalMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ComplexGoalMaster eventMaster);

    @Query("SELECT * FROM ComplexGoalMaster")
    public ComplexGoalMaster[] getAllComplexGoalMasters();


    // Managing OrdinaryEventMaster objects
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(OrdinaryEventMaster event);

    @Delete
    void deleteTask(OrdinaryEventMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(OrdinaryEventMaster eventMaster);

    @Query("SELECT * FROM OrdinaryEventMaster")
    public OrdinaryEventMaster[] getAllOrdinaryEventMasters();


    //Managing List Master objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(ListMaster event);

    @Delete
    void deleteTask(ListMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ListMaster eventMaster);

    @Query("SELECT * FROM ListMaster")
    public ListMaster[] getAllListMasters();


    // Managing ListObject objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(ListObject event);

    @Delete
    void deleteTask(ListObject event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ListObject eventMaster);

    @Query("SELECT * FROM ListObject")
    public ListObject[] getAllListObject();


    // Managing RepeatingEventMaster objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(RepeatingEventMaster event);

    @Delete
    void deleteTask(RepeatingEventMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(RepeatingEventMaster eventMaster);

    @Query("SELECT * FROM RepeatingEventMaster")
    public RepeatingEventMaster[] getAllRepeatingEventMaster();


    // Manages RepeatingEventsChild objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(RepeatingEventsChild event);

    @Delete
    void deleteTask(RepeatingEventsChild event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(RepeatingEventsChild eventMaster);

    @Query("SELECT * FROM RepeatingEventsChild")
    public RepeatingEventsChild[] getAllRepeatingEventsChild();


    // Manages SubGoalMaster objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(SubGoalMaster event);

    @Delete
    void deleteTask(SubGoalMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(SubGoalMaster eventMaster);

    @Query("SELECT * FROM SubGoalMaster")
    public SubGoalMaster[] getAllSubGoalMaster();

}
