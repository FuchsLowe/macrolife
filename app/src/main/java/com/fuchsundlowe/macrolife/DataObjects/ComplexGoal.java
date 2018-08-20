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
    @PrimaryKey
    protected int hashID;
    protected String taskName;
    @Ignore
    public Calendar taskEndTime; //Todo: Do I need This
    private Calendar lastTimeModified;
    protected Calendar taskCreatedTimeStamp;
    protected boolean isTaskCompleted;
    private SourceType taskOriginalSource ;
    private String purpose;

    public ComplexGoal(int hashID, String taskName, Calendar taskCreatedTimeStamp,
                       Calendar lastTimeModified,
                       boolean isTaskCompleted, SourceType taskOriginalSource,
                       String purpose) {


        this.hashID = hashID;
        this.taskName = taskName;
        this.taskCreatedTimeStamp =taskCreatedTimeStamp;
        this.isTaskCompleted = isTaskCompleted;
        this.taskOriginalSource = taskOriginalSource;
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

    public Calendar getTaskEndTime() {
        return taskEndTime;
    }
    public void setTaskEndTime(Calendar taskEndTime) {
        this.taskEndTime = taskEndTime;
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

    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }
    public void setTaskCompleted(boolean taskCompleted) {
        isTaskCompleted = taskCompleted;
    }

    public SourceType getTaskOriginalSource() {
        return taskOriginalSource;
    }
    public void setTaskOriginalSource(SourceType taskOriginalSource) {
        this.taskOriginalSource = taskOriginalSource;
    }
}
