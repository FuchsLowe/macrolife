package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import java.util.Calendar;

// A fundamental Data Type for this App Local storage
@Entity(foreignKeys = @ForeignKey(entity = ComplexGoal.class, parentColumns = "hashID",
        childColumns = "parentGoal", onDelete = ForeignKey.SET_NULL, onUpdate = ForeignKey.CASCADE))
public class TaskObject {
    @PrimaryKey(autoGenerate = true)
    private int hashID;
    private int parentGoal; // if this goal is a part of ComplexGoal, this would be ID of that master
    private int subGoalMaster; // reference to a optional next in hierarchy goal
    private String taskName;
    private Calendar taskCreatedTimeStamp;
    private Calendar taskStartTime;
    private Calendar taskEndTime;
    private Calendar lastTimeModified;
    private CheckableStatus isTaskCompleted;
    private String note;
    private boolean isList;
    private boolean isRecurring;
    private int mX, mY; // for location on the screen

    // Constructor:
    public TaskObject(int hashID, int parentGoal, int subGoalMaster, String taskName,
                      Calendar taskCreatedTimeStamp, Calendar taskStartTime, Calendar taskEndTime,
                      Calendar lastTimeModified, CheckableStatus isTaskCompleted, String note,
                      boolean isList, boolean isRecurring, int mX, int mY) {

        this.hashID = hashID;
        this.parentGoal = parentGoal;
        this.subGoalMaster = subGoalMaster;
        this.taskName = taskName;
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
        this.taskStartTime = taskStartTime;
        this.taskEndTime = taskEndTime;
        this.lastTimeModified = lastTimeModified;
        this.isTaskCompleted = isTaskCompleted;
        this.note = note;
        this.isList = isList;
        this.isRecurring = isRecurring;
        this.mX = mX;
        this.mY = mY;
    }

    // Generic Getters and Setters:
    public int getHashID() {
        return hashID;
    }
    public void setHashID(int hashID) {
        this.hashID = hashID;
    }

    public int getParentGoal() {
        return parentGoal;
    }
    public void setParentGoal(int parentGoal) {
        this.parentGoal = parentGoal;
    }

    public int getSubGoalMaster() {
        return subGoalMaster;
    }
    public void setSubGoalMaster(int subGoalMaster) {
        this.subGoalMaster = subGoalMaster;
    }

    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Calendar getTaskCreatedTimeStamp() {
        return taskCreatedTimeStamp;
    }
    public void setTaskCreatedTimeStamp(Calendar taskCreatedTimeStamp) {
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
    }

    public Calendar getTaskStartTime() {
        return taskStartTime;
    }
    public void setTaskStartTime(Calendar taskStartTime) {
        this.taskStartTime = taskStartTime;
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

    public CheckableStatus getIsTaskCompleted() {
        return isTaskCompleted;
    }
    public void setIsTaskCompleted(CheckableStatus isTaskCompleted) {
        this.isTaskCompleted = isTaskCompleted;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public boolean isList() {
        return isList;
    }
    public void setList(boolean list) {
        isList = list;
    }

    public boolean isRecurring() {
        return isRecurring;
    }
    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
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


    // Enum that defines the types of checkable statuses that exist
    public enum CheckableStatus {
        notCheckable, incomplete, completed;
    }
}
