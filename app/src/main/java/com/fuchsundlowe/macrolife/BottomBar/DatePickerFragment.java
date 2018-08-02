package com.fuchsundlowe.macrolife.BottomBar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private EditTaskProtocol protocol;
    private TaskObject taskObject;
    private boolean isEditingStartValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year, month, day;
        Calendar c;
        if (isEditingStartValue) {
            if (taskObject.getTaskStartTime() != null) {
                c = taskObject.getTaskStartTime();
            } else {
                c = Calendar.getInstance();
            }
        } else {
            if (taskObject.getTaskEndTime() != null) {
                c = taskObject.getTaskEndTime();
            } else {
                c = Calendar.getInstance();
            }
        }

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void defineMe(TaskObject objectManipulated, EditTaskProtocol protocol, boolean isEditingStartValue) {
        this.protocol = protocol;
        this.taskObject = objectManipulated;
        this.isEditingStartValue = isEditingStartValue;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar valueEdited;
        if (isEditingStartValue) {
            valueEdited = taskObject.getTaskStartTime();
        } else {
            valueEdited = taskObject.getTaskEndTime();
        }
        valueEdited.set(Calendar.YEAR, year);
        valueEdited.set(Calendar.MONTH, month);
        valueEdited.set(Calendar.DAY_OF_MONTH, day);
        protocol.saveTask(taskObject, null);
    }
}