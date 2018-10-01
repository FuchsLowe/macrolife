package com.fuchsundlowe.macrolife.BottomBar;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.BottomBar.Pickers.EventDatePicker;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

// Fragment used to create and edit complex goals...
public class EditComplexGoal_BottomBar extends Fragment {

    private ViewGroup baseView;
    private EditText name, purpose;
    private Button deadline;
    private ImageButton clear, delete;
    private ComplexGoal goal;
    private DataProviderNewProtocol dataMaster;
    private LayoutInflater inflater;
    private Calendar valueToEditForDeadline;

    public EditComplexGoal_BottomBar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_complex_goal__bottom_bar, container, false);

        //Connect the Views:
        name = baseView.findViewById(R.id.name_goalFragment);
        purpose = baseView.findViewById(R.id.purpose_goalFragment);
        deadline = baseView.findViewById(R.id.deadline_goalFragment);
        clear = baseView.findViewById(R.id.clearButton_goalFragment);
        delete = baseView.findViewById(R.id.deleteButton_goalFragment);

        dataMaster = LocalStorage.getInstance(baseView.getContext());
        this.inflater = inflater;

        defineEditTextListeners();
        defineBroadcastReceiver();

        return baseView;
    }

    public void defineMe(ComplexGoal goal) {
        this.goal = goal;
    }

    @Override
    public void onStart() {
        super.onStart();
        // We assume that we should be in create ComplexGoal Mode:
        if (goal == null) {
            displayCreateGoalMode();
        } else {
            editGoal(goal);
        }
    }

    private void displayCreateGoalMode() {
        goal = null;

        name.setText("");
        name.setHint(R.string.hint_newGoal);

        purpose.setHint(R.string.hint_purpose);
        purpose.setVisibility(View.GONE);

        deadline.setVisibility(View.GONE);
        deadline.setText(R.string.setDeadline);

        clear.setVisibility(View.GONE);

        delete.setVisibility(View.GONE);

    }

    public void editGoal(ComplexGoal goalToEdit) {
        this.goal = goalToEdit;
        name.setText(goal.getTaskName());

        purpose.setVisibility(View.VISIBLE);
        purpose.setText(goal.getPurpose());

        deadline.setVisibility(View.VISIBLE);
        if (goal.hasDeadline()) {
            deadline.setText(formatDateForDeadline(goal.getDeadline()));
            clear.setVisibility(View.VISIBLE);
        } else {
            deadline.setText(R.string.setDeadline);
            clear.setVisibility(View.GONE);
        }

        delete.setVisibility(View.VISIBLE);

    }

    // Used to define and initiate listeners for text input for two Edit text fields ( Name and Purpose)
    private void defineEditTextListeners() {
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (goal == null) {
                        // means we create a new task
                        Calendar now = Calendar.getInstance();
                        goal = new ComplexGoal(0, name.getText().toString(), now, now, null);
                        // execute save:
                        dataMaster.saveComplexGoal(goal);
                    }
                    // reload self with new task in mind.
                    editGoal(goal);
                    return true;
                }
                return false;
            }
        });

        purpose.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    goal.setPurpose(purpose.getText().toString());
                    dataMaster.saveComplexGoal(goal);
                    return true;
                }
                return false;
            }
        });
    }

    // Button on click implementations:
    public void onClear(View view) {
        deadline.setText(R.string.setDeadline);
        goal.setDeadline(null);
    }
    public void onDelete(View view) {
        // Produce the pop up box for warning...
        View warningBox = inflater.inflate(R.layout.delete_warrning, null, false);
        float WIDTH_BY_SCREEN_PERCENTAGE = 0.8f;
        float HEIGHT_BY_SCREEN_PERCENTAGE = 0.25f;

        DisplayMetrics displayMetrics = baseView.getContext().getResources().getDisplayMetrics();
        int calculatedWidth = (int) (displayMetrics.widthPixels * WIDTH_BY_SCREEN_PERCENTAGE);
        int calculatedHeight = (int) (displayMetrics.heightPixels * HEIGHT_BY_SCREEN_PERCENTAGE);

        TextView tittle = warningBox.findViewById(R.id.tittle_deleteWarning);
        tittle.setText(R.string.Toast_Tittle_WARNING);
        TextView subtitle = warningBox.findViewById(R.id.subtitle_deleteWarning);
        subtitle.setText(R.string.Toast_Subtitle);

        final PopupWindow popupWindow = new PopupWindow(warningBox, calculatedWidth, calculatedHeight);
        popupWindow.setFocusable(true);        // TODO: Define animation
        popupWindow.showAtLocation(baseView, Gravity.CENTER,0,0);

        Button deleteButton = warningBox.findViewById(R.id.deleteButton_deleteWarning);
        deleteButton.setText("DELETE");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // We report back to DayView to complete the deletion Progress and decide what to do
                // whith this view
                popupWindow.dismiss();
                dataMaster.deleteComplexGoal(goal);
                displayCreateGoalMode();
            }
        });
        Button cancelButton = warningBox.findViewById(R.id.cancelButton_deleteWarning);
        cancelButton.setText("CANCEL");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the whole Charade
                popupWindow.dismiss();
            }
        });
    }
    public void onSetDeadline(View view) {
        EventDatePicker datePicker = new EventDatePicker();
        valueToEditForDeadline = goal.getDeadline();
        Calendar now = Calendar.getInstance();
        if (valueToEditForDeadline == null) {
            valueToEditForDeadline = (Calendar) now.clone();
        }
        datePicker.defineMe(valueToEditForDeadline, now, baseView.getContext());
        datePicker.show(this.requireFragmentManager(), "DatePicker");
    }

    private void defineBroadcastReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseView.getContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.END_VALUE_DONE);
        manager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.END_VALUE_DONE)) {
                    goal.setDeadline(valueToEditForDeadline);
                    dataMaster.saveComplexGoal(goal);
                    deadline.setText(formatDateForDeadline(valueToEditForDeadline));
                    clear.setVisibility(View.VISIBLE);
                }
            }
        }, filter);
    }

    private String formatDateForDeadline(Calendar deadline) {
        SimpleDateFormat format = new SimpleDateFormat("m/d/yy");
        return format.format(deadline.getTime());
    }

}
