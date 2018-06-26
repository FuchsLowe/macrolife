package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;
/*
 * Defines the portion of editTaskBottomBar where you have name and checkbox available o
 */
public class EditingView_BottomBar extends FrameLayout {

    private CheckBox box;
    private TextView taskName;
    private TaskObject taskObject;
    private RepeatingEvent event;
    private EditTaskProtocol protocolProvider;
    private View baseView;

    public EditingView_BottomBar(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.edit_task, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams. MATCH_PARENT);
        baseView.setLayoutParams(params);
        addView(baseView);

        box = findViewById(R.id.edit_task_checkBox);
        box.setVisibility(GONE);
        box.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof CheckBox) {
                    if (((CheckBox) v).isChecked()) {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.completed);
                    } else {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.incomplete);
                    }
                    reportDataEdited();
                }
            }
        });

        taskName = findViewById(R.id.edit_task_taskName);
        taskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        taskObject.setTaskName(v.getText().toString());
                        reportDataEdited();
                    }
                }
                return false;
            }
        });

    }
    // Methods:
    public void insertData(TaskObject taskObject, @Nullable RepeatingEvent event, EditTaskProtocol protocol) {
        this.protocolProvider = protocol;
        this.taskObject = taskObject;
        taskName.setText(taskObject.getTaskName());
        switch (taskObject.getIsTaskCompleted()) {
            case notCheckable:
                box.setVisibility(GONE);
                break;
            case incomplete:
                box.setVisibility(VISIBLE);
                box.setChecked(false);
                break;
            case completed:
                box.setVisibility(VISIBLE);
                box.setChecked(true);
                break;
        }
        this.event = event;

    }
    public boolean toggleCheckBoxExistance() { // returns true if box is now visible and flase if its not
        if (box.getVisibility() == VISIBLE) {
            box.setVisibility(GONE);
            return false;
        } else {
            box.setVisibility(VISIBLE);
            return true;
        }
    }
    private void reportDataEdited() {
        protocolProvider.saveTask(taskObject, event);
    }
}
