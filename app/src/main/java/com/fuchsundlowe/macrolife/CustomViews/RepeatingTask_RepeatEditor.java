package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
        baseView = inflater.inflate(R.layout.repeating_task_repeating_editor, this, false);
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
    }
    // Initialization methods:
    // If this is being copied...
    public void defineMe(TaskObject master, RepeatingEvent event) {
        this.master = master;
        this.event = event;
        // Define my start and end time etc...
    }
    // If this is createdAnew:
    public void createMe(TaskObject master, Calendar startTime, DayOfWeek day) {
        this.master = master;
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MINUTE, 15);

        //dataProvider.saveRepeatingEvent(); TODO: Save repeating event
    }
    public Calendar getTaskStartTime() {
        return event.getStartTime();
    }
    public Calendar getTaskEndTime() {
        return event.getEndTime();
    }

}
