package com.fuchsundlowe.macrolife.DayView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import com.fuchsundlowe.macrolife.BottomBar.RecommendationBar;
import com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DayViewTopFragmentCallback;
import com.fuchsundlowe.macrolife.R;
import org.joda.time.DateTime;
import org.joda.time.Days;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.util.Calendar.YEAR;


/**
 * This class provides day view fragment for app. Main features expected here are:
 *  Chronological view of the daily duties.
 *  Requests information to fill in this specific day based on day attribute.
 */
public class DayView extends FragmentActivity implements DayViewTopFragmentCallback, BottomBarCommunicationProtocol {

    private DataProviderNewProtocol dataMaster;
    private Calendar currentDisplayedDay;
    private Calendar startPosition; // The First Day that Activity Presented, the zero day
    private ViewGroup central, bottom;
    private ViewPager dateDisplay, dayDisplay;
    private PagerAdapter  dayPageAdapter, datePageAdapter;
    private DayView self;
    private int currentDayPosition, currentDatePosition;
    private FragmentManager fragmentManager;
    private SharedPreferences preferences;
    private String recommendationBarTag = "RecommendationBar";
    private String editTaskBarTag = "EditTaskBarTag";


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
        // Test Phase
        preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        defineBroadcastReceiver();
        defineDragAndDrop();
    }

    // Date Manipulation & DayViewCallback interface implementation
    public void setNewSelectedDay(Calendar newSelectedDay) {
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Log.d("C5", "new date we received is: " + f.format(newSelectedDay.getTime()));
        // Making sure its not the same day we are trying to point
        /*
        if (!(newSelectedDay.get(Calendar.YEAR) == currentDisplayedDay.get(Calendar.YEAR)
                &&
            newSelectedDay.get(Calendar.DAY_OF_YEAR) == currentDisplayedDay.get(Calendar.DAY_OF_YEAR))) {
            */
            // Working on DateDisplay:

            // This is used to change selected value in Date Buttons
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment instanceof DateDisplay_DayView) {
                    if (((DateDisplay_DayView) fragment).getWeekWeDisplay().get(Calendar.WEEK_OF_YEAR)
                            == newSelectedDay.get(Calendar.WEEK_OF_YEAR)) {
                        // We need to select the day now withing the week displayed
                        ((DateDisplay_DayView) fragment).changeSelection(newSelectedDay);
                    }
                }
            }
            // Move to new dateDisplay:
            int ammountToMoveWeeks = distanceToMoveWeeks(currentDisplayedDay, newSelectedDay);
            currentDisplayedDay = newSelectedDay;
            dateDisplay.setCurrentItem(dateDisplay.getCurrentItem() +
                    ammountToMoveWeeks, true);

            // Sets the day Display
            long differenceInDays = distanceInDays(startPosition, newSelectedDay);
            Log.d("C5", "Distance in days cal:" + differenceInDays);
            dayDisplay.setCurrentItem((int) (365 + differenceInDays), true);

    }
    /*
     * Only returns values -1, 0, 1. Indicates if two days are in the same week, or newDate is before
     * currentDate ( value returned then is -1 ) or after ( value then is 1 )
     */
    private int distanceToMoveWeeks(Calendar currentDate, Calendar newDate) {
        int toReturn = 0;
        if (currentDate.get(YEAR) ==  newDate.get(YEAR)) {
            if (currentDate.get(Calendar.WEEK_OF_YEAR) == newDate.get(Calendar.WEEK_OF_YEAR)) {
                toReturn= 0;
            } else if (currentDate.get(Calendar.WEEK_OF_YEAR) > newDate.get(Calendar.WEEK_OF_YEAR)) {
                toReturn = -1;
            } else {
                toReturn = 1;
            }
        } else if (currentDate.get(YEAR) > newDate.get(YEAR)) {
            toReturn = -1;
        } else {
            toReturn = 1;
        }
        return toReturn;
    }
    private long distanceInDays(Calendar start, Calendar end) {
        DateTime mk1 = new DateTime(start.getTimeInMillis());
        DateTime mk2 = new DateTime(end.getTimeInMillis());

        return Days.daysBetween(mk1, mk2).getDays();
    }

    // BottomBar communication protocol:
    public void reportDeleteTask(TaskObject object) {
        dataMaster.deleteTask(object);
        provideRecommendationFetcher();
    }

    // Page Transformers
    private class TopPageAdapter extends FragmentStatePagerAdapter {
        final int NUMBER_OF_WEEKS = 104; // good for scrolling up to a year in both directions

        TopPageAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            DateDisplay_DayView newFragment = new DateDisplay_DayView();
            Calendar toPass = (Calendar) startPosition.clone();
            toPass.add(Calendar.WEEK_OF_YEAR,position - 52);
            newFragment.defineTopBar(self, toPass);
            return newFragment;
        }
        @Override
        public int getCount() {
            return NUMBER_OF_WEEKS;
        }
    }
    private class CentralPageAdapter extends FragmentStatePagerAdapter {

        final int NUMBER_OF_DAYS = 730; // or 2 years worth of days
        CentralPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DayDisplay_DayView newFragment = new DayDisplay_DayView();
            Calendar toPass = (Calendar) startPosition.clone();
            toPass.add(Calendar.DAY_OF_YEAR, position - 365);
            newFragment.defineChronoView(toPass);

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
                /*
                 * What does this part do?
                 * It needs to report new day selection and report the newDay update to take place
                 * I need to translate the position argument to Calendar
                 * Is there a way to know the date from accessing the fragment?
                 * No
                 * IF I know the position zero and date, then I can always calculate the
                 * distance between the position zero and new position and add/subtract
                 * so many weeks as there is between them
                 */
                // New implementation:
                int zeroDayValue = datePageAdapter.getCount()/2;  // positional value of startPosition
                int diffInWeeks = position - zeroDayValue;
                Log.d("C4:", "difference reported:" + diffInWeeks);
                Calendar newDate = (Calendar) startPosition.clone();
                newDate.add(Calendar.WEEK_OF_YEAR, diffInWeeks);
                currentDisplayedDay = newDate;
                setNewSelectedDay(newDate);

                /* Old Implementation
                if (position != currentDatePosition) {
                    Calendar newDate = (Calendar) startPosition.clone();
                    newDate.add(Calendar.WEEK_OF_YEAR, position - 52);
                    // TODO: Test -> What date are we passing?
                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-YYYY-ww");
                    Log.d("C4: ", "Displayed Date: " + f.format(currentDisplayedDay.getTime()));
                    Log.d("C4: ", "New Date we calculated: " + f.format(newDate.getTime()));
                    if (newDate.get(YEAR) == currentDisplayedDay.get(YEAR)
                            &&
                            newDate.get(Calendar.WEEK_OF_YEAR) == currentDisplayedDay.get(Calendar.WEEK_OF_YEAR)) {
                        // Do nothing
                    } else {
                        setNewSelectedDay(newDate);
                        currentDatePosition = position;
                    }
                }
                */
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
               if (position != currentDayPosition) {
                   Calendar newDate = (Calendar) startPosition.clone();
                   newDate.add(Calendar.DAY_OF_YEAR, position - 365);
                   setNewSelectedDay(newDate);
                   currentDayPosition = position;
               }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    // BottomBar:
    private void provideRecommendationFetcher() {
        // Check if current tab displayed is recommendationBar, if it is we skip it...
        if (bottom.getTag() == null || !bottom.getTag().equals(recommendationBarTag)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            RecommendationBar recommendationBar = new RecommendationBar();
            transaction.replace(bottom.getId(), recommendationBar);
            if (!isFinishing()) {
                transaction.commit();
                bottom.setTag(recommendationBarTag);
            }
        } else {
            // Check if there is any
        }
    }
    private void provideEditTask(TaskObject taskBeingEdited) {
        // Implementation of Fragment Transaction
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        EditTaskBottomBar editTask = new EditTaskBottomBar();
        transaction.replace(bottom.getId(), editTask);
        if (!isFinishing()) {
            transaction.commit();
            bottom.setTag(editTaskBarTag);
            editTask.defineMe(EditTaskBottomBar.EditTaskState.editTask, taskBeingEdited, this, bottom.getWidth());
        }
    }

    // Broadcast management & Outside Event management:
    private void defineBroadcastReceiver() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == Constants.INTENT_FILTER_GLOBAL_EDIT) {
                    // grab the task & insert it into editTasl
                    int hashID = intent.getIntExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, -1);
                    //TaskObject objectEdited = dataMaster.findTaskObjectBy(hashID);
                    TaskObject objectEdited = dataMaster.findTaskObjectBy(hashID);

                    if (objectEdited != null) {
                        provideEditTask(objectEdited);
                    }
                } else if (intent.getAction() == Constants.INTENT_FILTER_NEW_TASK) {
                    Calendar currentTime = Calendar.getInstance();
                    int thirtyMinutesInMilliseconds = 1800000;
                    Calendar startTime = Calendar.getInstance();
                    startTime.setTimeInMillis(
                            intent.getLongExtra(Constants.INTENT_FILTER_FIELD_START_TIME,
                                    currentTime.getTimeInMillis()));
                    Calendar endTime = (Calendar) startTime.clone();
                    endTime.add(Calendar.MILLISECOND, thirtyMinutesInMilliseconds);
                    int newHashID = dataMaster.findNextFreeHashIDForTask();
                    TaskObject newTask = new TaskObject(
                            newHashID,
                            0,
                            0,
                            getResources().getString(R.string.NewTask),
                            currentTime,
                            startTime,
                            endTime,
                            currentTime,
                            TaskObject.CheckableStatus.notCheckable,
                            "",
                            0,
                            0,
                            "",
                            TaskObject.TimeDefined.dateAndTime
                            );
                    dataMaster.saveTaskObject(newTask);
                    provideEditTask(newTask);
                } else if (intent.getAction() == Constants.INTENT_FILTER_RECOMMENDATION) {
                    provideRecommendationFetcher();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENT_FILTER_GLOBAL_EDIT);
        intentFilter.addAction(Constants.INTENT_FILTER_NEW_TASK);
        intentFilter.addAction(Constants.INTENT_FILTER_RECOMMENDATION);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                intentFilter);
    }
    private void defineDragAndDrop() {
        central.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT)
                        ||
                        event.getClipDescription().getLabel().equals(Constants.REPEATING_EVENT)) {
                    // We don't accept these but we need to change the bottom bar to Recommendation fetcher
                    // TODO: Should empty space at least appear if there is no taskPresented to display?
                    provideRecommendationFetcher();
                }
                return false;
            }
        });
    }
}

