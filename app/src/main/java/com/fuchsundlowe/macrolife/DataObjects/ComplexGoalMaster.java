package com.fuchsundlowe.macrolife.DataObjects;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;


import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
    This is a class that defines functionality of data holder for Complex goal. Holds subgoals
 and has accompaning calls and additional variables.
 *
 */
public class ComplexGoalMaster extends DataMasterClass {

    // Variables:
    private String purpose;

    public ComplexGoalMaster(int hashID, String taskName, Calendar taskStartTime,
                             Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                             boolean taskCompleted, SourceType taskOriginalSource,
                             StorageMaster storageMaster, String purpose) {

        super(hashID, taskName, taskStartTime,
                taskEndTime, taskCreatedTimeStamp,
                taskCompleted, taskOriginalSource,
                storageMaster);

        this.purpose = purpose;


    }
    /*
     *TODO: How will I store color?
     * First define what type of colors do I support,
     * then implement the enum that will cover that...
    */

    // Returns all the subgoals associated with this master:
    public Set<SubGoalMaster> getAllSubGoals() {
        return this.getStorageMaster().getSubGoalsOfMaster(this.getHashID());
    }

    // Specific getters and setters to the class;

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public String getPurpose() {
        return this.purpose;
    }

    // Methods:

    @Override
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }
    }
}
