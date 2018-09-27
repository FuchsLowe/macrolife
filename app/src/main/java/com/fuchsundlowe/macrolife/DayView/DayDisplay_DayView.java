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
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class DayDisplay_DayView extends Fragment {

    private ViewGroup baseView; // View that holds all others, central View
    private ScrollView chronoViewHolder; // View that shows time and holds tasks
    private RecyclerView reminderView; // View that shows tasks that are not assigned time
    private RecyclerView.LayoutManager reminderViewLayoutManager;
    private ReminderViewAdapter reminderViewAdapeter;
    private ChronoView chronoView;
    private Calendar dayWeDisplay;
    private SharedPreferences preferences;
    private DataProviderNewProtocol dataMaster;
    private Set<Integer> taskIDs;
    private List<TaskEventHolder> dataHolder;

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
        taskIDs = new HashSet<>();
        dataHolder = new ArrayList<>();

        // MARK: Subscribe to live data:
        // Tasks implementation:
        dataMaster.getTaskThatIntersects(dayWeDisplay).observe(this, new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> objects) {
                // Remove all existing data Tasks
                List<TaskEventHolder> toDelete = new ArrayList<>();
                for (TaskEventHolder object: dataHolder) {
                    if (object.isTask()) {
                        toDelete.add(object);
                    }
                }
                dataHolder.removeAll(toDelete);
                // Wrap the new ones and insert them into dataHolder
                if (objects != null) {
                    for (TaskObject task: objects) {
                        dataHolder.add(new TaskEventHolder(task, null));
                    }
                }
                // insert data to ChronoView:
                chronoView.insertData(dataHolder);
            }
        });
        // Events implementation:
        dataMaster.getEventsThatIntersect(dayWeDisplay).observe(this, new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> events) {
                // Remove all existing data Tasks
                List<TaskEventHolder> toDelete = new ArrayList<>();
                for (TaskEventHolder object: dataHolder) {
                    if (!object.isTask()) {
                        toDelete.add(object);
                    }
                }
                dataHolder.removeAll(toDelete);
                // Wrap the new ones and insert them into dataHolder
                if (events != null) {
                    for (RepeatingEvent event: events) {
                        dataHolder.add(new TaskEventHolder(null,  event));
                    }
                }
                // insert data to ChronoView:
                chronoView.insertData(dataHolder);
            }
        });
        defineLocalBroadcast();
        defineReminderView();
        return baseView;
    }
    public void defineChronoView(Calendar toDisplay) {
        this.dayWeDisplay = toDisplay;
    }
    @Override
    public void onResume() {
        super.onResume();
        final Calendar currentTime = Calendar.getInstance();
        scrollToInChronoView(currentTime.get(Calendar.HOUR_OF_DAY));
        Handler h = new Handler(Looper.getMainLooper());
        Runnable m = new Runnable() {
            @Override
            public void run() {
                scrollToInChronoView(currentTime.get(Calendar.HOUR_OF_DAY));
            }
        };
        h.postDelayed(m, 500);
    }

    // Methods:
    private void scrollToInChronoView(int hour) {
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
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
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
        //LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter); NOTE: Not being used for now...
    }

    // ReminderView Implementation
    void defineReminderView() {
        // Defining the layout Manager
        final GridLayoutManager glm = new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        reminderViewLayoutManager = glm;
        reminderView.setLayoutManager(reminderViewLayoutManager);
        final List<TaskEventHolder> reminders = new ArrayList<>();

        // Setting the adapter
        reminderViewAdapeter = new ReminderViewAdapter();
        reminderView.setAdapter(reminderViewAdapeter);
        // Subscribe to liveData:
        // for Tasks:
        dataMaster.getTasksForRemindersView(dayWeDisplay).observe(this, new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> objects) {
                // Update changes:
                List<TaskEventHolder> toDelete = new ArrayList<>();
                for (TaskEventHolder object: reminders) {
                    if (object.isTask()) {
                        toDelete.add(object);
                    }
                }
                reminders.removeAll(toDelete);
                // Wrapping Tasks into TaskEventHolders and adding them
                if (objects != null) {
                    for (TaskObject task : objects) {
                        reminders.add(new TaskEventHolder(task, null));
                    }
                }
                // define the size of the reminder bar...
                if (reminders.size() > 1) {
                    glm.setSpanCount(2);
                } else {
                    glm.setSpanCount(1);
                }
                reminderViewAdapeter.addNewData(reminders);
            }
        });
        // for Events:
        dataMaster.getEventsForRemindersView(dayWeDisplay).observe(this, new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> events) {
                // Update changes:
                List<TaskEventHolder> toDelete = new ArrayList<>();
                for (TaskEventHolder object: reminders) {
                    if (!object.isTask()) {
                        toDelete.add(object);
                    }
                }
                reminders.removeAll(toDelete);
                // Wrapping Events into TaskEventHolders and adding them:
                if (events != null) {
                    for (RepeatingEvent event: events) {
                        reminders.add(new TaskEventHolder(null, event));
                    }
                }
                // define the size of the reminder bar...
                if (reminders.size() > 1) {
                    glm.setSpanCount(2);
                } else {
                    glm.setSpanCount(1);
                }
                reminderViewAdapeter.addNewData(reminders);
            }
        });
        reminderView.setHasFixedSize(false);

        reminderView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // determine if I should accept this
                        return event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT) ||
                                event.getClipDescription().getLabel().equals(Constants.REPEATING_EVENT);
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DROP:
                        /*
                         * How I accept the drop?
                         * Grab the object,
                         * alter its values
                         * save it
                         */
                        Object dropData = event.getLocalState();
                        if (dropData instanceof TaskObject) {
                            ((TaskObject) dropData).setTimeDefined(TaskObject.TimeDefined.onlyDate);
                            ((TaskObject) dropData).setTaskStartTime(dayWeDisplay);
                            dataMaster.saveTaskObject((TaskObject) dropData);
                        } else if (dropData instanceof RepeatingEvent) {
                            ((RepeatingEvent) dropData).setStartTime(dayWeDisplay);
                            ((RepeatingEvent) dropData).setEndTimeWithReturn(null);
                            dataMaster.saveRepeatingEvent((RepeatingEvent) dropData);
                        } else {
                            return false;
                        }
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                }
                return true;
            }
        });

        /*
         * Scrolling implementation:
         * IF there are any to the right we move
         * wait 3 secs and repeat
         * if there are no to the right anymore we move to first...
         * IF user has touhced the view, I want to remove the option of moving
         */
        final Boolean[] hasViewBeenTouched = {false};
        final Integer[] currentPosition = {1};
        reminderView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                hasViewBeenTouched[0] = true;
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        final Handler main = new Handler(Looper.getMainLooper());
        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (hasViewBeenTouched[0]) {
                    // if view has been tempered with, we stop the execution:
                    t.cancel();
                } else {
                    if (reminderViewAdapeter.getItemCount() > currentPosition[0]) {
                        // Means there are more Views to show
                        currentPosition[0] += 1;
                    } else {
                        currentPosition[0] = 0;
                    }
                    main.post(new Runnable() {
                        @Override
                        public void run() {
                            reminderView.smoothScrollToPosition(currentPosition[0]);
                        }
                    });
                }
            }
        }, 2500, 2500);
    }

}
