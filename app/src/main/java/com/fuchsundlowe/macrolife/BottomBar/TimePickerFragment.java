package com.fuchsundlowe.macrolife.BottomBar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;

import java.util.Calendar;

import static java.util.Calendar.YEAR;

public class TimePickerFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditTaskProtocol protocol;
    private TaskObject taskObject;
    private boolean isEditiingStartValue;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour, minute;
        Calendar c;
        if (isEditiingStartValue) {
            if (taskObject.getTaskStartTime() != null) {
                c = taskObject.getTaskStartTime();
            } else {
                c = Calendar.getInstance();
            }
        } else {
            if (taskObject.getTaskEndTime() != null) {
                c = taskObject.getTaskEndTime();
            } else {
                c= Calendar.getInstance();
            }
        }
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void defineMe(TaskObject objectManipulated, EditTaskProtocol protocol, boolean isEditingStartValue) {
        this.protocol = protocol;
        this.taskObject = objectManipulated;
        this.isEditiingStartValue = isEditingStartValue;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar valueEdited;
        if (isEditiingStartValue) {
            valueEdited = taskObject.getTaskStartTime();
        } else {
            valueEdited = taskObject.getTaskEndTime();
        }
        valueEdited.set(Calendar.HOUR_OF_DAY, hourOfDay);
        valueEdited.set(Calendar.MINUTE, minute);
        protocol.saveTask(taskObject, null);
    }
}