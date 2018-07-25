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

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private ArrayList<TaskObject.Mods> allMods;
    private ArrayList<TaskObject.Mods> acceptableMods;
    String mods;

    void defien() {
        mods = "";
        allMods = new ArrayList<>();
        acceptableMods = new ArrayList<>();
        acceptableMods.add(TaskObject.Mods.repeating);
        acceptableMods.add(TaskObject.Mods.repeatingMultiValues);
        acceptableMods.add(TaskObject.Mods.note);
    }

    @Test
    public void myTest() {
        defien();
        Set<Integer> bob = new HashSet<>();
        bob.


        String Jacka ="Jackal hasnaj";
    }


    public void addMod(TaskObject.Mods modToAdd) {
        if (acceptableMods.contains(modToAdd)) {
            if (!allMods.contains(modToAdd)) {
                allMods.add(modToAdd);

                // This implementation prevents us from having both mods because they are mutually exclusive
                if (modToAdd == TaskObject.Mods.repeating) {
                    removeAMod(TaskObject.Mods.repeatingMultiValues);
                } else if (modToAdd == TaskObject.Mods.repeatingMultiValues) {
                    removeAMod(TaskObject.Mods.repeating);
                }
            }
            updateMods();
        }
    }
    public void removeAMod(TaskObject.Mods modToRemove) {
        allMods.remove(modToRemove);
        updateMods();
    }

    private void updateMods() {
        mods = ""; // We clean the mods
        for (TaskObject.Mods mod : allMods) {
            mods+= "\n"+ mod.toString();
        }
    }

}