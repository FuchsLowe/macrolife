package com.fuchsundlowe.macrolife.Interfaces;


import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

// Used by fragments to report changes back to parent and sign up for
public interface P1 {
    // Used to report the change made in one of TaskEvents so it should be re-sifted
    void reportChange(TaskEventHolder beingChanged);
    void subscribeToCompleted(P2 protocol);
    void subscribeToOverdue(P2 protocol);
    void subscribeToUnassigned(P2 protocol);
    void subscribeToUpcoming(P2 protocol);
}
