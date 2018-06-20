package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject.CheckableStatus;

import java.util.Calendar;


/**
 * Created by macbook on 1/30/18.
 * A simple holder that doesn't inherit from DataMaster class
 */
@Entity(foreignKeys = @ForeignKey(entity = TaskObject.class, parentColumns = "hashID",
        childColumns = "parentID", onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE))
public class RepeatingEvent {

    // Instance variables
    private int parentID;
    private Calendar startTime;
    private Calendar endTime;
    private Calendar lastTimeModified;
    private DayOfWeek dayOfWeek; // TODO: How should I represent this? This is found in startTime thou
    @PrimaryKey(autoGenerate = true)
    private int hashID;
    private CheckableStatus isTaskCompleted;

    public RepeatingEvent(int parentID, Calendar startTime,
                          Calendar endTime, DayOfWeek dayOfWeek, int hashID, Calendar lastTimeModified) {

        this.parentID = parentID;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastTimeModified = lastTimeModified;

    }

    // Methods:
    public void setHashID(int hashID) {
        this.hashID = hashID;
    }
    public int getHashID() {
        return this.hashID;
    }
    public int getParentID() {
        return this.parentID;
    }
    public void setParentID(int parentID){
        this.parentID = parentID;
    }
    public Calendar getStartTime() {
        return this.startTime;
    }
    public void setStartTime(Calendar newTime) {
        this.startTime = newTime;
    }
    public Calendar getEndTime() {
        return this.endTime;
    }
    public CheckableStatus getIsTaskCompleted() {
        return isTaskCompleted;
    }
    public void setIsTaskCompleted(CheckableStatus isTaskCompleted) {
        this.isTaskCompleted = isTaskCompleted;
    }

    // Returns true if it can end time comes after begin time, false otherwise.
    public boolean setEndTimeWithReturn(Calendar endTime) {
        Calendar startTime = this.getStartTime();
        if (endTime.after(getStartTime())) {
            this.endTime = endTime;
            return true;
        } else {
            return false;
        }
    }
    @Deprecated
    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }
    public void setDayOfWeek(DayOfWeek day) {
        this.dayOfWeek = day;
    }

    public Calendar getLastTimeModified() {
        return lastTimeModified;
    }
    public void setLastTimeModified(Calendar lastTimeModified) {
        this.lastTimeModified = lastTimeModified;
    }
}
