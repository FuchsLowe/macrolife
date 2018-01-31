package com.fuchsundlowe.macrolife.DataObjects;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 */

public class ListMaster extends DataMasterClass {
    public ListMaster(String taskName, SourceType originalSourceOfTask,
                      Calendar originalCreationTime, int taskUniqueIdentifier) {


    }

    private ArrayList<Integer> subGoalsList;

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
