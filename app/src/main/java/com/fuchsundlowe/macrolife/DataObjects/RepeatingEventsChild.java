package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import java.util.Calendar;
import java.util.Random;


/**
 * Created by macbook on 1/30/18.
 * A simple holder that doesn't inherit from DataMaster class
 */
@Entity(primaryKeys = {"hashID"})
public class RepeatingEventsChild {

    // Instance variables
    private int parentID;
    private Calendar startTime;
    private Calendar endTime;
    private DayOfWeek dayOfWeek;
    private int hashID;
    @Ignore
    private StorageMaster storageMaster;

    public RepeatingEventsChild(int parentID, Calendar startTime,
                                Calendar endTime, DayOfWeek dayOfWeek, int hashID) {

        this.parentID = parentID;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.storageMaster = StorageMaster.optionalStorageMaster();
        if ((Integer) hashID == null) {
            this.hashID = this.createNextID();
        } else {
            this.hashID = hashID;
        }
    }


    // Methods:
    public void setHashID(int hashID) {
        this.hashID = hashID;
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

    private StorageMaster getStorageMaster() {
        return this.storageMaster;
    }

    public int getHashID() {
        return this.hashID;
    }

    private int createNextID() {
        Random random = new Random();
        int newHash;
        do {
            newHash = random.nextInt(Integer.MAX_VALUE - 1);
        } while (storageMaster.checkIfIDisAssigned(newHash));
        return newHash;
    }

    //Should check if parent has him, if not add him
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }

        RepeatingEventMaster parent = this.getStorageMaster().getMasterByInt(this.getParentID());
        if (parent != null) {
            parent.addChild(this);
        }
    }

    // SHoudl be removed from parent and storage...
    public void deleteMe() {
        RepeatingEventMaster parent = this.getStorageMaster().getMasterByInt(this.getParentID());
        if (parent != null) {
            parent.removeChild(this);
        }
    }
}
