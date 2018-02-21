package com.fuchsundlowe.macrolife;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
       Calendar start = Calendar.getInstance();
       Calendar end = Calendar.getInstance();
       Calendar inBetween = Calendar.getInstance();

       start.set(2008,2,3,0,0);
       end.set(2008,2,3,16,11);

       inBetween.set(2009,2,3,4,12);

       System.out.print(checkIfBelongsTimeWise(start,end,inBetween));

    }

    // Returns true if a date falls between start and end time
    private boolean checkIfBelongsTimeWise(Calendar startTime, Calendar endTime, Calendar checkTime) {
        if (checkTime.after(startTime) && checkTime.before(endTime)) {
            return true;
        } else {
            return false;
        }
    }
}