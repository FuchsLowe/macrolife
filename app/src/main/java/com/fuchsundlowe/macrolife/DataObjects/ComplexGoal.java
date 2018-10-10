package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
    This is a class that defines functionality of data holder for Complex goal. Holds subgoals
 and has accompaning calls and additional variables.
 *
 */

@Entity
public class ComplexGoal {

    // Variables list;
    @PrimaryKey(autoGenerate = true)
    protected int hashID;
    protected String taskName;
    @Ignore
    public Calendar deadline;
    private Calendar lastTimeModified;
    protected Calendar taskCreatedTimeStamp;
    private String purpose;

    public ComplexGoal(int hashID, String taskName, Calendar taskCreatedTimeStamp,
                       Calendar lastTimeModified,
                       String purpose) {

        this.hashID = hashID;
        this.taskName = taskName;
        this.taskCreatedTimeStamp =taskCreatedTimeStamp;
        this.purpose = purpose;
        this.lastTimeModified = lastTimeModified;
    } 


    // Specific getters and setters to the class;

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public String getPurpose() {
        return this.purpose;
    }

    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getHashID() {
        return hashID;
    }
    public void setHashID(int hashID) {
        this.hashID = hashID;
    }

    public Calendar getDeadline() {
        return deadline;
    }
    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public Calendar getLastTimeModified() {
        return lastTimeModified;
    }
    public void setLastTimeModified(Calendar lastTimeModified) {
        this.lastTimeModified = lastTimeModified;
    }

    public Calendar getTaskCreatedTimeStamp() {
        return taskCreatedTimeStamp;
    }
    public void setTaskCreatedTimeStamp(Calendar taskCreatedTimeStamp) {
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
    }
    // Checks the existance of deadline for this Goal
    public boolean hasDeadline() {
        return deadline != null && deadline.getTimeInMillis() > 1;
    }
}
