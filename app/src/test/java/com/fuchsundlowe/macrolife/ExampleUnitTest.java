package com.fuchsundlowe.macrolife;

import com.fuchsundlowe.macrolife.DataObjects.Constants;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.ReadableInstant;
import org.joda.time.Weeks;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
        // Chnages
        mk2.add(Calendar.WEEK_OF_YEAR, -52);
        //Chnages edn
        distanceInWeeks(mk1, mk2);

    }

    private long distanceInWeeks(Calendar start, Calendar end) {
        long distance = 0;
        int firstDayOfWeek = 1;
        Calendar mk1 = (Calendar) start.clone();
        Calendar mk2 = (Calendar) end.clone();

        if (start.before(end)) {
            if (firstDayOfWeek == 1) {
                mk1.add(Calendar.DAY_OF_WEEK, 7 - mk1.get(Calendar.DAY_OF_WEEK));
            } else if ( firstDayOfWeek == 2) {
                mk1.add(Calendar.DAY_OF_WEEK, 8 - mk1.get(Calendar.DAY_OF_WEEK));
            }
            while (mk1.before(mk2)) {
                distance += 1;
                mk1.add(Calendar.DAY_OF_YEAR, 7);
            }
            distance -=1;
        } else {
            if (firstDayOfWeek == 1) {
                mk2.add(Calendar.DAY_OF_WEEK, 7 - mk1.get(Calendar.DAY_OF_WEEK));
            } else if ( firstDayOfWeek == 2) {
                mk2.add(Calendar.DAY_OF_WEEK, 8 - mk1.get(Calendar.DAY_OF_WEEK));
            }
            while (mk2.before(mk1)) {
                distance += 1;
                mk2.add(Calendar.DAY_OF_YEAR, 7);
            }

            distance *= -1;
        }
        return distance;
    }
}