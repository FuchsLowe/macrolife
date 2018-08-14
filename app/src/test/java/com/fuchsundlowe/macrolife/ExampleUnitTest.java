package com.fuchsundlowe.macrolife;

import android.arch.persistence.room.Ignore;
import android.graphics.Point;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.Weeks;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Calendar.YEAR;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void myTest() {
        int toAdd = 6;
        DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);
        Calendar day = Calendar.getInstance();
        day.set(Calendar.DAY_OF_WEEK, -1);
        print("First day is" + format.format(day.getTime()));
        day.add(Calendar.DAY_OF_WEEK, toAdd);
        print("Last Day is" + format.format(day.getTime()));

        Calendar newDay = Calendar.getInstance();
        newDay.setFirstDayOfWeek(Calendar.MONDAY);
        print("New day is: " + format.format(newDay.getTime()));
        newDay.add(Calendar.DAY_OF_WEEK, toAdd);
        print("New last day is " + format.format(newDay.getTime()));
    }
    void print(String val) {
        System.out.println(val);
    }
}