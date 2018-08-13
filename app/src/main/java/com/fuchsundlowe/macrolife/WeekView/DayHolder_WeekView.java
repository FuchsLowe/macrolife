package com.fuchsundlowe.macrolife.WeekView;


import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayHolder_WeekView extends Fragment {

    private ViewGroup baseView;
    private FrameLayout titleBar;
    private LinearLayout taskBar;
    private TextView dayDescription;
    private Calendar dayThisHolderPresents;
    private DataProviderNewProtocol dataProvider;
    private List<TaskObject> displayedTasks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and find views
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_day_holder__week_view, container, false);
        titleBar = baseView.findViewById(R.id.topBar_dayHolder_weekView);
        taskBar = baseView.findViewById(R.id.linearLayout_dayHolder_weekView);
        dayDescription = baseView.findViewById(R.id.dayDescription_dayHolder_weekView);

        dataProvider = LocalStorage.getInstance(container.getContext());

        defineDragAndDropListener();

        return baseView;
    }

    // This is called to insert data
    public void defineMe(Calendar dayIPresent) {
        this.dayThisHolderPresents = dayIPresent;
        // Defining the titleBar first
        String toPresent = DateFormat.getDateInstance(DateFormat.FULL).format(dayIPresent.getTime());
        dayDescription.setText(toPresent);

        // Filtering and providing data for WeekTasks and displaying the week tasks in taskBar

        final List<TimeCapsule> capsules = new ArrayList<>(); // holder of all the capsules
        final List<WeekTaskData> dataToPresent = new ArrayList<>();
        // If this doesn't work, then just create a new public method that receives List of task Objects from parent
        // Or discover how fragment can implemmt it, as it makes more sense...
        dataProvider.getTaskThatIntersects(dayIPresent).observe((LifecycleOwner) this, new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> objects) {
                if (objects != null) {
                    displayedTasks = objects;
                    for (TaskObject task : objects) {
                        if (task.getRepeatingMod() == null) {
                            // means this task has no rep events
                            // Create a capsule
                            TimeCapsule capsule = new TimeCapsule(
                                    task.getTaskStartTime(),
                                    task.getTaskEndTime(),
                                    task.getHashID()
                            );
                            capsules.add(capsule);
                            // Create WeeksTaskData entry
                            WeekTaskData data = new WeekTaskData(task, null, capsules);
                            dataToPresent.add(data);
                        } else {
                            // this consists of repeating events as well
                            // Find the matching events with mod and ultimately create WeekData
                            List<RepeatingEvent> companionEvents = new ArrayList<>();
                            if (task.getRepeatingMod() == TaskObject.Mods.repeating) {
                                // means repeats only one value:
                                companionEvents.addAll(dataProvider.getEventsBy(task.getHashID(), TaskObject.Mods.repeating));
                                // now create capsule for each event:
                                for (RepeatingEvent event: companionEvents) {
                                    TimeCapsule capsule = new TimeCapsule(
                                            event.getStartTime(),
                                            event.getEndTime(),
                                            event.getHashID()
                                            );
                                    capsules.add(capsule);
                                }
                            } else {
                                // means repeats multi values... Filter only for this day
                                for (RepeatingEvent event: dataProvider.getEventsBy(task.getHashID(),
                                        TaskObject.Mods.repeatingMultiValues)) {
                                    if (event.getDayOfWeek().getValue() == dayThisHolderPresents.get(Calendar.DAY_OF_YEAR)) {
                                        // if we have the same day we only add it as companion:
                                        companionEvents.add(event);
                                        // create a capsule:
                                        TimeCapsule capsule = new TimeCapsule(
                                                event.getStartTime(),
                                                event.getEndTime(),
                                                event.getHashID()
                                        );
                                        capsules.add(capsule);
                                    }
                                }
                            }
                            /* Now we create WeekTaskData and add it, if there are companion events to
                             * go with it, because we don't want to present a repeating event task object
                             * by mistake of not having companion events to follow it.
                             */
                            if (companionEvents.size() > 0) {
                                WeekTaskData data = new WeekTaskData(task, companionEvents, capsules);
                                dataToPresent.add(data);
                            }
                        }
                    }
                    //Now we create WeekTasks and assign them data
                    for (WeekTaskData data : dataToPresent) {
                        WeekTask task = new WeekTask(baseView.getContext());
                        taskBar.addView(task);
                        task.defineMe(data.object, data.events, data.capsules);
                    }
                }
            }
        });
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
    // A simple data holder that will pass the info to WeekTask when all is filtered
    private class WeekTaskData {

        TaskObject object;
        List<RepeatingEvent> events;
        List<TimeCapsule> capsules;

        WeekTaskData(TaskObject object, List<RepeatingEvent> events, List<TimeCapsule> capsules) {
            this.object = object;
            this.events = events;
            this.capsules = capsules;
        }
    }

    // TODO: DragAndDropListener:
    /*
     * Drag and drop listener that accepts new data dragged from outside this field...
     * If I drag and discover that I have that one presented I assume drag has failed and I do nothing.
     * Else I assign it only date no time... but if it has specified time then I can say that
     *
     * It should just save it and when it gets live-data update it will redraw everything... by default
     *
     * consider corruption issues of the data format... so find the task and just change the day
     */
    private void defineDragAndDropListener() {
        baseView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Determine if we can accept the current drag event:
                        if (event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT)) {
                            // If task is coming from this view, then I need to reject it
                            Object dropData = event.getLocalState();
                            if (dropData instanceof TaskObject) {
                                for (TaskObject displayedTask: displayedTasks) {
                                    if (displayedTask.getHashID() == ((TaskObject) dropData).getHashID()) {
                                        // If we have it, then we reject it...
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
                            task.getTaskStartTime().set(Calendar.YEAR, dayThisHolderPresents.get(Calendar.YEAR));
                            task.getTaskStartTime().set(Calendar.DAY_OF_YEAR, dayThisHolderPresents.get(Calendar.DAY_OF_YEAR));
                            if (task.getTimeDefined() == TaskObject.TimeDefined.noTime) {
                                task.setTimeDefined(TaskObject.TimeDefined.onlyDate);
                            }
                            // Now we save the task and layout will be updated via LiveData Calls
                            dataProvider.saveTaskObject(task);
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
    private void createNewTask(View view) {
        // TODO: Implement sonny!
    }

}
