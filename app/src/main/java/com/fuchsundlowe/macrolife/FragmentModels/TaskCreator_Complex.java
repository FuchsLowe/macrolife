package com.fuchsundlowe.macrolife.FragmentModels;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class TaskCreator_Complex extends android.app.Fragment {

    private EditText name;
    private EditText purpose;
    private LinearLayout buttonBar;



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

    }



    // Methods:

    public void providePurposeInput() {
        purpose.setVisibility(View.VISIBLE);
    }
    private void provideButtonBar() {
        buttonBar.setVisibility(View.VISIBLE);
    }

    public void enableEndTimeButton() {
        Button endTime = getView().findViewById(R.id.endTime_TaskCreator);
        endTime.setVisibility(View.VISIBLE);
    }

    public void provideStartTimePopUp() {

    }

    public void provideEndTimePopUp() {

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


}
