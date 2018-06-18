package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;

// This protocol defines a communication back channel to Activity from Bottom Bar Engine Protocol implementor
public interface BottomBarCommunicationProtocol {
    void reportDeleteTask(TaskObject objectToDelete);
}
