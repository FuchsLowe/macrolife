package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

// Holds the ChronoView type of view for RepeatEditor
public class CronoViewFor_RepeatEditor extends ViewGroup {

    // StandardValues
    private int LINE_COLOR = Color.BLACK;
    private int SCALE_FACTOR = 10; // Defines how many hours we will show at screen at one time...
    private int RIGHT_PADDING = 5;
    private int LEFT_PADDING = 10;
    private float LEFT_OFFSET = 24;
    private boolean SHOW_HOURS = false;
    private int LINE_WIDTH = 1;
    private int ROW_IN_DP = 36;
    private boolean ROW_BY_SCREEN = true; // If we should render by screen scale or by abs dp
    private int TEXT_SIZE = 22;
    private float lineOffset;

    // Description Values passed from RepeatEventEditor:
    private List<RepeatingTask_RepeatEditor> events;
    private RepeatChronoViewProtocol dayProtocol;

    // Variables to be calculated
    private Paint lineMarker;
    private Paint textMarker;
    private int timeUnitSize;
    private int calculatedLineWidth;
    private int calculatedTextSize;
    private SharedPreferences preferences;
    private DataProviderNewProtocol dataProvider;
    private GestureDetectorCompat longPressDetector;
    private TaskObject objectPresented;


    public CronoViewFor_RepeatEditor(@NonNull Context context) {
        super(context);
        dataProvider = LocalStorage.getInstance(context);

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

        this.setWillNotDraw(false);

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SHOW_HOURS = preferences.getBoolean(Constants.TIME_REPRESENTATION, false);

        timeUnitSize = dpToPixConverter(preferences.getInt(Constants.HOUR_IN_PIXELS,108));

        longPressDetector = new GestureDetectorCompat(context, new LongPressDetector());

    }

    // LifeCycle:
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setWillNotDraw(false);

        // Can draw on this canvas here because its gonna be static
        String[] time = getTimeRepresentation();
        lineOffset = maxTextSize(time);
        // Drawing:
        for (int i = 0; i<24; i++) {
            int y = i* timeUnitSize;
            int x = 10; // To be calculated by the maxWidth of text
            canvas.drawText(time[i], x, y, textMarker);
            canvas.drawLine(x + lineOffset + dpToPixConverter(5) , y, canvas.getWidth(), y, lineMarker);
        }
        canvas.save();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width, height;

        height = timeUnitSize * 24; // Because day has 24 hours
        width = widthMeasureSpec - (RIGHT_PADDING + LEFT_PADDING);

        setMeasuredDimension(width, height);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (lineOffset <= 0) {
            lineOffset = maxTextSize(getTimeRepresentation());
        }
        int childCount = getChildCount();
        int top = 0;
        int bottom = 0;
        int left = (int) lineOffset;
        int right = getWidth() - RIGHT_PADDING;
        for (int i = 0; i<childCount; i++) {
            measureChildren(getWidth(), getHeight());
            View child = getChildAt(i);
            if (child instanceof RepeatingTask_RepeatEditor && child.getVisibility() == VISIBLE) {
                child.layout(left,
                        getPixelLocationOf(((RepeatingTask_RepeatEditor) child).getTaskStartTime(), false),
                        right,
                        getPixelLocationOf(((RepeatingTask_RepeatEditor) child).getTaskEndTime(), false)
                        );
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        longPressDetector.onTouchEvent(event);
        return true;
    }

    // This is called to initiate the view with values
    public void defineMe(TaskObject masterTask, List<RepeatingTask_RepeatEditor> eventHolder, RepeatChronoViewProtocol parentProtocol) {
        this.objectPresented = masterTask;
        this.events = eventHolder;
        this.dayProtocol = parentProtocol;

    }
    // Called by RepeatEventEditor when changes in describing values have been changed.
    public void updateValues() {
        /*
         * what this guy needs to do?
         *
         * Check for any inconsistencies from the values and then presents only ones that are not
         * reminders... but full fledged events...
         */
        if (events != null) {
            this.removeAllViews();
            // The adding phase...
            for (RepeatingTask_RepeatEditor event: events) {
                if (!event.isReminder() && event.getDay() == dayProtocol.getDay()) {
                    this.addView(event);
                }
            }
        }
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getParent() instanceof ScrollView) {
            // We move either to first task or we go to time of the day
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int pixelDestination = 0;
                    // If there is a task
                    if (getChildCount() > 0) {
                        pixelDestination = (int) getChildAt(0).getY();
                    } else { // Else we go to current time in day
                        Calendar currentTime = Calendar.getInstance();
                        pixelDestination = getPixelLocationOf(currentTime, true);
                    }
                    ((ScrollView) getParent()).smoothScrollTo(0, pixelDestination);
                }
            }, 500);
        }

    }

    // Methods:
    private int dpToPixConverter(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private String[] getTimeRepresentation(){
        if (SHOW_HOURS) {
            return getHourly();
        } else {
            return getAmerican();
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
        float max = 0f;
        for (String text: textVals) {
            max = Math.max(max, textMarker.measureText(text));
        }
        LEFT_OFFSET = max;
        return max;
    }
    private Calendar getTimeLocationOf(float pixelY) {
        float hourInterval = pixelY / timeUnitSize;
        float minuteInterval = (pixelY % timeUnitSize) / (timeUnitSize / 4);

        Calendar timeToReturn = Calendar.getInstance();
        timeToReturn.set(Calendar.HOUR_OF_DAY, (int) hourInterval);
        timeToReturn.set(Calendar.MINUTE, (int) (minuteInterval * 15));

        return timeToReturn;
    }
    private int getPixelLocationOf(Calendar time, boolean precisionInMinute) {
        if (precisionInMinute) {
            float minutePerPixel = timeUnitSize / 60;
            int calendarTime = time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
            return (int) (minutePerPixel * calendarTime);
        } else {
            int calendarTime = time.get(Calendar.HOUR_OF_DAY) * timeUnitSize +
                    ((time.get(Calendar.MINUTE) / 15) * (timeUnitSize /4));
            return calendarTime;
        }
    }

    class LongPressDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            RepeatingTask_RepeatEditor toAddView = new RepeatingTask_RepeatEditor(getContext());
            Calendar startTime = getTimeLocationOf(e.getY());
            if (dayProtocol.getDay() != DayOfWeek.universal) {
                startTime.set(Calendar.DAY_OF_WEEK, dayProtocol.getDay().getValue());
            }
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.MINUTE, 30); // NOTE: Default amount is 30 min.
            DayOfWeek dayToPass;
            switch (dayProtocol.getDay()) {
                case monday:
                    dayToPass = DayOfWeek.monday;
                    break;
                case tuesday:
                    dayToPass = DayOfWeek.tuesday;
                    break;
                case wednesday:
                    dayToPass = DayOfWeek.wednesday;
                    break;
                case thursday:
                    dayToPass = DayOfWeek.thursday;
                    break;
                case friday:
                    dayToPass = DayOfWeek.friday;
                    break;
                case saturday:
                    dayToPass = DayOfWeek.saturday;
                    break;
                case sunday:
                    dayToPass = DayOfWeek.sunday;
                    break;
                default:
                    dayToPass = DayOfWeek.universal;
            }
            toAddView.defineMe(objectPresented.getTaskName(), startTime, endTime, dayToPass);
            events.add(toAddView);
            updateValues();
        }

    }
}
