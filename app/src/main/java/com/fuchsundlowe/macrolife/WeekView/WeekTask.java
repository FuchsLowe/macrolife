package com.fuchsundlowe.macrolife.WeekView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.Gravity;
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
import com.fuchsundlowe.macrolife.WeekView.DayHolder_WeekView.TimeCapsule;

// A task object used in WeekDisplay
public class WeekTask extends FrameLayout {

    private Context context;
    private DataProviderNewProtocol dataMaster;
    private View baseView;
    private CheckBox checkBox;
    private TextView taskName, masterTaskName;
    private LinearLayout modHolder;
    private LinearLayout timeBar;
    private FrameLayout top_TimeBar, center_TimeBar, bottom_TimeBar;
    private TaskObject taskWePresent;
    private List<RepeatingEvent> events; // Array of events we hold to represent
    private List<TimeCapsule> timeCapsules;
    private List<Integer> hashIDRepo;
    private List<TimeCapsule> capsToDrawTime;

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

        defineClickFunctions();
    }

    // The data insertion and interpretation, doesn't filter data...
    public void defineMe(TaskObject taskObject, @Nullable List<RepeatingEvent> events, List<TimeCapsule> timeCapsules) {
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
        HashMap<TaskObject.Mods, ImageView> modMap = new HashMap<>();
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

        // Create the bar
        float textSize = taskName.getTextSize() * 0.8f;
        int xCoordinate = (int) (this.getWidth() * 0.05f);
        int yCoordinate = (int) textSize + 1 + dpToPixConverter(3);

        // Defining the dayBar
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
        addTimeCapsulesInDayBar(timeCapsules, dayBar, minuteInAPixel, textSize);

    }
    // Fills in the hashIDRepo with id's
    void createIDRepository() {
        hashIDRepo = new ArrayList<>();
        if (events.size() > 0) {
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
    void addTimeCapsulesInDayBar(List<TimeCapsule> timeCapsules, FrameLayout dayBar,
                                 float minuteInAPixel, float textSize) {
        int defaultColor = Color.GRAY;
        int taskColor = Color.GREEN;
        createIDRepository();
        capsToDrawTime = new ArrayList<>(); // this is used to store TimeCapsules whose values we will use to draw time
        // Now define and add a capsule into it
        for (TimeCapsule capsule : timeCapsules) {
            View capsuleView = new View(context);
            long durationInMinutes = (capsule.endTime.getTimeInMillis() - capsule.startTime.getTimeInMillis()) / 60000;
            capsuleView.setLayoutParams(new LayoutParams((int) (durationInMinutes * minuteInAPixel),
                    ViewGroup.LayoutParams.MATCH_PARENT));
            dayBar.addView(capsuleView);
            int xCoordinate = (int) (minuteOfDay(capsule.startTime) * minuteInAPixel);
            capsuleView.setTranslationX(xCoordinate);
            // Determine if this one belongs to chosen ones
            if (hashIDRepo.contains(capsule.hashID)) {
                capsToDrawTime.add(capsule);
                capsuleView.setBackgroundColor(taskColor);
                // TODO: Maybe I need to put these in front of others...
            } else {
                capsuleView.setBackgroundColor(defaultColor);
            }
        }
        // Now we only need to draw the Time
        drawTaskTimes(minuteInAPixel, taskColor, textSize);
    }
    // This function draws the task times above and optionally below the the task times
    void drawTaskTimes(float minuteInPixels, int colorForText, float sizeOfText) {
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        // defining the time Formatter based on set preference
        SimpleDateFormat timeFormatter;
        if (preferences.getBoolean(Constants.TIME_REPRESENTATION, false)) {
            // if true, showing hours as time rep
            timeFormatter = new SimpleDateFormat("HH:mm");
        } else {
            // showing it as PM/AM
            timeFormatter = new SimpleDateFormat("hh:mm a");
        }
        // Params that will be used by both texts...
        LayoutParams universalLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        universalLayoutParams.gravity = Gravity.CENTER;

        // Going through the times
        for (TimeCapsule mCapsule : capsToDrawTime) {
            //Start Time:
            String startTimeFormatted = timeFormatter.format(mCapsule.startTime.getTime());
            TextView startTimeText = new TextView(context);
            startTimeText.setLayoutParams(universalLayoutParams);
            startTimeText.setText(startTimeFormatted);
            startTimeText.setTextSize(sizeOfText);
            startTimeText.setTextColor(colorForText);
            top_TimeBar.addView(startTimeText);
            // TODO MAKE SURE MEASUREMENTS ARE RIGHT:
            // Like the height is sizeOfText, not width, and that getWidth isn't 0
            int xCoordinateStartText = (int) (startTimeText.getWidth() / 2 + minuteOfDay(mCapsule.startTime) * minuteInPixels); // using the middle as x coordinate
            startTimeText.setX(xCoordinateStartText);

            // End time:
            String endTimeFormatted = timeFormatter.format(mCapsule.endTime.getTime());
            TextView endTimeText = new TextView(context);
            endTimeText.setLayoutParams(universalLayoutParams);
            endTimeText.setText(endTimeFormatted);
            endTimeText.setTextSize(sizeOfText);
            endTimeText.setTextColor(colorForText);
            if (capsToDrawTime.size() > 1) {
                // we put it into the second bar
                bottom_TimeBar.addView(endTimeText);
            } else {
                // goes into first bar
                top_TimeBar.addView(endTimeText);
            }
            int xCoordinateEndText = (int) (endTimeText.getWidth() / 2 + minuteOfDay(mCapsule.endTime) * minuteInPixels);
            endTimeText.setX(xCoordinateEndText);
        }
        if (capsToDrawTime.size() <= 1) {
            bottom_TimeBar.setVisibility(GONE);
        }

    }
    // From Calendar returns the minute of the day; EX: 1:30 AM is 90th min...
    int minuteOfDay(Calendar time) {
        return time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
    }

    // Click functionality implementation:
    private void sendGlobalEditBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.INTENT_FILTER_GLOBAL_EDIT);
        intent.putExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, taskWePresent.getHashID());
        manager.sendBroadcast(intent);
    }
    private void initDragAndDrop() {
        String[] MIME_Type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        DragShadowBuilder defaultShadowBuilder = new DragShadowBuilder(this);
        Integer height = getHeight(); // used for compatibility reasons
        ClipData.Item dataItem = new ClipData.Item(height.toString());
        ClipData data = new ClipData(Constants.TASK_OBJECT, MIME_Type, dataItem);
        startDrag(data, defaultShadowBuilder, taskWePresent, 0);
    }
    void defineClickFunctions() {
        // Make on Click Listener for edit
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGlobalEditBroadcast();
            }
        });
        // On Long press for drag and drop
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                initDragAndDrop();
                return true;
            }
        });
    }

    // Other Methods:
    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
}
