package com.fuchsundlowe.macrolife.FragmentModels;

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
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

public class DayDisplay_DayView extends Fragment {

    private ViewGroup baseView; // View that holds all others, central View
    private ScrollView chronoViewHolder; // View that shows time and holds tasks
    private ViewGroup reminderView; // View that shows tasks that are not assigned time
    private ChronoView chronoView;
    private Calendar dayWeDisplay;
    private SharedPreferences preferences;
    private BroadcastReceiver broadcastReceiver;

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

        defineLocalBroadcast();
        return baseView;
    }
    private void defineChronoView(Context context) {
        chronoView = new ChronoView(context, dayWeDisplay);
        chronoViewHolder.addView(chronoView);
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
                int taskHashID = intent.getIntExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, 0);
                // Todo: query a database and find the task, then send the task to Bottom bar
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                new IntentFilter(Constants.INTENT_FILTER_GLOBAL_EDIT));
    }

}
