package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;

import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * This is a holder class for ordinary Tasks
 */
public class OrdinaryEventMaster extends DataMasterClass {


    public OrdinaryEventMaster(int hashID, String taskName, Calendar taskStartTime,
                               Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                               boolean taskCompleted, SourceType taskOriginalSource,
                               StorageMaster storageMaster) {
        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted,
                taskOriginalSource, storageMaster);


    }

    @Override
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }
    }
}
