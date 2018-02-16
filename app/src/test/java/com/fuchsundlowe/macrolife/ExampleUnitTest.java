package com.fuchsundlowe.macrolife;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        Calendar day = Calendar.getInstance();

        SimpleDateFormat toDay = new SimpleDateFormat("EEEE");

        SimpleDateFormat toDate = new SimpleDateFormat("MMMM dd, yyyy");

        System.out.print(toDay.format(day.getTime()));
        System.out.print("\n");
        System.out.print(toDate.format(day.getTime()));
    }
}