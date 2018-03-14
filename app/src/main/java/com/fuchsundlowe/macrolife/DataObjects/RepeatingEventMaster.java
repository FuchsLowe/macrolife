package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by macbook on 1/29/18.
 * This Class holds a specific set of days and occupation times
 * This class also holds a set of repeating schemas.
 */
    /* TODO: How Will I create and implement such functionality...?
        Holds standard start and end days that define duration of the period...
        Holds a day
        Holds a event duration in that day
        ====================
        Maybe it needs a special element that holds a date and duration of some sort of the thing...
        1. You are prompet to create a template for week
            1.1. You are asked if you want to repeat this till end of designated period
        2. YOu can optionally create 2nd, 3rd & 4th template
        3. If you have more than 1 template then you will be able prompet to month view
            3.1. Here you designate each template per week...
        4. Once that month is done, you are asked if you want this to be repeated till end of period...
         */
@Entity(primaryKeys = {"hashID"})
public class RepeatingEventMaster extends DataMasterClass {

    // Variables:
    @Ignore
    private Map<DayOfWeek, Set<RepeatingEventsChild>> events;

    public RepeatingEventMaster(int hashID, String taskName, Calendar taskStartTime,
                                Calendar taskEndTime, Calendar taskCreatedTimeStamp,
                                boolean taskCompleted, SourceType taskOriginalSource) {
        super(hashID, taskName, taskStartTime, taskEndTime, taskCreatedTimeStamp,
                taskCompleted, taskOriginalSource);
        // Called to fill in the values for subEvents.
        //populateMe(); TODO: Define how will I do this... WHo should be in charge of finding the children?
    }

    private void createMap() {
        events = new HashMap<>();
        events.put(DayOfWeek.monday, new HashSet<RepeatingEventsChild>());
        events.put(DayOfWeek.tuesday, new HashSet<RepeatingEventsChild>());
        events.put(DayOfWeek.wednesday, new HashSet<RepeatingEventsChild>());
        events.put(DayOfWeek.thursday, new HashSet<RepeatingEventsChild>());
        events.put(DayOfWeek.friday, new HashSet<RepeatingEventsChild>());
        events.put(DayOfWeek.saturday, new HashSet<RepeatingEventsChild>());
        events.put(DayOfWeek.sunday, new HashSet<RepeatingEventsChild>());
    }

    // Manages the creation of map and populates it with values:
    private void populateMe() {
        // Creates hashMap
        createMap();

        // Needs to find all children of this class
        Set<RepeatingEventsChild> children = this.getStorageMaster().
                getAllRepeatingChildrenByParent(this.getHashID());

        // Arranges them in right order
        for (RepeatingEventsChild child: children) {
            this.addChild(child);
        }
    }

    @Override
    public void updateMe() {
        if (this.getStorageMaster().checkIfIDisAssigned(this.getHashID())) {
            this.getStorageMaster().updateObject(this);
        } else {
            this.getStorageMaster().insertObject(this);
        }
    }

    // Called when need to add an event child
    public void addChild(RepeatingEventsChild child) {
            switch (child.getDayOfWeek()) {
                case monday:
                    events.get(DayOfWeek.monday).add(child);
                case tuesday:
                    events.get(DayOfWeek.tuesday).add(child);
                case wednesday:
                    events.get(DayOfWeek.wednesday).add(child);
                case thursday:
                    events.get(DayOfWeek.thursday).add(child);
                case friday:
                    events.get(DayOfWeek.friday).add(child);
                case saturday:
                    events.get(DayOfWeek.saturday).add(child);
                default:
                    events.get(DayOfWeek.sunday).add(child);
            }
    }

    // Called when need to remove event child
    public void removeChild(RepeatingEventsChild child) {
        switch (child.getDayOfWeek()) {
            case monday:
                events.get(DayOfWeek.monday).remove(child);
            case tuesday:
                events.get(DayOfWeek.tuesday).remove(child);
            case wednesday:
                events.get(DayOfWeek.wednesday).remove(child);
            case thursday:
                events.get(DayOfWeek.thursday).remove(child);
            case friday:
                events.get(DayOfWeek.friday).remove(child);
            case saturday:
                events.get(DayOfWeek.saturday).remove(child);
            default:
                events.get(DayOfWeek.sunday).remove(child);
        }
    }
}
