package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.Nullable;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.NewDAO;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.RoomDataBaseObject;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import java.util.ArrayList;
import java.util.Calendar;

public class LocalStorage implements DataProviderNewProtocol {

    private static LocalStorage self;
    private RoomDataBaseObject dataBase;

    // Constructor implementation:
    public static  @Nullable LocalStorage getInstance(@Nullable Context context) {
        if (self != null) {
            return self;
        } else if (context != null) {
            self = new LocalStorage(context);
            return self;
        } else { return null; }
    }
    private LocalStorage(Context context) {
        // Deals with database initialization ofc
        dataBase = Room.databaseBuilder(context, RoomDataBaseObject.class,
                Constants.DATA_BASE_NAME).build();
    }

    // Database Calls:
    @Override
    public LiveData<ArrayList<TaskObject>> getTasksFor(Calendar day) {
        return null;
    }
    @Override // Static return value
    public ComplexGoal findComplexGoal(int byID) {
        return null;
    }
    @Override
    public TaskObject findTaskObjectBy(int ID) {
        // TODO: To implement search from static database
        return null;
    }
    public LiveData<ArrayList<TaskObject>> getTaskThatIntersects(Calendar day) {
        // Get the long values of start and end of day...
        long[] results = returnStartAndEndTimesForDay(day);

        return dataBase.newDAO().getTaskThatIntersects(results[0], results[1]);
    }
    public LiveData<ArrayList<RepeatingEvent>> getEventsThatIntersect(Calendar day) {
        // Get the long values of start and end of day...
        long[] results = returnStartAndEndTimesForDay(day);

        return dataBase.newDAO().getEventThatIntersects(results[0], results[1]);
    }

    // Method calls:
    // first value is start time and second value is end time
   private long[] returnStartAndEndTimesForDay(Calendar day) {
       Calendar dayToWorkWith = (Calendar) day.clone();

       dayToWorkWith.set(Calendar.HOUR_OF_DAY,0);
       dayToWorkWith.set(Calendar.MINUTE,0);
       dayToWorkWith.set(Calendar.SECOND,0);
       dayToWorkWith.set(Calendar.MILLISECOND,0);
       long startTimeStamp = day.getTimeInMillis();

       dayToWorkWith.set(Calendar.HOUR_OF_DAY, 23);
       dayToWorkWith.set(Calendar.MINUTE, 59);
       long endTimeStamp = day.getTimeInMillis();

       return new long[]{startTimeStamp, endTimeStamp};
   }
}
