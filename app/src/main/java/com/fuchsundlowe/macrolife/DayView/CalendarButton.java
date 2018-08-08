package com.fuchsundlowe.macrolife.DayView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Calendar;

/*
 * TODO: Button Requirements:
 * if its weekend then color is different
 * if current day then gets underline color
 * if not currentMonth, then color is grayish
 * if selected then it gets a rectangle around it to indicate slection
 */

public class CalendarButton extends android.support.v7.widget.AppCompatButton {

    private boolean isWeekend, isCurrentDay, isCurrentMonth, isSelected;
    private Paint selectorMarker;
    private int STROKE_WIDTH = 5;
    private Calendar timeValue;

    public CalendarButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        STROKE_WIDTH = dpToPixConverter(STROKE_WIDTH);

        selectorMarker = new Paint();
        selectorMarker.setStrokeWidth(STROKE_WIDTH);
        selectorMarker.setStyle(Paint.Style.STROKE);
    }

    public void defineButton(Calendar timeValue, boolean isSelected, boolean isCurrentDay, boolean
                             isCurrentMonth) {
        this.timeValue = timeValue;
        this.isCurrentDay = isCurrentDay;
        this.isSelected = isSelected;
        this.isCurrentMonth = isCurrentMonth;
        isWeekend = timeValue.get(Calendar.DAY_OF_WEEK) == 1 || timeValue.get(Calendar.DAY_OF_WEEK) == 7;
        this.setText(String.valueOf(timeValue.get(Calendar.DAY_OF_MONTH)));
        requestLayout();
    }

    public Calendar getTimeValue() {
        return this.timeValue;
    }

    @Deprecated
    public void characterizeDay(int day, boolean weekend, boolean currentDay,
                                boolean currentMonth, boolean selected) {
        this.setText(day);
        isWeekend = weekend;
        isCurrentDay = currentDay;
        isCurrentMonth = currentMonth;
        isSelected = selected;

        if (currentMonth) {
            if (weekend) {
                this.setTextColor(Color.RED);
            }
        } else {
            if (weekend) {
                this.setTextColor(Color.CYAN);
            } else {
                this.setTextColor(Color.DKGRAY);
            }
        }

        requestLayout();
    }
    public void toggleSelected(boolean isSelected) {
        if (this.isSelected != isSelected) {
            this.isSelected = isSelected;
            requestLayout();
        } // we don't have to redraw the button then...
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (isSelected) {
            canvas.drawRect(0,0, getWidth(),getHeight(), selectorMarker);
        } else if (isCurrentDay){
            canvas.drawLine(0, getHeight(),getWidth(), getHeight(), selectorMarker);
        }
    }

    private int dpToPixConverter(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
}
