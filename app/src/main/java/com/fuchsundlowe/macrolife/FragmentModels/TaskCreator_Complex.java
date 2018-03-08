package com.fuchsundlowe.macrolife.FragmentModels;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreator_Complex extends Fragment {

    private EditText name;
    private EditText purpose;
    private LinearLayout buttonBar;
    private Button startDate;
    private Button endDate;
    private Button done;


    public TaskCreator_Complex() {
        // Required empty public constructor
    }

    // Lifecycle:

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_creator__complex, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        defineName();
        purpose = getView().findViewById(R.id.setPurpose_TaskCreatorComplex);
        buttonBar = getView().findViewById(R.id.buttonBar_TaskCreator);
        defineButtons();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // Methods:

    public void providePurposeInput() {
        purpose.setVisibility(View.VISIBLE);
    }
    private void provideButtonBar() {
        buttonBar.setVisibility(View.VISIBLE);
    }

    private void enableEndTimeButton() {
        Button endTime = getView().findViewById(R.id.endTime_TaskCreator);
        endTime.setVisibility(View.VISIBLE);
    }


    public void doneEditing() {
        // Do the transition to another screen where creation of complex will be provide.
    }

    private void defineName() {
        name = getView().findViewById(R.id.setName_TaskCreatorComplex);
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    providePurposeInput();
                    provideButtonBar();
                }
                return true;
            }
        });

    }

    private void defineButtons() {
        // StartDate&Time Button:
        startDate = getView().findViewById(R.id.startTime_TaskCreator);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For startDate
            }
        });

        // EndDate&Time Button:
        endDate = getView().findViewById(R.id.endTime_TaskCreator);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For endDate
            }
        });

        // For Done Button:
        done = getView().findViewById(R.id.done_TaskCreator);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For Done button implementation
                doneClicked();
            }
        });
    }

    // PopUps providers:

    private void provideStartTime() {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(),"TimePickerFragment");
    }

    private void provideEndTime() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getFragmentManager(), "DatePickerFragment");
    }

    private void doneClicked() {

    }


}
