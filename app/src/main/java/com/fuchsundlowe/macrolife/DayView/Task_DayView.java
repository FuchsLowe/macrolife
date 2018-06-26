package com.fuchsundlowe.macrolife.DayView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

// A custom view Class that creates a taskView intended for usage in DayView's Chrono-View

public class Task_DayView extends FrameLayout {
    private TextView taskName, masterTaskName;
    private LinearLayout modsHolder;
    private CheckBox box;
    private int boxSize;
    private int timeUnitSize; // size in px defining 15 min worth of time in a task
    private SharedPreferences preferences;
    private TaskObject task; // Never use by Iteself, use its helper method
    private RepeatingEvent repeatingEvent; // Never use by itself, just helper methods
    private DataProviderNewProtocol storageMaster; // TODO: Define this
    private boolean globalEdit = false;
    private GestureDetectorCompat longPressDetector;
    private float storedX, storedY;
    private LocalBroadcastManager manager;
    private ClickLocation clickLocation;

    public Task_DayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.task_object_day_view,this,true);

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        timeUnitSize = (preferences.getInt(Constants.HOUR_IN_PIXELS, 108) / 4 );

        longPressDetector = new GestureDetectorCompat(context, new LongPressDetector());

        clickLocation = ClickLocation.none;
    }

    // Lifecycle:
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        taskName = findViewById(R.id.taskName_RecomendationTask);
        masterTaskName = findViewById(R.id.masterTaskName);
        modsHolder = findViewById(R.id.modsHodler);
        box = findViewById(R.id.checkBox);
    }
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    // Layout operations
    public void myLayout(int left, int top, int right, int bottom) {
        if (bottom - top > timeUnitSize) {
            // only if its more than 15 min we will allow it
            this.layout(left, top, right, bottom);
            if (masterTaskName.getBottom() < bottom) {
                masterTaskName.setVisibility(VISIBLE);
            } else {
                masterTaskName.setVisibility(GONE);
            }
        }
    }
    // Used to make the layout snap to one of 4 different quarters of day
    private void snapLayoutAndSaveNewTime() {
        // Should Snap layout back in standard values and save new time...

        // Snapping procedure:
        int quarterInterval = timeUnitSize / 4;
        int top = (getTop() / quarterInterval) * quarterInterval;
        int bottom = (getBottom() / quarterInterval) * quarterInterval;
        myLayout(getLeft(),top, getRight(),bottom);

        // Saving Procedure:
        int startHour = this.getTop() / timeUnitSize;
        int startMinute = (this.getTop() % timeUnitSize / (timeUnitSize / 4)) * 15;

        int endHour = this.getBottom() / timeUnitSize;
        int endMinute = (this.getBottom() % timeUnitSize / (timeUnitSize / 4)) * 15;
        if (repeatingEvent != null) {
            repeatingEvent.getStartTime().set(Calendar.HOUR_OF_DAY, startHour);
            repeatingEvent.getStartTime().set(Calendar.MINUTE, startMinute);
            repeatingEvent.getEndTime().set(Calendar.HOUR_OF_DAY, endHour);
            repeatingEvent.getEndTime().set(Calendar.MINUTE, endMinute);
        } else {
            task.getTaskStartTime().set(Calendar.HOUR_OF_DAY, startHour);
            task.getTaskStartTime().set(Calendar.MINUTE, startMinute);
            task.getTaskEndTime().set(Calendar.HOUR_OF_DAY, endHour);
            task.getTaskEndTime().set(Calendar.MINUTE, endMinute);
        }
        // Todo: How do we commit save?
    }

    //TouchEvents:
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Determines if press is long press:
        longPressDetector.onTouchEvent(event);
        if (globalEdit) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    storedX = event.getX();
                    storedY = event.getY();
                    detectClickLocation(event);
                    //signalGlobalEdit(true);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float currentX = event.getX();
                    float currentY = event.getY();
                    switch (clickLocation) {
                        case top:
                            myLayout(getLeft(), (int) (getTop() + currentY - storedY), getRight(), getBottom());
                            break;
                        case center: // This requires Drag and drop operation
                            // is this done by the drag and drop operation?
                            break;
                        case bottom:
                            myLayout(getLeft(), getTop(), getRight(), (int)(getBottom() + currentY - storedY));
                            break;
                    }
                    storedX = currentX;
                    storedY = currentY;

                case MotionEvent.ACTION_UP:
                    // Needs to save the new time...
                    clickLocation = ClickLocation.none;
                    snapLayoutAndSaveNewTime();
                    break;
            }

        } else {
            return false;
        }
        return true;
    }
    private class LongPressDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            signalGlobalEdit(true);
        }
    }
    public void signalGlobalEdit(boolean isEditing) {
        this.globalEdit = isEditing;
        if (isEditing) {
            sendGlobalEditBroadcast();
            initiateDragAndDrop();
        }
    }
    private ClickLocation detectClickLocation(MotionEvent event) {
        ClickLocation returnValue;

        int detectionRadius = dpToPixConverter(25);
        float fingerYCoordinate = event.getY();
        if (fingerYCoordinate <= detectionRadius) {
            returnValue =  ClickLocation.top;
        } else if (fingerYCoordinate >= getHeight() - detectionRadius) {
            returnValue =  ClickLocation.bottom;
        } else {
            returnValue = ClickLocation.center;
        }
        clickLocation = returnValue;
        return returnValue;
    }

    // Data definition of self
    public void insertData(TaskObject data, @Nullable RepeatingEvent repeatingEvent) {
        this.task = data;
        this.repeatingEvent = repeatingEvent;
        if (data.getParentGoal() > 0) { // make sure there is one
            ComplexGoal result = storageMaster.findComplexGoal(data.getParentGoal());
            if (result != null) {
                masterTaskName.setText(result.getTaskName());
            }
        }
        taskName.setText(data.getTaskName());

        if (repeatingEvent != null) {
            switch (repeatingEvent.getIsTaskCompleted()) {
                case notCheckable:
                    box.setVisibility(GONE);
                    break;
                case completed:
                    box.setVisibility(VISIBLE);
                    box.setChecked(true);
                    break;
                case incomplete:
                    box.setVisibility(VISIBLE);
                    box.setChecked(false);
                    break;
            }
        } else {
            switch (data.getIsTaskCompleted()) {
                case notCheckable:
                    box.setVisibility(GONE);
                    break;
                case completed:
                    box.setVisibility(VISIBLE);
                    box.setChecked(true);
                    break;
                case incomplete:
                    box.setVisibility(VISIBLE);
                    box.setChecked(false);
                    break;
            }
        }

        // TODO: About mods, how will I retrieve and present them?

        // TODO: Is this good enough?
        this.requestLayout();
    }

    //Methods:
    public Calendar getTaskStartTime() {
        if (repeatingEvent == null) {
            return task.getTaskStartTime();
        } else {
            return repeatingEvent.getStartTime();
        }
    }
    public Calendar getTaskEndTime() {
        if (repeatingEvent == null) {
            return task.getTaskEndTime();
        } else {
            return repeatingEvent.getEndTime();
        }
    }
    public void setNewTaskTimes(@Nullable Calendar startTime, @Nullable Calendar endTime) {
        if (startTime != null) {
            if (repeatingEvent != null) {
                repeatingEvent.setStartTime(startTime);
            } else {
                task.setTaskStartTime(startTime);
            }
        }
        if (endTime != null) {
            if (repeatingEvent != null) {
                repeatingEvent.setEndTimeWithReturn(endTime);
            } else {
                task.setTaskEndTime(endTime);
            }
        }
        // TODO: How should this change in time be enforced into GUI?
    }
    private int dpToPixConverter(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private void initiateDragAndDrop() {
        String[] MIME_Type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        DragShadowBuilder defaultShadowBuilder = new DragShadowBuilder(this);
        if (repeatingEvent == null) {
            ClipData.Item dataItem = new ClipData.Item("" + task.getHashID());
            ClipData data = new ClipData(Constants.TASK_OBJECT, MIME_Type, dataItem);
            this.startDrag(data, defaultShadowBuilder, task, 0);
        } else {
            ClipData.Item dataItem = new ClipData.Item("" + repeatingEvent.getHashID());
            ClipData data = new ClipData(Constants.REPEATING_EVENT, MIME_Type, dataItem);
            this.startDrag(data, defaultShadowBuilder, repeatingEvent, 0);
        }
    }

    private void sendGlobalEditBroadcast() {
        manager = LocalBroadcastManager.getInstance(getContext());
        Intent intent = new Intent(Constants.INTENT_FILTER_GLOBAL_EDIT);
        intent.putExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, task.getHashID());
        manager.sendBroadcast(intent);
    }
    // Retrives RepeatingEvent ID if there is one, if not returns TaskObject ID
    public int getActiveHashID() {
        if (repeatingEvent == null) {
            return task.getHashID();
        } else {
            return repeatingEvent.getHashID();
        }
    }

    // Local Enum:
    private enum ClickLocation{
        top, center, bottom, none;
    }

}


