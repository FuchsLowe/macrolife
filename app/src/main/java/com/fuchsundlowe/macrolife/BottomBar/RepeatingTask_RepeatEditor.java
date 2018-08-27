package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private TaskObject master;

    private DataProviderNewProtocol dataProvider;

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
        View base = LayoutInflater.from(context).inflate(R.layout.repeating_task_repeating_editor, null,false);
        this.addView(base);
        tittle = findViewById(R.id.taskTitle_RepeatEditor);
        Button deleteButton = findViewById(R.id.deleteButton_RepeatEditor);
        dataProvider = LocalStorage.getInstance(context);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete Self
                dataProvider.deleteRepeatingEvent(event);
                // TODO: Remove self from master view? and its local group... if there is one...
                setVisibility(GONE);
            }
        });

    }
    // Used when initiating the task with existing values...
    public void defineMe(TaskObject master) {
        this.master = master;

        tittle.setText(master.getTaskName());
    }
    // Used for creating the task anew.
    public void createMe(TaskObject master, Calendar startTime, com.fuchsundlowe.macrolife.DataObjects.DayOfWeek day) {
        this.master = master;
        tittle.setText(master.getTaskName());
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MINUTE, 30);
        int newHashID = dataProvider.findNextFreeHashIDForEvent();
        TaskObject.CheckableStatus status;
        if (master.getIsTaskCompleted() == TaskObject.CheckableStatus.notCheckable) {
            status = TaskObject.CheckableStatus.notCheckable;
        } else {
            status = TaskObject.CheckableStatus.incomplete;
        }
        RepeatingEvent newEvent = new RepeatingEvent(master.getHashID(), startTime, endTime,
                newHashID, Calendar.getInstance(), status);

        dataProvider.saveTaskObject(master);

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
        return event.getStartTime();
    }
    public Calendar getTaskEndTime() {
        return event.getEndTime();
    }

}
