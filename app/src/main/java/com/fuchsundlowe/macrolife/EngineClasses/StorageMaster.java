package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;

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
@Deprecated
public class StorageMaster implements DataProviderProtocol {
    // Database Class:
    private DAO dataAccessObject;
    private static StorageMaster self = null;
    // Public constructors...
    public static StorageMaster getInstance(Context context) {
        if (self == null) {
            self = new StorageMaster(context);
        }
        return self;
    }
    public static StorageMaster optionalStorageMaster() {
        return self;
    }

    // ComplexGoal
    private LiveData<List<ComplexGoal>> allComplexGoals;
    private LiveData<List<ComplexGoal>> getComplexGoals() {
        return allComplexGoals;
    }
    @Override
    public void subscribeObserver_ComplexGoal(LifecycleOwner lifecycleOwner,
                                              Observer<List<ComplexGoal>> observer){
        getComplexGoals().observe(lifecycleOwner, observer);
    }
    @Override
    public ComplexGoal getComplexGoalBy(int masterGoalID) {
        for (ComplexGoal goalObject: getAllComplexGoals().getValue() ) {
            if (goalObject.getHashID() == masterGoalID) {
                return goalObject;
            }
        }
        return null;
    }
    public LiveData<List<ComplexGoal>> getAllComplexGoals() {
        return allComplexGoals;
    }
    public void insertObject(final ComplexGoal object){

        Thread tempAlocator = new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        });

        tempAlocator.start();

    }
    public void updateObject(ComplexGoal object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(final ComplexGoal object) {
        for (SubGoalMaster subGoal: this.getSubGoalsOfMaster(object.getHashID())) {
            this.deleteObject(subGoal);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object);

            }
        }).start();
    }
    @Override
    public Set<ComplexGoal> getComplexGoalsByDay(Calendar day) {
        Set<ComplexGoal> tempSet = new HashSet<>();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        for (ComplexGoal goal: getComplexGoals().getValue()) {
            if (checkIfBelongsTimeWise(goal.getTaskStartTime(), goal.getTaskEndTime(), day) ) {
                tempSet.add(goal);
            }

        }

        return tempSet;
    }
    public boolean amIStored(ComplexGoal object) {
        int myID = object.getHashID();
        for (ComplexGoal value: getComplexGoals().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }


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
    @Override
    public LiveData<ListMaster> getListMasterByID(int masterID) {
        return dataAccessObject.getListMasterByID(masterID);
    }
    public void updateObject(ListMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(final ListMaster object) {
        for (ListObject subGoal: getListObjectsOfParent(object.getHashID())) {
            this.deleteObject(subGoal);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object);

            }
        }).start();
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
    public void deleteObject(final ListObject object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object); // Removes it from permanent storage
            }
        }).start();
    }
    @Override
    public void subscribeObserver_ListObject(LifecycleOwner lifecycleOwner, Observer<List<ListObject>> observer) {
        getListObjects().observe(lifecycleOwner, observer);
    }
    @Override
    public LiveData<List<ListObject>> getListObjectsByParent(int parentHashID) {
        return dataAccessObject.getListObjectsByParent(parentHashID);
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
    public void deleteObject(final OrdinaryEventMaster object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object); // Removes it from permanent storage
            }
        }).start();
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
    @Override
    public LiveData<List<SubGoalMaster>> findAllChildren(int ofMaster) {
        return dataAccessObject.findAllChildren(ofMaster);
    }

    @Override
    public void closeDatabase() {

    }

    @Override
    public LiveData<List<PopUpData>> loadPopUpValues() {
        return null;
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
    public Set<RepeatingEventMaster>getSubordinateRepearingStaticMasters(int forMasterID) {
        Set<RepeatingEventMaster> tempHolder = new HashSet<>();
        for (RepeatingEventMaster object: getAllRepeatingEventMasters().getValue()) {
            if (object.getParentID() == forMasterID) {tempHolder.add(object);}
        }
        return tempHolder;
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
    @Override
    public LiveData<List<RepeatingEventMaster>> getAllSubordinateRepeatingEventMasters(int forMasterID) {
        return dataAccessObject.getAllRepeatingMasters(forMasterID);
    }
    public LiveData<List<RepeatingEventMaster>> getAllRepeatingEventMasters() {
        return allRepeatingEventMasters;
    }
    public void updateObject(RepeatingEventMaster object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(final RepeatingEventMaster object) {
        for (RepeatingEvent subTask: getAllRepeatingChildrenByParent(object.getHashID())) {
            deleteObject(subTask);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object); // Removes it from permanent storage
            }
        }).start();
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
    private LiveData<List<RepeatingEvent>> allRepeatingEventChildren;
    public LiveData<List<RepeatingEvent>> getAllRepeatingEventChildren() {
        return allRepeatingEventChildren;
    }
    public void insertObject(final RepeatingEvent object){
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.insertTask(object);
            }
        }).start();

    }
    @Override
    public void subscribeObserver_RepeatingCgild(LifecycleOwner lifecycleOwner, Observer<List<RepeatingEvent>> observer) {

    }
    public LiveData<List<RepeatingEvent>> getAllRepeatingEventChild() {
        return allRepeatingEventChildren;
    }
    public void updateObject(RepeatingEvent object) {
        dataAccessObject.updateTask(object);
    }
    public void deleteObject(final RepeatingEvent object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object); // Removes it from permanent storage
            }
        }).start();
    }
    public boolean amIStored(RepeatingEvent object) {
        int myID = object.getHashID();
        for (RepeatingEvent value: getAllRepeatingEventChildren().getValue()) {
            if (value.getHashID() == myID) { return true; }
        }
        return false;
    }
    public Set<RepeatingEvent> getAllRepeatingChildrenByParent(int parentId) {
        Set<RepeatingEvent> hashSet = new HashSet<RepeatingEvent>();
        for (RepeatingEvent child: getAllRepeatingEventChildren().getValue()) {
            if (child.getParentID() == parentId) {
                hashSet.add(child);
            }
        }
        return hashSet;
    }


    // SubGoalMaster
    private List<SubGoalMaster> allSubGoalMasters;
    public LiveData<List<SubGoalMaster>> getAllSubGoalMasters() {
        return dataAccessObject.getAllSubGoalMaster();
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
    public void updateObject(final SubGoalMaster object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.updateTask(object);
            }
        }).start();
    }
    public void deleteObject(final SubGoalMaster object) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataAccessObject.deleteTask(object); // Removes it from permanent storage
            }
        }).start();

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
        this.dataBase = Room.databaseBuilder(appContext, DataProvider.class, "StandardDB").build();
        this.dataAccessObject = dataBase.daoObject();
        initializeLiveData();
    }
    // General purpose methods:
    // TODO: Check if this and other ones do really check for consistency?
    public boolean checkIfIDisAssigned(int idToCheck) {
        List<ComplexGoal> complexGoalSet =  getComplexGoals().getValue();
        if (complexGoalSet != null) {
            for (ComplexGoal value : complexGoalSet) {
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

        List<RepeatingEvent> repeatingEventSet = getAllRepeatingEventChildren().getValue();
        if (repeatingEventSet != null) {
            for (RepeatingEvent value : repeatingEventSet) {
                if (value.getHashID() == idToCheck) {
                    return true;
                }
            }
        }

        List<SubGoalMaster> subGoalMasterSet = allSubGoalMasters;
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
        ComplexGoal[] complexGoalMastersArray = dataAccessObject.getAllComplexGoalMasters().getValue();
        if (complexGoalMastersArray != null) {
            for(ComplexGoal master: complexGoalMastersArray) {
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
        RepeatingEvent[] repeatingEventsChildrenArray = dataAccessObject.getAllRepeatingEventsChild();
        if (repeatingEventsChildrenArray != null) {
            for (RepeatingEvent child: repeatingEventsChildrenArray) {
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
        // SubGoalMaster observer forever
        dataAccessObject.getAllSubGoalMaster().observeForever(new Observer<List<SubGoalMaster>>() {
            @Override
            public void onChanged(@Nullable List<SubGoalMaster> subGoalMasters) {
                allSubGoalMasters = subGoalMasters;
            }
        });
        //allSubGoalMasters = dataAccessObject.getAllSubGoalMaster();
        allRepeatingEventMasters = dataAccessObject.getAllRepeatingEventMaster();
    }

}
