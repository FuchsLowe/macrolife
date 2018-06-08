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
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import java.util.ArrayList;
import java.util.Calendar;

public class LocalStorage implements DataProviderNewProtocol {

    private static LocalStorage self;
    private RoomDatabaseObject dataBase;

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
        dataBase = Room.databaseBuilder(context, RoomDatabaseObject.class,
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
    public LiveData<ArrayList<TaskObject>> getTaskThatIntersects(Calendar day) {
        // Get the long values of start and end of day...
        day.set(Calendar.HOUR_OF_DAY,0);
        day.set(Calendar.MINUTE,0);
        day.set(Calendar.SECOND,0);
        day.set(Calendar.MILLISECOND,0);
        long startTimeStamp = day.getTimeInMillis();

        day.set(Calendar.HOUR_OF_DAY, 23);
        day.set(Calendar.MINUTE, 59);
        long endTimeStamp = day.getTimeInMillis();
        return null;
    }

    // Database Implementation:
    @Database(entities = {ComplexGoal.class, TaskObject.class, ListObject.class, RepeatingEvent.class},
    version = 1, exportSchema = false)
    @TypeConverters({com.fuchsundlowe.macrolife.DataObjects.TypeConverters.class})
    private abstract class RoomDatabaseObject extends RoomDatabase {
        public abstract NewDAO newDAO();
    }


}
