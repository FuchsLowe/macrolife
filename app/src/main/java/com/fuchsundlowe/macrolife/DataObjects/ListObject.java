package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * Small data holder that doesn't inherit from DataMasterClass
 * Holds single enrty and is used exclusivlely as subgoal for listMaster
 * USed as subelement in grocery lists and alike.
 * TODO: To delete self on request? How will I manage this?
 */
@Entity
public class ListObject {
    // Local Variables
    private String taskName;
    private boolean taskStatus;
    private int masterID;
    @PrimaryKey(autoGenerate = true)
    private int hashID;
    private Calendar lastTimeModified;


    //Constructor:
    public ListObject(String taskName, boolean taskStatus,
                      int masterID, int hashID, Calendar lastTimeModified) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.masterID = masterID;
        this.lastTimeModified = lastTimeModified;

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
