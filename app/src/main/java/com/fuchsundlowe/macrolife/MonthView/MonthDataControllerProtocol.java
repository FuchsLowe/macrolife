package com.fuchsundlowe.macrolife.MonthView;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

import java.util.Calendar;
import java.util.List;
// Interface that defines methods required by the Month view and its fragments in order
// to obtain data from database to work with.
interface MonthDataControllerProtocol {

    // For providing the single holder. Used for Edit Bottom Bar
    TaskEventHolder findAsTask(int hashID);
    TaskEventHolder findAsEvent(int hashID);
    void deleteTask(TaskObject task, Calendar date);
    void deleteEvent(RepeatingEvent event, Calendar date);
    // Return time table usages for specific year. Like how many min/day of tasks we have for day.
    // Held as array with length of total max days for that year, and values are total min of tasks for that day.
    int[][] timeTablesFor(short year);

    // List of tasks for specific day...
    List<TaskEventHolder> holdersFor(int dayOfYear, int year);

    /*
     * Called on start of the list and every time user changes year. So if year that opened the
     * activity was 2018 and user moves to 2019 it reports back that year has changed. Reason for
     * so is that the Data needs to prepare a new set of data for the following year and presumably
     * get rid of previous year.
     */
    void newYearSet(int year);
    int getFreeHashIDForTask();
    void saveTaskEventHolder(TaskEventHolder toSave);
}
