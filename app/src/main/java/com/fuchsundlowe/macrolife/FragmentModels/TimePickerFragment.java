package com.fuchsundlowe.macrolife.FragmentModels;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.fuchsundlowe.macrolife.Interfaces.DateAndTimeProtocol;

import java.util.Calendar;

/**
 * Created by macbook on 3/7/18.
 */

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public DateAndTimeProtocol toReport;
    boolean isStartTime;

    @Override
    public  Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if (isStartTime) {
            toReport.setStartTime(hourOfDay, minute,0);
        } else {
            toReport.setEndTime(hourOfDay, minute, 0);
        }
    }
}