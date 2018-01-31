package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * TODO: Array list is not thread safe... Consider this.
    This is a class that defines functionality of data holder for Complex goal. Holds subgoals
 and has accompaning calls and additional variables.
 *
 */
@Entity(primaryKeys = {"hashID"})
public class ComplexGoalMaster extends DataMasterClass {

    // Variables:
    private ArrayList<Integer> subTasks;
    private String purpose;
    // TODO: How will I store color?

    // Constructor: TODO: Insuficient constructor
    public ComplexGoalMaster(String taskName, SourceType originalSourceOfTask,
                             Calendar originalCreationTime, int taskUniqueIdentifier,
                             boolean isTaskCompleted) {
        this.subTasks = new ArrayList<Integer>();
    }


    // Specific getters and setters to the class;
    public ArrayList<Integer> getSubTasksIDs() {
        // TODO: Needs implemnetation. If this is empty, needs to querry the results.
        return this.subTasks;
    }
    public void addSubtaskToMasterViaID(int id) {
        this.subTasks.add(id);
        // TODO: This implementation is insuficinet. Need to reflect persistance.
    }
    public void removeSubtask(int id) {
        // TODO: As well this should be reflected in database.
        try {
            this.subTasks.remove(id);
        } catch (IndexOutOfBoundsException error) {
            // TODO: I should consider what to do with this errors and exceptions...
            // I need a way to report to self about them... A global try catch maybe that
            // sends me an email for such errors.
        }

    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    public String getPurpose() {
        return this.purpose;
    }
}
