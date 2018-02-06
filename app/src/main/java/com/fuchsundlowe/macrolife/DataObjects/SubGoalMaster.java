package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Ignore;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
 * This is sub goal of Complex goal master.
 * TODO: Eventually will need the implememtation of location as a refference in system.
 */

public class SubGoalMaster extends DataMasterClass {
    // Instance variables:
    private int parentID; // Is reference to a ComplexGoalMaster, a must
    private int parentSubGoal; // Is reference to a another subGoal that lies up in chain or
    // even master goal... If returns nill then it is not subjetcted to any pattern
    @Ignore
    private Set<Integer> childrenGoalsIDs;

    // Constructor:
    public SubGoalMaster(int hashID, String taskName, Calendar taskStartTime,
                         Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                         boolean taskCompleted, SourceType taskOriginalSource,
                         StorageMaster storageMaster, int parentID,
                         int parentSubGoal) {

        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted,
                taskOriginalSource, storageMaster);
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

    // Finds parent if any and returns it as Object. Can be SubMaster or ComplexGoalMaster
    public Object getParentGoal() {
        if (getParentID() == getParentSubGoal()) {
            return getStorageMaster().getComplexGoalBy(this.getParentID());
        } else {
            return getStorageMaster().getSubGoalMasterBy(this.getParentSubGoal());
        }
    }

/* Question: Is this needed? How will I implemeent this functionality?
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
*/
    @Override
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }
    }
}
