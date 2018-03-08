package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;
import com.fuchsundlowe.macrolife.DataObjects.*;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by macbook on 2/2/18.
 * Purpose of this class is to store all data that will be used in app.
 * It loads database on creation of the app, does abstarction over the way it stores data
 * Manages all calls from data objects
 * TODO: Shoudl all children remove themselves if no parent is alive?
 */

public class StorageMaster implements DataProviderProtocol {
    // Database Class:
    private DataProvider dataBase;
    private DAO dataAccessObject;
    private static StorageMaster self = null;


    public static StorageMaster getInstance(Context context) {
        if (self == null) {
            self = new StorageMaster(context);
        }
        return self;
    }


    public static StorageMaster optionalStorageMaster() {
        return self;
    }

    // ComplexGoalMaster
    private Set<ComplexGoalMaster> allComplexGoals; // HashSet<>

    public Set<ComplexGoalMaster> getComplexGoals() {
        return allComplexGoals;
    }

    public ComplexGoalMaster getComplexGoalBy(int id) {
        for (ComplexGoalMaster goalMaster: getComplexGoals()) {
            if (goalMaster.getHashID() == id) {
                return goalMaster;
            }
        }
        return null;
    }

    public void insertObject(ComplexGoalMaster object){
        allComplexGoals.add(object);
        dataAccessObject.insertTask(object);
    }



    public void updateObject(ComplexGoalMaster object) {
        dataAccessObject.updateTask(object);
    }

    public void deleteObject(ComplexGoalMaster object) {
        for (SubGoalMaster subGoal: this.getSubGoalsOfMaster(object.getHashID())) {
            this.deleteObject(subGoal);
        }

        allComplexGoals.remove(object);
        dataAccessObject.deleteTask(object);
    }

