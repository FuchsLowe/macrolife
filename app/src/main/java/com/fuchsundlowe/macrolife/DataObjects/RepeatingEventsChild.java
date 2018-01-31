package com.fuchsundlowe.macrolife.DataObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by macbook on 1/30/18.
 */

public class RepeatingEventsChild {
    // Instance variables
    private int parentID;
    private Calendar startTime;
    private Calendar endTime;
    private ArrayList<Integer> pictureIDs;

    public RepeatingEventsChild(int parentID, Calendar startTime,
                                Calendar endTime, ArrayList<Integer> pictureIDs) {
        this.parentID = parentID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pictureIDs = pictureIDs;


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

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public ArrayList<Integer> getPictureIDs() {
        return this.pictureIDs;
    }
    public void setPictureIDs() {
        // TODO: Consider this implementation
    }

    public boolean isTherePicture() {
        return !this.pictureIDs.isEmpty();
    }
}
