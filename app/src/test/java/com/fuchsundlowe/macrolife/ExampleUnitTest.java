package com.fuchsundlowe.macrolife;

import android.util.Log;

import com.fuchsundlowe.macrolife.FragmentModels.DatePickerFragment;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Before
    public void initData() {

    }

    @Test
    public void checkDatabase()  {
        Calendar mk1 = Calendar.getInstance();
        mk1.set(Calendar.DAY_OF_WEEK, 1);

        String stop = "Anything will do";

    }
    @After
    public void closeData() {

    }
}