package com.fuchsundlowe.macrolife.DataObjects;

/**
 * Simple enum of days in a week with numerical values for comparison with Calendar class
 */
public enum DayOfWeek {

    monday(2), tuesday(3), wednesday(4), thursday(5), friday(6), saturday(7), sunday(1), universal(0);

    private int value;

    DayOfWeek(int value) {
        this.value = value;
    }
    public int getValue() { return value;}
}
