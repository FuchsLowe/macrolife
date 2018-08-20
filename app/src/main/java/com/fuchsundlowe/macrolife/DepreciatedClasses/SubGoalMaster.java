package com.fuchsundlowe.macrolife.DepreciatedClasses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DepreciatedClasses.Chevronable;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
 * This is sub goal of Complex goal master.
 * TODO: Eventually will need the implememtation of location as a refference in system.
 */
@Deprecated
@Entity(primaryKeys = {"hashID"})
public class SubGoalMaster extends Chevronable {

    @Ignore
    private Set<Integer> childrenGoalsIDs; // Of any usage?

    // Constructor:

    public SubGoalMaster(int hashID, String taskName, Calendar taskStartTime,
                         Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                         boolean taskCompleted, SourceType taskOriginalSource,
                         int parentSubGoal, int parentID, int mX, int mY) {
        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted, taskOriginalSource,
                parentSubGoal, parentID, mX, mY);
    }


    // Finds parent if any and returns it as Object. Can be SubMaster or ComplexGoal
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
