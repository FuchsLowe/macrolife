package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
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
public class RepeatingTask_RepeatEditor extends FrameLayout {

    private View baseView;
    private TextView tittle;
    private TaskObject master;
    private RepeatingEvent event;
    private DataProviderNewProtocol dataProvider;

    public RepeatingTask_RepeatEditor(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.repeating_task_repeating_editor, null, false);

        this.addView(baseView);

        tittle = baseView.findViewById(R.id.taskTitle_RepeatEditor);
        Button deleteButton = baseView.findViewById(R.id.deleteButton_RepeatEditor);
        dataProvider = LocalStorage.getInstance(context);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete Self
                dataProvider.deleteRepeatingEvent(event);
                // TODO: Remove self from master view? and its local group... if there is one...
            }
        });
        // TODO: TEST
        this.setBackgroundColor(Color.GRAY);
    }


    // Initialization methods:
    public void defineMe(TaskObject master, RepeatingEvent event) {
        this.master = master;
        this.event = event;
        tittle.setText(master.getTaskName());
    }
    public void createMe(TaskObject master, Calendar startTime, com.fuchsundlowe.macrolife.DataObjects.DayOfWeek day) {
        this.master = master;
        tittle.setText(master.getTaskName());
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MINUTE, 30);
        int newHashID = dataProvider.findNextFreeHashIDForEvent();
        RepeatingEvent newEvent = new RepeatingEvent(master.getHashID(), startTime, endTime, day,
                newHashID, Calendar.getInstance());
        event = newEvent;
        dataProvider.saveRepeatingEvent(event);

    }


    // Lifecycle:

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (baseView != null) {
            baseView.layout(0, 0, w, h);
        }
    }

    public Calendar getTaskStartTime() {
        return event.getStartTime();
    }
    public Calendar getTaskEndTime() {
        return event.getEndTime();
    }

}
