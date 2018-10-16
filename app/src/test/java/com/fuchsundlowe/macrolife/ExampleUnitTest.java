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
import org.mockito.exceptions.misusing.CannotVerifyStubOnlyMock;

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
      Calendar mk1 = Calendar.getInstance();
      Calendar mk2 = Calendar.getInstance();

      mk1.set(1970,1,1,0 ,0,0);
      mk1.set(Calendar.MILLISECOND, 0);
      mk2.set(1970, 1,2,0,0,0);
      mk2.set(Calendar.MILLISECOND, 0);
      long dist = mk1.getTimeInMillis() -  mk2.getTimeInMillis();
      distanceInDays(mk1, mk2);

    }

    private long distanceInDays(Calendar start, Calendar end) {
       return -1;
    }

    private long nDist(Calendar one, Calendar two) {
        if (one.get(YEAR) == two.get(YEAR)) {
            return one.get(Calendar.DAY_OF_YEAR ) - two.get(Calendar.DAY_OF_YEAR);
        } else if (one.before(two)) {
            if (one.get(YEAR) +1 == two.get(YEAR)) {
                // Consider if this year has 365 or 366 days
                if (one.get(Calendar.DAY_OF_YEAR) == one.getActualMaximum(Calendar.DAY_OF_YEAR)) {
                    if (two.get(Calendar.DAY_OF_YEAR) == 1) {
                        return 1;
                    } else {
                        return 2;
                    }
                } else {
                    return  2;
                }
            } else {
                // more than a year diff
                return 2;
            }
        } else {
            if (one.get(YEAR) -1 ==two.get(YEAR)) {
                if (two.get(Calendar.DAY_OF_YEAR) == two.getActualMaximum(Calendar.DAY_OF_YEAR)) {
                    if (one.get(Calendar.DAY_OF_YEAR)== 1) {
                        return -1;
                    } else {
                        return -2;
                    }
                } else {
                    return -2;
                }
            } else {
                // More than a year diff
                return -2;
            }
        }
    }
}