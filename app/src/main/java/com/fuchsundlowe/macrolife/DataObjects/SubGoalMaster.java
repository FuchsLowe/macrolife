package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
 * This is sub goal of Complex goal master.
 * TODO: Eventually will need the implememtation of location as a refference in system.
 */
@Entity(primaryKeys = {"hashID"})
public class SubGoalMaster extends DataMasterClass {
    // Instance variables:
    private int parentID; // Is reference to a ComplexGoalMaster, a must
    private int parentSubGoal; // Is reference to a another subGoal that lies up in chain or
    // even master goal... If returns nill then it is not subjetcted to any pattern

    private int mX; // These two are positional elements for Chevron View
    private int mY;


    @Ignore
    private Set<Integer> childrenGoalsIDs; // Of any usage?

    // Constructor:
    public SubGoalMaster(int hashID, String taskName, Calendar taskStartTime,
                         Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                         boolean taskCompleted, SourceType taskOriginalSource, int parentID,
                         int parentSubGoal, Integer mX, Integer mY) {

        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted,
                taskOriginalSource);
        this.mX = mX;
        this.mY = mY;
        this.parentID=parentID;
        this.parentSubGoal=parentSubGoal;
    }

    // Methods:
    public int getParentID() {
        return this.parentID;
    }
    public int getParentSubGoal() {
        return this.parentSubGoal;
    }
    public void setParentSubGoal(int newParent){
        parentSubGoal = newParent;
    }

    public int getMX() {
        return this.mX;
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



    // Finds parent if any and returns it as Object. Can be SubMaster or ComplexGoalMaster
    public Object getParentGoal() {
        if (getParentID() == getParentSubGoal()) {
            return getStorageMaster().getComplexGoalBy(this.getParentID());
        } else {
            return getStorageMaster().getSubGoalMasterBy(this.getParentSubGoal());
        }
    }

    @Override
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }
    }

    @Override
    public void deleteMe() {
        this.getStorageMaster().deleteObject(this);
    }


}
