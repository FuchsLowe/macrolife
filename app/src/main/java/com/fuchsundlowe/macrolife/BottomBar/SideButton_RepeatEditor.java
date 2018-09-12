package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.R;

// A simple class that
public class SideButton_RepeatEditor extends android.support.v7.widget.AppCompatButton {

    public DayOfWeek dayOfWeek;
    private boolean selected = false;
    public SideButton_RepeatEditor(Context context) {
        super(context);
    }

    public SideButton_RepeatEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideButton_RepeatEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void defineMe(DayOfWeek dayOfWeek, OnClickListener clickListener) {
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

    public void highliteSelection(boolean select) {
        this.selected = select;
        if (select) {
            this.setZ(20);
            setBackgroundColor(Color.YELLOW);
            // TODO: Change implementation to reflect selection
        } else {
            this.setZ(10);
            setBackgroundColor(Color.LTGRAY);
        }
    }

    public boolean isSelected() {
        return this.selected;
    }
}
