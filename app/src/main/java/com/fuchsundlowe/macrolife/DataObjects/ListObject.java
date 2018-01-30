package com.fuchsundlowe.macrolife.DataObjects;

/**
 * Created by macbook on 1/29/18.
 */

public class ListObject {
    /*
    Name
    Completed
    Master
    UniqueID
     */
    public ListObject(String taskName, boolean isTaskDone,
                      int masterID, int hashID) {
        this.taskName = taskName;
        this.isTaskDone = isTaskDone;
        this.masterID = masterID;
        this.hashID = hashID;
    }

    private String taskName;
    private boolean isTaskDone;
    private int masterID;
    private int hashID;

    public void changeTaskName(String newTaskName) {
        this.taskName = newTaskName;
    }
    public String getTaskName() {
        return this.taskName;
    }

    public void changeTaskDoneStatus(boolean taskIsComplete) {
        this.isTaskDone = taskIsComplete;
    }

    public boolean isTaskDone() {
        return this.isTaskDone;
    }

    public int getMasterID() {
        return this.masterID;
    }

    public int getTaskUniqueID() {
        return this.hashID;
    }
}
