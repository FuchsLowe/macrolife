package com.fuchsundlowe.macrolife.FragmentModels;

import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.CustomViews.ChronoView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.ArrayList;
import java.util.Calendar;

public class DayDisplay_DayView extends Fragment {

    private ViewGroup baseView; // View that holds all others, central View
    private ScrollView chronoViewHolder; // View that shows time and holds tasks
    private ViewGroup reminderView; // View that shows tasks that are not assigned time
    private ChronoView chronoView;
    private Calendar dayWeDisplay;
    private SharedPreferences preferences;
    private BroadcastReceiver broadcastReceiver;
    private DataProviderNewProtocol dataMaster;

    //Lifecycle Methods:
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        baseView = (ViewGroup) inflater.inflate(R.layout.day_view_central_bar, container, false);
        this.reminderView = baseView.findViewById(R.id.reminders_view);
        this.chronoViewHolder = baseView.findViewById(R.id.chronoView);
        // Init the Views
        defineChronoView(getContext());

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        dataMaster = LocalStorage.getInstance(getContext());

        defineLocalBroadcast();
        return baseView;
    }
    private void defineChronoView(Context context) {
        chronoView = new ChronoView(context, dayWeDisplay);
        chronoViewHolder.addView(chronoView);

        // Subscribe to live data:
        dataMaster.getEventsThatIntersect(dayWeDisplay).observe(this, new Observer<ArrayList<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable ArrayList<RepeatingEvent> repeatingEvents) {
                chronoView.setData(null, repeatingEvents);
            }
        });
        dataMaster.getTaskThatIntersects(dayWeDisplay).observe(this, new Observer<ArrayList<TaskObject>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TaskObject> taskObjects) {
                chronoView.setData(taskObjects, null);
            }
        });

        Calendar currentTime = Calendar.getInstance();
        scrollTo(currentTime.get(Calendar.HOUR_OF_DAY));
    }
    public void init(Calendar toDisplay) {
        this.dayWeDisplay = toDisplay;
    }

    // Methods:
    private void scrollTo(int hour) {
        int scrollAmount;
        if (preferences != null) {
            scrollAmount = preferences.getInt(Constants.HOUR_IN_PIXELS, 108) * hour;
        } else {
            preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            scrollAmount =  preferences.getInt(Constants.HOUR_IN_PIXELS, 108) * hour;
        }
            chronoViewHolder.scrollTo(0, scrollAmount);

    }
    void defineLocalBroadcast() {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction() == Constants.INTENT_FILTER_GLOBAL_EDIT) {
                    // we grab the ID"s and we find the task and then we send it to bottom bar
                    int taskId = intent.getIntExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, 0);

                } else if (intent.getAction() == Constants.INTENT_FILTER_NEW_TASK) {
                    // create a new task with given loations
                    Calendar taskStartTime = Calendar.getInstance();
                    taskStartTime.setTimeInMillis(
                            intent.getIntExtra(Constants.INTENT_FILTER_FIELD_START_TIME, 0)
                    );

                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.INTENT_FILTER_GLOBAL_EDIT);
        intentFilter.addAction(Constants.INTENT_FILTER_NEW_TASK);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                intentFilter);
    }

}
