package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventsChild;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/26/18.
 */

public interface DataProviderProtocol {


    Set<ComplexGoalMaster> getComplexGoals();
    void deleteObject(ComplexGoalMaster object);
    Set<ComplexGoalMaster> getComplexGoalsByDay(Calendar day);
    void updateObject(ComplexGoalMaster object);
    void insertObject(ComplexGoalMaster object);

    Set<ListMaster>getAllListMasters();
    void deleteObject(ListMaster object);
    Set<ListMaster>getAllListMastersByDay(Calendar day);
    void updateObject(ListMaster object);
    void insertObject(ListMaster object);
    Set<ListObject>getListObjects();
    void deleteObject(ListObject object);

    Set<OrdinaryEventMaster>getAllOrdinaryEventMasters();
    void deleteObject(OrdinaryEventMaster object);
    Set<OrdinaryEventMaster>getAllOrdinaryTasksByDay(Calendar day);
    void updateObject(OrdinaryEventMaster object);
    void insertObject(OrdinaryEventMaster object);

    Set<RepeatingEventMaster>getAllRepeatingEventMasterss();
    void deleteObject(RepeatingEventMaster object);
    Set<RepeatingEventMaster>getAllRepeatingEventMastersByDay(Calendar day);
    void updateObject(RepeatingEventMaster object);
    void insertObject(RepeatingEventMaster object);

    Set<RepeatingEventsChild>getAllRepeatingEventChildren();
    void deleteObject(RepeatingEventsChild object);
    Set<RepeatingEventsChild>getAllRepeatingChildrenByParent(int parentId);
    void updateObject(RepeatingEventsChild object);
    void insertObject(RepeatingEventsChild object);

    Set<SubGoalMaster>getAllSubGoalMasters();
    void deleteObject(SubGoalMaster object);
    Set<SubGoalMaster>getAllSubGoalsByMasterId(int masterID);
    void updateObject(SubGoalMaster object);
    void insertObject(SubGoalMaster object);

    void closeDatabase();
}
