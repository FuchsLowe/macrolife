package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.TypeConverter;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by macbook on 1/31/18.
 * Does conversions for Room from boxed objects to primitive values and other way around.
 */

public class TypeConverters {

    @TypeConverter
    public static Calendar fromLongToCalendar(long value) {
            Date tempDate = new Date(value);
            Calendar calendarValue = Calendar.getInstance();
            calendarValue.setTime(tempDate);
            if (calendarValue != null) {
                return calendarValue;
            } else {
                // TODO: This needs to be addressed
                return Calendar.getInstance();
            }
    }
    @TypeConverter
    public static long fromCalendarToLong(Calendar calendar) {
        if (calendar != null) {
            return calendar.getTimeInMillis();
        } else {
            return 0;
        }
    }
    @TypeConverter
    public static boolean fromIntToBoolean(int number) {
        if (number == 1) {
            return true;
        } else {
            return false;
        }
    }
    @TypeConverter
    public static int fromBooleanToInt(Boolean bool) {
        if (bool) {
            return 1;
        } else {
            return 0;
        }
    }

    @TypeConverter
    public static SourceType fromNumberToSource(int number) {
        switch (number) {
            case 0: return SourceType.local;
            case 1: return SourceType.googleCalendar;
            case 2: return SourceType.yahooCalendar;
            default: return SourceType.other;
        }
    }
    @TypeConverter
    public static int fromSourceTypeToInt( SourceType type) {
        switch (type){
            case local: return 0;
            case googleCalendar: return 1;
            case yahooCalendar: return 2;
            default: return -1;
        }
    }

    @TypeConverter
    public static DayOfWeek fromIntToDay(int value) {
        switch (value) {
            case 1: return DayOfWeek.monday;
            case 2: return DayOfWeek.tuesday;
            case 3: return DayOfWeek.wednesday;
            case 4: return DayOfWeek.thursday;
            case 5: return DayOfWeek.friday;
            case 6: return DayOfWeek.saturday;
            default: return DayOfWeek.sunday;
        }
    }

    @TypeConverter
    public static int fromDayToInt(DayOfWeek day) {
        switch (day) {
            case monday: return 1;
            case tuesday: return 2;
            case wednesday: return 3;
            case thursday: return 4;
            case friday: return 5;
            case saturday: return 6;
            default: return 7;
        }
    }
}
