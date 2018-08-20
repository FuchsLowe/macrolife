package com.fuchsundlowe.macrolife.ListView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

public class RegularTask_ListView extends FrameLayout {

    private View baseView;
    private TextView taskName, masterTaskName, timeText;
    private LinearLayout modHolder;
    private CheckBox box;
    private TaskObject taskObject;
    private DataProviderNewProtocol dataMaster;

    // Many public constructors:
    public RegularTask_ListView(@NonNull Context context) {
        super(context);
        init();
    }
    public RegularTask_ListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public RegularTask_ListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public RegularTask_ListView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    // Default initiator:
    private void init() {
        baseView = inflate(this.getContext(), R.layout.regular_task_list_view, this);
        this.addView(baseView);
        dataMaster = LocalStorage.getInstance(baseView.getContext());

        // Connecting tasks
        taskName = baseView.findViewById(R.id.taskName_listView);
        masterTaskName = baseView.findViewById(R.id.masterTask_listView);
        timeText = baseView.findViewById(R.id.dateText_listView);
        box = baseView.findViewById(R.id.checkBox_listView);
        modHolder = baseView.findViewById(R.id.modHolder_listView);
    }
    // The task is defined by values specified by the Task
    public void defineMe(TaskObject task) {
        this.taskObject = task;

        taskName.setText(task.getTaskName());

        // Establishing the master task if there is one:
        if (task.getParentGoal() > 0) {

        }

    }

    /* Task Time state:
     * If completed, we show end date
     * if today, we show if done
     *  today ended at "TIME"
     *  else if not done we show"
     *   today at "TIME"
     *   if now, we show "Now in progress"
     *   IF not today, we show tomorrow, or if this week we show "THIS 'dayOfTheWeek'
     *   or if not this week, we show Monday, December 21st
     */

    // On click:

}
