package com.fuchsundlowe.macrolife.MonthView;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.BottomBar.RecommendationBar;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;
import java.util.Date;

// Month Activity and main coordinator of Month View in General
public class MonthView extends FragmentActivity implements BottomBarCommunicationProtocol{

    private MonthDataControllerProtocol model;
    private ViewPager topBar, centralBar;
    private ViewGroup bottomBar;
    private Calendar currentDateDisplayed;
    private Calendar initialDay;

    private int MAX_YEARS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);

        initialDay = Calendar.getInstance();
        long tempLong;
        if (savedInstanceState != null) {
            tempLong = savedInstanceState.getLong(Constants.DAY_TO_DISPLAY, initialDay.getTimeInMillis());
        } else {
            tempLong = initialDay.getTimeInMillis();
        }
        initialDay.setTime(new Date(tempLong));
        currentDateDisplayed = (Calendar) initialDay.clone();
        model = new MonthViewModel(this, this);

        topBar = findViewById(R.id.topBar_MonthView);
        topBar.setAdapter( new TopBarAdapter(getSupportFragmentManager()));
        topBar.setCurrentItem(MAX_YEARS /2);

        centralBar = findViewById(R.id.centerBar_MonthView);
        centralBar.setAdapter(new CentralBarAdapter(getSupportFragmentManager()));
        centralBar.setCurrentItem(MAX_YEARS *6); // since MaxYears * 12 / 2 gets simplified.

        definePageChangeListeners();

        bottomBar = findViewById(R.id.bottomBar_MonthView);

        // Initiate with default fragment:
        provideRecommendationBar();

        defineLocalBroadcastReceiver();

        // TEST COLORS:
        topBar.setBackgroundColor(Color.GREEN);
        centralBar.setBackgroundColor(Color.DKGRAY);
    }

    // TODO: Mod done? Like click on side to provide data?

    // View Pager Listeners
    private void definePageChangeListeners() {
        topBar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar newDay = (Calendar) initialDay.clone();
                int newPosition = MAX_YEARS /2 -position;
                newDay.add(Calendar.YEAR, newPosition);
                if (currentDateDisplayed.get(Calendar.YEAR) > newDay.get(Calendar.YEAR)) {
                    // we moved back
                    centralBar.setCurrentItem(centralBar.getCurrentItem() - 12, true);
                } else if (currentDateDisplayed.get(Calendar.YEAR) < newDay.get(Calendar.YEAR)) {
                    // we moved forward a year
                    centralBar.setCurrentItem(centralBar.getCurrentItem() + 12, true);
                }
                currentDateDisplayed = newDay;
                reportNewDateSelected(newDay);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        centralBar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Calendar newDay = (Calendar) initialDay.clone();
                int newPosition = MAX_YEARS *6 -position;
                newDay.add(Calendar.MONTH, newPosition);
                if (currentDateDisplayed.get(Calendar.YEAR) > newDay.get(Calendar.YEAR)) {
                    topBar.setCurrentItem(topBar.getCurrentItem() -1, true);
                } else if (currentDateDisplayed.get(Calendar.YEAR) < newDay.get(Calendar.YEAR)) {
                    topBar.setCurrentItem(topBar.getCurrentItem() +1, true);
                }
                currentDateDisplayed = newDay;
                reportNewDateSelected(newDay);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    // BottomBar transitions:
    private void provideEditHolder(TaskEventHolder holderToEdit) {
        TaskObject task = null;
        RepeatingEvent event = null;
        if (holderToEdit.isTask()) {
            task = holderToEdit.getTask();
        } else {
            event = holderToEdit.getEvent();
        }
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        EditTaskBottomBar editTask = new EditTaskBottomBar();
        transaction.replace(bottomBar.getId(), editTask);
        if (!isFinishing()) {
            transaction.commit();
            editTask.displayEditTask(EditTaskBottomBar.EditTaskState.editTask, task, event,this, bottomBar.getWidth());
        }
    }
    private void provideListFor(Calendar date) {
        if (date != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            TaskDisplayer taskDisplayer = new TaskDisplayer();
            transaction.replace(bottomBar.getId(), taskDisplayer);
            if (!isFinishing()) {
                transaction.commit();
                int dayOfYear = date.get(Calendar.DAY_OF_YEAR);
                int year = date.get(Calendar.YEAR);
                taskDisplayer.defineMe(model.holdersFor(dayOfYear, (short) year), model);
            }
        }
    }
    private void provideRecommendationBar() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RecommendationBar bar = new RecommendationBar();
        transaction.replace(bottomBar.getId(), bar);
        if (!isFinishing()) {
            transaction.commit();
        }
    }

    // Broadcasts:
    private void defineLocalBroadcastReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(topBar.getContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_GLOBAL_EDIT);
        filter.addAction(Constants.INTENT_FILTER_RECOMMENDATION);
        filter.addAction(Constants.INTENT_FILTER_DAY_CLICKED);
        filter.addAction(Constants.INTENT_FILTER_NEW_MONTH_SELECTED);
        manager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_FILTER_GLOBAL_EDIT)) {
                    // Means we need to present editing of bottom bar now:
                    int id = intent.getIntExtra(Constants.INTENT_FILTER_TASK_ID, -1);
                    TaskEventHolder toEdit = model.findAsTask(id);
                    if (toEdit == null) {
                        id = intent.getIntExtra(Constants.INTENT_FILTER_EVENT_ID, -1);
                        toEdit = model.findAsEvent(id);
                    }
                    provideEditHolder(toEdit);
                } else if (intent.getAction().equals(Constants.INTENT_FILTER_RECOMMENDATION)) {
                    // Provide recommendation
                    provideRecommendationBar();
                } else if (intent.getAction().equals(Constants.INTENT_FILTER_DAY_CLICKED)) {
                    // click occurred for specific day so you need to provide a list for bottom bar
                    Calendar date = Calendar.getInstance();
                    date.setTime(new Date(intent.getLongExtra(Constants.INTENT_FILTER_DATE_VALUE, -1)));
                    provideListFor(date);
                } else if (intent.getAction().equals(Constants.INTENT_FILTER_NEW_MONTH_SELECTED)) {
                    // Click on top bar / new month has occurred.
                    long reportLong = intent.getLongExtra(Constants.INTENT_FILTER_DATE_VALUE, -1);
                    if (reportLong > 1) {
                        // Means that we have a credible value
                        Calendar date = Calendar.getInstance();
                        date.setTimeInMillis(reportLong);
                        int newPos = date.get(Calendar.MONTH) - currentDateDisplayed.get(Calendar.MONTH);
                        centralBar.setCurrentItem(newPos, true);
                        currentDateDisplayed = date;
                    }
                }
            }
        }, filter);
    }
    // Reports new Value to TopBar so it can change it month selection:
    private void reportNewDateSelected(Calendar newDate) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(topBar.getContext());
        Intent report = new Intent();
        report.setAction(Constants.INTENT_FILTER_NEW_DATE_SET);
        report.putExtra(Constants.INTENT_FILTER_DATE_VALUE, newDate.getTimeInMillis());
        manager.sendBroadcast(report);
    }

    // Bottom Bar Communication Protocol Implementation:
    public void reportDeleteTask(TaskObject objectToDelete) {
        model.deleteTask(objectToDelete, currentDateDisplayed);
        if (currentDateDisplayed != null) {
            provideListFor(currentDateDisplayed);
        } else {
            provideRecommendationBar();
        }
    }
    public void reportDeleteEvent(RepeatingEvent eventToDelete) {
        model.deleteEvent(eventToDelete, currentDateDisplayed);
        if (currentDateDisplayed != null) {
            provideListFor(currentDateDisplayed);
        } else {
            provideRecommendationBar();
        }
    }

    // Top Bar Fragment Implementation:
    private class TopBarAdapter extends FragmentStatePagerAdapter {

        TopBarAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TopBarFrag_MonthView frag = new TopBarFrag_MonthView();
            Calendar toPass = (Calendar) initialDay.clone();
            int diff = MAX_YEARS /2 - position;
            toPass.add(Calendar.YEAR, diff);
            frag.defineMe(toPass);
            return frag;
        }

        @Override
        public int getCount() {
            return MAX_YEARS;
        }
    }

    // Central Bar Fragment Implementation:
    private class CentralBarAdapter extends FragmentStatePagerAdapter {

        CentralBarAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            CentralBarFrag_MonthView frag = new CentralBarFrag_MonthView();
            Calendar toPass = (Calendar) initialDay.clone();
            int diff = MAX_YEARS *6 - position;
            toPass.add(Calendar.MONTH, diff);
            frag.defineMe(toPass);
            return frag;
        }

        @Override
        public int getCount() {
            return MAX_YEARS * 12;
        }
    }

}
