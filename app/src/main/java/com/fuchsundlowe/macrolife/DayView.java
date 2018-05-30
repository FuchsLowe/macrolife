package com.fuchsundlowe.macrolife;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.FragmentModels.DateDisplay_DayView;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
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
public class DayView extends FragmentActivity implements DayViewTopFragmentCallback {

    private DataProviderProtocol dataMaster;
    private Calendar currentDisplayedDay;
    private Calendar startPosition;
    private ViewGroup central, bottom;
    private ViewPager dateDisplay, dayDisplay;
    private PagerAdapter dayPageAdapter, datePageAdapter;
    private DayView self;
    private int currentDayPosition, currentDatePosition;
    private ArrayList<DateDisplay_DayView> topBarFragments;

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
        dataMaster = StorageMaster.getInstance(this);
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
    }
    public void setNewSelectedDay(Calendar newSelectedDay) {
        /*
         * Here We receive the new day to display
         * we need to get the week and select the day to display from that week
         *
         * we need to show that day in bottom bar as well
         */
        getDate(newSelectedDay);
        //sets the week:
        long differenceInWeeks = distanceInWeeks(startPosition, newSelectedDay);
        dateDisplay.setCurrentItem((int) (52 + differenceInWeeks),
                true);

        long differenceInDays = distanceInDays(startPosition, newSelectedDay);
        dayDisplay.setCurrentItem((int) (365 + differenceInDays), true);

        currentDisplayedDay = newSelectedDay;
    }
    private long distanceInWeeks(Calendar start, Calendar end) {
        long difference = start.getTimeInMillis() - end.getTimeInMillis();

        return difference / (1000 * 3600 * 24 * 7);
    }
    private long distanceInDays(Calendar start, Calendar end) {
        long difference = start.getTimeInMillis() - end.getTimeInMillis();

        return difference / (1000 * 3600 * 24);
    }

    // Page Transformers
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
    private class TopPageAdapter extends FragmentStatePagerAdapter {
        final int NUMBER_OF_WEEKS = 104; // good for scrolling up to a year in both directions

        public TopPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            DateDisplay_DayView newFragment = new DateDisplay_DayView();
            Calendar toPass = (Calendar) currentDisplayedDay.clone();
            toPass.roll(Calendar.WEEK_OF_YEAR,position - 52);
            calendarDatesHolder.add(toPass);
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
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
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
        Log.d("Date Sent:"," " + value );
        return  value;
    }
}
