package com.fuchsundlowe.macrolife.DataObjects;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 */

public class SubGoalMaster extends DataMasterClass {
    public SubGoalMaster(String taskName, SourceType originalSourceOfTask,
                         Calendar originalCreationTime, int taskUniqueIdentifier,
                         int masterGoalID, ArrayList<Integer> childrenGoals) {
        this.mainComplexGoal = masterGoalID;
        this.appendChildrenGoals(childrenGoals);
    }


    // An unique id of master goal
    private int mainComplexGoal;
    // An unique id of next in line goal
    private int parentGoal;
    // An unique set of id's for subgoals if any
    private ArrayList<Integer> childrenGoals;
    // TODO: private color; unclear how I will implement color selection

    public int getMasterGoalid() {
        return this.mainComplexGoal;
    }
    public void setMasterGoalID(int masterGoalID) {
        this.mainComplexGoal = masterGoalID;
    }

    public int getParentGoalID() {
        return this.parentGoal;
    }

    public void setParentGoal(int newParentGoalID) {
        this.parentGoal = newParentGoalID;
    }
    // TODO - To test this
    public void appendChildrenGoals(ArrayList<Integer> children) {
        if (this.childrenGoals != null) {
            this.childrenGoals.addAll(children);
        } else {
            this.childrenGoals = children;
        }
    }


}