    @Override
    public Set<ComplexGoalMaster> getComplexGoalsByDay(Calendar day) {
        Set<ComplexGoalMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (ComplexGoalMaster goal: getComplexGoals()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
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
        for (ListObject subGoal: getListObjectsOfParent(object.getHashID())) {
            this.deleteObject(subGoal);
        }

        allListMasters.remove(object);
        dataAccessObject.deleteTask(object);
    }

    @Override
    public Set<ListMaster> getAllListMastersByDay(Calendar day) {
        Set<ListMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (ListMaster goal: getAllListMasters()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
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

    public Set<ListObject> getListObjectsOfParent(int parentID) {
        Set<ListObject> listObjects = new HashSet<ListObject>();
        for (ListObject list: getListObjects()) {
            if (list.getMasterID() == parentID) {
                listObjects.add(list);
            }
        }
        return listObjects;
    }


    // OrdinaryEventMaster
    private Set<OrdinaryEventMaster> allOrdinaryEventMasters;
    public Set<OrdinaryEventMaster> getAllOrdinaryEventMasters() {
        return allOrdinaryEventMasters;
    }

    public void insertObject(OrdinaryEventMaster object){
        allOrdinaryEventMasters.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(OrdinaryEventMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(OrdinaryEventMaster object) {
        allOrdinaryEventMasters.remove(object); // Removes it from heap here
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public Set<OrdinaryEventMaster> getAllOrdinaryTasksByDay(Calendar day) {
        Set<OrdinaryEventMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (OrdinaryEventMaster goal: getAllOrdinaryEventMasters()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }

    public boolean amIStored(OrdinaryEventMaster object) {
        int myID = object.getHashID();
        for (OrdinaryEventMaster value: getAllOrdinaryEventMasters()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    // RepeatingEventMaster
    private Set<RepeatingEventMaster> allRepeatingEventMasters;
    public Set<RepeatingEventMaster> getAllRepeatingEventMasterss() {
        return allRepeatingEventMasters;
    }

    public void insertObject(RepeatingEventMaster object){
        allRepeatingEventMasters.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(RepeatingEventMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(RepeatingEventMaster object) {
        for (RepeatingEventsChild subTask: getAllRepeatingChildrenByParent(object.getHashID())) {
            deleteObject(subTask);
        }

        allRepeatingEventMasters.remove(object); // Removes it from heap here
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public Set<RepeatingEventMaster> getAllRepeatingEventMastersByDay(Calendar day) {
        Set<RepeatingEventMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (RepeatingEventMaster goal: getAllRepeatingEventMasterss()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }

    public boolean amIStored(RepeatingEventMaster object) {
        int myID = object.getHashID();
        for (RepeatingEventMaster value: getAllRepeatingEventMasterss()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    public RepeatingEventMaster getMasterByInt(int masterID) {
        for (RepeatingEventMaster master: this.getAllRepeatingEventMasterss()) {
            if (master.getHashID() == masterID) {
                return master;

            }
        }
        return null;
    }
    // RepeatingEventChild
    private Set<RepeatingEventsChild> allRepeatingEventChildren;
    public Set<RepeatingEventsChild> getAllRepeatingEventChildren() {
        return allRepeatingEventChildren;
    }

    public void insertObject(RepeatingEventsChild object){
        allRepeatingEventChildren.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(RepeatingEventsChild object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(RepeatingEventsChild object) {
        allRepeatingEventChildren.remove(object); // Removes it from heap here
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }



    public boolean amIStored(RepeatingEventsChild object) {
        int myID = object.getHashID();
        for (RepeatingEventsChild value: getAllRepeatingEventChildren()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }


    public Set<RepeatingEventsChild> getAllRepeatingChildrenByParent(int parentId) {
        Set<RepeatingEventsChild> hashSet = new HashSet<RepeatingEventsChild>();
        for (RepeatingEventsChild child: getAllRepeatingEventChildren()) {
            if (child.getParentID() == parentId) {
                hashSet.add(child);
            }
        }
        return hashSet;
    }
    // SubGoalMaster
    private Set<SubGoalMaster> allSubGoalMasters;
    public Set<SubGoalMaster> getAllSubGoalMasters() {
        return allSubGoalMasters;
    }

    public void insertObject(SubGoalMaster object){
        allSubGoalMasters.add(object);
        dataAccessObject.insertTask(object);
    }

    public void updateObject(SubGoalMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(SubGoalMaster object) {
        allSubGoalMasters.remove(object); // Removes it from heap here
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public Set<SubGoalMaster> getAllSubGoalsByMasterId(int masterID) {
        Set<SubGoalMaster> temp = new HashSet<>();
        for (SubGoalMaster subGoal: getAllSubGoalMasters()) {
            if (subGoal.getParentID() == masterID) {
                temp.add(subGoal);
            }
        }
        return temp;
    }

    public boolean amIStored(SubGoalMaster object) {
        int myID = object.getHashID();
        for (SubGoalMaster value: getAllSubGoalMasters()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    public Set<Integer> getIDsOfSubGoalfMaster(int withMasterID) {
        HashSet<Integer> hashSet = new HashSet<>();
        for (SubGoalMaster subGoal: getAllSubGoalMasters()) {
            if (subGoal.getHashID() == withMasterID) {
                hashSet.add(subGoal.getHashID());
            }
        }
        return hashSet;
    }
    // Used to retrive the set of subgoals associated with master ID
    public Set<SubGoalMaster> getSubGoalsOfMaster(int withMasterID) {
        HashSet<SubGoalMaster> hashSet = new HashSet<>();
        for (SubGoalMaster goal: getAllSubGoalMasters()) {
            if (goal.getParentID() == withMasterID) {
                hashSet.add(goal);
            }
        }
        return hashSet;
    }
    // Used to retrive a single instance SubGoalMaster via ID
    public SubGoalMaster getSubGoalMasterBy(int ID) {
        for (SubGoalMaster goal: getAllSubGoalMasters()) {
            if (goal.getHashID() == ID) {
                return goal;
            }
        }
        return null;
    }

    // onCreation:
    private StorageMaster(Context appContext) {
        Log.d("Storage Master ","Reported");
        this.dataBase = Room.databaseBuilder(appContext, DataProvider.class, "StandardDB").build();
        this.dataAccessObject = dataBase.daoObject();

        // Shoudl We initiate all database retivals?
        initiateAllValues();
    }




    // General purpose methods:

    public boolean checkIfIDisAssigned(int idToCheck) {
        Set<ComplexGoalMaster> complexGoalMasterSet =  getComplexGoals();
        if (complexGoalMasterSet != null) {
            for (ComplexGoalMaster value : complexGoalMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        Set<ListMaster> listMasterSet = getAllListMasters();
        if (listMasterSet != null) {
            for (ListMaster value : listMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        Set<ListObject> listObjectSet = getListObjects();
        if (listObjectSet != null) {
            for (ListObject value : listObjectSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        Set<OrdinaryEventMaster> ordinaryEventMastersSet = getAllOrdinaryEventMasters();
        if (ordinaryEventMastersSet != null) {
            for (OrdinaryEventMaster value : ordinaryEventMastersSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        Set<RepeatingEventMaster> repeatingEventMasterSet = getAllRepeatingEventMasterss();
        if (repeatingEventMasterSet != null) {
            for (RepeatingEventMaster value : repeatingEventMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        Set<RepeatingEventsChild> repeatingEventsChildSet = getAllRepeatingEventChildren();
        if (repeatingEventsChildSet != null) {
            for (RepeatingEventsChild value : repeatingEventsChildSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        Set<SubGoalMaster> subGoalMasterSet = getAllSubGoalMasters();
        if (subGoalMasterSet != null) {
            for (SubGoalMaster value : subGoalMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        return false;

    }
    // Returns true if a date falls between start and end time
    private boolean checkIfBelongsTimeWise(Calendar startTime, Calendar endTime, Calendar checkTime) {
        if (checkTime.after(startTime) && checkTime.before(endTime)) {
            return true;
        } else {
            return false;
        }
    }

    // DatabaseConnector interface methods:

    private void initiateAllValues() {
        allComplexGoals = new HashSet<>();
        ComplexGoalMaster[] complexGoalMastersArray = dataAccessObject.getAllComplexGoalMasters();
        if (complexGoalMastersArray != null) {
            for(ComplexGoalMaster master: complexGoalMastersArray) {
                allComplexGoals.add(master);
            }
        }

        allListMasters = new HashSet<>();
        ListMaster[] listMastersArray = dataAccessObject.getAllListMasters();
        if (listMastersArray != null) {
            for (ListMaster master: listMastersArray) {
                allListMasters.add(master);
            }
        }

        allListObjects = new HashSet<>();
        ListObject[] listObjectsArray = dataAccessObject.getAllListObject();
        if (listObjectsArray != null) {
            for (ListObject object: listObjectsArray) {
                allListObjects.add(object);
            }
        }

        allOrdinaryEventMasters = new HashSet<>();
        OrdinaryEventMaster[] ordinaryEventMastersArray = dataAccessObject.getAllOrdinaryEventMasters();
        if (ordinaryEventMastersArray != null) {
            for (OrdinaryEventMaster event: ordinaryEventMastersArray) {
                allOrdinaryEventMasters.add(event);
            }
        }

        allRepeatingEventMasters = new HashSet<>();
        RepeatingEventMaster[] repeatingEventMasterArray = dataAccessObject.getAllRepeatingEventMaster();
        if (repeatingEventMasterArray != null) {
            for (RepeatingEventMaster master: repeatingEventMasterArray) {
                allRepeatingEventMasters.add(master);
            }
        }

        allRepeatingEventChildren = new HashSet<>();
        RepeatingEventsChild[] repeatingEventsChildrenArray = dataAccessObject.getAllRepeatingEventsChild();
        if (repeatingEventsChildrenArray != null) {
            for (RepeatingEventsChild child: repeatingEventsChildrenArray) {
                allRepeatingEventChildren.add(child);
            }
        }

        allSubGoalMasters = new HashSet<>();
        SubGoalMaster[] subGoalMastersArray = dataAccessObject.getAllSubGoalMaster();
        if (subGoalMastersArray != null) {
            for (SubGoalMaster master: subGoalMastersArray) {
                allSubGoalMasters.add(master);
            }
        }
    }

    public void closeDatabase() {
        dataBase.close();
    }

}
