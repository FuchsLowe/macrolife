package com.fuchsundlowe.macrolife.WeekView;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

/**
 * The functional part of the WeekView Display... Holder of all things.
 */
public class WeekDisplay_WeekView extends Fragment {

    private ViewGroup baseView;
    private DataProviderNewProtocol dataProvider;
    /*
     * TODO: How should I store the day-reference of week here?
     *
     * Maybe It can be a Calendar instance that holds the first day of week... Like having it
     * be Monday or Sunday... And then based on that it searches for data and presents it...
     */
    private final int DAYS_IN_WEEK = 7;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_week_display__week_view, container, false);
        dataProvider = LocalStorage.getInstance(container.getContext());

        return baseView;
    }

    /*
     * TODO: How should I obtain the data for displaying into elements?
     *
     * A one stream system where one live data is received and then distributed to containers that
     * present the tasks further.
     */

    /*
     * TODO: Define the data insertion class that will re-distribute data to children
     *
     *
     */
    // This class creates time capsules from Tasks
    private TimeCapsule[] createTimeCapsules(TaskObject[] tasks) {
        //TODO: IMPLEMENT!
        return null;
    };

    /*
     * A holder class designed to hold instances of start and end times of tasks that will be further
     * passed as array from WeekDisplay_WeekView to WeekTask for usage in defining the look and feel
     * of WeekTask instances.
     */
    protected class TimeCapsule {

        Calendar startTime, endTime;

        TimeCapsule(Calendar startTime, Calendar endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

}
