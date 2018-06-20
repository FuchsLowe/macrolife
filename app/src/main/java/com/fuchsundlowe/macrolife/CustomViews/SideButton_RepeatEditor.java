package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;

import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.R;

// A simple class that
public class SideButton_RepeatEditor extends android.support.v7.widget.AppCompatButton {

    public DayOfWeek dayOfWeek;

    public SideButton_RepeatEditor(Context context, DayOfWeek dayOfWeek, OnClickListener clickListener) {
        super(context);
        this.dayOfWeek = dayOfWeek;
        switch (dayOfWeek) {
            case monday:
                setText(R.string.Monday_Short);
                break;
            case tuesday:
                setText(R.string.Tuesday_Short);
                break;
            case wednesday:
                setText(R.string.Wednesday_Short);
                break;
            case thursday:
                setText(R.string.Thursday_Short);
                break;
            case friday:
                setText(R.string.Friday_Short);
                break;
            case saturday:
                setText(R.string.Saturday_Short);
                break;
            case sunday:
                setText(R.string.Sunday_Short);
                break;
        }
        setOnClickListener(clickListener);
    }

}
