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
        return number == 1;
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
            case 7: return DayOfWeek.sunday;
            default: return DayOfWeek.universal;
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
            case sunday: return 7;
            default: return 0;
        }
    }

    @TypeConverter
    public static int fromCheckableStatusToInt(TaskObject.CheckableStatus status) {
        if (status != null) {
            switch (status) {
                case incomplete:
                    return 1;
                case completed:
                    return 2;
                default:
                    return 0;
            }
        }else {
            return 0;
        }
    }
    @TypeConverter
    public static TaskObject.CheckableStatus fromIntToCheckableStatus(int value) {
        switch (value) {
            case 1:
                return TaskObject.CheckableStatus.incomplete;
            case 2:
                return TaskObject.CheckableStatus.completed;
            default:
                    return TaskObject.CheckableStatus.notCheckable;

        }
    }

    @TypeConverter
    public static int fromTimeDefinedToInt(TaskObject.TimeDefined timeStatus) {
        switch (timeStatus) {
            case onlyDate: return 1;
            case dateAndTime: return 2;
            default: return 0;
        }
    }
    @TypeConverter
    public static TaskObject.TimeDefined fromIntToTimeDefined(int value) {
        switch (value) {
            case 1: return TaskObject.TimeDefined.onlyDate;
            case 2: return TaskObject.TimeDefined.dateAndTime;
            default: return TaskObject.TimeDefined.noTime;
        }
    }
}
