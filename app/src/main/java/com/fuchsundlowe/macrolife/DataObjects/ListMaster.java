package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Ignore;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * This class holds list objects and is used to create views that dispaly list like goals.
 * Examples would be grocelry list etc.
 */

public class ListMaster extends DataMasterClass {
    // Instance Variables:
    @Ignore
    private ArrayList<Integer> subGoalsList;

    // TODO: Needs complete implementation, needs better constructor
    public ListMaster(String taskName, SourceType originalSourceOfTask,
                      Calendar originalCreationTime, int taskUniqueIdentifier) {


    }

    public ArrayList<Integer> getAllSubGoalsIDs() {
        return this.subGoalsList;
    }
    public void addNewGoal(int newGoalId) {
        this.subGoalsList.add(newGoalId);
    }
    public void setSubGoalsList( ArrayList<Integer> newList) {
        this.subGoalsList = newList;
    }
}
