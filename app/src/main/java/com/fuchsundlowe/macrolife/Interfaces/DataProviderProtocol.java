package com.fuchsundlowe.macrolife.Interfaces;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventsChild;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

/**
 * Created by macbook on 1/26/18.
 */

public interface DataProviderProtocol {


    //LiveData<List<ComplexGoalMaster>> getComplexGoals();
    void deleteObject(ComplexGoalMaster object);
    Set<ComplexGoalMaster> getComplexGoalsByDay(Calendar day);
    void updateObject(ComplexGoalMaster object);
    void insertObject(ComplexGoalMaster object);
    void subscribeObserver_ComplexGoal(LifecycleOwner lifecycleOwner, Observer<List<ComplexGoalMaster>> observer);
   // LiveData<List<ComplexGoalMaster>> getAllComplexGoals();

    //Set<ListMaster>getAllListMasters();
    void deleteObject(ListMaster object);
    Set<ListMaster>getAllListMastersByDay(Calendar day);
    void updateObject(ListMaster object);
    void insertObject(ListMaster object);
    void subscribeObserver_ListMaster(LifecycleOwner lifecycleOwner, Observer<List<ListMaster>> observer);
    //LiveData<List<ListMaster>> getAllListMasters();

    void insertObject(ListObject object);
    //Set<ListObject>getListObjects();
    void updateListObject(ListObject object);
    void deleteObject(ListObject object);
    void subscribeObserver_ListObject(LifecycleOwner lifecycleOwner, Observer<List<ListObject>> observer);
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

    //Set<RepeatingEventsChild>getAllRepeatingEventChildren();
    void deleteObject(RepeatingEventsChild object);
    Set<RepeatingEventsChild>getAllRepeatingChildrenByParent(int parentId);
    void updateObject(RepeatingEventsChild object);
    void insertObject(RepeatingEventsChild object);
    void subscribeObserver_RepeatingCgild(LifecycleOwner lifecycleOwner, Observer<List<RepeatingEventsChild>> observer);
    //LiveData<List<RepeatingEventsChild>> getAllRepeatingEventChild();

    //Set<SubGoalMaster>getAllSubGoalMasters();
    void deleteObject(SubGoalMaster object);
    Set<SubGoalMaster>getAllSubGoalsByMasterId(int masterID);
    void updateObject(SubGoalMaster object);
    void insertObject(SubGoalMaster object);
    void subscribeObserver_SubGoal(LifecycleOwner lifecycleOwner, Observer<List<SubGoalMaster>> observer);
    //LiveData<List<SubGoalMaster>>getAllSubGoalMasters();

    void closeDatabase();

}
