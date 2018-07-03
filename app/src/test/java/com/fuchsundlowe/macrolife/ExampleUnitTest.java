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
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void myTest() {

        Calendar mk1 = Calendar.getInstance();
        long[] result = returnStartAndEndTimesForDay(mk1);
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-YYYY-HH:mm");
        String r1 = f.format(new Date(result[0]));
        String r2 = f.format(new Date(result[1]));

        System.out.print(r1);
        System.out.print("\n");
        System.out.print(r2);
        System.out.print("\nDistance is: ");
        System.out.print(result[1] - result[0]);

    }

    private long[] returnStartAndEndTimesForDay(Calendar day) {
        Calendar dayToWorkWith = (Calendar) day.clone();
        dayToWorkWith.set(Calendar.HOUR_OF_DAY,0);
        dayToWorkWith.set(Calendar.MINUTE,0);
        dayToWorkWith.set(Calendar.SECOND,0);
        dayToWorkWith.set(Calendar.MILLISECOND,0);
        long startTimeStamp = dayToWorkWith.getTimeInMillis();

        dayToWorkWith.set(Calendar.HOUR_OF_DAY, 23);
        dayToWorkWith.set(Calendar.MINUTE, 59);
        dayToWorkWith.set(Calendar.SECOND, 59);
        long endTimeStamp = dayToWorkWith.getTimeInMillis();

        return new long[]{startTimeStamp, endTimeStamp};
    }
}