package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

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

    /*
    @Query("SELECT * FROM ComplexGoalMaster")
    public ComplexGoalMaster[] getAllComplexGoalMasters();
    */


    @Query("SELECT * FROM ComplexGoalMaster")
    LiveData<List<ComplexGoalMaster>> getAllComplexGoalMasters();


    // Managing OrdinaryEventMaster objects
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(OrdinaryEventMaster event);

    @Delete
    void deleteTask(OrdinaryEventMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(OrdinaryEventMaster eventMaster);

    @Query("SELECT * FROM OrdinaryEventMaster")
    public LiveData<List<OrdinaryEventMaster>> getAllOrdinaryEventMasters();


    //Managing List Master objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(ListMaster event);

    @Delete
    void deleteTask(ListMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ListMaster eventMaster);

    @Query("SELECT * FROM ListMaster")
    public LiveData<List<ListMaster>> getAllListMasters();

    @Query("SELECT * FROM ListMaster WHERE hashID = :masterID")
    LiveData<ListMaster> getListMasterByID(int masterID);

    // Managing ListObject objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(ListObject event);

    @Delete
    void deleteTask(ListObject event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(ListObject eventMaster);

    @Query("SELECT * FROM ListObject")
    public LiveData<List<ListObject>> getAllListObject();

    @Query("SELECT * FROM ListObject WHERE masterID = :parentID")
    LiveData<List<ListObject>>getListObjectsByParent(int parentID);


    // Managing RepeatingEventMaster objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(RepeatingEventMaster event);

    @Delete
    void deleteTask(RepeatingEventMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(RepeatingEventMaster eventMaster);

    @Query("SELECT * FROM RepeatingEventMaster")
    public LiveData<List<RepeatingEventMaster>> getAllRepeatingEventMaster();

    @Query("SELECT * FROM RepeatingEventMaster WHERE parentID =:withParentID")
    LiveData<List<RepeatingEventMaster>> getAllRepeatingMasters(int withParentID);

    // Manages RepeatingEventsChild objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(RepeatingEventsChild event);

    @Delete
    void deleteTask(RepeatingEventsChild event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(RepeatingEventsChild eventMaster);

    @Query("SELECT * FROM RepeatingEventsChild")
    public LiveData<List<RepeatingEventsChild>> getAllRepeatingEventsChild();



    // Manages SubGoalMaster objects:
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(SubGoalMaster event);

    @Delete
    void deleteTask(SubGoalMaster event);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(SubGoalMaster eventMaster);

    @Query("SELECT * FROM SubGoalMaster")
    public LiveData<List<SubGoalMaster>> getAllSubGoalMaster();

    @Query("SELECT * FROM SubGoalMaster WHERE parentID = :ofMaster")
    LiveData<List<SubGoalMaster>> findAllChildren(int ofMaster);

    // TestQuerry:

    @Query("SELECT ComplexGoalMaster.hashID AS hashID, ComplexGoalMaster.taskName as name " +
    "FROM ComplexGoalMaster " + "WHERE taskCompleted = 0"
    )
    LiveData<List<PopUpData>> loadPopUpValues();
}
