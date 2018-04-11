package com.fuchsundlowe.macrolife;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    String[] arry;
    String txt;

    @Before
    public void doShit() {
        arry = new String[4];
        txt = "I Love BUtter Cookies Jack... God Dam it man!";
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        for (String t: txt.split(" ", 4)) {
            Log.d("Lengtof Arry: ", "" + arry.length);
        }

    }
}
