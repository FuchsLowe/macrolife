package com.fuchsundlowe.macrolife.ListView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.ComplexGoal.ComplexTaskActivity;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.R;


public class ComplexTask extends FrameLayout {

    private TextView taskName, progressReport, nextTask;
    private ViewGroup baseView;
    private ComplexGoal goal;
    private Context mContext;

    public ComplexTask(@NonNull Context context) {
        super(context);
        universalInit();
    }
    public ComplexTask(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        universalInit();
    }
    public ComplexTask(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        universalInit();
    }
    public ComplexTask(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        universalInit();
    }

    private void universalInit() {
        baseView = (ViewGroup) inflate(this.getContext(), R.layout.complex_task_list_view, this);
        taskName = baseView.findViewById(R.id.name_complexTask);
        progressReport = baseView.findViewById(R.id.progress_complexTask);
        nextTask = baseView.findViewById(R.id.nextTask_complexTask);

        mContext = baseView.getContext();

        defineOnClickListenerAndActions();
    }

    public void defineMe(ComplexGoal goal, Point taskCount, @Nullable TaskEventHolder nextGoal) {
        this.goal = goal;
        taskName.setText(goal.getTaskName());
        updateStats(taskCount, nextGoal);
    }

    public void updateStats(Point taskCount, TaskEventHolder nextGoal) {
        // Define the progress Bar: Assuming for taskCount that first it completed, second is incomplete
        int total = taskCount.x + taskCount.y;
        StringBuilder textToDisplay = new StringBuilder();
        textToDisplay.append(taskCount.x);
        textToDisplay.append("/");
        textToDisplay.append(total);
        progressReport.setText(textToDisplay.toString());

        // Determine what text to display:
        if (total == taskCount.x) {
            // means Complex Task Is Completed...
            nextTask.setText(mContext.getString(R.string.listView_complexGoal_completedComplexGoal));
            // TODO: Maybe a different look?
        } else if (nextGoal == null) {
            // means that we have tasks but none are designated as next in line
            nextTask.setText(mContext.getString(R.string.listView_complexGoal_notScheduled));
        } else {
            // we have at least one next task to display...
            String textToSet = mContext.getString(R.string.listView_complexGoal_nextTaskIs) + nextGoal.getName();
            nextTask.setText(textToSet);
        }
    }

    private void defineOnClickListenerAndActions() {
        baseView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // On short click, we go to complex activity
                Intent toComplexView = new Intent(mContext, ComplexTaskActivity.class);
                toComplexView.putExtra(Constants.INTENT_FILTER_COMPLEXGOAL_ID, goal.getHashID());
                mContext.startActivity(toComplexView);
            }
        });
        baseView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // On Long click, we provide the editing of the complex task
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
                Intent mIntent = new Intent(Constants.INTENT_FILTER_COMPLEXGOAL_EDIT);
                mIntent.putExtra(Constants.INTENT_FILTER_COMPLEXGOAL_ID, goal.getHashID());
                broadcastManager.sendBroadcast(mIntent);
                return true;
            }
        });
    }
}
