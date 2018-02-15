package com.fuchsundlowe.macrolife.CustomViews;

/**
 * Created by macbook on 2/13/18.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;


/**
 * Created by macbook on 2/13/18.
 */

public class SimpleChronoView extends View {

    // StandardValues
    private int LINE_COLOR = Color.BLACK;
    private int SCALE_FACTOR = 10; // Defines how many hours we will show at screen at one time?
    private int RIGHT_PADDING = 5;
    private int LEFT_PADDING = 10;
    private boolean SHOW_DOTTED_LINE = false; //TODO: Not yet implemented
    private boolean SHOW_HOURS = false;
    private int LINE_WIDTH = 1;
    private int ROW_IN_DP = 36;
    private boolean ROW_BY_SCREEN = true; // If we should render by screen scale or by abs dp
    private int TEXT_SIZE = 22;

    // Variables to be calculated
    private Paint lineMarker;
    private Paint textMarker;
    private Context context;
    private int lineSpacing;
    private int calculatedLineWidth;
    private int calculatedTextSize;


    // Public constructor that makes initialization of values as well at the same time
    public SimpleChronoView(Context context) {
        super(context);
        this.context = context;

        calculatedLineWidth = dpToPixConverter(LINE_WIDTH);
        calculatedTextSize = dpToPixConverter(TEXT_SIZE);
        // Defines line Marker for dots
        lineMarker = new Paint();
        lineMarker.setStrokeWidth(calculatedLineWidth);
        lineMarker.setColor(LINE_COLOR);
        lineMarker.setAlpha(255);

        textMarker = new Paint();
        textMarker.setAlpha(255);
        textMarker.setColor(LINE_COLOR);
        textMarker.setTextSize(calculatedTextSize);

    }

    // Methods:

    // This method updates preferences that have been established in SharedPreferences
    public void updatePreferences() {

    }

    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }

    // If passed null defaults to settings, else implements either or
    private String[] getTimeRepresentation(Boolean showAM_PM) {
        if (showAM_PM !=null) {
            if (showAM_PM) {
                return getAmerican();
            } else {
                return getHourly();
            }
        } else {
            if (SHOW_HOURS) {
                return getHourly();
            } else {
                return getAmerican();
            }
        }
    }

    // Returns string[] of hours like 1:00, 13:00 etc
    private String[] getHourly() {
        String[] toReturn = new String[24];

        for (int i = 0; i<24; i++) {
            if (i == 0) {
                toReturn[i] = "00:00";
            } else {
                toReturn[i] = i + ":00";
            }
        }
        return toReturn;
    }

    // Returns string[] of time in PM/AM representation starting from 12AM
    private String[] getAmerican() {
        String[] toReturn = new String[24];
        toReturn[0] = "12AM";
        for (int i = 1; i < 12; i++) {
            toReturn[i] = i+"AM";
        }
        toReturn[12] = "12PM";
        for (int b = 1; b<12;b++) {
            toReturn[12+b] = b+"PM";
        }


        return toReturn;
    }

    private int calculateRowHeight(boolean byScreenScale, int screenHeight) {
        if (byScreenScale) {
            return screenHeight / SCALE_FACTOR;
        } else {
            return dpToPixConverter(ROW_IN_DP);
        }
    }
    // Calculates text size so it can offset the line
    private float maxTextSize(String[] textVals) {
        float max = 0;
        for (String text: textVals) {
            max = Math.max(max, textMarker.measureText(text));
        }
        return max;
    }

    // The Lifecycle events:

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Can draw on this canvas here because its gonna be static
        String[] time = getTimeRepresentation(true);
        float lineOffset = maxTextSize(time);
        Log.d("MaxTextSize is: ", (String.valueOf(lineOffset)));
        // Drawing:
        for (int i = 0; i<24; i++) {
            int y = i*lineSpacing;
            int x = 10; // To be calculated by the maxWidth of text
            canvas.drawText(time[i], x, y, textMarker);
            canvas.drawLine(x + lineOffset + dpToPixConverter(5) , y, canvas.getWidth(), y,lineMarker);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /* Needs to accomodate 24H show...
        *So this is the deal, I need to divide by 10 and then I need to add 24
        */
        int width = 0;
        int height = 0;

        lineSpacing = calculateRowHeight(ROW_BY_SCREEN, heightMeasureSpec);

        height = lineSpacing * 24; // Because day has 24 hours
        width = widthMeasureSpec - (RIGHT_PADDING + LEFT_PADDING);

        setMeasuredDimension(width,height);
    }

    @Override
    public void invalidate() {
        updatePreferences();
        super.invalidate();

    }
}
