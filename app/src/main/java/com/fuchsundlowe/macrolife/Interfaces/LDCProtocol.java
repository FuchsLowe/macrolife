package com.fuchsundlowe.macrolife.Interfaces;


import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

// Used by fragments to report changes back to parent and sign up for, implemented by LDC...
public interface LDCProtocol {
    // Used to report the change made in one of TaskEvents so it should be re-sifted
    void subscribeToCompleted(LDCToFragmentListView protocol);
    void subscribeToOverdue(LDCToFragmentListView protocol);
    void subscribeToUnassigned(LDCToFragmentListView protocol);
    void subscribeToUpcoming(LDCToFragmentListView protocol);
    void destroy();
}
