package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject.CheckableStatus;

import java.util.Calendar;

import static android.arch.persistence.room.ForeignKey.CASCADE;


/**
 * Created by macbook on 1/30/18.
 * A simple holder for repeating events
 */
@Entity
public class RepeatingEvent {

    // Instance variables
    private int parentID;
    private Calendar startTime;
    private Calendar endTime;
    private Calendar lastTimeModified;
    @PrimaryKey(autoGenerate = true)
    private int hashID;
    private CheckableStatus isTaskCompleted;

    public RepeatingEvent(int parentID, Calendar startTime,
                          Calendar endTime, int hashID, Calendar lastTimeModified,
                          CheckableStatus isTaskCompleted) {

        this.parentID = parentID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastTimeModified = lastTimeModified;
        this.hashID = hashID;
        this.isTaskCompleted = isTaskCompleted;
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
        if (endTime != null) {
            if (endTime.after(getStartTime())) {
                this.endTime = endTime;
                return true;
            } else {
                return false;
            }
        } else {
            endTime = null;
            return true;
        }
    }
    @Deprecated
    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public Calendar getLastTimeModified() {
        return lastTimeModified;
    }
    public void setLastTimeModified(Calendar lastTimeModified) {
        this.lastTimeModified = lastTimeModified;
    }
    // returns true if this has only date defined, no time
    public boolean isOnlyDate() {
        return endTime == null || endTime.before(startTime);
    }
}
