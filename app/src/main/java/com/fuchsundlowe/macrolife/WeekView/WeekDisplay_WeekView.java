package com.fuchsundlowe.macrolife.WeekView;


import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;

import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * The functional part of the WeekView Display... Holder of all things.
 */
public class WeekDisplay_WeekView extends Fragment {

    private ViewGroup baseView;
    private LinearLayout listLayout;
    private TextView weekNumber, weekDescription;
    private Calendar weekIPresent;
    private DataProviderNewProtocol dataMaster;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataMaster = LocalStorage.getInstance(this.getContext());
        // Inflate the layout for this fragment
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_week_display__week_view, container, false);
        listLayout = baseView.findViewById(R.id.listView_weekDisplay);

        weekNumber = baseView.findViewById(R.id.weekNumber_weekView);
        String weekNumberText = getString(R.string.week) + " " + weekIPresent.get(Calendar.WEEK_OF_YEAR);
        weekNumber.setText(weekNumberText);

        weekDescription = baseView.findViewById(R.id.weekDescription_weekView);
        DateFormat daysFormatter = DateFormat.getDateInstance(DateFormat.LONG);
        String firstDay = daysFormatter.format(weekIPresent.getTime());
        Calendar lastDayTime = (Calendar) weekIPresent.clone();
        lastDayTime.add(Calendar.DAY_OF_WEEK, 6);
        String lastDay = daysFormatter.format(lastDayTime.getTime());
        String wholeText = firstDay + " " +  getString(R.string.to) + " " + lastDay;
        weekDescription.setText(wholeText);
        return baseView;
    }
    @Override
    public void onResume() {
        super.onResume();
        initiateData();
        defineOnClickEvents();
    }
    // Calendar is assigned with first day of the week
    public void defineMe(Calendar weekIRepresent) {
        this.weekIPresent = weekIRepresent;
    }
    // Used to create appropriate calendar events and pass them to children:
    private void initiateData() {
        int daysInAWeek = 7;
        listLayout.removeAllViews();
        for (int i = 0; i < daysInAWeek; i++) {
            final DayHolder_WeekView mDay = new DayHolder_WeekView(baseView.getContext());
            // Add it to view hierarchy
            listLayout.addView(mDay);
            // Assign the appropriate value
            Calendar dayToInsert = (Calendar) weekIPresent.clone();
            dayToInsert.add(Calendar.DAY_OF_WEEK, i);
            mDay.defineMe(dayToInsert);
            // The real insertion of data, one that gets constant updates...
            final List<TaskEventHolder> dataHolder = new ArrayList<>();
            dataMaster.getTasksForWeekView(dayToInsert).observe(this, new Observer<List<TaskObject>>() {
                @Override
                public void onChanged(@Nullable List<TaskObject> objects) {
                    List<TaskEventHolder> toDelete = new ArrayList<>();
                    for (TaskEventHolder object: dataHolder) {
                        if (object.isTask()) {
                            toDelete.add(object);
                        }
                    }
                    dataHolder.removeAll(toDelete);
                    // Now you gotta insert them
                    if (objects!= null) {
                        for (TaskObject task: objects) {
                            dataHolder.add(new TaskEventHolder(task, null));
                        }
                    }
                    mDay.dataInsertion(dataHolder);
                }
            });
            dataMaster.getEventsForWeekView(dayToInsert).observe(this, new Observer<List<RepeatingEvent>>() {
                @Override
                public void onChanged(@Nullable List<RepeatingEvent> events) {
                    List<TaskEventHolder> toDelete = new ArrayList<>();
                    for (TaskEventHolder object: dataHolder) {
                        if (!object.isTask()) {
                            toDelete.add(object);
                        }
                    }
                    dataHolder.removeAll(toDelete);
                    // Insertion:
                    if (events != null) {
                        for (RepeatingEvent event : events) {
                            dataHolder.add(new TaskEventHolder(null, event));
                        }
                    }

                    mDay.dataInsertion(dataHolder);
                }
            });
        }
    }
    private void defineOnClickEvents() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRecommendationBroadcast();
            }
        };
        baseView.setOnClickListener(listener);
        listLayout.setOnClickListener(listener);
    }
    private void callRecommendationBroadcast() {
        Intent recommendationIntent = new Intent(Constants.INTENT_FILTER_RECOMMENDATION);
        LocalBroadcastManager.getInstance(baseView.getContext()).sendBroadcast(recommendationIntent);
    }
}
