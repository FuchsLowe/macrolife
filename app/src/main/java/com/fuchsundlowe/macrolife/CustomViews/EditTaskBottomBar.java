package com.fuchsundlowe.macrolife.CustomViews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fuchsundlowe.macrolife.DataObjects.Chevronable;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;


public class EditTaskBottomBar extends Fragment {

    //Variables and instances:
    private DataProviderNewProtocol dataProvider;
    private ViewGroup baseView;
    private FrameLayout dynamicArea;
    private LinearLayout modAreaOne, modAreaTwo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = (ViewGroup) inflater.inflate(R.layout.edit_task_bottom_bar, container, false);
        dynamicArea = baseView.findViewById(R.id.dynamicArea_editTask);
        modAreaOne = baseView.findViewById(R.id.modAreaOne_editTAsk);
        modAreaTwo = baseView.findViewById(R.id.modAreaTwo_editTask);

        dataProvider = LocalStorage.getInstance(getContext());

        return baseView;
    }

    //Methods:
    /* Based on data it receives it will sprung into action its looks. Return value indicates that
     * operation of creating desired View state is failure or success...
     * Must receive the taskManipulatd if setState == editTask, else returns false
     */
    public boolean setState(EditTaskState setState, @Nullable TaskObject taskManipulated) {

        switch (setState) {
            case createTask:
                // define TextView and wait

                return true;
            case editTask:
                if (taskManipulated != null) {
                    // open all the windows
                    // initialize the values with taskManipulated
                    return true;
                } else {return false;}
        }
        return false;
    }

    public enum EditTaskState {
        createTask, editTask
    }

}
