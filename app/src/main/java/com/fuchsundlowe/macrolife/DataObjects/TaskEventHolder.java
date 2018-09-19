package com.fuchsundlowe.macrolife.DataObjects;

import android.support.annotation.Nullable;

import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;

import java.util.Calendar;
import java.util.List;

/*
 * Class that is used to hold either a task or event, and is used to present these two objects in
 * one wrapper class, by abstracting the type (event or task) as much as possible.
 *
 * This is very useful when I need to present some task/event to lets say DayView without worrying
 * what type it is.
 */
public class TaskEventHolder {
    private TaskObject task;
    private RepeatingEvent event;

    public TaskEventHolder(@Nullable TaskObject task, @Nullable RepeatingEvent event) {
        this.task = task;
        this.event = event;
    }
    public boolean isTask() {
        return task != null;
    }
    public TaskObject.CheckableStatus getCompletionState() {
        if (isTask()) {
            return task.getIsTaskCompleted();
        } else {
            return event.getIsTaskCompleted();
        }
    }
    public String getName() {
        if (isTask()) {
            return task.getTaskName();
        } else {
            // MARK: Gotta fetch it via DataBase
            return LocalStorage.getInstance(null).findTaskObjectBy(event.getParentID()).getTaskName();
        }
    }
    public List<TaskObject.Mods> getAllMods() {
        if (isTask()) {
            return task.getAllMods();
        } else {
            return LocalStorage.getInstance(null).findTaskObjectBy(event.getParentID()).getAllMods();
        }
    }
    // Returns the taskObjects hashID
    public int getMasterHashID() {
        if (isTask()) {
            return task.getHashID();
        } else {
            return event.getParentID();
        }
    }
    // Returns whatever active ID is used, either Tasks if its a task or Events otherwise:
    public int getActiveID() {
        if (isTask()) {
            return task.getHashID();
        } else {
            return event.getHashID();
        }
    }
    // Returns the ComplexGoalID if any:
    public int getComplexGoalID() {
        if (isTask()) {
            return task.getComplexGoalID();
        } else {
            return LocalStorage.getInstance(null).findTaskObjectBy(event.getParentID()).getComplexGoalID();
        }
    }
    public TaskObject.TimeDefined getTimeDefined() {
        if (isTask()) {
            return task.getTimeDefined();
        } else {
            if (event.getEndTime() == null || event.getEndTime().before(event.getStartTime())) {
                return TaskObject.TimeDefined.onlyDate;
            } else {
                return TaskObject.TimeDefined.dateAndTime;
            }
        }
    }
    public Calendar getStartTime() {
        if (isTask()) {
            return task.getTaskStartTime();
        } else {
            return event.getStartTime();
        }
    }
    public @Nullable Calendar getEndTime() {
        if (isTask()) {
            if (getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
                return task.getTaskEndTime();
            } else { return null; }
        } else {
            if (getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
                return event.getEndTime();
            } else {return null;}
        }
    }
    @Nullable
    public TaskObject getTask() {
        return task;
    }
    @Nullable
    public RepeatingEvent getEvent() {
        return event;
    }
    public Calendar getLastTimeModified() {
        if (isTask()) {
            return task.getLastTimeModified();
        } else {
            return event.getLastTimeModified();
        }
    }

}
