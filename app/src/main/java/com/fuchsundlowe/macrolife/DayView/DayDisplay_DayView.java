package com.fuchsundlowe.macrolife.DayView;

import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.DayView.ChronoView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;
import java.util.List;
import java.util.TimerTask;

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

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        dataMaster = LocalStorage.getInstance(getContext());

        chronoView = new ChronoView(getContext(), dayWeDisplay);
        chronoViewHolder.addView(chronoView);

        // Subscribe to live data:
        dataMaster.getEventsThatIntersect(dayWeDisplay).observe(this, new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> repeatingEvents) {
                chronoView.setData(null, repeatingEvents);

            }
        });

        dataMaster.getTaskThatIntersects(dayWeDisplay).observe(this, new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> taskObjects) {
                chronoView.setData(taskObjects, null);
            }
        });

        defineLocalBroadcast();
        return baseView;
    }
    public void defineChronoView(Calendar toDisplay) {
        this.dayWeDisplay = toDisplay;
    }

    @Override
    public void onResume() {
        super.onResume();
        final Calendar currentTime = Calendar.getInstance();
        scrollTo(currentTime.get(Calendar.HOUR_OF_DAY));
        Handler h = new Handler(Looper.getMainLooper());
        Runnable m = new Runnable() {
            @Override
            public void run() {
                scrollTo(currentTime.get(Calendar.HOUR_OF_DAY));
            }
        };
        h.postDelayed(m, 500);
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

                } else if (intent.getAction() == Constants.INTENT_FILTER_NEW_TASK) {

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
