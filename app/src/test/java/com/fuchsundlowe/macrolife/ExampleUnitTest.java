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
        Calendar timeNow = Calendar.getInstance();
        Calendar endTime, startTime;

        int TIMES_TO_REPEAT = 250;

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();
        for (int i = -1; i>-TIMES_TO_REPEAT; i--) {
            endTime.add(Calendar.WEEK_OF_YEAR, i);
            if (distanceToMoveWeeks(startTime, endTime) != -1) {
                throw new AssertionError("Value is wrong for -1");
            }else {
                System.out.print("WE PASSED TEST for i = " + i + "\n");
            }
        }
        endTime = Calendar.getInstance();

        for (int i = 0; i < TIMES_TO_REPEAT; i++) {
            endTime.add(Calendar.WEEK_OF_YEAR, 0);
            if (distanceToMoveWeeks(startTime, endTime) != 0) {
                throw new AssertionError("Value is wrong for 0");
            }else {
                System.out.print("WE PASSED TEST for i = " + i + "\n");
            }
        }

        endTime = Calendar.getInstance();
        for (int i = 1; i<TIMES_TO_REPEAT; i++) {
            endTime.add(Calendar.WEEK_OF_YEAR, i);
            if (distanceToMoveWeeks(startTime, endTime) != 1) {
                throw new AssertionError("Value is wrong for 1");
            } else {
                System.out.print("WE PASSED TEST for i = " + i + "\n") ;
            }
        }
    }
    int distanceToMoveWeeks(Calendar currentDate, Calendar newDate) {
        int toReturn = 0;
        if (currentDate.get(YEAR) ==  newDate.get(YEAR)) {
            if (currentDate.get(Calendar.WEEK_OF_YEAR) == newDate.get(Calendar.WEEK_OF_YEAR)) {
                toReturn= 0;
            } else if (currentDate.get(Calendar.WEEK_OF_YEAR) > newDate.get(Calendar.WEEK_OF_YEAR)) {
                toReturn = -1;
            } else {
                toReturn = 1;
            }
        } else if (currentDate.get(YEAR) > newDate.get(YEAR)) {
            toReturn = -1;
        } else {
            toReturn = 1;
        }
        return toReturn;
    }
}