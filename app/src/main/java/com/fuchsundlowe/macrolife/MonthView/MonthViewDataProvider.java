package com.fuchsundlowe.macrolife.MonthView;

import android.arch.lifecycle.LiveData;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;

import java.util.List;

public interface MonthViewDataProvider extends DataProviderNewProtocol {
    LiveData<List<TaskObject>> tasksForAYear(int year);
    LiveData<List<RepeatingEvent>> eventsForAYear(int year);
}
