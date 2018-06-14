package com.fuchsundlowe.macrolife.Interfaces;

import android.support.annotation.Nullable;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

public interface EditTaskProtocol {
    void saveTask(TaskObject task, @Nullable RepeatingEvent event);
    void clickOnMod(TaskObject.Mods mod);
}
