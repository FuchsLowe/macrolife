package com.fuchsundlowe.macrolife.MonthView;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A Fragment that holds months of the year and provides functionality for selecting
 * different months withing a year.
 */
public class TopBarFrag_MonthView extends Fragment {

    private Map<Integer,Button> buttonMap;
    private Calendar yearPresented;
    private LocalBroadcastManager manager;

    public TopBarFrag_MonthView() {
        // Required empty public constructor
    }

    public void defineMe(Calendar yearToPresent) {
        this.yearPresented = (Calendar) yearToPresent.clone();
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View base = inflater.inflate(R.layout.fragment_top_bar_frag__month_view, container, false);

        TextView yearLabel = base.findViewById(R.id.yearLabel_topBar_monthView);
        yearLabel.setText(String.valueOf(yearPresented.get(Calendar.YEAR)));

        buttonMap = new HashMap<>(12);

        buttonMap.put(0, (Button) base.findViewById(R.id.january));
        buttonMap.put(1, (Button) base.findViewById(R.id.february));
        buttonMap.put(2, (Button) base.findViewById(R.id.march));
        buttonMap.put(3, (Button) base.findViewById(R.id.april));
        buttonMap.put(4, (Button) base.findViewById(R.id.may));
        buttonMap.put(5, (Button)  base.findViewById(R.id.june));
        buttonMap.put(6, (Button) base.findViewById(R.id.july));
        buttonMap.put(7, (Button)  base.findViewById(R.id.august));
        buttonMap.put(8, (Button)  base.findViewById(R.id.september));
        buttonMap.put(9, (Button)  base.findViewById(R.id.october));
        buttonMap.put(10, (Button)  base.findViewById(R.id.november));
        buttonMap.put(11, (Button)  base.findViewById(R.id.december));

        manager = LocalBroadcastManager.getInstance(base.getContext());

        defineOnClickListeners();
        // Select Current Month:
        selectMonth(yearPresented.get(Calendar.MONTH));

        // Broadcasts:
        defineBroadcastListener();
        return base;
    }

    private void defineOnClickListeners(){
        // Defining the click listener:
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer tagVal = Integer.valueOf(v.getTag().toString());
                if (v.getTag().toString() == null || v.getTag().toString().length() == 0) {
                    throw new Error("Could not get value out of day");
                    // TODO REmove from production...
                };
                selectMonth(tagVal);
                newMonthSelectedBroadcast(yearPresented);
            }
        };

        // Assigning the Listener:
        for (Button mButton: buttonMap.values()) {
            mButton.setOnClickListener(buttonClickListener);
        }
    }
    // Deselects the current month and selects new one
    private void selectMonth(int newMonth) {
        for (int i = 0; i<12; i++) {
            if ((newMonth == i)) {
                buttonMap.get(i).setBackgroundColor(Color.DKGRAY);
            } else {
                buttonMap.get(i).setBackgroundColor(Color.LTGRAY);
            }
        }
        // Changing the month:
        yearPresented.set(Calendar.MONTH, newMonth);
    }
    // Local Broadcast Listener:
    /*
     * Listens for changes in month selection broadcasts, and if selection reefers to this year,
     * it will change selection...
     */
    private void defineBroadcastListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_NEW_DATE_SET);
        manager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_FILTER_NEW_DATE_SET)) {
                    long timeValue =  intent.getLongExtra(Constants.INTENT_FILTER_DATE_VALUE, -1);
                    if (timeValue > 1) {
                        Calendar newTime = Calendar.getInstance();
                        newTime.setTime(new Date(timeValue));
                        if (newTime.get(Calendar.YEAR) == yearPresented.get(Calendar.YEAR)) {
                            selectMonth(newTime.get(Calendar.MONTH));
                        }
                    }
                }
            }
        }, filter);
    }
    // Sends broadcast of new month being selected from this fragment:
    private void newMonthSelectedBroadcast(Calendar newMonth) {
        Intent report = new Intent(Constants.INTENT_FILTER_NEW_MONTH_SELECTED);
        report.putExtra(Constants.INTENT_FILTER_DATE_VALUE, newMonth.getTimeInMillis());
        manager.sendBroadcast(report);
    }
}
