package com.fuchsundlowe.macrolife.BottomBar;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.DatePicker;

import com.fuchsundlowe.macrolife.DataObjects.Constants;

import java.util.Calendar;

public class EventDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    Calendar value, minValue;
    Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog dialog;
        int year, month, day;
        year = value.get(Calendar.YEAR);
        month = value.get(Calendar.MONTH);
        day = value.get(Calendar.DAY_OF_MONTH);

        dialog = new DatePickerDialog(mContext,this, year, month, day);
        if (minValue != null) {
            dialog.getDatePicker().setMinDate(minValue.getTimeInMillis());
        }
        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        value.set(year, month, dayOfMonth);
        // This changes selection of this type and defines that startTime is set
        sendBroadcast();

    }

    // Value is the calendar to which we
    public void defineMe(Calendar value, Calendar minValue, Context context) {
        this.value = value;
        this.minValue = minValue;
        this.mContext = context;
    }

    // This reports to EditTaskBottom Bar that we have set the value to respective calendar value.
    void sendBroadcast() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(mContext);
        Intent intentReport;
        if (minValue == null) {
            // we know its start then
            intentReport = new Intent(Constants.START_VALUE_DONE);
        } else {
            // we know its end value
            intentReport = new Intent(Constants.END_VALUE_DONE);
        }
        manager.sendBroadcast(intentReport);
    }
}
