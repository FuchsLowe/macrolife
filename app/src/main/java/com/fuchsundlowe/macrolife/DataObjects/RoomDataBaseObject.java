package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {ComplexGoal.class, TaskObject.class, ListObject.class, RepeatingEvent.class},
        version = 1, exportSchema = false)
@android.arch.persistence.room.TypeConverters({com.fuchsundlowe.macrolife.DataObjects.TypeConverters.class})
public abstract class RoomDataBaseObject extends RoomDatabase {
    public abstract NewDAO newDAO();
}