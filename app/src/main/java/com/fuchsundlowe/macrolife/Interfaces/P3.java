package com.fuchsundlowe.macrolife.Interfaces;

public interface P3 {

    void markTasksReady();
    void markEventsReady();

    void changedOverdue();
    void changedUpcoming();
    void changedCompleted();
    void changedUnassigned();

    void flushChanges();
}
