package com.fuchsundlowe.macrolife.DataObjects;


import java.util.Calendar;

/**
 This class is the superclass of all data objects used in this software. It provides common
 values and their respective getters and setters.
 */

public abstract class DataMasterClass {

    // Variables list;
    private String taskName;
    private Calendar taskStartTime;
    private boolean isTaskCompleted;
    private final int hashID;
    private Calendar taskCreatedTimeStamp;
    private Calendar taskEndTime;
    private final SourceType taskOriginalSource ; // Should this be made available? In what way?

    // private byte picture; How Should I store picture id to the class? Should It be array?

    // public constructor
    public DataMasterClass(String taskName,
                                      SourceType originalSourceOfTask,
                                      Calendar originalCreationTime,
                                      int taskUniqueIdentifier) {

        this.taskName = taskName;
        this.taskOriginalSource = originalSourceOfTask;
        this.taskCreatedTimeStamp = originalCreationTime;
        this.hashID = taskUniqueIdentifier;

    }

    // public getters and setters for variables and support methods

    public String getTaskName(){
        return this.taskName;
    }
    public void changeTaskName(String newTaskName) {
        this.taskName = newTaskName;
    }

    public Calendar getTaskStartTime() {
        return this.taskStartTime;
    }
    public void setNewTaskStartTime(Calendar newTaskStartTime) {
        this.taskStartTime = newTaskStartTime;
    }
    // Returns true if end time is after the beginning, and false if end time is before start time.
    public boolean setNewTaskEndTime(Calendar endTime) {
        if (this.taskStartTime.after(endTime)) {
            return false;
        }
        this.taskEndTime = endTime;
        return true;
    }

    public Calendar getTaskEndTimeAsCalendar() {
        return this.taskEndTime;
    }

    /* Returns either 0 if there is no end Task time,
    * -1 if there is error and positive if value is possible
    */
    public int getTaskDurationInSeconds() {
       try {
           int timeValue = this.getTaskEndTimeAsCalendar().compareTo(this.getTaskStartTime());

           return timeValue;

       } catch (NullPointerException error) {
        return -1;
       }
    }

    public boolean isTaskCompleted() {
        return this.isTaskCompleted;
    }

    public void setTaskCompletionState(boolean isTaskCompleted) {
        this.isTaskCompleted = isTaskCompleted;
    }

    public SourceType getTaskOriginalSource() {
        return this.taskOriginalSource;
    }

    public Calendar getOriginalCreationTimeOfTask() {
        return this.taskCreatedTimeStamp;
    }

    public int taskUniqueIdentifier() {
        return this.hashID;
    }

    // Testing of the Enum class for Source

    public enum SourceType {
        local, googleCalendar, yahooCalendar, other;
    }
}

