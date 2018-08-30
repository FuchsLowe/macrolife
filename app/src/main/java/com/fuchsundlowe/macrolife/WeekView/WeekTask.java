package com.fuchsundlowe.macrolife.WeekView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
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
import com.fuchsundlowe.macrolife.DayView.DayDisplay_DayView.TaskEventHolder;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


import com.fuchsundlowe.macrolife.WeekView.DayHolder_WeekView.TimeCapsule;

import static com.fuchsundlowe.macrolife.DataObjects.TaskObject.CheckableStatus.completed;
import static com.fuchsundlowe.macrolife.DataObjects.TaskObject.CheckableStatus.incomplete;
import static com.fuchsundlowe.macrolife.DataObjects.TaskObject.CheckableStatus.notCheckable;


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
    private TaskEventHolder dataToPresent;
    private List<TimeCapsule> timeCapsules;
    private DayHolderCommunicationInterface parentProtocol;

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
    public void defineMe(TaskEventHolder data,
                         List<TimeCapsule> timeCapsules, DayHolderCommunicationInterface protocol) {

        this.dataToPresent = data;
        this.timeCapsules = timeCapsules;
        this.parentProtocol = protocol;

        // Assigning values to fields:
        switch (dataToPresent.getCompletionState()) {
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
        taskName.setText(dataToPresent.getName());
        // We check if it has master if not, then we skip this
        if (dataToPresent.getComplexGoalID() > 0) {
            ComplexGoal masterGoal = dataMaster.getComplexGoalBy(dataToPresent.getComplexGoalID());
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
        // iterates over all mods this task has and sets them VISIBLE from GONE
        for (TaskObject.Mods mod : dataToPresent.getAllMods()) {
            modMap.get(mod).setVisibility(VISIBLE);
        }

        // If this task has defined dateAndTime then we will show timeBar, otherwise we don't need to:
        if (dataToPresent.getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
            timeBar.setVisibility(VISIBLE);
            defineTimeBar();
        } else {
            timeBar.setVisibility(GONE);
        }

    }
    // This function is in charge of drawing the elements for timeBar
    private void defineTimeBar() {

        // Create the bar
        final float textSize = taskName.getMaxHeight() * 0.8f; //
        int xCoordinate = (int) (this.getWidth() * 0.05f); // todo: width is zero
        int yCoordinate = (int) textSize + 1 + dpToPixConverter(3);

        // Defining the dayBar
        final FrameLayout dayBar = new FrameLayout(context);
        dayBar.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPixConverter(7)));
        dayBar.setBackgroundColor(Color.LTGRAY);
        center_TimeBar.addView(dayBar);
        center_TimeBar.setTranslationX(xCoordinate);
        center_TimeBar.setTranslationY(yCoordinate);
        // TODO: Round the corners of dayBar

        // Filing dayBar with TimeCapsules

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                addTimeCapsulesInDayBar(timeCapsules, dayBar);
            }
        }, 300);
        //addTimeCapsulesInDayBar(timeCapsules, dayBar, minuteInAPixel, textSize);
    }

    // Receives a time capsules, creates views and adds it to dayBar, lights up the bars that are this tasks
    private void addTimeCapsulesInDayBar(List<TimeCapsule> timeCapsules, FrameLayout dayBar) {
        int defaultColor = Color.BLUE;
        int taskColor = Color.GREEN;
        float minutesInADay = 1440;
        float minuteInAPixel = dayBar.getWidth() / minutesInADay; // defines the pixel size of each minute
        //createIDRepository();
        // Now define and add a capsule into it
        for (TimeCapsule capsule : timeCapsules) {
            View capsuleView = new View(context);
            long durationInMinutes = (capsule.endTime.getTimeInMillis() - capsule.startTime.getTimeInMillis()) / 60000;
            int timeToPresent;
            // If end time doesn't belong to today's day then we use max value instead of timeValue
            if (capsule.endTime.get(Calendar.DAY_OF_YEAR) != parentProtocol.getDayHoldersDay().get(Calendar.DAY_OF_YEAR)) {
                timeToPresent = dayBar.getWidth();
            } else {
                // But if task has started on other day than today... then this would be off...
                if (capsule.startTime.get(Calendar.DAY_OF_YEAR) != parentProtocol.getDayHoldersDay().get(Calendar.DAY_OF_YEAR)) {
                    Calendar startTimeOfDay = (Calendar) capsule.endTime.clone();
                    startTimeOfDay.set(Calendar.HOUR_OF_DAY, 0);
                    startTimeOfDay.set(Calendar.MINUTE, 0);
                    durationInMinutes = (capsule.endTime.getTimeInMillis() - startTimeOfDay.getTimeInMillis()) / 60000;
                    timeToPresent = (int) (durationInMinutes * minuteInAPixel);
                } else {
                    timeToPresent = (int) (durationInMinutes * minuteInAPixel);
                }
            }
            capsuleView.setLayoutParams(new LayoutParams(timeToPresent,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            // Determine if this one belongs to chosen ones, and thus we highlight it
            if (dataToPresent.getActiveID() == capsule.hashID) {
                capsuleView.setTranslationZ(5);
                capsuleView.setBackgroundColor(taskColor);
            } else {
                capsuleView.setBackgroundColor(defaultColor);
            }
            dayBar.addView(capsuleView);
            // If it starts on same day as this we represent, then we use that as our start point, else
            // we start from 0
            int xCoordinate;
            if (capsule.startTime.get(Calendar.DAY_OF_YEAR) == parentProtocol.getDayHoldersDay().get(Calendar.DAY_OF_YEAR)) {
                xCoordinate = (int) (minuteOfDay(capsule.startTime) * minuteInAPixel);
            } else {
                xCoordinate = 0;
            }
            capsuleView.animate().x(xCoordinate).setDuration(2000).setStartDelay(2000).start();
            //capsuleView.setX(xCoordinate);
        }
        // Now we only need to draw the Time
        treeTimesRepresentation();
    }
    // Makes 3 text views and places them into bottom container with respect of local time representation
    private void treeTimesRepresentation() {
        // Establish what type of system we use based on preference:
        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        String[] timeValues;
        if (preferences.getBoolean(Constants.TIME_REPRESENTATION, false)) {
            // if true, showing hours as time rep
            timeValues = new String[]{"6", "12", "18"};
        } else {
            // showing it as PM/AM
            timeValues = new String[]{"6", "12", "6"};
        }
        // We Define the TextViews:
        int textColor = Color.CYAN;
        TextView morning, noon, evening;

        morning = new TextView(getContext());
        morning.setTextColor(textColor);
        morning.setTextSize(getResources().getDimension(R.dimen.week_view_time));

        noon =  new TextView(getContext());
        noon.setTextColor(textColor);
        noon.setTextSize(getResources().getDimension(R.dimen.week_view_time));

        evening =  new TextView(getContext());
        evening.setTextColor(textColor);
        evening.setTextSize(getResources().getDimension(R.dimen.week_view_time));

        // We add text:
        morning.setText(timeValues[0]);
        noon.setText(timeValues[1]);
        evening.setText(timeValues[2]);

        // Now we measure the text size:
        int mLenght, nLenght, eLenght;
        int oneHour = bottom_TimeBar.getWidth() / 24;

        morning.measure(0,0);
        mLenght = morning.getMeasuredWidth();

        noon.measure(0,0);
        nLenght = noon.getMeasuredWidth();

        evening.measure(0,0);
        eLenght = evening.getMeasuredWidth();

        // Now we add them and position them
        bottom_TimeBar.addView(morning);
        morning.setX(6 * oneHour - mLenght/2);

        bottom_TimeBar.addView(noon);
        noon.setX(12 * oneHour - nLenght/2);

        bottom_TimeBar.addView(evening);
        evening.setX(18 * oneHour - eLenght/2);
    }
    // This function draws the task times above and optionally below the the task times

    // From Calendar returns the minute of the day; EX: 1:30 AM is 90th min...
    private int minuteOfDay(Calendar time) {
        return time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
    }

    // Click functionality implementation:
    private void sendGlobalEditBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(Constants.INTENT_FILTER_GLOBAL_EDIT);
        intent.putExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, dataToPresent.getMasterHashID());
        manager.sendBroadcast(intent);
    }
    private void initDragAndDrop() {
        // TODO: Refractor to support Repaeting events:
        String[] MIME_Type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        DragShadowBuilder defaultShadowBuilder = new DragShadowBuilder(this);
        Integer height = getHeight(); // used for compatibility reasons
        ClipData.Item dataItem = new ClipData.Item(height.toString());
        ClipData data = new ClipData(Constants.TASK_OBJECT, MIME_Type, dataItem);
        startDrag(data, defaultShadowBuilder, dataToPresent, 0);
    }
    private void defineClickFunctions() {
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
