package com.fuchsundlowe.macrolife.DataObjects;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

/**
 This class is the superclass of most data objects used in this software. It provides common
 values and their respective getters and setters.
 TODO: The calendar class is useless here. I need to accept integer and then I need to create

 */

public abstract class DataMasterClass {

    // Variables list;
    protected int hashID;
    protected String taskName;
    protected Calendar taskStartTime;
    protected Calendar taskEndTime;
    protected Calendar taskCreatedTimeStamp;
    protected boolean taskCompleted;
    @Ignore
    protected SourceType taskOriginalSource ; // Should this be made available? In what way?

    // public constructor - none


    // public getters and setters for variables and support methods
    public int getHashID() { return this.hashID;}
    public void setHashID(int hashID) {this.hashID = hashID;}

    public String getTaskName(){
        return this.taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Calendar getTaskStartTime() {
        return this.taskStartTime;
    }
    public void setTaskStartTime(Calendar taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    // Returns true if end time is after the beginning, and false if end time is before start time.
    public boolean setTaskEndTime(Calendar taskEndTime) {
        Calendar startTime = this.getTaskStartTime();
        if (taskEndTime.after(startTime)) {
            // TODO: Implement this, needs to format to date and time and convert to calendar, check comapt
            this.taskEndTime = taskEndTime;
            return true;
        } else {
            return false;
        }
    }
    public Calendar getTaskEndTime() {
        return this.taskEndTime;
    }

    public Calendar getTaskCreatedTimeStamp() {
        return this.taskCreatedTimeStamp;
    }
    public void setTaskCreatedTimeStamp(Calendar taskCreatedTimeStamp) {
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
    }

    public void setTaskCompleted(boolean taskCompleted) {
        this.taskCompleted = taskCompleted;
    }
    public boolean isTaskCompleted() {
        return this.taskCompleted;
    }
    public SourceType getTaskOriginalSource() {
        return this.taskOriginalSource;
    }


    // Testing of the Enum class for Source

    public enum  SourceType {
        local, googleCalendar, yahooCalendar, other;
    }
}

