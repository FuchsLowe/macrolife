package com.fuchsundlowe.macrolife.WeekView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class WeekTask extends FrameLayout {

    private Context context;
    private DataProviderNewProtocol dataMaster;
    private View baseView;
    private CheckBox checkBox;
    private TextView taskName, masterTaskName;
    private LinearLayout modHolder;
    private HashMap<TaskObject.Mods, ImageView> modMap;
    private LinearLayout timeBar;
    private FrameLayout top_TimeBar, center_TimeBar, bottom_TimeBar;
    private TaskObject taskWePresent;
    private RepeatingEvent[] events; // Array of events we hold to represent
    private WeekDisplay_WeekView.TimeCapsule[] timeCapsules;
    private List<Integer> hashIDRepo;

    // Public constructors:
    public WeekTask(Context context) {
        super(context);
        this.context = context;
        init();
    }
    public WeekTask(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        init();
    }
    public WeekTask(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        init();
    }
    public WeekTask(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

        init();
    }

    // The universal constructor
    private void init() {
        // Defining the views in this layout
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        baseView = inflate(context, R.layout.task_week_view, this);
        checkBox = baseView.findViewById(R.id.checkBox_weekTask);
        taskName = baseView.findViewById(R.id.taskName_weekTask);
        masterTaskName = baseView.findViewById(R.id.masterTaskName_weekTask);
        modHolder = baseView.findViewById(R.id.modHolder_weekTask);
        timeBar = baseView.findViewById(R.id.timeBar_weekTask);
        top_TimeBar = baseView.findViewById(R.id.top_timeBar_weekTask);
        center_TimeBar = baseView.findViewById(R.id.center_timeBar_weekTask);
        bottom_TimeBar = baseView.findViewById(R.id.bottom_timeBar_weekTask);

        dataMaster = LocalStorage.getInstance(context);
    }

    // The data insertion and interpretation:
    public void defineMe(TaskObject taskObject, @Nullable RepeatingEvent[] events, WeekDisplay_WeekView.TimeCapsule[] timeCapsules) {
        this.taskWePresent = taskObject;
        this.events = events;
        this.timeCapsules = timeCapsules;

        // Assigning values to fields:
        switch (taskObject.getIsTaskCompleted()) {
            case notCheckable:
                checkBox.setVisibility(GONE);
                break;
            case incomplete:
                checkBox.setVisibility(VISIBLE);
                checkBox.setChecked(false);
                break;
            case completed:
                checkBox.setVisibility(VISIBLE);
                checkBox.setChecked(true);
                break;
        }
        taskName.setText(taskObject.getTaskName());
        // We check if it has master if not, then we skip this
        if (taskObject.getParentGoal() > 0) {
            ComplexGoal masterGoal = dataMaster.getComplexGoalBy(taskObject.getParentGoal());
            if (masterGoal != null) {
                masterTaskName.setVisibility(VISIBLE);
                masterTaskName.setText(masterGoal.getTaskName());
            } else {
                // What we do if there is none?
                masterTaskName.setVisibility(GONE);
            }
        } else {
            masterTaskName.setVisibility(GONE);
        }
        //Displaying the Mods:
        modMap.put(TaskObject.Mods.note, (ImageView) baseView.findViewById(R.id.noteMod_weekTask));
        modMap.put(TaskObject.Mods.list, (ImageView) baseView.findViewById(R.id.listMod_weekTask));
        modMap.put(TaskObject.Mods.repeating, (ImageView) baseView.findViewById(R.id.repeatOneMod_weekTask));
        modMap.put(TaskObject.Mods.repeatingMultiValues, (ImageView) baseView.findViewById(R.id.repeatMulti_weekTask));
        // iterates over all mods this task has and sets them VISIBLE from GONE
        for (TaskObject.Mods mod : taskObject.getAllMods()) {
            modMap.get(mod).setVisibility(VISIBLE);
        }

        // If this task has defined dateAndTime then we will show timeBar, otherwise we don't need to:
        if (taskObject.getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
            timeBar.setVisibility(VISIBLE);
            defineTimeBar();
        } else {
            timeBar.setVisibility(INVISIBLE); // TODO: Should Gone be Better?
        }

    }
    // This function is in charge of drawing the elements for timeBar
    private void defineTimeBar() {
        /*
         * What Do I need to make this work?
         * Size of the screen to determine the size of the area to work with
         * The Size of text? How big should it be?
         *  Maybe a little smaller than the text in TaskName?
         *
         * Time locale to determine HH or PM/AM system
         *
         * if its a repeating event, it might get multiple events for that day...
         */
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        // defining the time Formatter based on set preference
        SimpleDateFormat timeFormatter;
        if (preferences.getBoolean(Constants.TIME_REPRESENTATION, false)) {
            // if true, showing hours as time rep
            timeFormatter = new SimpleDateFormat("HH:mm");
        } else {
            // showing it as PM/AM
            timeFormatter = new SimpleDateFormat("hh:mm a" );
        }

        // Create the bar
        /*
         * View with rounded corners that spans 90% of the width of the bar
         * Height that is 70% of the height of its text
         *
         * X coordinate is text height + 3dp
         *
         * what type it should be? frame layout?
         */
        float textSize = taskName.getTextSize() * 0.8f;
        int xCoordinate = (int) (this.getWidth() * 0.05f);
        int yCoordinate = (int) textSize + 1 + dpToPixConverter(3);

        // Defining the dayBar capsule
        int barSize = (int) (center_TimeBar.getWidth() * 0.9f);
        FrameLayout dayBar = new FrameLayout(context);
        dayBar.setLayoutParams(new FrameLayout.LayoutParams(barSize, (int) (textSize * 0.7f)));
        center_TimeBar.addView(dayBar);
        center_TimeBar.setTranslationX(xCoordinate);
        center_TimeBar.setTranslationY(yCoordinate);
        // TODO: Round the corners of dayBar

        // Filing dayBar with TimeCapsules
        int minutesInADay = 1440;
        float minuteInAPixel = dayBar.getWidth() / minutesInADay; // defines the pixel size of each minute
        addTimeCapuselsInDayBar(timeCapsules, dayBar, minuteInAPixel);

    }
    // Fills in the hashIDRepo with id's
    void createIDRepository() {
        hashIDRepo = new ArrayList<>();
        if (events.length > 0) {
            // create if from events:
            for (RepeatingEvent event: events) {
                hashIDRepo.add(event.getHashID());
            }
        } else {
            // its only one taskObject not a repeating event
            hashIDRepo.add(taskWePresent.getHashID());
        }
    }
    // Receives a time capsules, creates views and adds it to dayBar, lights up the bars that are this tasks
    void addTimeCapuselsInDayBar(WeekDisplay_WeekView.TimeCapsule[] timeCapsules, FrameLayout dayBar, float minuteInAPixel) {
        int defualtColor = Color.GRAY;
        int taskColor = Color.GREEN;
        createIDRepository();
        // Now define and add a capsule into it
        for (WeekDisplay_WeekView.TimeCapsule capsule : timeCapsules) {
            View capsuleView = new View(context);
            long durationInMinutes = (capsule.endTime.getTimeInMillis() - capsule.startTime.getTimeInMillis()) / 60000;
            capsuleView.setLayoutParams(new LayoutParams((int) (durationInMinutes * minuteInAPixel),
                    ViewGroup.LayoutParams.MATCH_PARENT));
            dayBar.addView(capsuleView);
            int xCoordinate = (int) ((capsule.startTime.get(Calendar.HOUR_OF_DAY) * 60 + capsule.startTime.get(Calendar.MINUTE)) * minuteInAPixel);
            capsuleView.setTranslationX(xCoordinate);
            // Determine if this one belongs to chosen ones

            capsuleView.setBackgroundColor(defualtColor);
        }

    }
    @Deprecated
    void addThisTask(WeekDisplay_WeekView.TimeCapsule capsule, FrameLayout dayBar, float minuteInAPixel) {
        // Defining the base values:
        int taskColor = Color.GREEN;
        long durationInMinutes = (capsule.endTime.getTimeInMillis() - capsule.startTime.getTimeInMillis()) / 60000;
        long thisTaskXCoordinate = capsule.startTime.get(Calendar.HOUR_OF_DAY) * 60 + capsule.startTime.get(Calendar.MINUTE);

        // Creating and laying the view:
        View myTask = new View(context);
        myTask.setLayoutParams(new LayoutParams((int) (durationInMinutes * minuteInAPixel), ViewGroup.LayoutParams.MATCH_PARENT));
        myTask.setBackgroundColor(taskColor);
        // Adding the start and end time Values
    }
    // TODO: The Drag and Drop:

    // TODO: Click to Edit:

    // Other Methods:
    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
}
