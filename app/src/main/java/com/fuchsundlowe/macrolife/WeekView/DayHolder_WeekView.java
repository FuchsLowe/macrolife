package com.fuchsundlowe.macrolife.WeekView;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.DayView.DayDisplay_DayView;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.DayView.DayDisplay_DayView.TaskEventHolder;
import com.fuchsundlowe.macrolife.R;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * Holds the Week Task of a specific day...
 */
public class DayHolder_WeekView extends FrameLayout implements DayHolderCommunicationInterface {

    private ViewGroup baseView;
    private FrameLayout titleBar;
    private LinearLayout taskBar;
    private TextView dayDescription;
    private Calendar dayThisHolderPresents;
    private DataProviderNewProtocol dataProvider;
    private List<TaskEventHolder> displayedTasks;

    public DayHolder_WeekView(@NonNull Context context) {
        super(context);
        init();
    }
    public DayHolder_WeekView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public DayHolder_WeekView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public DayHolder_WeekView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // Inflate the layout for this fragment and find views
        baseView = (ViewGroup) inflate(this.getContext(), R.layout.fragment_day_holder__week_view, this);
        titleBar = baseView.findViewById(R.id.topBar_dayHolder_weekView);
        taskBar = baseView.findViewById(R.id.linearLayout_dayHolder_weekView);
        dayDescription = baseView.findViewById(R.id.dayDescription_dayHolder_weekView);

