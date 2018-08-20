package com.fuchsundlowe.macrolife.BottomBar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;

import java.util.Calendar;

public class TimePickerFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditTaskProtocol protocol;
    private TaskObject taskObject;
    private boolean isEditingStartValue;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int hour, minute;
        Calendar c;
        TimePickerDialog timePicker;
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
                c= Calendar.getInstance();
            }
        }
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        timePicker =  new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        return timePicker;
    }

    public void defineMe(TaskObject objectManipulated, EditTaskProtocol protocol, boolean isEditingStartValue) {
        this.protocol = protocol;
        this.taskObject = objectManipulated;
        this.isEditingStartValue = isEditingStartValue;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar valueEdited;
        if (isEditingStartValue) {
            valueEdited = taskObject.getTaskStartTime();
        } else {
            valueEdited = taskObject.getTaskEndTime();
        }
        // Todo: This is known to throw the error because sometimes the valueEdited is null...
        valueEdited.set(Calendar.HOUR_OF_DAY, hourOfDay);
        valueEdited.set(Calendar.MINUTE, minute);
        if (!isEditingStartValue) {
            // We will let default taskObject implementation deal with this inconsistency if any
            taskObject.setTaskEndTime(valueEdited);
        }
        protocol.saveTask(taskObject, null);
    }
}