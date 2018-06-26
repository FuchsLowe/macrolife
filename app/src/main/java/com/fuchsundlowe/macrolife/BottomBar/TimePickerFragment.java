package com.fuchsundlowe.macrolife.BottomBar;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;

import java.util.Calendar;

public class TimePickerFragment extends android.support.v4.app.DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private EditTaskProtocol protocol;
    private TaskObject taskObject;
    private boolean isEditiingStartValue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

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