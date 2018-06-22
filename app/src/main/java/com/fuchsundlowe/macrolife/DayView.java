package com.fuchsundlowe.macrolife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.BottomBar.RecommendationBar;
import com.fuchsundlowe.macrolife.CustomViews.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.FragmentModels.DateDisplay_DayView;
import com.fuchsundlowe.macrolife.FragmentModels.DayDisplay_DayView;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DayViewTopFragmentCallback;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * This class provides day view fragment for app. Main features expected here are:
 *  Chronological view of the daily duties.
 *  Requests information to fill in this specific day based on day atribute.
 */
public class DayView extends FragmentActivity implements DayViewTopFragmentCallback, BottomBarCommunicationProtocol {

    private DataProviderNewProtocol dataMaster;
    private Calendar currentDisplayedDay;
    private Calendar startPosition;
    private ViewGroup central, bottom;
    private ViewPager dateDisplay, dayDisplay;
    private PagerAdapter dayPageAdapter, datePageAdapter;
    private DayView self;
    private int currentDayPosition, currentDatePosition;
    private ArrayList<DateDisplay_DayView> topBarFragments;
    private FragmentManager fragmentManager;

    // Life-cycle events:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We are expecting a passed day in form of long to be delivered
        if (savedInstanceState != null) {
            Long day = savedInstanceState.getLong(Constants.DAY_TO_DISPLAY);
            if (day != null) {
                currentDisplayedDay.setTimeInMillis(day);
            } else {
                currentDisplayedDay = Calendar.getInstance();
            }
        } else {
            currentDisplayedDay = Calendar.getInstance();
        }
        startPosition = (Calendar) currentDisplayedDay.clone();

        topBarFragments = new ArrayList<>();

        setContentView(R.layout.day_layout);
        self = this;

        dataMaster = LocalStorage.getInstance(this);

        central = findViewById(R.id.DayView_Central);

        dateDisplay = findViewById(R.id.DateDisplay);
        datePageAdapter = new TopPageAdapter(getSupportFragmentManager());
        dateDisplay.setAdapter(datePageAdapter);
        dateDisplay.setCurrentItem(52); // we drop it in the middle

        dayDisplay = findViewById(R.id.DayDisplay);
        dayPageAdapter = new CentralPageAdapter(getSupportFragmentManager());
        dayDisplay.setAdapter(dayPageAdapter);
        dayDisplay.setCurrentItem(365);

        definePageTransformerCallbacks();

        bottom = findViewById(R.id.DayView_Bottom);
        fragmentManager = getSupportFragmentManager();
        provideRecommendationFetcher();

    }

    // Date Manipulation & DayViewCallback interface implementation
    public void setNewSelectedDay(Calendar newSelectedDay, boolean shouldChangeWeek) {
        currentDisplayedDay = newSelectedDay;

        //sets the week:
        if (shouldChangeWeek) { // makes calculation if it should change week
            long differenceInWeeks = distanceInWeeks(startPosition, newSelectedDay);
            dateDisplay.setCurrentItem((int) (52 + differenceInWeeks),
                    true);
        }
        // Sets the day Display
        long differenceInDays = distanceInDays(startPosition, newSelectedDay);
        dayDisplay.setCurrentItem((int) (365 + differenceInDays), true);
    }
    private long distanceInWeeks(Calendar start, Calendar end) {
        long difference;

        if (start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
            difference = end.get(Calendar.WEEK_OF_YEAR) - start.get(Calendar.WEEK_OF_YEAR);
        } else if (start.before(end)) {
            difference = 52 - start.get(Calendar.WEEK_OF_YEAR) + end.get(Calendar.WEEK_OF_YEAR);
        } else {
            difference = end.get(Calendar.WEEK_OF_YEAR) - 52 - start.get(Calendar.WEEK_OF_YEAR);
        }

        return difference;
    }
    private long distanceInDays(Calendar start, Calendar end) {
        long difference;

        if (start.get(Calendar.YEAR) == end.get(Calendar.YEAR)) {
            difference = end.get(Calendar.DAY_OF_YEAR) - start.get(Calendar.DAY_OF_YEAR);
        } else if (start.before(end)) {
            difference = 365 - start.get(Calendar.DAY_OF_YEAR) + end.get(Calendar.DAY_OF_YEAR);
        } else {
            difference = start.get(Calendar.DAY_OF_YEAR) + 365 - end.get(Calendar.DAY_OF_YEAR);
            difference *= -1;
        }

        //Test clause
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            long chronoDistance = ChronoUnit.DAYS.between(start.toInstant(),end.toInstant());
            if (chronoDistance != difference) {
                throw (new Error("Invalid calculation~!"));
            }
        }

        return difference;
    }

    // BottomBar communication protocol:
    public void reportDeleteTask(TaskObject object) {
        dataMaster.deleteTask(object);
        provideRecommendationFetcher();
    }

    // Page Transformers
    private class TopPageAdapter extends FragmentStatePagerAdapter {
        final int NUMBER_OF_WEEKS = 104; // good for scrolling up to a year in both directions

        public TopPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            DateDisplay_DayView newFragment = new DateDisplay_DayView();
            Calendar toPass = (Calendar) startPosition.clone();
            toPass.add(Calendar.WEEK_OF_YEAR,position - 52);
            calendarDatesHolder.add(toPass); // TODO: part of test
            newFragment.defineTopBar(self, toPass);
            topBarFragments.add(newFragment);
            return newFragment;
        }
        @Override
        public int getCount() {
            return NUMBER_OF_WEEKS;
        }
    }
    private class CentralPageAdapter extends FragmentStatePagerAdapter {

        final int NUMBER_OF_DAYS = 730; // or 2 years worth of days
        public CentralPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DayDisplay_DayView newFragment = new DayDisplay_DayView();
            // TODO: pass info to define it.

            return newFragment;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_DAYS;
        }
    }
    private void definePageTransformerCallbacks() {
        dateDisplay.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentDatePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dayDisplay.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentDayPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    // BottomBar:
    private void provideRecommendationFetcher() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        RecommendationBar recommendationBar = new RecommendationBar();
        transaction.replace(bottom.getId(), recommendationBar);
        transaction.commit();
    }
    private void provideEditTask(TaskObject taskBeingEdited) {
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        EditTaskBottomBar editTask = new EditTaskBottomBar();
        editTask.setState(EditTaskBottomBar.EditTaskState.editTask, taskBeingEdited, this);
        transaction.replace(bottom.getId(), editTask);
        transaction.commit();
    }


    // Temporary methods and stuff:
    ArrayList<Calendar> calendarDatesHolder = new ArrayList<>(40);
    String defineCalendars() {
        StringBuilder toReturn = new StringBuilder();
        for (Calendar type : calendarDatesHolder) {
            toReturn.append(getDate(type));
            toReturn.append("\n");
        }
        return toReturn.toString();
    }
    private String getDate(Calendar val) {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String value = format.format(val.getTime());
        return  value;
    }

}
