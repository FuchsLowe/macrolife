package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// A fundamental Data Type for this App Local storage
@Entity
public class TaskObject {
    @PrimaryKey(autoGenerate = true)
    private int hashID; // Passing 0 will make system auto-generate a key when data is formed...
    private int parentGoal; // if this goal is a part of ComplexGoal, this would be ID of that master
    private int subGoalMaster; // reference to a optional next in hierarchy goal
    private String taskName;
    private Calendar taskCreatedTimeStamp; // set once, never changes
    private Calendar taskStartTime;
    private Calendar taskEndTime;
    private Calendar lastTimeModified;
    private CheckableStatus isTaskCompleted;
    private TimeDefined timeDefined;
    private String note;
    private String mods;
    private int mX, mY; // for location on the screen of Complex Activity
    @Ignore
    private ArrayList<Mods> allMods;
    @Ignore
    private ArrayList<Mods> acceptableMods;

    // Constructor:
    public TaskObject(int hashID, int parentGoal, int subGoalMaster, String taskName,
                      Calendar taskCreatedTimeStamp, Calendar taskStartTime, Calendar taskEndTime,
                      Calendar lastTimeModified, CheckableStatus isTaskCompleted, String note,
                      int mX, int mY, String mods, TimeDefined timeDefined) {

        this.hashID = hashID;
        this.parentGoal = parentGoal;
        this.subGoalMaster = subGoalMaster;
        this.taskName = taskName;
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
        this.taskStartTime = taskStartTime;
        this.taskEndTime = taskEndTime;
        this.lastTimeModified = lastTimeModified;
        this.isTaskCompleted = isTaskCompleted;
        this.note = note;
        this.mX = mX;
        this.mY = mY;
        this.mods = mods;
        this.timeDefined = timeDefined;

        acceptableMods = new ArrayList<>();
        acceptableMods.add(Mods.note);
        acceptableMods.add(Mods.repeating);
        acceptableMods.add(Mods.list);
        acceptableMods.add(Mods.repeatingMultiValues);

        allMods = new ArrayList<>(4);
        defineMods();
    }

    // Methods:
    private void defineMods() {
        String[] modsSplit = this.mods.split("\n");
        for (String mod: modsSplit) {
            switch (mod) {
                case "note":
                    addMod(Mods.note);
                    break;
                case "repeating":
                    addMod(Mods.repeating);
                    break;
                case "repeatingMultiValues":
                    addMod(Mods.repeatingMultiValues);
                    break;
                case "list":
                    addMod(Mods.list);
                    break;
                default:
                    break;
            }
        }
    }
    public void addMod(Mods modToAdd) {
        if (acceptableMods.contains(modToAdd)) {
            if (!allMods.contains(modToAdd)) {
                allMods.add(modToAdd);
                // TODO:Should this save the data?
                if (!mods.contains(modToAdd.name())) {
                    mods += "\n" + modToAdd.name();
                }
                // This implementation prevents us from having both mods because they are mutually exclusive
                if (modToAdd == Mods.repeating) {
                    removeAMod(Mods.repeatingMultiValues);
                } else if (modToAdd == Mods.repeatingMultiValues) {
                    removeAMod(Mods.repeating);
                }
            }
        }
    }
    public void removeAMod(Mods modToRemove) {
        allMods.remove(modToRemove);
        mods.replace("\n" + modToRemove.name(),"");
        // TODO: Should this save data?
    }
    public List<Mods> getAllMods() {
        return this.allMods;
    }
    @Nullable
    public Mods getRepeatingMod() {
        if (allMods.contains(Mods.repeating)) {
            return Mods.repeating;
        } else if (allMods.contains(Mods.repeatingMultiValues)){
            return Mods.repeatingMultiValues;
        }
        return null;
    }
    // Generic Getters and Setters:
    public int getHashID() {
        return hashID;
    }
    public void setHashID(int hashID) {
        this.hashID = hashID;
    }

    public int getParentGoal() {
        return parentGoal;
    }
    public void setParentGoal(int parentGoal) {
        this.parentGoal = parentGoal;
    }

    public int getSubGoalMaster() {
        return subGoalMaster;
    }
    public void setSubGoalMaster(int subGoalMaster) {
        this.subGoalMaster = subGoalMaster;
    }

    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Calendar getTaskCreatedTimeStamp() {
        return taskCreatedTimeStamp;
    }
    public void setTaskCreatedTimeStamp(Calendar taskCreatedTimeStamp) {
        this.taskCreatedTimeStamp = taskCreatedTimeStamp;
    }

    public Calendar getTaskStartTime() {
        return taskStartTime;
    }
    public void setTaskStartTime(Calendar taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Calendar getTaskEndTime() {
        return taskEndTime;
    }
    public boolean setTaskEndTime(Calendar taskEndTime) {
        Calendar startTime = this.getTaskStartTime();
        if (taskEndTime.after(startTime)) {
            this.taskEndTime = taskEndTime;
            return true;
        } else {
            return false;
        }
    }

    public Calendar getLastTimeModified() {
        return lastTimeModified;
    }
    public void setLastTimeModified(Calendar lastTimeModified) {
        this.lastTimeModified = lastTimeModified;
    }

    public CheckableStatus getIsTaskCompleted() {
        return isTaskCompleted;
    }
    public void setIsTaskCompleted(CheckableStatus isTaskCompleted) {
        this.isTaskCompleted = isTaskCompleted;
    }

    public String getMods() {return this.mods;}
    public void setMods(String mods) { this.mods = mods;}

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }

    public int getMX() {
        return mX;
    }
    public void setMX(int mX) {
        this.mX = mX;
    }

    public int getMY() {
        return mY;
    }
    public void setMY(int mY) {
        this.mY = mY;
    }

    public TimeDefined getTimeDefined() {
        return timeDefined;
    }
    public void setTimeDefined(TimeDefined timeDefined) {
        this.timeDefined = timeDefined;
    }

    // Enum that defines the types of checkable statuses that exist
    public enum CheckableStatus {
        notCheckable, incomplete, completed;
    }
    public enum TimeDefined {
        noTime, onlyDate, dateAndTime
    }
    // Lists all mods that task can have
    public enum Mods {
        note, repeating, repeatingMultiValues, list, checkable, dateAndTime, delete;
    }
}
