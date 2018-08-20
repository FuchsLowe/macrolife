package com.fuchsundlowe.macrolife.WeekView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.BottomBar.RecommendationBar;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;
import java.util.Calendar;


public class WeekView extends AppCompatActivity implements BottomBarCommunicationProtocol {

    private ViewPager centralBar;
    private FrameLayout bottomBar;
    private DataProviderNewProtocol dataProvider;
    private WeekView self;
    private Calendar startDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        dataProvider = LocalStorage.getInstance(this);
        this.self = this;

        centralBar = findViewById(R.id.centralBar_weekView);
        bottomBar = findViewById(R.id.bottomBar_weekView);

        defineBroadcastReceiver();

        // Creates the page adapter:
        startDay = Calendar.getInstance();
        /*
         * Check if there is savedState first, if not then check if there is intent, and if not,
         * then the start value is initiated with current time:
         */
        if (savedInstanceState != null && savedInstanceState.getLong(Constants.DAY_TO_DISPLAY) > 0) {
            startDay.setTimeInMillis(savedInstanceState.getLong(Constants.DAY_TO_DISPLAY));
        } else {
            Long intentValue = getIntent().getLongExtra(Constants.DAY_TO_DISPLAY, Calendar.getInstance().getTimeInMillis());
            startDay.setTimeInMillis(intentValue);
        }
        // Define the first day of the week:
        SharedPreferences preferences = this.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        int firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK, 1);
        startDay.setFirstDayOfWeek(firstDayOfWeek);
        // Page adapter implementation:
        PagerAdapter mAdapter = new CentralPageAdapter(getSupportFragmentManager());
        centralBar.setAdapter(mAdapter);
        centralBar.setCurrentItem(52); // drop it in the middle as we should have 2 yrs worth of scrolling

        provideRecommendationFetcher();
    }
    /*
     * This broadcast receiver will lead the info from Intent object and present it into bottom
     * part. It will load the data from database to create the bottom bar...
     */
    private void defineBroadcastReceiver() {
        // Defining the action of broadcast receiver, ie what to do when it receives event:
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_FILTER_GLOBAL_EDIT)) {
                    // grab the task & insert it into editTask
                    int hashID = intent.getIntExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, -1);
                    TaskObject objectEdited = dataProvider.findTaskObjectBy(hashID);

                    if (objectEdited != null) {
                        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
                        EditTaskBottomBar editTask = new EditTaskBottomBar();
                        transaction.replace(bottomBar.getId(), editTask);
                        transaction.commitAllowingStateLoss();
                        //bottomBar.setTag(editTaskBarTag);
                        editTask.defineMe(EditTaskBottomBar.EditTaskState.editTask, objectEdited, self, bottomBar.getWidth());
                    }
                } else if (intent.getAction().equals(Constants.INTENT_FILTER_NEW_TASK)) {
                    // So click occurred to create new task... We need to initiate the edit of it
                    TaskObject newTask = (TaskObject) intent.getSerializableExtra(Constants.TASK_OBJECT);
                    if (newTask != null) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        EditTaskBottomBar taskEditor = new EditTaskBottomBar();
                        transaction.replace(bottomBar.getId(), taskEditor);
                        transaction.commit();
                        taskEditor.defineMe(EditTaskBottomBar.EditTaskState.editTask, newTask, self, bottomBar.getWidth());
                    }
                } else if (intent.getAction().equals(Constants.INTENT_FILTER_RECOMMENDATION)) {
                    provideRecommendationFetcher();
                }
            }
        };
        // Defining the filters for broadcast receiver:
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENT_FILTER_NEW_TASK);
        intentFilter.addAction(Constants.INTENT_FILTER_GLOBAL_EDIT);
        intentFilter.addAction(Constants.INTENT_FILTER_RECOMMENDATION);
        // Registering receiver with filters:
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }
    // Provides default bottom implementation of recommendation fetcher.
    private void provideRecommendationFetcher() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        RecommendationBar recommendationBar = new RecommendationBar();
        transaction.replace(bottomBar.getId(), recommendationBar);
        if (!isFinishing()) {
            transaction.commit();
        }
    }

    // Bottom Bar Communication Protocol Implementation:
    @Override
    public void reportDeleteTask(TaskObject objectToDelete) {
        // Here we delete the task from database and set
        dataProvider.deleteTask(objectToDelete);
        provideRecommendationFetcher();
    }
    // Page Adapter in charge of presenting WeekDisplays:
    private class CentralPageAdapter extends FragmentStatePagerAdapter {
        final int NUMBER_OF_WEEKS = 104;

        CentralPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WeekDisplay_WeekView newFragment = new WeekDisplay_WeekView();
            // Calculate the new week number to present:
            int offsetFromStart = position - NUMBER_OF_WEEKS/2 ;
            Calendar newValueToPresent = (Calendar) startDay.clone();
            newValueToPresent.add(Calendar.WEEK_OF_YEAR, offsetFromStart);
            // Set the date to be the first day of the week:
            int firstDayOfTheWeek = newValueToPresent.getFirstDayOfWeek();
            newValueToPresent.set(Calendar.DAY_OF_WEEK, firstDayOfTheWeek);
            // initiate fragment with new value:
            newFragment.defineMe(newValueToPresent);
            return newFragment;
        }

        @Override
        public int getCount() {
            return NUMBER_OF_WEEKS;
        }
    }
}
