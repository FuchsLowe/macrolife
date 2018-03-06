package com.fuchsundlowe.macrolife.FragmentModels;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.fuchsundlowe.macrolife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskCreator_Complex extends Fragment {

    private EditText name;
    private EditText purpose;


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
        //name = getView().findViewById(R.id.setName_TaskCreatorComplex);
        //purpose = getView().findViewById(R.id.setPurpose_TaskCreatorComplex);
    }

    // Methods:

    public void providePurposeInput() {
        purpose.setVisibility(View.VISIBLE);
    }

    public void doneEditing() {
        // Do the transition to another screen where creation of complex will be provide.
    }
}
