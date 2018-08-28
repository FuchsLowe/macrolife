package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.time.DayOfWeek;
import java.util.Calendar;

// Task that is used by Repeating event Editor to show task in DayView_RepeatEditor
public class RepeatingTask_RepeatEditor extends LinearLayout {

    //private View baseView;
    private TextView tittle;
    private Context mContext;
    protected Calendar startTime, endTime;
    protected com.fuchsundlowe.macrolife.DataObjects.DayOfWeek day;


    // Default Init's:
    public RepeatingTask_RepeatEditor(Context context) {
        super(context);
        universalInit(context);
    }
    public RepeatingTask_RepeatEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        universalInit(context);
    }
    public RepeatingTask_RepeatEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        universalInit(context);
    }


    // Initialization methods:
    private void universalInit(Context context) {
        this.mContext = context;

        LayoutInflater.from(context).inflate(R.layout.repeating_task_repeating_editor, this,true);

        tittle = findViewById(R.id.taskTitle_RepeatEditor);

        Button deleteButton = findViewById(R.id.deleteButton_RepeatEditor);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deleting self...
                setVisibility(GONE);
                startTime = null;
                endTime = null;
                broadcastDeletedEvent();
            }
        });

    }
    // Used when initiating the task with existing values...
    public void defineMe(String taskName, Calendar startTime, Calendar endTime, com.fuchsundlowe.macrolife.DataObjects.DayOfWeek day) {
        tittle.setText(taskName);
        this.startTime = startTime;
        this.endTime = endTime;
        this.day = day;
    }


    // Lifecycle:
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        layoutOnlyChild();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        layoutOnlyChild();
    }
    private void layoutOnlyChild() {
        View onlyChild = getChildAt(0);
        if (onlyChild != null) {
            onlyChild.layout(0, 0, getWidth(), getHeight());
            onlyChild.invalidate();
        }
    }

    // Methods:
    public Calendar getTaskStartTime() {
        return startTime;
    }
    public Calendar getTaskEndTime() {
        return endTime;
    }
    // Returns true if this is intended to be a reminder not a time-defied event
    public boolean isReminder() {
        return endTime != null && endTime.getTimeInMillis() != 0;
    }
    public com.fuchsundlowe.macrolife.DataObjects.DayOfWeek getDay() {
        return day;
    }
    // This will signal the RepeatingEventEditor that item has been deleted.
    // In turn when it iterates over the values, it will find and delete event without startTime
    private void broadcastDeletedEvent() {
        Intent mIntent = new Intent(Constants.INTENT_FILTER_EVENT_DELETED);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
    }
    // TODO: Create the system for changing the position as well as changing the duration...<<<<<<<<

}
