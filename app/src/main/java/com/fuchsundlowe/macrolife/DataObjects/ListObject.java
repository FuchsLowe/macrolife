package com.fuchsundlowe.macrolife.DataObjects;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;

/**
 * Created by macbook on 1/29/18.
 * Small data holder that doesn't inherit from DataMasterClass
 * Holds single enrty and is used exclusivlely as subgoal for listMaster
 * USed as subelement in grocery lists and alike.
 * TODO: To add itself to data if created anew
 * TODO: To delete self on request? How will I manage this?
 */

public class ListObject {
    // Local Variables
    private String taskName;
    private boolean taskStatus;
    private int masterID;
    private int hashID;
    private StorageMaster storageMaster;

    //Constructor:
    public ListObject(String taskName, boolean taskStatus,
                      int masterID, int hashID, StorageMaster storageMaster) {
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.masterID = masterID;
        this.hashID = hashID;
        this.storageMaster = storageMaster;
        if (!amIStored()) {
            this.storeMe();
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
    public boolean isTaskDone() {
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
        storageMaster.updateObject(this);
    }
    private void storeMe() {
        storageMaster.insertObject(this);
    }
    private boolean amIStored(){
        return storageMaster.amIStored(this);
    }
}
