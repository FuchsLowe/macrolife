package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// A fundamental Data Type for this App Local storage
@Entity
public class TaskObject implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int hashID; // Passing 0 will make system auto-generate a key when data is formed...
    private int complexGoalID; // if this goal is a part of ComplexGoal, this would be ID of that master
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
    private String repeatDescriptor; // field used to save repeating event patterns
    private int mX, mY; // for location on the screen of Complex Activity
    /*
     * AllMods is enum based holder that manages insertion, removal and retrieval of mods from string
     * and acts as a intermediary so outside
     * implementation don't have to work with strings in the first place. It is accessed by getters
     * and setters named addMod and removeMod.
     * The initializer methods that on creation grabs the info and converts it into enums is called
     * defineMods and takes no parameters.
     */
    @Ignore
    private ArrayList<Mods> allMods;
    @Ignore
    private ArrayList<Mods> acceptableMods;

    /*
     *          TODO: Implementing the BottomBar position value and repeating Descriptor
     *
     *          note: BottomBar position:
     *          How can I manage the position of the bottom bar?
     *          It smells like I need to define a array somewhere that would arrange the positon of
     *          values in some order. As well, it needs to persist the changes made to UI.
     *
     *
     *          note: Repeating Descriptor:
     *          So does it matter for me to save the pattern in the first place?
     *          When will it be retrived? To present the dates? When asked about the nature of
     *          repeating events? That can be retrived by database call as well...
     *
     *
     */


    // Mark Constructor:
    public TaskObject(int hashID, int complexGoalID, int subGoalMaster, String taskName,
                      Calendar taskCreatedTimeStamp, Calendar taskStartTime, Calendar taskEndTime,
                      Calendar lastTimeModified, CheckableStatus isTaskCompleted, String note,
                      int mX, int mY, String mods, TimeDefined timeDefined, String repeatDescriptor) {

        this.hashID = hashID;
        this.complexGoalID = complexGoalID;
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
        this.repeatDescriptor = repeatDescriptor;

        acceptableMods = new ArrayList<>();
        acceptableMods.add(Mods.note);
        acceptableMods.add(Mods.repeating);
        acceptableMods.add(Mods.list);

        allMods = new ArrayList<>(4);
        defineMods();
    }


    // Mark: Methods:
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
                case "list":
                    addMod(Mods.list);
                    break;
                default:
                    break;
            }
        }
    }
    public void addMod(TaskObject.Mods modToAdd) {
        if (acceptableMods.contains(modToAdd)) {
            if (!allMods.contains(modToAdd)) {
                allMods.add(modToAdd);
            }
            updateMods();
        }
    }
    public void removeAMod(TaskObject.Mods modToRemove) {
        allMods.remove(modToRemove);
        updateMods();
    }

    private void updateMods() {
        mods = ""; // We clean the mods
        for (TaskObject.Mods mod : allMods) {
            mods+= "\n"+ mod.toString();
        }
    }
    public List<Mods> getAllMods() {
        return this.allMods;
    }

    public boolean isThisRepeatingEvent() {
        return allMods.contains(Mods.repeating);
    }

    // Mark: Generic Getters and Setters:
    public int getHashID() {
        return hashID;
    }
    public void setHashID(int hashID) {
        this.hashID = hashID;
    }

    public int getComplexGoalID() {
        return complexGoalID;
    }
    public void setComplexGoalID(int complexGoalID) {
        this.complexGoalID = complexGoalID;
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
        if (taskStartTime != null) {
            // If We don't have end time then we can at max have only date as variable defined in TimeDef.
            if (taskEndTime != null) {
                if (taskEndTime.before(taskStartTime) ||
                        taskEndTime.getTimeInMillis() == taskStartTime.getTimeInMillis()) {
                    // we can't have end time appearing before start time or happening at same time
                    this.taskEndTime = (Calendar) taskStartTime.clone();
                    // We will add 15 min to differentiate the two
                    this.taskEndTime.add(Calendar.MINUTE, 15);
                }
            } else {
                setTimeDefined(TimeDefined.onlyDate);
            }
        } else {
            this.taskEndTime = null;
            timeDefined = TimeDefined.noTime;
        }
    }

    public Calendar getTaskEndTime() {
        return taskEndTime;
    }
    public void setTaskEndTime(Calendar taskEndTime) {
        // Must prevent start and end time collision and inconsistency
        if (taskStartTime != null && taskEndTime != null) {
            if (taskEndTime.before(taskStartTime)) {
                // we can't accept this value...  we
                this.taskEndTime = (Calendar) taskStartTime.clone();
                // We set 15 min after start Time
                this.taskEndTime.add(Calendar.MINUTE, 15);
            } else {
                // We can save:
                this.taskEndTime = taskEndTime;
            }
            timeDefined =TimeDefined.dateAndTime;
        } else {
            // We can't set the time, because we don't have start time
            this.taskEndTime = null;
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
        if (timeDefined == TimeDefined.noTime) {
            setTaskStartTime(null);
            setTaskEndTime(null);
        } else if (timeDefined == TimeDefined.onlyDate) {
            setTaskEndTime(null);
        }
    }

    public String getRepeatDescriptor() {
        return repeatDescriptor;
    }
    // passing a null will delete the repeating value
    public void setRepeatDescriptor(String repeatDescriptor) {
        if (repeatDescriptor != null || repeatDescriptor.length() > 1) {
            this.repeatDescriptor = repeatDescriptor;
            addMod(Mods.repeating);
        } else {
            removeAMod(Mods.repeating);
        }
    }


    // Mark: Enum that defines the types of checkable statuses that exist
    public enum CheckableStatus {
        notCheckable, incomplete, completed
    }
    public enum TimeDefined {
        noTime, onlyDate, dateAndTime
    }
    // Mark: Lists all mods that task can have
    public enum Mods {
        note, repeating, list, checkable, dateAndTime, delete
    }
}