        Button onClick = findViewById(R.id.createNewTask_dayHolder_weekView);
        onClick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTask();
            }
        });

        dataProvider = LocalStorage.getInstance(this.getContext());

        defineDragAndDropListener();
    }

    // This is called to insert Data
    public void dataInsertion(List<TaskEventHolder>dataToPresent) {
        /*
         * So we got here a list of tasks we need to present... I need to do two things,
         * first create a capsules from all of them...
         * Second, I need to present all tasks assigned here...
         *
         * as last thing, i need to sort the values so they are presented in normal manner...
         *
         * then I insert
         */

        // Filtering data:
        if (dataToPresent != null) {
            taskBar.removeAllViews();
            List<TimeCapsule> capsules = new ArrayList<>(); // holder of all the capsules
            // Now we work for displaying tasks:
            displayedTasks = dataToPresent;
            // Capsules Creation:
            for (TaskEventHolder object : dataToPresent) {
                capsules.add(new TimeCapsule(object.getStartTime(), object.getEndTime(), object.getActiveID()));
            }
            // Sorting:
            dataToPresent = sort(dataToPresent);
            // Displaying:
            for (TaskEventHolder objectToPresent : dataToPresent) {
                WeekTask task = new WeekTask(getContext());
                task.defineMe(objectToPresent, capsules, this);
                taskBar.addView(task);
            }
        }
    }
    // Sorting by dateOnly first and then in chronological order other dates by start time
    private List<TaskEventHolder> sort(List<TaskEventHolder> dataToSort) {
        List<TaskEventHolder> temp = new ArrayList<>();
        // Arranging the ones without the time first.
        for (TaskEventHolder object: dataToSort) {
            if (object.getTimeDefined() == TaskObject.TimeDefined.onlyDate) {
                temp.add(object);
            }
        }
        // Now ones with time...
        dataToSort.removeAll(temp);

        TaskEventHolder mObject;
        boolean swapped;
        for (int i = 0; i<dataToSort.size() -1; i++) {
            swapped = false;
            for (int j = 0; j < dataToSort.size() - i -1; j++) {
                if (dataToSort.get(j).getStartTime().before(dataToSort.get(j+1).getStartTime())) {
                    mObject = dataToSort.get(j);
                    dataToSort.set(j, dataToSort.get(j + 1));
                    dataToSort.set(j+1, mObject);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }
        temp.addAll(dataToSort);
        return temp;
    }
    // This is called to insert data
    public void defineMe(final Calendar dayIPresent) {
        this.dayThisHolderPresents = dayIPresent;
        // Defining the titleBar first
        final SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM");
        final String toPresent = formatter.format(dayIPresent.getTime());
        dayDescription.setText(toPresent);
    }
    /*
     * A holder class designed to hold instances of start and end times of tasks that will be further
     * passed as array from WeekDisplay_WeekView to WeekTask for usage in defining the look and feel
     * of WeekTask instances.
     */
    protected class TimeCapsule {
        Calendar startTime, endTime;
        int hashID;
        TimeCapsule(Calendar startTime, Calendar endTime, int hashID) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.hashID = hashID;
        }
    }
    /*
     * Drag and drop listener that accepts new data dragged from outside this field...
     * If I drag and discover that I have that one presented I assume drag has failed and I do nothing.
     * Else I assign it only date no time... but if it has specified time then I can say that
     *
     * It should just save it and when it gets live-data update it will redraw everything... by default
     *
     * Consider corruption issues of the data format... so find the task and just change the day
     */
    private void defineDragAndDropListener() {
        baseView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determine if we can accept the current drag event:
                        if (event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT)
                                ||
                                event.getClipDescription().getLabel().equals(Constants.REPEATING_EVENT)) {
                            // If task is coming from this view, then I need to reject it
                            Object dropData = event.getLocalState();
                            if (dropData instanceof TaskObject) {
                                for (TaskEventHolder displayedTask: displayedTasks) {
                                    if (displayedTask.getActiveID() == ((TaskObject) dropData).getHashID()) {
                                        // If we have it, then we reject it...
                                        return false;
                                    }
                                }
                            } else if (dropData instanceof RepeatingEvent) {
                                for (TaskEventHolder displayedTask: displayedTasks) {
                                    if (displayedTask.getActiveID() == ((RepeatingEvent) dropData).getHashID()) {
                                        // We have it and we reject it...
                                        return false;
                                    }
                                }
                            }
                            return true;
                        } else {
                            return false;
                        }
                    case DragEvent.ACTION_DRAG_LOCATION:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        /*
                         * Find the Task in database, and set the date for today
                         * If it has noDate then we set that value to date
                         */
                        Object dropData = event.getLocalState();
                        if (dropData instanceof TaskObject) {
                            /* Now we find task in DB, this is done because it is known
                             * for this transfer to corrupt the Calendar Values, and this is the way
                             * to ensure data is consistent.
                             */
                            TaskObject task = dataProvider.findTaskObjectBy(((TaskObject) dropData).getHashID());
                            // Setting new values
                            if (task.getTimeDefined() == TaskObject.TimeDefined.noTime || task.getTimeDefined() == TaskObject.TimeDefined.onlyDate) {
                                task.setTimeDefined(TaskObject.TimeDefined.onlyDate);
                                task.getTaskStartTime().set(Calendar.YEAR, dayThisHolderPresents.get(Calendar.YEAR));
                                task.getTaskStartTime().set(Calendar.DAY_OF_YEAR, dayThisHolderPresents.get(Calendar.DAY_OF_YEAR));
                            } else {
                                long timeDiff = task.getTaskEndTime().getTimeInMillis() - task.getTaskStartTime().getTimeInMillis();
                                task.getTaskStartTime().set(Calendar.YEAR, dayThisHolderPresents.get(Calendar.YEAR));
                                task.getTaskStartTime().set(Calendar.DAY_OF_YEAR, dayThisHolderPresents.get(Calendar.DAY_OF_YEAR));
                                Calendar newEndTime = (Calendar) task.getTaskStartTime().clone();
                                newEndTime.add(Calendar.MILLISECOND, (int) timeDiff);
                                task.setTaskEndTime(newEndTime);
                            }
                            // Now we save the task and layout will be updated via LiveData Calls
                            dataProvider.saveTaskObject(task);
                        } else if (dropData instanceof RepeatingEvent) {
                            RepeatingEvent mEvent = dataProvider.getEventWith(((RepeatingEvent) dropData).getHashID());
                            // Setting values:
                            if (mEvent.isOnlyDate()) {
                                // means its only reminder style
                                mEvent.getStartTime().set(Calendar.YEAR, dayThisHolderPresents.get(Calendar.YEAR));
                                mEvent.getStartTime().set(Calendar.DAY_OF_YEAR, dayThisHolderPresents.get(Calendar.DAY_OF_YEAR));
                            } else {
                                long timeDiff = mEvent.getEndTime().getTimeInMillis() - mEvent.getStartTime().getTimeInMillis();
                                mEvent.getStartTime().set(Calendar.YEAR, dayThisHolderPresents.get(Calendar.YEAR));
                                mEvent.getStartTime().set(Calendar.DAY_OF_YEAR, dayThisHolderPresents.get(Calendar.DAY_OF_YEAR));
                                Calendar newEndTime = (Calendar) mEvent.getStartTime().clone();
                                newEndTime.add(Calendar.MILLISECOND, (int) timeDiff);
                                mEvent.setEndTimeWithReturn(newEndTime);
                            }
                            dataProvider.saveRepeatingEvent(mEvent);
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                }
                return true;
            }
        });
    }
    // A link to click button that creates a new task in this specific day
    private void createNewTask() {
        int newHashId = dataProvider.findNextFreeHashIDForTask();
        String taskName = getContext().getString(R.string.NewTask);
        TaskObject newTaskWeCreate = new TaskObject(
                newHashId,
                0,
                0,
                taskName,
                Calendar.getInstance(),
                dayThisHolderPresents,
                null,
                Calendar.getInstance(),
                TaskObject.CheckableStatus.notCheckable,
                "",
                0,
                0,
                "",
                TaskObject.TimeDefined.onlyDate,
                ""
                );
        dataProvider.saveTaskObject(newTaskWeCreate);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        Intent mIntent = new Intent(Constants.INTENT_FILTER_NEW_TASK);
        mIntent.putExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, newHashId);
        mIntent.putExtra(Constants.TASK_OBJECT, newTaskWeCreate);
        broadcastManager.sendBroadcast(mIntent);
    }

    // DayHolderCommunicationInterface implementation:
    @Override
    public Calendar getDayHoldersDay() {
        return dayThisHolderPresents;
    }
}
