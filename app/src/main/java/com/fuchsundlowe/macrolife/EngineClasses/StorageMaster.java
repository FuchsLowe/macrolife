package com.fuchsundlowe.macrolife.EngineClasses;

import android.content.Context;

import com.fuchsundlowe.macrolife.DataObjects.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by macbook on 2/2/18.
 * Purpose of this class is to store all data that will be used in app.
 * It loads database on creation of the app, does abstarction over the way it stores data
 * Manages all calls from data objects
 */

public class StorageMaster {
    // Database Class:
    private DataProvider dataBase;
    private DAO dataAccessObject;

    // ComplexGoalMaster
    private Set<ComplexGoalMaster> allComplexGoals; // HashSet<>

    public Set<ComplexGoalMaster> getComplexGoals() {
        return allComplexGoals;
    }

    public void insertObject(ComplexGoalMaster object){
        allComplexGoals.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(ComplexGoalMaster object) {
        dataAccessObject.updateTask(object);
    }

    public void deleteObject(ComplexGoalMaster object) {
        allComplexGoals.add(object);
        dataAccessObject.deleteTask(object);
    }

    public boolean amIStored(ComplexGoalMaster object) {
        int myID = object.getHashID();
        for (ComplexGoalMaster value: getComplexGoals()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }


    // ListMaster
    private Set<ListMaster> allListMasters;
    public Set<ListMaster> getAllListMasters() {
        return this.allListMasters;
    }
    public void insertObject(ListMaster object){
        allListMasters.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(ListMaster object) {
        dataAccessObject.updateTask(object);
    }

    public void deleteObject(ListMaster object) {
        allListMasters.add(object);
        dataAccessObject.deleteTask(object);
    }

    public boolean amIStored(ListMaster object) {
        int myID = object.getHashID();
        for (ListMaster value: getAllListMasters()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }


    // ListObject
    private Set<ListObject> allListObjects;
    public Set<ListObject> getListObjects() {
        return allListObjects;
    }

    public void insertObject(ListObject object){
        allListObjects.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(ListObject object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(ListObject object) {
        allListObjects.remove(object); // Removes it from heap here
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }
    public boolean amIStored(ListObject object) {
        int myID = object.getHashID();
        for (ListObject value: getListObjects()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }




    // OrdinaryEventMaster
    private Set<OrdinaryEventMaster> allOrdinaryEventMasters;
    // RepeatingEventMaster
    private Set<RepeatingEventMaster> allRepeatingEventMasters;
    // RepeatingEventChild
    private Set<RepeatingEventsChild> allRepeatingEventChildren;
    // SubGoalMaster
    private Set<SubGoalMaster> allSubGoalMasters;

    // onCreation:
    public StorageMaster(Context appContext) {

    }

    // General purpose methods:
    public boolean checkIfIDisAssigned(int idToCheck) {
        for (ComplexGoalMaster value: getComplexGoals()) {
            if (value.getHashID() == idToCheck) { return true; }
        }
        return false;
    }

}
