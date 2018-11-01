package com.fuchsundlowe.macrolife.MonthView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A Fragment that displays a recyclerView of tasks for specific day
 */
public class TaskDisplayer extends Fragment {

    private View baseView;
    private RecyclerView recyclerView;
    private List<TaskEventHolder> dataToDisplay;
    private MonthDataControllerProtocol controller;

    public TaskDisplayer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_task_displayer, container, false);

        recyclerView = baseView.findViewById(R.id.recyclerView_TaskDisplayer_MonthView);
        recyclerView.setLayoutManager(new LinearLayoutManager(baseView.getContext()));
        recyclerView.setAdapter(new DisplayerAdapter());

        // TODO: What should be our layout params? How do we fit inside various screens?

        // Defining functionality of the CreateNew task text editor.
        EditText newTaskText = baseView.findViewById(R.id.textField_TaskDisplayer_MonthView);
        newTaskText.setSingleLine();
        newTaskText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        newTaskText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // We respond to enter, save the task if there is more than 1 character
                    if (v.getText().length() > 0) {
                        TaskObject newTask = createNewTask((String) v.getText());
                        removeSoftKeyboard((EditText) v);
                        sendEditTaskBroadcast(newTask.getHashID());
                        return true;
                    }
                }
                return false;
            }
        });

        return baseView;
    }

    public void defineMe(List<TaskEventHolder> holders, MonthDataControllerProtocol controller) {
        this.dataToDisplay = holders;
        this.controller = controller;
    }
    private void removeSoftKeyboard(EditText taskName) {
        InputMethodManager imm = (InputMethodManager)
                baseView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(this.baseView.getWindowToken(), 0);
            taskName.clearFocus();
        } catch (NullPointerException e) {
            Log.e("Keyboard Error", "Null pointer error -> " + e.getMessage());
        }

    }
    private TaskObject createNewTask(String taskName) {
        return new TaskObject(controller.getFreeHashIDForTask(), 0, 0, taskName, Calendar.getInstance(),
                null, null, Calendar.getInstance(), TaskObject.CheckableStatus.notCheckable,
                null, 0, 0, "", TaskObject.TimeDefined.noTime, "");
    }
    // Sends local broadcast with ID of the task being edited...
    private void sendEditTaskBroadcast(int hashID) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseView.getContext());
        Intent editTask = new Intent();
        editTask.setAction(Constants.EDIT_TASK_BOTTOM_BAR);
        editTask.putExtra(Constants.INTENT_FILTER_TASK_ID, hashID);
        manager.sendBroadcast(editTask);
    }
    private void sendEditEventBroadcast(int hashID) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseView.getContext());
        Intent editTask = new Intent();
        editTask.setAction(Constants.EDIT_TASK_BOTTOM_BAR);
        editTask.putExtra(Constants.INTENT_FILTER_EVENT_ID, hashID);
        manager.sendBroadcast(editTask);
    }

    // Adapter class implementation:
    private class DisplayerAdapter extends RecyclerView.Adapter<DisplayerAdapter.TaskHolder>{

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // For now I am using the task_object_day_view layout as default platform since it
            // easily covers all things required by this view...
            View layout =  LayoutInflater.from(parent.getContext()).inflate(R.layout.task_object_day_view, parent, false);
            return new TaskHolder(layout);
        }

        @Override
        public void onBindViewHolder(@NonNull TaskHolder holder, int position) {
           holder.defineMe(dataToDisplay.get(position));
        }

        @Override
        public int getItemCount() {
           return dataToDisplay != null ? dataToDisplay.size() : 0;
        }

        class TaskHolder extends RecyclerView.ViewHolder {
            TextView taskName, masterTaskName;
            CheckBox box;
            LinearLayout modHolder;
            TaskEventHolder dataDescribed;

            TaskHolder(View view) {
                super(view);
                // Connecting the outlets, assuming its task_object_day_view layout
                taskName = view.findViewById(R.id.taskName_RecomendationTask);
                masterTaskName = view.findViewById(R.id.masterTaskName);
                box = view.findViewById(R.id.checkBox);
                modHolder = view.findViewById(R.id.modsHodler);

                // Click on CheckBox should lead to saving the task
                box.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        controller.saveTaskEventHolder(dataDescribed);
                    }
                });
                // Click on Task should do what? Long press?
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int id = dataDescribed.getActiveID();
                        if (dataDescribed.isTask()) {
                            sendEditTaskBroadcast(id);
                        } else {
                            sendEditEventBroadcast(id);
                        }
                        return true;
                    }
                });

            }

            void defineMe(TaskEventHolder dataToDescribe) {
                this.dataDescribed = dataToDescribe;
                taskName.setText(dataToDescribe.getName());
                String m = dataToDescribe.getMasterTaskName();
                if (m != null) { masterTaskName.setText(m); }
                switch (dataToDescribe.getCompletionState()) {
                    case completed: box.setChecked(true); break;
                    case incomplete: box.setChecked(false); break;
                    case notCheckable: box.setVisibility(View.GONE);
                }
                for (TaskObject.Mods mod :dataToDescribe.getAllMods()) {
                    ImageView imageView = new ImageView(baseView.getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                    imageView.setLayoutParams(params);
                    switch (mod) {
                        case note:
                            imageView.setImageResource(R.drawable.note_add_24px);
                            modHolder.addView(imageView);
                            break;
                        case list:
                            imageView.setImageResource(R.drawable.list_alt_24px);
                            modHolder.addView(imageView);
                            break;
                        case repeating:
                            imageView.setImageResource(R.drawable.repeat_24px);
                            modHolder.addView(imageView);
                            break;
                    }
                }
            }
        }
    }
}
