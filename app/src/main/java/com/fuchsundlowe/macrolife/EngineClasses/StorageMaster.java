package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;
import com.fuchsundlowe.macrolife.DataObjects.*;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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
    private LiveData<List<ComplexGoalMaster>> allComplexGoals;

    private LiveData<List<ComplexGoalMaster>> getComplexGoals() {
        return allComplexGoals;
    }
    @Override
    public void subscribeObserver_ComplexGoal(LifecycleOwner lifecycleOwner,
                                              Observer<List<ComplexGoalMaster>> observer){
        getComplexGoals().observe(lifecycleOwner, observer);
    }


    public LiveData<List<ComplexGoalMaster>> getAllComplexGoals() {
        return allComplexGoals;
    }


    public ComplexGoalMaster getComplexGoalBy(int id) {
        for (ComplexGoalMaster goalMaster: getComplexGoals().getValue()) {
            if (goalMaster.getHashID() == id) {
                return goalMaster;
            }
        }
        return null;
    }

    public void insertObject(final ComplexGoalMaster object){

        Thread tempAlocator = new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        });

        tempAlocator.start();

    }



    public void updateObject(ComplexGoalMaster object) {
        dataAccessObject.updateTask(object);
    }

    public void deleteObject(ComplexGoalMaster object) {
        for (SubGoalMaster subGoal: this.getSubGoalsOfMaster(object.getHashID())) {
            this.deleteObject(subGoal);
        }

        dataAccessObject.deleteTask(object);
    }

    @Override
    public Set<ComplexGoalMaster> getComplexGoalsByDay(Calendar day) {
        Set<ComplexGoalMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (ComplexGoalMaster goal: getComplexGoals().getValue()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }

    public boolean amIStored(ComplexGoalMaster object) {
        int myID = object.getHashID();
        for (ComplexGoalMaster value: getComplexGoals().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

//================================================================================
    // ListMaster
    private LiveData<List<ListMaster>> allListMasters;
    public LiveData<List<ListMaster>> getAllListMasters() {
        return this.allListMasters;
    }

    @Override
    public void updateListObject(ListObject object) {
        dataAccessObject.updateTask(object);
    }

    public void insertObject(final ListMaster object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();

    }

    @Override
    public void subscribeObserver_ListMaster(LifecycleOwner lifecycleOwner, Observer<List<ListMaster>> observer){
        getAllListMasters().observe(lifecycleOwner, observer);
    }


    public void updateObject(ListMaster object) {
        dataAccessObject.updateTask(object);
    }

    public void deleteObject(ListMaster object) {
        for (ListObject subGoal: getListObjectsOfParent(object.getHashID())) {
            this.deleteObject(subGoal);
        }

        dataAccessObject.deleteTask(object);
    }

    @Override
    public Set<ListMaster> getAllListMastersByDay(Calendar day) {
        Set<ListMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (ListMaster goal: getAllListMasters().getValue()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }

    public boolean amIStored(ListMaster object) {
        int myID = object.getHashID();
        for (ListMaster value: getAllListMasters().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }


    // ListObject
    private LiveData<List<ListObject>> allListObjects;
    public LiveData<List<ListObject>> getListObjects() {
        return allListObjects;
    }

    public void insertObject(final ListObject object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();

    }

    public void updateObject(ListObject object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(ListObject object) {
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public void subscribeObserver_ListObject(LifecycleOwner lifecycleOwner, Observer<List<ListObject>> observer) {
        getListObjects().observe(lifecycleOwner, observer);
    }


    public LiveData<List<ListObject>> getAllListObjects() {
        return allListObjects;
    }

    public boolean amIStored(ListObject object) {
        int myID = object.getHashID();
        for (ListObject value: getListObjects().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    public Set<ListObject> getListObjectsOfParent(int parentID) {
        Set<ListObject> listObjects = new HashSet<ListObject>();
        for (ListObject list: getListObjects().getValue()) {
            if (list.getMasterID() == parentID) {
                listObjects.add(list);
            }
        }
        return listObjects;
    }


    // OrdinaryEventMaster
    private LiveData<List<OrdinaryEventMaster>> allOrdinaryEventMasters;
    public LiveData<List<OrdinaryEventMaster>> getAllOrdinaryEventMasters() {
        return allOrdinaryEventMasters;
    }

    public void insertObject(final OrdinaryEventMaster object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();


    }

    @Override
    public void subscribeObserver_OrdinaryEvent(LifecycleOwner lifecycleOwner, Observer<List<OrdinaryEventMaster>> observer) {
        getAllOrdinaryEventMasters().observe(lifecycleOwner, observer);
    }


    public LiveData<List<OrdinaryEventMaster>> getAllOrdinaryEvents() {
        return allOrdinaryEventMasters;
    }

    public void updateObject(OrdinaryEventMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(OrdinaryEventMaster object) {
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public Set<OrdinaryEventMaster> getAllOrdinaryTasksByDay(Calendar day) {
        Set<OrdinaryEventMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (OrdinaryEventMaster goal: getAllOrdinaryEventMasters().getValue()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }

    public boolean amIStored(OrdinaryEventMaster object) {
        int myID = object.getHashID();
        for (OrdinaryEventMaster value: getAllOrdinaryEventMasters().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    // RepeatingEventMaster
    private LiveData<List<RepeatingEventMaster>> allRepeatingEventMasters;
    public LiveData<List<RepeatingEventMaster>> getAllRepeatingEventMasterss() {
        return allRepeatingEventMasters;
    }

    public void insertObject(final RepeatingEventMaster object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();

    }

    @Override
    public void subscribeObserver_RepeatingMaster(LifecycleOwner lifecycleOwner, Observer<List<RepeatingEventMaster>> observer) {
        getAllRepeatingEventMasterss().observe(lifecycleOwner, observer);
    }


    public LiveData<List<RepeatingEventMaster>> getAllRepeatingEventMasters() {
        return allRepeatingEventMasters;
    }

    public void updateObject(RepeatingEventMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(RepeatingEventMaster object) {
        for (RepeatingEventsChild subTask: getAllRepeatingChildrenByParent(object.getHashID())) {
            deleteObject(subTask);
        }

        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public Set<RepeatingEventMaster> getAllRepeatingEventMastersByDay(Calendar day) {
        Set<RepeatingEventMaster> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (RepeatingEventMaster goal: getAllRepeatingEventMasterss().getValue()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }

    public boolean amIStored(RepeatingEventMaster object) {
        int myID = object.getHashID();
        for (RepeatingEventMaster value: getAllRepeatingEventMasterss().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    public RepeatingEventMaster getMasterByInt(int masterID) {
        for (RepeatingEventMaster master: this.getAllRepeatingEventMasterss().getValue()) {
            if (master.getHashID() == masterID) {
                return master;

            }
        }
        return null;
    }
    // RepeatingEventChild
    private LiveData<List<RepeatingEventsChild>> allRepeatingEventChildren;
    public LiveData<List<RepeatingEventsChild>> getAllRepeatingEventChildren() {
        return allRepeatingEventChildren;
    }

    public void insertObject(final RepeatingEventsChild object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();

    }

    @Override
    public void subscribeObserver_RepeatingCgild(LifecycleOwner lifecycleOwner, Observer<List<RepeatingEventsChild>> observer) {

    }


    public LiveData<List<RepeatingEventsChild>> getAllRepeatingEventChild() {
        return allRepeatingEventChildren;
    }

    public void updateObject(RepeatingEventsChild object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(RepeatingEventsChild object) {
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }



    public boolean amIStored(RepeatingEventsChild object) {
        int myID = object.getHashID();
        for (RepeatingEventsChild value: getAllRepeatingEventChildren().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }


    public Set<RepeatingEventsChild> getAllRepeatingChildrenByParent(int parentId) {
        Set<RepeatingEventsChild> hashSet = new HashSet<RepeatingEventsChild>();
        for (RepeatingEventsChild child: getAllRepeatingEventChildren().getValue()) {
            if (child.getParentID() == parentId) {
                hashSet.add(child);
            }
        }
        return hashSet;
    }
    // SubGoalMaster
    private LiveData<List<SubGoalMaster>> allSubGoalMasters;
    public LiveData<List<SubGoalMaster>> getAllSubGoalMasters() {
        return allSubGoalMasters;
    }

    public void insertObject(final SubGoalMaster object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();

    }

    @Override
    public void subscribeObserver_SubGoal(LifecycleOwner lifecycleOwner, Observer<List<SubGoalMaster>> observer) {

    }

    public void updateObject(SubGoalMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(SubGoalMaster object) {
        dataAccessObject.deleteTask(object); // Removes it from permanent storage
    }

    @Override
    public Set<SubGoalMaster> getAllSubGoalsByMasterId(int masterID) {
        Set<SubGoalMaster> temp = new HashSet<>();
        for (SubGoalMaster subGoal: getAllSubGoalMasters().getValue()) {
            if (subGoal.getParentID() == masterID) {
                temp.add(subGoal);
            }
        }
        return temp;
    }

    public boolean amIStored(SubGoalMaster object) {
        int myID = object.getHashID();
        for (SubGoalMaster value: getAllSubGoalMasters().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }

    public Set<Integer> getIDsOfSubGoalfMaster(int withMasterID) {
        HashSet<Integer> hashSet = new HashSet<>();
        for (SubGoalMaster subGoal: getAllSubGoalMasters().getValue()) {
            if (subGoal.getHashID() == withMasterID) {
                hashSet.add(subGoal.getHashID());
            }
        }
        return hashSet;
    }
    // Used to retrive the set of subgoals associated with master ID
    public Set<SubGoalMaster> getSubGoalsOfMaster(int withMasterID) {
        HashSet<SubGoalMaster> hashSet = new HashSet<>();
        for (SubGoalMaster goal: getAllSubGoalMasters().getValue()) {
            if (goal.getParentID() == withMasterID) {
                hashSet.add(goal);
            }
        }
        return hashSet;
    }
    // Used to retrive a single instance SubGoalMaster via ID
    public SubGoalMaster getSubGoalMasterBy(int ID) {
        for (SubGoalMaster goal: getAllSubGoalMasters().getValue()) {
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

        initializeLiveData();
    }




    // General purpose methods:

    public boolean checkIfIDisAssigned(int idToCheck) {
        List<ComplexGoalMaster> complexGoalMasterSet =  getComplexGoals().getValue();
        if (complexGoalMasterSet != null) {
            for (ComplexGoalMaster value : complexGoalMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<ListMaster> listMasterSet = getAllListMasters().getValue();
        if (listMasterSet != null) {
            for (ListMaster value : listMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<ListObject> listObjectSet = getListObjects().getValue();
        if (listObjectSet != null) {
            for (ListObject value : listObjectSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<OrdinaryEventMaster> ordinaryEventMastersSet = getAllOrdinaryEventMasters().getValue();
        if (ordinaryEventMastersSet != null) {
            for (OrdinaryEventMaster value : ordinaryEventMastersSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<RepeatingEventMaster> repeatingEventMasterSet = getAllRepeatingEventMasterss().getValue();
        if (repeatingEventMasterSet != null) {
            for (RepeatingEventMaster value : repeatingEventMasterSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<RepeatingEventsChild> repeatingEventsChildSet = getAllRepeatingEventChildren().getValue();
        if (repeatingEventsChildSet != null) {
            for (RepeatingEventsChild value : repeatingEventsChildSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<SubGoalMaster> subGoalMasterSet = getAllSubGoalMasters().getValue();
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

    // A legacy call that is used instead of the LiveData Objects.
    @Deprecated
    private void initiateAllValues() {
        /*
        allComplexGoals = new HashSet<>();
        ComplexGoalMaster[] complexGoalMastersArray = dataAccessObject.getAllComplexGoalMasters().getValue();
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
        */
    }

    // TODO: When do we close database?
    public void closeDatabase() {
        dataBase.close();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeDatabase();
    }

    private void initializeLiveData() {
        allComplexGoals = dataAccessObject.getAllComplexGoalMasters();
        allListMasters = dataAccessObject.getAllListMasters();
        allListObjects = dataAccessObject.getAllListObject();
        allOrdinaryEventMasters = dataAccessObject.getAllOrdinaryEventMasters();
        allRepeatingEventChildren = dataAccessObject.getAllRepeatingEventsChild();
        allSubGoalMasters = dataAccessObject.getAllSubGoalMaster();
        allRepeatingEventMasters = dataAccessObject.getAllRepeatingEventMaster();
    }

}
