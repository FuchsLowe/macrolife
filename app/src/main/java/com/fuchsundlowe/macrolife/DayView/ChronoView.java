package com.fuchsundlowe.macrolife.DayView;

/**
 * Created by macbook on 2/13/18.
 */

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.DayView.DayDisplay_DayView.TaskEventHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ChronoView extends ViewGroup {

    // StandardValues
    private int LINE_COLOR = Color.BLACK;
    private int SCALE_FACTOR = 10; // Defines how many hours we will show at screen at one time...
    private int RIGHT_PADDING = 5;
    private int LEFT_PADDING = 10;
    private boolean SHOW_HOURS = false;
    private int LINE_WIDTH = 1;
    private int ROW_IN_DP = 36;
    private boolean ROW_BY_SCREEN = true; // If we should render by screen scale or by abs dp
    private int TEXT_SIZE = 22;
    private int TIMER_UPDATE_INTERVAL = 1000; // 10 seconds
    private float lineStartMark = 0;

    // Variables to be calculated
    private Paint lineMarker;
    private Paint textMarker;
    private Context context;
    private int timeUnitSize;
    private int calculatedLineWidth;
    private int calculatedTextSize;
    private Calendar dayDisplayed;
    private Timer timerLoop;
    private View tempTimeDisplayer;
    private DataProviderNewProtocol dataProvider;
    private GestureDetectorCompat longPressDetector;
    private LocalBroadcastManager manager;
    private String[] time;



    // Public constructor that makes initialization of values as well at the same time
    public ChronoView(Context context, Calendar dayDisplaying) {
        super(context);

        this.context = context;
        this.dayDisplayed = dayDisplaying;

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

        SharedPreferences preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SHOW_HOURS = preferences.getBoolean(Constants.TIME_REPRESENTATION, false);

        timeUnitSize = dpToPixConverter(preferences.getInt(Constants.HOUR_IN_PIXELS,108));

        createTempTimerDisplay();
        timerLoop(true);

        registerDragAndDropListeners();

        longPressDetector = new GestureDetectorCompat(context, new LongPressDetector());
        dataProvider = LocalStorage.getInstance(context);

        // Calculating the position to offset the Hour Divider line
        time = getTimeRepresentation();
        float lineOffset = maxTextSize(time);
        lineStartMark = lineOffset + dpToPixConverter(5);
    }

    // Methods:
    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    // Generates the Time Format to be drawn on the ChronoView background.
    private String[] getTimeRepresentation(){
        String[] toReturn = new String[24];
        if (SHOW_HOURS) { // Hour system of 24h in a day
            for (int i = 0; i<24; i++) {
                if (i == 0) {
                    toReturn[i] = "00:00";
                } else {
                    toReturn[i] = i + ":00";
                }
            }
        } else { // American system of AM/PM
            toReturn[0] = "12AM";
            for (int i = 1; i < 12; i++) {
                toReturn[i] = i+"AM";
            }
            toReturn[12] = "12PM";
            for (int b = 1; b<12;b++) {
                toReturn[12+b] = b+"PM";
            }
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
        return max;
    }
    private void createTempTimerDisplay() {
        tempTimeDisplayer = new View(getContext());
        tempTimeDisplayer.setBackgroundColor(Color.RED);
        tempTimeDisplayer.setVisibility(GONE);
    }
    public void timerLoop(boolean enabled) { // Who should manage the loop? Idealy it would be Someone
        // who has lifecyce of DayView in it...
        Calendar currentDay = Calendar.getInstance();
        if (currentDay.get(Calendar.YEAR) == dayDisplayed.get(Calendar.YEAR) &&
                currentDay.get(Calendar.DAY_OF_YEAR) == dayDisplayed.get(Calendar.DAY_OF_YEAR)) {

            tempTimeDisplayer.setVisibility(VISIBLE);
            if (timerLoop == null) {
                timerLoop = new Timer();
            }
            if (enabled) {
                final Handler main = new Handler(Looper.getMainLooper());
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // Whatever we will do with it?
                        if (tempTimeDisplayer == null) {
                           createTempTimerDisplay();
                        }
                        final Calendar toPass = Calendar.getInstance();
                        //toPass.set(Calendar.HOUR_OF_DAY, timeOfDay);
                        main.post(new Runnable() {
                            @Override
                            public void run() {
                                tempTimeDisplayer.setY(getPixelLocationOf(toPass,
                                        true));
                            }
                        });

                    }
                };
                timerLoop.scheduleAtFixedRate(timerTask, 0, TIMER_UPDATE_INTERVAL);
            } else {
                timerLoop.cancel();
            }
        }
    }
    private void registerDragAndDropListeners() {
        this.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // determine if I should accept this
                        if (event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT)
                                ||
                                event.getClipDescription().getLabel().equals(Constants.REPEATING_EVENT)) {
                            Object eventDragged = event.getLocalState();
                            // determine if these exist in current view hierarchy and remove them if so
                            if (eventDragged instanceof TaskObject) {
                                for (int i = 0; i< getChildCount(); i++) {
                                    View child = getChildAt(i);
                                    if (child instanceof Task_DayView && !((Task_DayView) child).isRepeatingEvent()) {
                                        if (((Task_DayView) child).getTaskObjectHashID() == ((TaskObject) eventDragged).getHashID()) {
                                            removeView(child);
                                            break;
                                        }
                                    }
                                }
                            } else if (eventDragged instanceof RepeatingEvent) {
                                for (int i = 0; i < getChildCount(); i++) {
                                    View child = getChildAt(i);
                                    if (child instanceof Task_DayView && ((Task_DayView) child).isRepeatingEvent()) {
                                        if (((Task_DayView) child).getActiveHashID() == ((RepeatingEvent) eventDragged).getHashID()) {
                                            removeView(child);
                                            break;
                                        }
                                    }
                                }
                            }
                            return true;
                        } else {
                            return false;
                        }

                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                       break;
                    case DragEvent.ACTION_DROP:
                        // Grab the data and set new times and dates for task...
                        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  'at' HH:mm:ss z");

                        Object dropData = event.getLocalState();
                        // Determining the start location based on the topBar of the DragShadow
                        ClipData.Item itemHeight = event.getClipData().getItemAt(0);
                        Integer height = Integer.valueOf(String.valueOf(itemHeight.getText()));

                        Calendar newStartTime = getTimeLocationOf(event.getY() - (height/2));
                        Calendar newEndTime = (Calendar) newStartTime.clone(); // not set yet to desired time
                        // I need to determine if there is time defined, if not then I need to set
                        // default 30 min duration...
                        if (dropData instanceof TaskObject) {
                            if (((TaskObject) dropData).getTimeDefined() == TaskObject.TimeDefined.dateAndTime &&
                                    ((TaskObject) dropData).getTaskEndTime() != null) {
                                // Calculate the duration of task in previous terms:
                                /* This is used instead of dropData because it is known that fields get
                                 * corrupted during transport, if that even makes sense. What was happening
                                 * was that the fields of start and end time would change values ( get
                                 * multiplied by 8 or possibly some other value ) and then they would't
                                 * make sense.
                                 */
                                TaskObject toHandle = dataProvider.findTaskObjectBy(((TaskObject) dropData).getHashID());

                                Calendar oldStartTime = toHandle.getTaskStartTime();
                                Calendar oldEndTime = toHandle.getTaskEndTime();

                                // TEST: Displaying data
                                String oldStartTimeString = format.format(oldStartTime.getTime());
                                String oldEndTimeString = format.format(oldEndTime.getTime());
                                Log.d("A1: ",
                                        "\nRECEIVED DRAG" +
                                                "\nSTART: " + oldStartTimeString +
                                                "\nEND:" + oldEndTimeString
                                        );
                                //END TEST//

                                long oldTaskDuration = oldEndTime.getTimeInMillis() - oldStartTime.getTimeInMillis();

                                newEndTime.add(Calendar.MILLISECOND, (int) oldTaskDuration);
                            } else {
                                // we set default 30 min duration
                                newEndTime.add(Calendar.MINUTE, 30);
                            }

                            ((TaskObject) dropData).setTaskStartTime(newStartTime);
                            ((TaskObject) dropData).setTaskEndTime(newEndTime);
                            ((TaskObject) dropData).setTimeDefined(TaskObject.TimeDefined.dateAndTime);

                            dataProvider.saveTaskObject((TaskObject) dropData);
                        } else if (dropData instanceof RepeatingEvent) {
                            // So It can only come from ChronoView itself? It can't come from anywhere else
                            // Then we only change the start and end time...
                            RepeatingEvent eventToHandle = dataProvider.getEventWith(((RepeatingEvent) dropData).getHashID());
                            if (eventToHandle!= null) {
                                long durationOfEvent = eventToHandle.getEndTime().getTimeInMillis()
                                        - eventToHandle.getStartTime().getTimeInMillis();
                                newEndTime.add(Calendar.MILLISECOND, (int) durationOfEvent);
                                ((RepeatingEvent) dropData).setStartTime(newStartTime);
                                ((RepeatingEvent) dropData).setEndTimeWithReturn(newEndTime);
                                // TODO Reposition it so you remove delay
                                dataProvider.saveRepeatingEvent((RepeatingEvent) dropData);
                            }
                        } else {
                            // Then we can't handle the drag
                            return false;
                        }
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                }
             return true;
            }
        });
    }
    private void sendCreateNewTaskWithLocationBroadcast(Calendar taskStartTime) {
        if (manager == null) {
            manager = LocalBroadcastManager.getInstance(getContext());
        }
        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_TASK);
        intent.putExtra(Constants.INTENT_FILTER_FIELD_START_TIME, taskStartTime.getTimeInMillis());
        manager.sendBroadcast(intent);
    }
    private void sendRequestRecommendationFetcherBroadcast() {
        if (manager == null) {
            manager = LocalBroadcastManager.getInstance(getContext());
        }
        Intent intent = new Intent(Constants.INTENT_FILTER_RECOMMENDATION);
        manager.sendBroadcastSync(intent);
    }

    // The Lifecycle events:
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        setWillNotDraw(false);

        // Drawing of hour text and line separator:
        for (int i = 0; i<24; i++) {
            int y = i * timeUnitSize;
            canvas.drawText(time[i], 0, y, textMarker);
            canvas.drawLine(lineStartMark , y, canvas.getWidth(), y, lineMarker);
            //canvas.save(); don't think there is need for this...
        }
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.addView(tempTimeDisplayer);
    }

    //Touch Events:
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        longPressDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sendRequestRecommendationFetcherBroadcast();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                // TODO: Possibly signal global edit done... to mark that user is done editing!
                break;
        }
        return true;
    }
    private class LongPressDetector extends GestureDetector.SimpleOnGestureListener  {
        @Override
        public void onLongPress(MotionEvent e) {
            sendCreateNewTaskWithLocationBroadcast(getTimeLocationOf(e.getY()));
        }
    }
    @Nullable
    private Task_DayView findTaskAt(int x, int y) {
        int childrenCountInChronoView = this.getChildCount();
        Rect hitRect = new Rect();
        View viewFound;
        for (int i = 0; i < childrenCountInChronoView ; i++) {
            viewFound = this.getChildAt(i);
            if (viewFound instanceof Task_DayView) {
                viewFound.getHitRect(hitRect);
                if (hitRect.contains(x, y)) {
                    return (Task_DayView) viewFound;
                }
            }
        }
        return null;
    }

    // Layout Technology
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = 0;
        int height = 0;

        height = timeUnitSize * 24; // Because day has 24 hours
        width = widthMeasureSpec - (RIGHT_PADDING + LEFT_PADDING);

        measureChildren(height, width);

        setMeasuredDimension(width, height);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int start, end, countOfChildViews;
        countOfChildViews = this.getChildCount();
        for (int i = 0; i < countOfChildViews; i++) {
            View viewObject = this.getChildAt(i);
            if (viewObject instanceof Task_DayView) {
                Calendar startTime = ((Task_DayView) viewObject).getTaskStartTime();
                Calendar endTime = ((Task_DayView) viewObject).getTaskEndTime();
                // evaluating the position of task
                if (startTime.get(Calendar.DAY_OF_YEAR) == dayDisplayed.get(Calendar.DAY_OF_YEAR)) {
                    start = getPixelLocationOf(startTime, false);
                } else { // means that task started before today
                    start = 0;
                }
                if (endTime.get(Calendar.DAY_OF_YEAR) == dayDisplayed.get(Calendar.DAY_OF_YEAR)) {
                    end = getPixelLocationOf(endTime, false);
                } else { // Means that task doesn't end on this day
                    end = 24 * timeUnitSize;
                }
                // I need distance for 30 min in pixels
                // Displays only minimun time of 30 min
                int thirtyMinInPixels = timeUnitSize / 2;
                int thirtyMinInMilliseconds = 1800000;
                if ((endTime.getTimeInMillis() - startTime.getTimeInMillis()) < thirtyMinInMilliseconds) {
                    end = start + thirtyMinInPixels;
                }

                // The actual laying procedure...
                ((Task_DayView) viewObject).myLayout((int) lineStartMark, start, this.getWidth(), end);
            } else {
                //Assuming its only the TimeDisplay
                int top = getPixelLocationOf(Calendar.getInstance(), true);
                tempTimeDisplayer.layout((int) lineStartMark, top, this.getWidth() - 5, top + 15);
            }
        }

    }
    // Returns the location of specific time in current ChronoLayout...
    private int getPixelLocationOf(Calendar time, boolean precisionInMinute) {
        if (precisionInMinute) {
            /*
            float minutePerPixel = timeUnitSize / 60;
            int calendarTime = time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
            return (int) (minutePerPixel * calendarTime);
            */
            float minutesPerPixel = timeUnitSize / 60;
            return time.get(Calendar.HOUR_OF_DAY) * timeUnitSize + (int)(time.get(Calendar.MINUTE) * minutesPerPixel);
        } else {
            int calendarTime = time.get(Calendar.HOUR_OF_DAY) * timeUnitSize +
                    ((time.get(Calendar.MINUTE) / 15) * (timeUnitSize /4));
            return calendarTime;
        }
    }
    // returns the Time location of YPixel coordinate
    private Calendar getTimeLocationOf(float pixelY) {
        float hourInterval = pixelY / timeUnitSize;
        float minuteInterval = (pixelY % timeUnitSize) / (timeUnitSize / 4);

        Calendar timeToReturn = (Calendar) dayDisplayed.clone();
        timeToReturn.set(Calendar.HOUR_OF_DAY, (int) hourInterval);
        timeToReturn.set(Calendar.MINUTE, (int) (minuteInterval * 15));

        return timeToReturn;
    }


    // Data Manipulation:
    public void insertData(List<TaskEventHolder> data) {
        // Remove all views that are Task_DayView
        List<View> toRemove= new ArrayList<>();
        for (int i =0; i<this.getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof Task_DayView) {
                toRemove.add(view);
            }
        }
        for (View v: toRemove) {
            this.removeView(v);
        }
        // Process of adding new tasks to view hierarchy
        for (TaskEventHolder holder: data) {
            if (holder.isTask()) {
                if (!holder.task.isThisRepeatingEvent()) {
                    addNewTask(holder.task, null);
                } else {
                    // its a repeating value and we don't show it...
                }
            } else {
                addNewTask(dataProvider.findTaskObjectBy(holder.getMasterHashID()), holder.event);
            }
        }
    }
    private void addNewTask(TaskObject task, @Nullable RepeatingEvent event) {
        Task_DayView dayView = new Task_DayView(getContext(), null);
        dayView.insertData(task, event);
        this.addView(dayView);
    }
    private long calculateTimeDifference(Calendar startTime, Calendar endTime) {
        return endTime.getTimeInMillis() - startTime.getTimeInMillis();
    }
    @Nullable
    private Task_DayView getDisplayedTask(int withHashID) {
        int numberOfViewsInChrono = this.getChildCount();
        for (int i = 0; i < numberOfViewsInChrono; i++) {
            View child = this.getChildAt(i);
            if (child instanceof Task_DayView) {
                if (((Task_DayView) child).getActiveHashID() == withHashID) {
                    return (Task_DayView) child;
                }
            }
        }
        return null;
    }

}
