package com.fuchsundlowe.macrolife.DataObjects;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * This is sub goal of Complex goal master.
 */

public class SubGoalMaster extends DataMasterClass {
    // Instance variables:
    private int parentID;
    private int parentSubGoal;
    private ArrayList<Integer> childrenGoalsIDs;
    // TODO: private color; unclear how I will implement color selection

    // Methods:
    public int getParentID() {
        return this.parentID;
    }
    public int getParentSubGoal() {return this.parentSubGoal;}
    public ArrayList<Integer> getChildrenGoalsIDs() {
        // TODO: Needs implementation for database retrival
        return this.childrenGoalsIDs;
    }
}
