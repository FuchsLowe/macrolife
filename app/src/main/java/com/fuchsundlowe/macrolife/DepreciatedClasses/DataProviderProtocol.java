package com.fuchsundlowe.macrolife.DepreciatedClasses;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DepreciatedClasses.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DepreciatedClasses.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DepreciatedClasses.PopUpData;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DepreciatedClasses.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.DepreciatedClasses.SubGoalMaster;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by macbook on 1/26/18.
 */
@Deprecated
public interface DataProviderProtocol {

    void deleteObject(ComplexGoal object);
    Set<ComplexGoal> getComplexGoalsByDay(Calendar day);
    void updateObject(ComplexGoal object);
    void insertObject(ComplexGoal object);
    void subscribeObserver_ComplexGoal(LifecycleOwner lifecycleOwner, Observer<List<ComplexGoal>> observer);
    ComplexGoal getComplexGoalBy(int masterGoalID);

    //Set<ListMaster>getAllListMasters();
    void deleteObject(ListMaster object);
    Set<ListMaster>getAllListMastersByDay(Calendar day);
    void updateObject(ListMaster object);
    void insertObject(ListMaster object);
    void subscribeObserver_ListMaster(LifecycleOwner lifecycleOwner, Observer<List<ListMaster>> observer);
    LiveData<ListMaster> getListMasterByID(int masterID);

    void insertObject(ListObject object);
    //Set<ListObject>getListObjects();
    void updateListObject(ListObject object);
    void deleteObject(ListObject object);
    void subscribeObserver_ListObject(LifecycleOwner lifecycleOwner, Observer<List<ListObject>> observer);
    LiveData<List<ListObject>> getListObjectsByParent(int parentHashID);
    //LiveData<List<ListObject>> getAllListObjects();

    //Set<OrdinaryEventMaster>getAllOrdinaryEventMasters();
    void deleteObject(OrdinaryEventMaster object);
    Set<OrdinaryEventMaster>getAllOrdinaryTasksByDay(Calendar day);
    void updateObject(OrdinaryEventMaster object);
    void insertObject(OrdinaryEventMaster object);
    void subscribeObserver_OrdinaryEvent(LifecycleOwner lifecycleOwner, Observer<List<OrdinaryEventMaster>> observer);
    //LiveData<List<OrdinaryEventMaster>> getAllOrdinaryEvents();

    //Set<RepeatingEventMaster>getAllRepeatingEventMasterss();
    void deleteObject(RepeatingEventMaster object);
    Set<RepeatingEventMaster>getAllRepeatingEventMastersByDay(Calendar day);
    void updateObject(RepeatingEventMaster object);
    void insertObject(RepeatingEventMaster object);
    void subscribeObserver_RepeatingMaster(LifecycleOwner lifecycleOwner, Observer<List<RepeatingEventMaster>> observer);
    //LiveData<List<RepeatingEventMaster>> getAllRepeatingEventMasters();
    LiveData<List<RepeatingEventMaster>>getAllSubordinateRepeatingEventMasters(int forMasterID);
    Set<RepeatingEventMaster>getSubordinateRepearingStaticMasters(int forMasterID);

    //Set<RepeatingEvent>getAllRepeatingEventChildren();
    void deleteObject(RepeatingEvent object);
    Set<RepeatingEvent>getAllRepeatingChildrenByParent(int parentId);
    void updateObject(RepeatingEvent object);
    void insertObject(RepeatingEvent object);
    void subscribeObserver_RepeatingCgild(LifecycleOwner lifecycleOwner, Observer<List<RepeatingEvent>> observer);
    //LiveData<List<RepeatingEvent>> getAllRepeatingEventChild();

    //Set<SubGoalMaster>getAllSubGoalMasters();
    void deleteObject(SubGoalMaster object);
    Set<SubGoalMaster>getAllSubGoalsByMasterId(int masterID);
    void updateObject(SubGoalMaster object);
    void insertObject(SubGoalMaster object);
    void subscribeObserver_SubGoal(LifecycleOwner lifecycleOwner, Observer<List<SubGoalMaster>> observer);
    //LiveData<List<SubGoalMaster>>getAllSubGoalMasters();
    LiveData<List<SubGoalMaster>> findAllChildren(int ofMaster);

    void closeDatabase();

    // TEST:
    LiveData<List<PopUpData>> loadPopUpValues();


}
