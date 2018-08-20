package com.fuchsundlowe.macrolife.DepreciatedClasses;

import android.arch.persistence.room.Entity;

import com.fuchsundlowe.macrolife.DataObjects.SourceType;

import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * This is a holder class for ordinary Tasks
 */
@Deprecated
@Entity(primaryKeys = {"hashID"})
public class OrdinaryEventMaster extends DataMasterClass {


    public OrdinaryEventMaster(int hashID, String taskName, Calendar taskStartTime,
                               Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                               boolean taskCompleted, SourceType taskOriginalSource) {
        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted,
                taskOriginalSource);


    }

    @Override
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }
    }

    @Override
    public void deleteMe() {
        this.getStorageMaster().deleteObject(this);
    }
}
