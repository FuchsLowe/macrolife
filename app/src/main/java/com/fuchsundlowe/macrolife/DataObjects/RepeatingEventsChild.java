package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import java.util.Calendar;
import java.util.Random;


/**
 * Created by macbook on 1/30/18.
 * A simple holder that doesn't inherit from DataMaster class
 */
@Entity
public class RepeatingEventsChild {

    // Instance variables
    private int parentID;
    private Calendar startTime;
    private Calendar endTime;
    private DayOfWeek dayOfWeek;
    private int hashID;
    private StorageMaster storageMaster;

    public RepeatingEventsChild(int parentID, Calendar startTime, StorageMaster storageMaster,
                                Calendar endTime, DayOfWeek day, int hashID) {

        this.parentID = parentID;
        this.dayOfWeek = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.storageMaster = storageMaster;
        if ((Integer) hashID == null) {
            this.hashID = this.createNextID();
        } else {
            this.hashID = hashID;
        }
    }


    // Methods:
    public int getParentID() {
        return this.parentID;
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
    public boolean setEndTime(Calendar endTime) {
        Calendar startTime = this.getStartTime();
        if (endTime.after(getStartTime())) {
            this.endTime = endTime;
            return true;
        } else {
            return false;
        }
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
