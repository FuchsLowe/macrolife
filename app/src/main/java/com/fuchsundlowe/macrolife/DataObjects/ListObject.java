package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;

@Entity
public class ListObject {
    // Local Variables

    @PrimaryKey(autoGenerate = true)
    private int hashID;

    private String taskName;
    private boolean taskStatus;
    private int masterID;
    private Calendar lastTimeModified;


    //Constructor:
    public ListObject(String taskName, boolean taskStatus,
                      int masterID, int hashID, Calendar lastTimeModified) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.masterID = masterID;
        this.lastTimeModified = lastTimeModified;
        this.hashID = hashID;

    }


    // Methods:
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskStatus(boolean taskStatus) {
        this.taskStatus = taskStatus;
    }
    public boolean getTaskStatus() {
        return this.taskStatus;
    }

    public void setMasterID(int masterID) { this.masterID = masterID; }
    public int getMasterID() {
        return this.masterID;
    }

    public void setHashID(int hashID) {
        this.hashID = hashID;
    }
    public int getHashID() {
        return this.hashID;
    }

    public Calendar getLastTimeModified() {
        return lastTimeModified;
    }
    public void setLastTimeModified(Calendar lastTimeModified) {
        this.lastTimeModified = lastTimeModified;
    }
}
