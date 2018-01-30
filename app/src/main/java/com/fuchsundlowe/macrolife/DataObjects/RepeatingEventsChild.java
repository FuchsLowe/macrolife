package com.fuchsundlowe.macrolife.DataObjects;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by macbook on 1/30/18.
 */

public class RepeatingEventsChild {
    private int parentID;
    private Calendar startTime;
    private int durationOfEvent;
    private ArrayList<Integer> pictureIDs;

    public RepeatingEventsChild(int parentID, Calendar startTime,
                                int durationOfEvent, ArrayList<Integer> pictureIDs) {
        this.parentID = parentID;
        this.startTime = startTime;
        this.durationOfEvent = durationOfEvent;
        this.pictureIDs = pictureIDs;


    }

    public int getParentID() {
        return this.parentID;
    }
    public Calendar getStartTime() {
        return this.startTime;
    }

    public void setStartTime(Calendar newTime) {
        this.startTime = newTime;
    }

    public int getDurationOfEvent() {
        return this.durationOfEvent;
    }

    public void setDurationOfEvent(int newDurationOfEvent) {
        this.durationOfEvent = newDurationOfEvent;
    }

    public ArrayList<Integer> getPictureIDs() {
        return this.pictureIDs;
    }

    public void addPictureID(int newIdToAdd) {
        this.pictureIDs.add(newIdToAdd);
    }

    public boolean isTherePicture() {
        return !this.pictureIDs.isEmpty();
    }
}
