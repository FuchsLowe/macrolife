package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Ignore;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import java.util.Calendar;
import java.util.Random;

/**
 This class is the superclass of most data objects used in this software. It provides common
 values and their respective getters and setters.
 */

public abstract class DataMasterClass {

    // Variables list;
    protected int hashID;
    protected String taskName;
    protected Calendar taskStartTime;
    public Calendar taskEndTime;
    protected Calendar taskCreatedTimeStamp;
    protected boolean taskCompleted;
    private SourceType taskOriginalSource ;
    @Ignore
    private StorageMaster storageMaster;

    // public constructor -
    public DataMasterClass(int hashID, String taskName, Calendar taskStartTime,
                           Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                           boolean taskCompleted, SourceType taskOriginalSource,
                           StorageMaster storageMaster) {

        this.taskName = taskName;
        this.taskStartTime = taskStartTime;
        this.taskEndTime = taskEndTime;
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
        this.taskCompleted = taskCompleted;
        this.taskOriginalSource = taskOriginalSource;
        this.storageMaster = storageMaster;

        if ((Integer)hashID == null ){
            if (storageMaster != null) {
                this.hashID = this.createNextID();

            }
        }
    }


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


    private int createNextID() {
        Random random = new Random();
        int newHash;
        do {
            newHash = random.nextInt(Integer.MAX_VALUE - 1);
        } while (storageMaster.checkIfIDisAssigned(newHash));
        return newHash;
    }

    public StorageMaster getStorageMaster() {return this.storageMaster; }


    public void setTaskOriginalSource(SourceType type) {
        this.taskOriginalSource = type;
    }

    public SourceType getTaskOriginalSource() {
        return this.taskOriginalSource;
    }

    // This method must check if there is stored ID and if yes update, if no insert. It also
    // should manage insertion into dataMaster class
    public abstract void updateMe();
}

