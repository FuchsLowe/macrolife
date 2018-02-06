package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Ignore;

import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * This Class holds a specific set of days and occupation times
 * This class also holds a set of repeating schemas.
 */

public class RepeatingEventMaster extends DataMasterClass {



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
    // TODO: Template master? SHould this be public or no?
    public void populateTemplatesIfAvailable() {
        // Looks for templates and then populates if there are any... 
    }


}
