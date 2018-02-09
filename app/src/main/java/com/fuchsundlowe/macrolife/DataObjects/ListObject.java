package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.content.Context;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;

import java.util.Random;

/**
 * Created by macbook on 1/29/18.
 * Small data holder that doesn't inherit from DataMasterClass
 * Holds single enrty and is used exclusivlely as subgoal for listMaster
 * USed as subelement in grocery lists and alike.
 * TODO: To delete self on request? How will I manage this?
 */
@Entity(primaryKeys = {"hashID"})
public class ListObject {
    // Local Variables
    private String taskName;
    private boolean taskStatus;
    private int masterID;
    private int hashID;
    @Ignore
    private StorageMaster storageMaster;

    //Constructor:
    public ListObject(String taskName, boolean taskStatus,
                      int masterID, int hashID) {
        this.storageMaster = StorageMaster.optionalStorageMaster();
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.masterID = masterID;

        if ((Integer) hashID != null) {
            this.hashID = hashID;
        } else {
            this.hashID = this.createNextID();
            this.updateMe();
        }
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
    // Returns true if status is done and false if not
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


    public void updateMe() {
        if (this.storageMaster.checkIfIDisAssigned(this.getHashID())) {
            this.storageMaster.updateObject(this);
        } else {
            this.storageMaster.insertObject(this);
        }
    }

    private boolean amIStored(){
        return storageMaster.amIStored(this);
    }

    private int createNextID() {
        Random random = new Random();
        int newHash;
        do {
            newHash = random.nextInt(Integer.MAX_VALUE - 1);
        } while (storageMaster.checkIfIDisAssigned(newHash));
        return newHash;
    }


}
