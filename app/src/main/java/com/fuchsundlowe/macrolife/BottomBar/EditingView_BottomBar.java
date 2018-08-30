package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/*
 * The Holder for name and check-box
 */
public class EditingView_BottomBar extends FrameLayout {

    private CheckBox box;
    private EditText taskName;
    private TaskObject taskObject;
    private RepeatingEvent event;
    private EditTaskProtocol protocolProvider;
    private View baseView;
    private Context context;

    public EditingView_BottomBar(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseView = inflater.inflate(R.layout.edit_task, this);
        box = baseView.findViewById(R.id.edit_task_checkBox);
        box.setVisibility(GONE);
        box.setOnClickListener(new View.OnClickListener() {
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

        taskName = baseView.findViewById(R.id.edit_task_taskName);
        taskName.setSingleLine();
        taskName.setImeOptions(EditorInfo.IME_ACTION_DONE);
        taskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        taskObject.setTaskName(v.getText().toString());
                        softKeyboard(false);
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
    public boolean toggleCheckBoxExistance() { // returns true if box is now visible and false if its not
        if (box.getVisibility() == VISIBLE) {
            box.setVisibility(GONE);
            return false;
        } else {
            box.setVisibility(VISIBLE);
            return true;
        }
    }
    // This method manages appearance and disappearance of the soft keyboard, the right way
    public void softKeyboard(boolean appearance) {
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            if (appearance) {
                taskName.requestFocus();
                imm.showSoftInput(taskName, 0);
            } else {

                imm.hideSoftInputFromWindow(this.baseView.getWindowToken(), 0);
                taskName.clearFocus();
            }
        } catch (NullPointerException e) {
            Log.e("Keyboard Error", "Null pointer error -> " + e.getMessage());
        }
    }
    private void reportDataEdited() {
        protocolProvider.saveTask(taskObject, event);
    }

}
