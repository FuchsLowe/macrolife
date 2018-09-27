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

        Calendar one = Calendar.getInstance();
        Calendar two = Calendar.getInstance();
        two.add(Calendar.MONTH, 2);

        long positive = distanceInDays(one,two);
        long negative = distanceInDays(two, one);

        String hint = "Where is the wisdom now Satan?";
    }

    private long distanceInDays(Calendar start, Calendar end) {
        DateTime mk1 = new DateTime(start.getTimeInMillis());
        DateTime mk2 = new DateTime(end.getTimeInMillis());

        return Days.daysBetween(mk1, mk2).getDays();
    }
}