package com.fuchsundlowe.macrolife.ListView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.ListView.ListView.bracketType;
import com.fuchsundlowe.macrolife.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

// A class that manages the regular task representation in listView...
public class RegularTask extends FrameLayout {

    private View baseView;
    private TextView taskName, masterTaskName, timeText;
    private CheckBox box;
    private TaskEventHolder holder;
    private bracketType type;
    private Handler timeTextTimer;
    private Context mContext;
    private static final String regexBreak = "_";
    private static final long minuteInMillis = 60000;
    private SharedPreferences preferences;


    // Many public constructors:
    public RegularTask(@NonNull Context context) {
        super(context);
        init();
    }
    public RegularTask(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RegularTask(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public RegularTask(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    // Default initiator:
    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        baseView = inflate(this.getContext(), R.layout.regular_task_list_view, this);
        baseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcastForEdit();
            }
        });
        mContext = baseView.getContext();
        // Connecting tasks
        taskName = baseView.findViewById(R.id.taskName_listView);
        masterTaskName = baseView.findViewById(R.id.masterTask_listView);
        timeText = baseView.findViewById(R.id.dateText_listView);
        box = baseView.findViewById(R.id.checkBox_listView);
        box.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                 * What needs to happen?
                 * Change the state and report change on self to whoever is in charge...
                 */
                if (box.isChecked()) {
                    box.setChecked(false);
                    holder.setCompletionStatus(TaskObject.CheckableStatus.incomplete);
                } else {
                    box.setChecked(true);
                    holder.setCompletionStatus(TaskObject.CheckableStatus.completed);
                }
                commitChangesOnHolder();
            }
        });
        preferences = mContext.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

    }
    // The task is defined by values specified by the Task
    public void defineMe(TaskEventHolder holder, bracketType type) {
        this.holder = holder;
        this.type = type;

        taskName.setText(holder.getName());
        // Establishing the master task if there is one:
        String complexTaskName = holder.getComplexGoalName();
        if (complexTaskName != null && complexTaskName.length() > 0) {
            masterTaskName.setText(complexTaskName);
        } else {
            masterTaskName.setVisibility(GONE);
        }
        // establishing if task is checkable or not
        switch (holder.getCompletionState()) {
            case completed:
                box.setVisibility(VISIBLE);
                box.setChecked(true);
                break;
            case incomplete:
                box.setVisibility(VISIBLE);
                box.setChecked(false);
                break;
            case notCheckable:
                box.setVisibility(GONE);
                box.setChecked(false);
                break;
        }
        // Defining the mods...
        HashMap<TaskObject.Mods, ImageView> modMap = new HashMap<>();
        modMap.put(TaskObject.Mods.note, (ImageView) baseView.findViewById(R.id.noteMod_listViewTask));
        modMap.put(TaskObject.Mods.list, (ImageView) baseView.findViewById(R.id.listMod_listViewTask));
        modMap.put(TaskObject.Mods.repeating, (ImageView) baseView.findViewById(R.id.repeatOneMod_listViewTask));
        for (TaskObject.Mods mod : holder.getAllMods()) {
            modMap.get(mod).setVisibility(VISIBLE);
        }
        // display the task times, if null we hide the the text bar...
        defineTimeBar();

        // switch states
        setTaskStateTheme(type);
    }

    // Function that sets the theme of the task in accordance with the status of the Holder
    private void setTaskStateTheme(bracketType typeToSet) {
        // TODO Implement:
        switch (typeToSet) {
            case upcoming:
                break;
            case overdue:
                break;
            case undefined:
                break;
            case completed:
                break;
        }
    }
    // Function that generates the right text to show for time bar and manages it appearance, can be called
    // from fragment when its needed to re-update the values due to fragment loosing focus or so...
    public void defineTimeBar() {
        String textToDisplay = defineTimeToShow(holder, type);
        if (textToDisplay == null || textToDisplay.length() < 1) {
            timeText.setVisibility(GONE);
        } else {
            timeText.setText(textToDisplay);
        }
    } // TODO: make a call to this if we have been paused in activity..?
    // Function that defines the type of text that should appear under the task...
    @Nullable
    private String defineTimeToShow(TaskEventHolder holder, bracketType taskType) {
        Calendar currentTime =  Calendar.getInstance();
        String valueToReturn = "";
        /*
            Completed: * Task Completed on <date>
            Undefined:
            Null
            Overdue:
            Indicate when it was supposed to be done
            Maybe was due 2h ago
            was due 1 min ago
            was due 2 days ago
            was due yesterday
            Upcoming:
            In progress
            Due today at <time>
            Due tomorrow at 12PM
            # if due tomorrow then say tomorrow. If is 7 days away then say the day of the week. If its more than 7 days, we display the date, not time then…
            How should I treat the oldCompletedMap?
         */
        switch (taskType) {
            case completed:
                valueToReturn += (mContext.getString(R.string.listView_textForCompletedTask));
                valueToReturn = valueToReturn.replaceFirst(regexBreak, provideDate(holder.getEndTime()));
                break;
            case overdue:
                Calendar timeUnit; // used to reference the Calendar object used as timeReference

                if (holder.getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
                    timeUnit = holder.getEndTime();
                } else {
                    timeUnit = holder.getStartTime();
                }
                long distanceInDays = distanceInDays(timeUnit, currentTime);
                if (distanceInDays > 0) {
                    if (distanceInDays == 1) {
                        // display yesterday type
                        valueToReturn += mContext.getString(R.string.listView_textForOverdueTask_yesterday);
                        resetValuesAtDaysEnd(currentTime);
                    } else {
                        // tell them how many days have passed
                        valueToReturn += mContext.getString(R.string.listView_textForOverdueTask_days);
                        valueToReturn = valueToReturn.replace(regexBreak, String.valueOf(distanceInDays));
                        resetValuesAtDaysEnd(currentTime);
                    }
                } else {
                    // its less than days
                    long distanceInHours = distanceInHours(timeUnit, currentTime);
                    if (distanceInHours > 0) {
                        // tell them how many hours have passed
                        valueToReturn += mContext.getString(R.string.listView_textForOverdueTask_hours);
                        valueToReturn = valueToReturn.replace(regexBreak, String.valueOf(distanceInHours));
                        resetValuesInMinute();
                    } else {
                        // its in minutes
                        long distanceInMinutes = distanceInMinutes(timeUnit, currentTime);
                        valueToReturn += mContext.getString(R.string.listView_textForOverdueTask_minutes);
                        valueToReturn = valueToReturn.replace(regexBreak, String.valueOf(distanceInMinutes));
                        resetValuesInMinute();
                    }
                }
                break;
            case upcoming:
                distanceInDays = distanceInDays(holder.getStartTime(), currentTime);
                if (distanceInDays == 0) { // means its today
                    if (holder.getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
                        resetValuesInMinute();
                        long distanceInHours = distanceInHours(holder.getStartTime(), currentTime);
                        if (distanceInHours > 1) {
                            // show hours
                            valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_today);
                            valueToReturn = valueToReturn.replace(regexBreak, provideTime(holder.getStartTime()));
                        } else {
                            // show minutes till
                            long distanceInMinutes = distanceInMinutes(currentTime, holder.getStartTime());
                            if (distanceInMinutes > 0) {
                                valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_underHour);
                                valueToReturn = valueToReturn.replace(regexBreak, String.valueOf(distanceInMinutes));
                            } else {
                                valueToReturn = mContext.getString(R.string.listView_textForUpcomingTask_inProgress);
                                timeTextTimer = null; // we don't need it anymore
                            }
                        }
                    } else {
                        valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_dueToday);
                    }
                } else if (distanceInDays == 1) {
                    resetValuesAtDaysEnd(currentTime);
                    // say tomorrow
                    if (holder.getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
                        valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_tomorrowAt);
                        valueToReturn = valueToReturn.replace(regexBreak, provideTime(holder.getStartTime()));
                    } else {
                        valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_tomorrow);
                    }
                } else if (distanceInDays > 1 && distanceInDays < 8) {
                    resetValuesAtDaysEnd(currentTime);
                    // then we say what kind of day is that, like
                    valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_dueOn);
                    valueToReturn = valueToReturn.replace(regexBreak, provideDayOfWeek(holder.getStartTime()));
                } else {
                    // means its more than a week away and we format date normally
                    valueToReturn += mContext.getString(R.string.listView_textForUpcomingTask_dueOn);
                    valueToReturn = valueToReturn.replace(regexBreak, provideDate(holder.getStartTime()));
                }
                break;
            case undefined:
                return null;
        }
        return valueToReturn;
    }
    // Returns day of Week in locale if supported by string locale if not, returns ENG long days
    private String provideDayOfWeek(Calendar dateObject) {
        switch (dateObject.get(Calendar.DAY_OF_WEEK)) {
            case 1: return mContext.getString(R.string.Sunday_Long);
            case 2: return mContext.getString(R.string.Monday_Long);
            case 3: return mContext.getString(R.string.Tuesday_Long);
            case 4: return mContext.getString(R.string.Wednesday_Long);
            case 5: return mContext.getString(R.string.Thursday_Long);
            case 6: return mContext.getString(R.string.Friday_Long);
            default: return mContext.getString(R.string.Saturday_Long);
        }
    }
    private String provideTime(Calendar dateObject) {
        String pattern;
        if (preferences.getBoolean(Constants.TIME_REPRESENTATION, false)) {
            pattern = "hh:mm";
        } else {
            pattern = "K:mm:a";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(dateObject);
    }
    private String provideDate(Calendar dateObject) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM, dd yyyy");
        return formatter.format(dateObject.getTime());
    }
    // Returns distance in days between start and end values
    private long distanceInDays(Calendar start, Calendar end) {
        DateTime mk1 = new DateTime(start.getTimeInMillis());
        DateTime mk2 = new DateTime(end.getTimeInMillis());
        return Days.daysBetween(mk1, mk2).getDays();
    }
    // Returns distance in hours between two values
    private long distanceInHours(Calendar start, Calendar end) {
        DateTime mk1 = new DateTime(start.getTimeInMillis());
        DateTime mk2 = new DateTime(end.getTimeInMillis());

        return Hours.hoursBetween(mk1,mk2).getHours();
    }
    // Returns distance in minutes between two values
    private long distanceInMinutes(Calendar start, Calendar end) {
        DateTime mk1 = new DateTime(start.getTimeInMillis());
        DateTime mk2 = new DateTime(end.getTimeInMillis());

        return Minutes.minutesBetween(mk1, mk2).getMinutes();
    }
    // These following methods are used to init the timeText to new correct value:
    private void resetValuesInMinute() {
        if (timeTextTimer == null) {
            timeTextTimer = new Handler(Looper.getMainLooper());
        }
        timeTextTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                defineTimeBar();
            }
        }, minuteInMillis);
    }
    private void resetValuesAtDaysEnd(Calendar currentTime) {
        Calendar endOfDayTime = (Calendar) currentTime.clone();
        endOfDayTime.set(Calendar.HOUR_OF_DAY, 23);
        endOfDayTime.set(Calendar.MINUTE, 59);
        endOfDayTime.set(Calendar.SECOND, 59);
        long distance = endOfDayTime.getTimeInMillis() - currentTime.getTimeInMillis();

        if (timeTextTimer == null) {
            timeTextTimer = new Handler(Looper.getMainLooper());
        }

        timeTextTimer.postDelayed(new Runnable() {
            @Override
            public void run() {
                defineTimeBar();
            }
        }, distance);
    }
    // On click:
    private void sendBroadcastForEdit() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(mContext);
        Intent intent = new Intent(Constants.INTENT_FILTER_GLOBAL_EDIT);
        if (holder.isTask()) {
            intent.putExtra(Constants.INTENT_FILTER_TASK_ID, holder.getActiveID());
        } else {
            intent.putExtra(Constants.INTENT_FILTER_EVENT_ID, holder.getActiveID());
        }
        manager.sendBroadcast(intent);
    }
    // Makes a call to database to initiate the saving operation of holder
    private void commitChangesOnHolder() {
        DataProviderNewProtocol dataMaster = LocalStorage.getInstance(baseView.getContext());
        if (holder.isTask()) {
            dataMaster.saveTaskObject(holder.getTask());
        } else {
            dataMaster.saveRepeatingEvent(holder.getEvent());
        }
    }

}
