package com.fuchsundlowe.macrolife.BottomBar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/*
 * This is the master presenter of the Repeating events.
 * Main Purpose of this one is to enable editing of the Repeat description schema.
 */
public class RepeatingEventEditor extends ConstraintLayout implements RepeatChronoViewProtocol{

    // Top Bar:
    private ImageButton saveButton, deleteButton;
    private TextView taskName;
    // Reminder Bar:
    private LinearLayout reminderBarHolder;
    private ImageButton addButton;
    // Left Side holder:
    private LinearLayout leftSideHolder;
    private HashMap<Integer, SideButton_RepeatEditor> weekButtons;
    // Central Bar - Representing the chronoView:
    private CronoViewFor_RepeatEditor dayView;
    private ScrollView dayViewHolder;
    // Bottom Bar Holder;
    private ViewPager bottomBarHolder;
    // Other variables:
    private LayoutInflater inflater;
    private TaskObject editedObject;
    private DataProviderNewProtocol localStorage;
    private SharedPreferences preferences;
    private OnClickListener leftSideButtonsClickListener;
    private RepeatType currentType;
    private List<RepeatingTask_RepeatEditor> eventsHolder;
    private DayOfWeek daySelected; // used if currentType is customWeek;


    public RepeatingEventEditor(Context context) {
        super(context);


        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Adding the editorView to the editor holder but hiding it at initial presentation
        ConstraintLayout editorView = (ConstraintLayout) inflater.inflate(R.layout.repeating_event_editor, this, true);

        // Two buttons that sit at the top of editorView and the TextView displaying the task name:
        taskName = editorView.findViewById(R.id.taskName_RepeatEditor);
        saveButton = editorView.findViewById(R.id.saveEvent_repeatingEditor);
        deleteButton = editorView.findViewById(R.id.deleteEvent_repeatingEditor);
        reminderBarHolder = editorView.findViewById(R.id.reminderBar_repeatingEditor);
        // Holder for the ChronoView implementation
        dayViewHolder = editorView.findViewById(R.id.DayViewHolder_RepeatEditor);
        dayView = new CronoViewFor_RepeatEditor(getContext());
        dayViewHolder.addView(dayView);
        leftSideHolder = editorView.findViewById(R.id.leftSideHolder_RepeatEditor);
        // Bottom bar holder, presenter of type of the repeating event, a ViewPager...
        bottomBarHolder = editorView.findViewById(R.id.bottomBar_RepeatEditor);
        defineViewPager();
        // The Add Button Implementation:
        addButton = editorView.findViewById(R.id.add_circle);

        // Other implementations:
        localStorage = LocalStorage.getInstance(context);
        weekButtons = new HashMap<>();
        defineButtonClickListener();
        defineBroadcastReceiver();

    }
    // MARK Methods:
    private void defineBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_EVENT_DELETED);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_FILTER_EVENT_DELETED))
                    updateDisplay();
            }
        }, filter);
    }
    // Simple adapter that should present the Regular view instead of Fragment. Implements callbacks.
    private void defineViewPager() {
        final SimplePageAdapter adapter = new SimplePageAdapter();
        bottomBarHolder.setAdapter(adapter);
        bottomBarHolder.setCurrentItem(0);
        // Listener so we could record the changes made...
        bottomBarHolder.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                RepeatType newPosition = adapter.presentingType[position];
                if (newPosition != currentType) {
                    // if its the same, we don't need to update anything...
                    currentType = adapter.presentingType[position];
                    // Called to update the events presented by DayView:
                    if (currentType == RepeatType.customWeek) {
                        leftSideHolder.setVisibility(VISIBLE);
                        daySelected = DayOfWeek.monday;
                        // I go over the buttons to establish if there are any ones selected so far,
                        // If so I set that as daySelected, else I define monday as default
                        for (SideButton_RepeatEditor button: weekButtons.values()) {
                            if (button.isSelected()) {
                                daySelected = button.dayOfWeek;
                                break;
                            }
                        }
                        /* Meaning that this might be the first init of the buttons and none was
                         * previously selected. If monday was selected it will make a redundant call
                         * if other day was selected, this will skip.
                         */
                        if (daySelected == DayOfWeek.monday) {
                            getLeftSideButton(DayOfWeek.monday).highliteSelection(true);
                        }
                    } else if (leftSideHolder.getVisibility() == VISIBLE) {
                        leftSideHolder.setVisibility(GONE);
                        daySelected = DayOfWeek.universal;
                    }
                    updateDisplay();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class SimplePageAdapter extends PagerAdapter {

        final RepeatType[] presentingType;

        SimplePageAdapter() {
            presentingType = new RepeatType[]{RepeatType.everyDay, RepeatType.customWeek, RepeatType.twoWeeks,
                    RepeatType.monthly, RepeatType.yearly};
        }

        @Override
        public int getCount() {
            return presentingType.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Context c = getContext();
            View view = LayoutInflater.from(c).inflate(R.layout.type_presenter, container, false);
            TextView text = view.findViewById(R.id.textView_typePresenter);
            container.addView(view);
            switch (presentingType[position]) {
                case everyDay:
                    text.setText(c.getString(R.string.daily));
                    break;
                case customWeek:
                    text.setText(c.getString(R.string.weekCustom));
                    break;
                case twoWeeks:
                    text.setText(c.getString(R.string.twoWeeks));
                    break;
                case monthly:
                    text.setText(c.getString(R.string.monthly));
                    break;
                case yearly:
                    text.setText(c.getString(R.string.yearly));
                    break;
            }
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
    // This function manages receiving of data and infuses fields and methods with it
    public void defineMe(TaskObject objectWeEdit) {
        editedObject = objectWeEdit;
        taskName.setText(objectWeEdit.getTaskName());
        defineLeftSideHolder();
        // Parse the schema and define what we are showing:
        eventsHolder = new ArrayList<>();
        parseDescriptor(editedObject.getRepeatDescriptor());
        // Present the values if any for defining...
        dayView.defineMe(objectWeEdit, eventsHolder, this);
        updateDisplay();
    }
    // Populates the events holder and sets the current type, if none defaults to EveryDay...
    private void parseDescriptor(String descriptor) {
        if (descriptor.length() > 0) {
            // Meaning we have some values in the first place...
            String[] values = descriptor.split("\\|");
            for (int i = 0; i<values.length; i++) {
                if (i == 0) {
                    // Defining the type
                    switch (Integer.valueOf((values[0]))) {
                        case 1:
                            setRepeatTypeTo(RepeatType.everyDay);
                            daySelected = DayOfWeek.universal;
                            break;
                        case 2:
                            setRepeatTypeTo(RepeatType.customWeek);
                            daySelected = DayOfWeek.monday;
                            getLeftSideButton(DayOfWeek.monday).highliteSelection(true);
                            break;
                        case 3:
                            setRepeatTypeTo(RepeatType.twoWeeks);
                            daySelected = DayOfWeek.universal;
                            break;
                        case 4:
                            setRepeatTypeTo(RepeatType.monthly);
                            daySelected = DayOfWeek.universal;
                            break;
                        case 5:
                            setRepeatTypeTo(RepeatType.yearly);
                            daySelected = DayOfWeek.universal;
                            break;
                    }
                } else {
                    // Creating the RepeatingTasks ( Slugs ):
                    String[] slugs = values[i].split("-");
                    Calendar startTime, endTime;
                    if (slugs.length > 0) {
                        // meaning we found something:
                        RepeatingTask_RepeatEditor task = new RepeatingTask_RepeatEditor(getContext());

                        startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(Long.valueOf(slugs[0]));

                        // Defining the day value:
                        DayOfWeek dayToPass;
                        if (currentType == RepeatType.customWeek) {
                            switch (startTime.get(Calendar.DAY_OF_WEEK)) {
                                case Calendar.MONDAY:
                                    dayToPass = DayOfWeek.monday;
                                    break;
                                case Calendar.TUESDAY:
                                    dayToPass = DayOfWeek.tuesday;
                                    break;
                                case Calendar.WEDNESDAY:
                                    dayToPass = DayOfWeek.wednesday;
                                    break;
                                case Calendar.THURSDAY:
                                    dayToPass = DayOfWeek.thursday;
                                    break;
                                case Calendar.FRIDAY:
                                    dayToPass = DayOfWeek.friday;
                                    break;
                                case Calendar.SATURDAY:
                                    dayToPass = DayOfWeek.saturday;
                                    break;
                                default:
                                    dayToPass = DayOfWeek.sunday;
                            }
                        } else {
                            dayToPass = DayOfWeek.universal;
                        }

                        // End Time defining and creation:
                        if (slugs.length > 1) {
                            endTime = Calendar.getInstance();
                            endTime.setTimeInMillis(Long.valueOf(slugs[1]));
                            task.defineMe(editedObject.getTaskName(), startTime,  endTime, dayToPass);
                        } else {
                            task.defineMe(editedObject.getTaskName(), startTime,  null, dayToPass);
                        }
                        // Adding this one to base:
                        eventsHolder.add(task);
                    }
                }
            }
        } else {
            // We are empty...
            setRepeatTypeTo(RepeatType.everyDay);
            daySelected = DayOfWeek.universal;
        }

    }
    //Presents or removes the left side of the bar.
    private void defineLeftSideHolder() {
        // grab buttons
        weekButtons.put(0, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_1));
        weekButtons.put(1, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_2));
        weekButtons.put(2, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_3));
        weekButtons.put(3, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_4));
        weekButtons.put(4, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_5));
        weekButtons.put(5, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_6));
        weekButtons.put(6, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_7));

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        int firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK,
                Calendar.getInstance().getFirstDayOfWeek());

        DayOfWeek[] daysOfWeek;
        switch (firstDayOfWeek) {
            case 1: // US - Sunday first day of week
                daysOfWeek = Constants.AMERICAN_WEEK_DAYS;
                break;
            default: // Europe - Monday first day of week
                daysOfWeek = Constants.EUROPEAN_WEEK_DAYS;
                break;
        }
        // Assigning phase
        for (int i = 0; i < 7; i++) {
            weekButtons.get(i).defineMe(daysOfWeek[i], leftSideButtonsClickListener);
        }
    }
    // Should not return null but there might be slight unforeseen flaw that might return it.
    @Nullable
    private SideButton_RepeatEditor getLeftSideButton(DayOfWeek byDay) {
        for (SideButton_RepeatEditor button: weekButtons.values()) {
            if (button.dayOfWeek == byDay) {
                return button;
            }
        }
        return null;
    }
    private void defineButtonClickListener() {
        leftSideButtonsClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof SideButton_RepeatEditor) {
                    // this is called by side button
                    daySelected = ((SideButton_RepeatEditor) v).dayOfWeek;
                    ((SideButton_RepeatEditor) v).highliteSelection(true);
                    for (SideButton_RepeatEditor button: weekButtons.values()) {
                        if (button.dayOfWeek != daySelected) {
                            // un-select the button:
                            button.highliteSelection(false);
                        }
                    }
                   updateDisplay();
                }
            }
        };
        // Save Button Implementation:
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * Grab repeat type and find all Slugs that fit the profile. Then Update the
                 * descriptor of task. Dismiss self, call provide the previous task displayer
                 */
                // Create a new Descriptor
                List<RepeatingTask_RepeatEditor> slugs = new ArrayList<>();
                // Look for all eligible values in holder
                for (RepeatingTask_RepeatEditor event: eventsHolder) {
                    if (daySelected == DayOfWeek.universal) {
                        if (event.getDay() == DayOfWeek.universal && event.startTime != null && event.startTime.getTimeInMillis() > 0) {
                            slugs.add(event);
                        }
                    } else {
                        if (event.getDay() != DayOfWeek.universal && event.startTime != null && event.startTime.getTimeInMillis() > 0) {
                            slugs.add(event);
                        }
                    }
                }
                if (slugs.size()>0) {
                    StringBuilder descriptor = new StringBuilder(String.valueOf(currentType.val) + "|");
                    for (RepeatingTask_RepeatEditor time: slugs) {
                        if (time.startTime != null && time.startTime.getTimeInMillis() > 0) {
                            descriptor.append(time.startTime.getTimeInMillis());
                            // Establishing if this is a reminder
                            if (time.endTime != null && time.endTime.getTimeInMillis() >
                                    time.startTime.getTimeInMillis()) {
                                descriptor.append("-");
                                descriptor.append(time.endTime.getTimeInMillis());
                                descriptor.append("|");
                            } else {
                                // means it it only a reminder:
                                descriptor.append("|");
                            }
                        }
                    }
                    // Saving the value
                    editedObject.setRepeatDescriptor(descriptor.toString());
                    // Report via Broadcast TypeDefined
                    Intent mIntent = new Intent(Constants.TYPE_DEFINED);
                    LocalBroadcastManager.getInstance(getContext()).sendBroadcast(mIntent);
                } else {
                    // same thing as on delete, we can' give green light since type isn't defined.
                    // Make a toast explaining that there is no type defined
                    Toast toast = Toast.makeText(getContext(), R.string.toast_no_pattern, Toast.LENGTH_SHORT);
                    toast.show();
                    // Make a broadcast to remove self.
                    callRemoveRepeatEditor();
                }
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
              callRemoveRepeatEditor();
            }
        });

        // In charge of creating the reminder.
        addButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating reminder style task here
                RepeatingTask_RepeatEditor reminder = new RepeatingTask_RepeatEditor(getContext());
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, 0);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.SECOND, 0);
                if (daySelected != DayOfWeek.universal) {
                    // NOTE: this ensures consistency when data is reconstructed
                    startTime.set(Calendar.DAY_OF_WEEK, daySelected.getValue());
                }
                reminder.defineMe(editedObject.getTaskName(), startTime, null, daySelected);
                eventsHolder.add(reminder);
                updateDisplay();
            }
        });
    }
    /*
     * Makes a broadcast that calls EditTaskBottom Bar to remove editor without selecting the
     * buttons, since no value has been set in Descriptor.
     */
    private void callRemoveRepeatEditor(){
        Intent mIntent = new Intent(Constants.TYPE_NOT_DEFINED);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(mIntent);
    }
    // Makes updates of presenting information for time defined events as well as reminders.
    private void updateDisplay() {
        // First we look to eliminate all the Reminder Views that have no start time, as that means
        // that they have been deleted:
        List<RepeatingTask_RepeatEditor> toDelete = new ArrayList<>();
        List<RepeatingTask_RepeatEditor> reminders = new ArrayList<>();
        for (RepeatingTask_RepeatEditor event: eventsHolder) {
            if (event.startTime == null || event.startTime.getTimeInMillis() < 1) {
                toDelete.add(event);
                event.setVisibility(GONE);
            } else if (event.isReminder()) {
                if (event.getDay() == daySelected) {
                    reminders.add(event);
                }
            }
        }
        eventsHolder.removeAll(toDelete);
        // Updates the reminderBar, if there are events to show, remove excess of them if are...
        // Finding all views which are not addButton
        List<View> remindersToRemove = new ArrayList<>();
        for (int i = 0; i<reminderBarHolder.getChildCount(); i++) {
            View v = reminderBarHolder.getChildAt(i);
            /*
             * Possible alternative solution:
             *
             * Add a tag to every RepeatingTask, and then check if it matches such a tag and then
             * remove it if the case...
             */
            if (v instanceof RepeatingTask_RepeatEditor) {
                remindersToRemove.add(v);
            }
        }
        // Removing all Views that Are not addButton from reminderBarHolder
        for (View v: remindersToRemove) {
            reminderBarHolder.removeView(v);
        }
        // Now We deal with presenting the reminders...
        if (reminders.size() > 0) {
            addButton.setVisibility(GONE);
            RepeatingTask_RepeatEditor toAddReminder = reminders.get(0);
            // Now establish if we have more than one
            if (reminders.size() > 1) {
                // We remove them all from holder
                eventsHolder.removeAll(reminders);
                // And now we add the only one we kept
                eventsHolder.add(toAddReminder);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            toAddReminder.setLayoutParams(params);
            reminderBarHolder.addView(toAddReminder);
        } else {
            // means none exist for this day... Lets produce the button to add one
            addButton.setVisibility(VISIBLE);
        }
        // Updates the ChronoView
        dayView.updateValues();
    }
    // RepeatCronoView Protocol implementation
    @Override
    public DayOfWeek getDay() {
        return daySelected;
    }


    // Method used to change  the selected value of ViewPager to one of the types
    private void setRepeatTypeTo(RepeatType type) {
        currentType = type;
        switch (type) {
            case everyDay:
                bottomBarHolder.setCurrentItem(0, true);
                leftSideHolder.setVisibility(GONE);
                break;
            case customWeek:
                bottomBarHolder.setCurrentItem(1, true);
                leftSideHolder.setVisibility(VISIBLE);
                break;
            case twoWeeks:
                bottomBarHolder.setCurrentItem(2, true);
                leftSideHolder.setVisibility(GONE);
                break;
            case monthly:
                bottomBarHolder.setCurrentItem(3, true);
                leftSideHolder.setVisibility(GONE);
                break;
            case yearly:
                bottomBarHolder.setCurrentItem(4, true);
                leftSideHolder.setVisibility(GONE);
                break;
        }
    }
    public enum RepeatType {
        everyDay(1), customWeek(2), twoWeeks(3), monthly(4), yearly(5);
        int val;
        RepeatType(int val) {
            this.val = val;
        }
    }


}
