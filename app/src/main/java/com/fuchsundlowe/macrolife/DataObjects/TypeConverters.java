package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.TypeConverter;

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
        return calendarValue;
    }
    @TypeConverter
    public static long fromCalendarToInt(Calendar calendar) {
        return calendar.getTimeInMillis();
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
    public static DataMasterClass.SourceType fromNumberToSource(int number) {
        switch (number) {
            case 0: return DataMasterClass.SourceType.local;
            case 1: return DataMasterClass.SourceType.googleCalendar;
            case 2: return DataMasterClass.SourceType.yahooCalendar;
            default: return DataMasterClass.SourceType.other;
        }
    }
    @TypeConverter
    public static int fromSourceTypeToInt( DataMasterClass.SourceType type) {
        switch (type){
            case local: return 0;
            case googleCalendar: return 1;
            case yahooCalendar: return 2;
            default: return -1;
        }
    }

}
