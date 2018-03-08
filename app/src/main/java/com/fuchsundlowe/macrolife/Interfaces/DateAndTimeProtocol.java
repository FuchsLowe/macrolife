package com.fuchsundlowe.macrolife.Interfaces;

import java.util.Calendar;

/**
 * Created by macbook on 3/7/18.
 * Purpose of this protocol is to provide for TimePickerFragment and DatePickerFragment
 * a way to report back their set values.
 */

public interface DateAndTimeProtocol {
    void setStartDate(int year, int month, int day);
    void setEndDate(int year, int month, int day);

    void setStartTime(int hour, int minute, int second);
    void setEndTime(int hour, int minute, int second);
}
