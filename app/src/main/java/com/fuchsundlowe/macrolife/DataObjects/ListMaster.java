package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import java.util.Calendar;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
 * This class holds list objects and is used to create views that dispaly list like goals.
 * Examples would be grocelry list etc.
 */
@Entity(primaryKeys = {"hashID"})
public class ListMaster extends DataMasterClass {
    // Instance Variables:

    // Constructor:
    public ListMaster(int hashID, String taskName, Calendar taskStartTime,
                      Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                      boolean taskCompleted, SourceType taskOriginalSource) {

        super(hashID, taskName, taskStartTime, taskEndTime,
                taskCreatedTimeStamp, taskCompleted, taskOriginalSource);


    }

    // Methods:

    public Set<ListObject> getChildren() {
        return getStorageMaster().getListObjectsOfParent(this.getHashID());
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
