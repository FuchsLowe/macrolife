package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;

import java.util.Calendar;

/*
 * A dataClass that provides extra functionality for representing in graphical space data objects
 */
@Deprecated
public abstract class Chevronable extends DataMasterClass {

    private int parentID; // Is reference to a ComplexGoal, a must
    private int parentSubGoal; // Is reference to a another subGoal that lies up in chain or
    // even master goal... If returns nill then it is not subjetcted to any pattern

    private int mX; // These two are positional elements for Chevron View
    private int mY;

    public Chevronable(int hashID, String taskName, Calendar taskStartTime,
                       Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                       boolean taskCompleted, SourceType taskOriginalSource,
                       int parentSubGoal, int parentID, int mX, int mY) {

        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted, taskOriginalSource);

        this.parentID = parentID;
        this.parentSubGoal = parentSubGoal;
        this.mX = mX;
        this.mY = mY;
    }

    public int getParentID() {
        return parentID;
    }
    public void setParentID(int parentID) {
        this.parentID = parentID;
    }
    public int getParentSubGoal() {
        return parentSubGoal;
    }
    public void setParentSubGoal(int parentSubGoal) {
        this.parentSubGoal = parentSubGoal;
    }
    public int getMX() {
        return mX;
    }
    public void setMX(int mX) {
        this.mX = mX;
    }
    public int getMY() {
        return mY;
    }
    public void setMY(int mY) {
        this.mY = mY;
    }


}
