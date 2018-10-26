package com.fuchsundlowe.macrolife.DataObjects;

import com.fuchsundlowe.macrolife.R;

/**
 * Created by macbook on 1/29/18.
 */

public class Constants {
    public static String DAY_TO_DISPLAY = "DayForFragment"; // Used to grab date saved in Bundle in various implementations
    public static String LIST_VIEW_TYPE_TO_DISPLAY = "LadyGaga";
    public static String LIST_VIEW_MASTER_ID = "Give me that master key";
    public static String FIRST_DAY_OF_WEEK = "Whats the first day of Week";
    public static String SHARED_PREFERENCES_KEY = "This key unlocks the shared preferences";
    public static String TIME_REPRESENTATION = "Should I show Hours as time representation?";
    public static String HOUR_IN_PIXELS = "Looks like a impossible way to think huh?";
    public static String TASK_OBJECT = "TaskObject";
    public static String REPEATING_EVENT = "RepeatingEvent";
    public static String DATA_BASE_NAME = "MK1";

    public static String INTENT_FILTER_COMPLEXGOAL_ID = "The ID of complex goal with which to open you";
    public static String INTENT_FILTER_COMPLEXGOAL_EDIT = "Editing the complex goal Sire";
    public static String INTENT_FILTER_GLOBAL_EDIT = "Global Edit Signaling";
    public static String INTENT_FILTER_NEW_TASK = "This is new task";
    public static String INTENT_FILTER_TASK_ID = "Get me the hash ID";
    public static String INTENT_FILTER_EVENT_ID = "Give me the events ID boo";
    public static String INTENT_FILTER_FIELD_START_TIME = "Time of start in Long";
    public static String INTENT_FILTER_RECOMMENDATION = "Get the recommendation engine";
    public static String INTENT_FILTER_EVENT_DELETED = "Event has been deleted";
    public static String INTENT_FILTER_STOP_EDITING = "We are done editing the bottom bar sire";
    public static String INTENT_FILTER_DAY_CLICKED = "This is the long description of day Sire";
    public static String INTENT_FILTER_NEW_DATE_SET = "New date is declared sire"; // Reports new date set
    public static String INTENT_FILTER_DATE_VALUE = "Give me that long number";// Used to get date as long of some Intent
    public static String INTENT_FILTER_NEW_MONTH_SELECTED = "New Month has been selected";

    public static DayOfWeek[] AMERICAN_WEEK_DAYS = {DayOfWeek.sunday, DayOfWeek.monday,
            DayOfWeek.tuesday, DayOfWeek.wednesday, DayOfWeek.thursday, DayOfWeek.friday,
    DayOfWeek.saturday};
    public static DayOfWeek[] EUROPEAN_WEEK_DAYS = {DayOfWeek.monday,
            DayOfWeek.tuesday, DayOfWeek.wednesday, DayOfWeek.thursday, DayOfWeek.friday,
            DayOfWeek.saturday, DayOfWeek.sunday};
    // These two are used for reporting setting the values in Calendars for start and end respectively:
    public static String START_VALUE_DONE = "Reporting that startValue is initiated";
    public static String END_VALUE_DONE = "Report that endValue is initiated";
    public static String TYPE_DEFINED = "Reporting that type is defined";
    public static String TYPE_NOT_DEFINED = "Reporting that type is not defined sir";
    // Fragment Tags:
    public static String EDIT_TASK_BOTTOM_BAR = "Editing the Task Bottom Bar";
    public static String EDIT_GOAL_BOTTOM_BAR = "Editing Complex Goals";
    // Magic Numbers:
    public static long millisInADay= 86400000;
}
