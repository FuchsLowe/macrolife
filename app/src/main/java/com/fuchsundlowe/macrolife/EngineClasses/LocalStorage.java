package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;

import java.util.ArrayList;
import java.util.Calendar;

public class LocalStorage implements DataProviderNewProtocol {

    private static LocalStorage self;

    // is singleton:
    public static  @Nullable LocalStorage getInstance(@Nullable Context context) {
        if (self != null) {
            return self;
        } else if (context != null) {
            self = new LocalStorage(context);
            return self;
        } else { return null; }
    }

    private LocalStorage(Context context) {
        // Deals with databse initialization ofc
    }

    @Override
    public LiveData<ArrayList<TaskObject>> getTasksFor(Calendar day) {
        return null;
    }
    @Override // Static return value
    public ComplexGoal findComplexGoal(int byID) {
        return null;
    }
}
