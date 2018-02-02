package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.*;

/**
 * Created by macbook on 1/31/18.
 * This is the class that manages Room database implemnetation.
 */

@Database(version = 1, entities = {OrdinaryEventMaster.class})
@android.arch.persistence.room.TypeConverters({TypeConverters.class})
public abstract class DataProvider extends RoomDatabase {

    public abstract DAO daoObject();
}
