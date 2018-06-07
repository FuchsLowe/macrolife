package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.*;

/**
 * Created by macbook on 1/31/18.
 * This is the class that manages Room database implemnetation.
 */
@Deprecated
@Database(version = 2, entities = {ComplexGoal.class, ListMaster.class, ListObject.class,
                                    OrdinaryEventMaster.class, RepeatingEvent.class,
                                    RepeatingEventMaster.class, SubGoalMaster.class})
@android.arch.persistence.room.TypeConverters({TypeConverters.class})
public abstract class DataProvider extends RoomDatabase {

    public abstract DAO daoObject();
}
