package com.fuchsundlowe.macrolife.DayView;

/**
 * Created by macbook on 2/13/18.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ChronoView extends ViewGroup {

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
    private int TIMER_UPDATE_INTERVAL = 1000; // 10 seconds

    // Variables to be calculated
    private Paint lineMarker;
    private Paint textMarker;
    private Context context;
    private int timeUnitSize;
    private int calculatedLineWidth;
    private int calculatedTextSize;
    private SharedPreferences preferences;
    private Calendar dayDisplayed;
    private Timer timerLoop;
    private View tempTimeDisplayer;
    private DataProviderNewProtocol dataProvider;
    private GestureDetectorCompat longPressDetector;
    private LocalBroadcastManager manager;


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

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SHOW_HOURS = preferences.getBoolean(Constants.TIME_REPRESENTATION, false);

        timeUnitSize = dpToPixConverter(preferences.getInt(Constants.HOUR_IN_PIXELS,108));

        tempTimeDisplayer = new View(getContext());
        tempTimeDisplayer.setBackgroundColor(Color.RED);
        tempTimeDisplayer.setVisibility(GONE);
        timerLoop(true);

        registerDragAndDropListeners();

        longPressDetector = new GestureDetectorCompat(context, new LongPressDetector());

    }

    // Methods:
    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
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
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        // Whatever we will do with it?
                        if (tempTimeDisplayer != null) {
                            Calendar toPass = Calendar.getInstance();
                            //toPass.set(Calendar.HOUR_OF_DAY, timeOfDay);
                            tempTimeDisplayer.setY(getPixelLocationOf(toPass,
                                    true));
                        }
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
                        if (event.getClipData().getDescription().getLabel() == Constants.TASK_OBJECT) {
                            return true;
                        } else {
                            return false;
                        }
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DROP:
                        Object dropData = event.getLocalState();
                        long timeDifferenceBetweenOldStartAndEndTime;
                        Calendar newStartTime = getTimeLocationOf(event.getY());
                        Calendar newEndTime;
                        if (dropData instanceof TaskObject) {
                            timeDifferenceBetweenOldStartAndEndTime = calculateTimeDifference(
                                    ((TaskObject) dropData).getTaskStartTime(),
                                    ((TaskObject) dropData).getTaskEndTime());
                            newEndTime = (Calendar) newStartTime.clone();
                            newEndTime.add(Calendar.MILLISECOND, (int)
                                    timeDifferenceBetweenOldStartAndEndTime);
                            Task_DayView viewToPresent = getDisplayedTask(
                                    ((TaskObject) dropData).getHashID());
                            if (viewToPresent != null) {
                                // Update time
                                viewToPresent.setNewTaskTimes(newStartTime, newEndTime);
                            } else {
                                // Create new view with new data:
                                ((TaskObject) dropData).setTaskStartTime(newStartTime);
                                ((TaskObject) dropData).setTaskEndTime(newEndTime);
                                addNewTask((TaskObject) dropData, null);
                            }

                        } else if (dropData instanceof  RepeatingEvent) {
                            timeDifferenceBetweenOldStartAndEndTime = calculateTimeDifference(
                                    ((RepeatingEvent) dropData).getStartTime(),
                                    ((RepeatingEvent) dropData).getEndTime());
                            newEndTime = (Calendar) newStartTime.clone();
                            newEndTime.add(Calendar.MILLISECOND, (int)
                                    timeDifferenceBetweenOldStartAndEndTime);
                            Task_DayView viewToPresent = getDisplayedTask(
                                    ((RepeatingEvent) dropData).getHashID());
                            if (viewToPresent != null) {
                                // Update time
                                viewToPresent.setNewTaskTimes(newStartTime, newEndTime);
                            } else {
                                // Create new view with new data:
                                ((RepeatingEvent) dropData).setStartTime(newStartTime);
                                ((RepeatingEvent) dropData).setEndTimeWithReturn(newEndTime);
                                addNewTask(
                                        dataProvider.findTaskObjectBy(((RepeatingEvent) dropData).getParentID()),
                                        (RepeatingEvent)dropData
                                );
                            }
                        } else {
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
    private void sendGlobalEditBroadcast(Calendar taskStartTime) {
        if (manager == null) {
            manager = LocalBroadcastManager.getInstance(getContext());
        }
        Intent intent = new Intent(Constants.INTENT_FILTER_NEW_TASK);
        intent.putExtra(Constants.INTENT_FILTER_FIELD_START_TIME, taskStartTime.getTimeInMillis());
        manager.sendBroadcast(intent);
    }

    // The Lifecycle events:
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        setWillNotDraw(false);

        // Can draw on this canvas here because its gonna be static
        String[] time = getTimeRepresentation();
        float lineOffset = maxTextSize(time);
        // Drawing:
        for (int i = 0; i<24; i++) {
            int y = i * timeUnitSize;
            int x = 10; // To be calculated by the maxWidth of text
            canvas.drawText(time[i], x, y, textMarker);
            canvas.drawLine(x + lineOffset + dpToPixConverter(5) , y, canvas.getWidth(), y, lineMarker);
            canvas.save();
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
            sendGlobalEditBroadcast(getTimeLocationOf(e.getY()));
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
                ((Task_DayView) viewObject).layout((int) LEFT_OFFSET, start, this.getWidth(), end);
            } else {
                //Assuming its only the TimeDisplayer
                int top = getPixelLocationOf(Calendar.getInstance(), true);
                tempTimeDisplayer.layout(20,top, this.getWidth() - 20, top + 15);
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
    public void setData(@Nullable List<TaskObject> tasks,
                        @Nullable List<RepeatingEvent> repeatingEvents) {
        if (dataProvider == null) {
            dataProvider = LocalStorage.getInstance(getContext());
        }
        // Replace tasks
        // Remove all exisitng ones
        // create new ones
        // TODO: Temp solution, revisit this
        if (tasks != null) {
            // Remove all taskObjects
            int count = this.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof Task_DayView) {
                    if (!((Task_DayView) child).isRepeatingEvent()) {
                        this.removeView(child);
                    }
                }
            }
            for (TaskObject newTask : tasks) {
                Task_DayView wrapper = new Task_DayView(context, null);
                wrapper.insertData(newTask, null);
                this.addView(wrapper);
            }
        }

        if (repeatingEvents != null) {
            int count = this.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof Task_DayView) {
                    if (((Task_DayView) child).isRepeatingEvent()) {
                        this.removeView(child);
                    }
                }
            }
            for (RepeatingEvent event : repeatingEvents) {
                TaskObject parent = dataProvider.findTaskObjectBy(event.getParentID());
                Task_DayView wrapper = new Task_DayView(context, null);
                wrapper.insertData(parent, event);
                this.addView(wrapper);
            }
        }

        /* Old implemnetation that might not work
        // Checks if these exist, either has a pair, or doesn't or gets removed?
        // Grabs all task ID's that we have currently displayed in ChronoView
        HashSet<Task_DayView> allTasksInCurrentView = new HashSet<>();
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof Task_DayView) {
                allTasksInCurrentView.add((Task_DayView) child);
            }
        }

        // Checks if there is any object to remove from:
        HashSet<Integer> arrivedTasksIDs = new HashSet<>();
        if (tasks != null) {
            for (TaskObject task : tasks) {
                arrivedTasksIDs.add(task.getHashID());
            }
        }
        if (repeatingEvents != null) {
            for (RepeatingEvent event : repeatingEvents) {
                arrivedTasksIDs.add(event.getHashID());
            }
        }
        HashSet<Task_DayView> itemsToDelete = new HashSet<>();
        if (!allTasksInCurrentView.isEmpty()) {
            for (Task_DayView dayView : allTasksInCurrentView) {
                if (!arrivedTasksIDs.contains(dayView)) {
                  itemsToDelete.add(dayView);
                }
            }
            // Now We delete items that are to be ousted:
            for (Task_DayView valueToDelete : itemsToDelete) {
                //TODO: Does this require redraw?
                this.removeView(valueToDelete);
            }
        }

        // Either updates or adds new task to ChronoView
        HashSet<Task_DayView> itemsToAdd = new HashSet<>();
        // Now we check to determine if we add or update items:
        if (repeatingEvents != null) {
            for (RepeatingEvent event : repeatingEvents) {

                 // If task exists, we attempt to update it, if it doesn't then we create a new one

                boolean didUpdate = false;
                Task_DayView dayView;
                for (Iterator<Task_DayView> it = allTasksInCurrentView.iterator(); it.hasNext(); ) {
                    dayView = it.next();
                    if (dayView.getActiveHashID() == event.getHashID()) {
                        dayView.insertData(dataProvider.findTaskObjectBy(event.getParentID()), event);
                        didUpdate = true;
                        break;
                    }
                }// end of for loop
                if (!didUpdate) {
                    addNewTask(dataProvider.findTaskObjectBy(event.getParentID()), event);
                }
            }
        }
        if (tasks != null) {
            for (TaskObject task : tasks) {
                Task_DayView dayView;
                boolean didUpdate = false;
                for (Iterator<Task_DayView> it = allTasksInCurrentView.iterator(); it.hasNext(); ) {
                    dayView = it.next();
                    if (dayView.getActiveHashID() == task.getHashID()) {
                        dayView.insertData(task, null);
                        didUpdate = true;
                        break;
                    }
                }// end of for loop
                if (!didUpdate) {
                    addNewTask(task, null);
                }
            }
        }
        */
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
