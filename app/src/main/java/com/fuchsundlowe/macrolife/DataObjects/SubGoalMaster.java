package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Ignore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
 * This is sub goal of Complex goal master.
 */

public class SubGoalMaster extends DataMasterClass {
    // Instance variables:
    private int parentID;
    private int parentSubGoal;
    @Ignore
    private Set<Integer> childrenGoalsIDs;

    // Constructor:
    // For this constrcuot to work I need to define how will all this be initiated?

    // Methods:
    public int getParentID() {
        return this.parentID;
    }
    public int getParentSubGoal() {return this.parentSubGoal;}
    public void setParentSubGoal(int newParent){
        parentSubGoal = newParent;
    }
    public Set<Integer> getChildrenGoalsIDs() {
        // TODO: Needs implementation for database retrival
        return this.childrenGoalsIDs;
    }
    public void addChildGoal(int child){
        // TODO: Needs implementation for database
        childrenGoalsIDs.add(child);
    }
    public void removeChild(int child){
        // TODO: Needs implementation for database
        childrenGoalsIDs.remove(child);
    }
}
