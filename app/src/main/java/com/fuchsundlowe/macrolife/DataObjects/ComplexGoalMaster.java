package com.fuchsundlowe.macrolife.DataObjects;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * TODO: Array list is not thread safe... Consider this.
 */

public class ComplexGoalMaster extends DataMasterClass {

    public ComplexGoalMaster(String taskName, SourceType originalSourceOfTask,
                             Calendar originalCreationTime, int taskUniqueIdentifier) {
        super(taskName, originalSourceOfTask, originalCreationTime, taskUniqueIdentifier);
        this.subTasks = new ArrayList<Integer>();

    }

    // Unique variables to this class:

    private ArrayList<Integer> subTasks;
    private String purpose;
   // private color; how do I store color?

    // Specific getters and setters to the class;
    public ArrayList<Integer> getSubTasksIDs() {
        return this.subTasks;
    }

    public void addSubtaskToMasterViaID(int id) {
        this.subTasks.add(id);
    }

    public void removeSubtask(int id) {
        try {
            this.subTasks.remove(id);
        } catch (IndexOutOfBoundsException error) {
            // TODO: I should consider what to do with this errors and exceptions...
            // I need a way to report to self about them... A global try catch maybe that
            // sends me an email for such errors.
        }
    }

    public void setPurposeText(String textOfNewPurpose) {
        this.purpose = textOfNewPurpose;
    }

    public String getPurpose() {
        return this.purpose;
    }
}
