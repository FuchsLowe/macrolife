package com.fuchsundlowe.macrolife.ListView;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;

import java.util.List;

/*
 * Class that manages database calls, filters data and creates LiveData Objects to be used for
 * subsequent distributions to UI Controllers that will use them.
 *
 * Custom class is used instead of ViewModel because this class needs to observe live objects from
 * DB while ViewModel implementations explicitly forbid this.
 */
protected class ListDataController {

    private Context mContext;
    private DataProviderNewProtocol dataMaster;

    private LiveData<List<TaskObject>> allTaskObjects;
    private Observer<List<TaskObject>> observerForTasks;

    private LiveData<List<RepeatingEvent>> allRepeatingEvents;
    private Observer<List<RepeatingEvent>> observerForEvents;

    private LiveData<List<ComplexGoal>> allComplexGoals;
    private Observer<List<ComplexGoal>> observerForGoals;

    private MutableLiveData<List<>> completed;
    private MutableLiveData<List<>> current;

    protected ListDataController(Context context) {
        // Initialization phase:
        this.mContext = context;
        dataMaster = LocalStorage.getInstance(mContext);

        // Defining the Observers:
        defineObservers();
        // Binding observers to LiveDataObjects
        allTaskObjects = dataMaster.getAllTaskObjects();
        allTaskObjects.observeForever(observerForTasks);

        allRepeatingEvents = dataMaster.getAllRepeatingEvents();
        allRepeatingEvents.observeForever(observerForEvents);

        allComplexGoals = dataMaster.getAllComplexGoals();
        allComplexGoals.observeForever(observerForGoals);

    }
    private void defineObservers() {
        // TODO Implement, the meat of class
        // Tasks:

        // Events:

        // Goals:
    }
    // This method is called from ListView onDestroy class to delete any LiveData observeForever calls
    protected void destroy() { // Todo Connect to onDestroy call
        if (allTaskObjects != null && observerForTasks != null) {
            allTaskObjects.removeObserver(observerForTasks);
        }
        if (allRepeatingEvents != null && observerForEvents != null) {
            allRepeatingEvents.removeObserver(observerForEvents);
        }
        if (allComplexGoals != null && observerForGoals != null) {
            allComplexGoals.removeObserver(observerForGoals);
        }
    }

    protected

}
