package com.fuchsundlowe.macrolife.ListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.BottomBar.EditComplexGoal_BottomBar;
import com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.R;

public class ListView extends AppCompatActivity implements BottomBarCommunicationProtocol {

    private FrameLayout bottomBarHolder;
    private ViewPager centerBar;
    private LDCProtocol dataProvider;
    private EditTaskBottomBar editTaskBottomBar;
    private EditComplexGoal_BottomBar editComplexGoal;
    private BottomBarCommunicationProtocol bottomBarProtocol;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        centerBar = findViewById(R.id.centerBar_listView);
        centerBar.setAdapter(new ListViewAdapter(getSupportFragmentManager()));

        bottomBarHolder = findViewById(R.id.bottomBar_listView);

        dataProvider = new ListDataController(this);
        defineLiveDataCalls();
        bottomBarProtocol = this;

        defineBroadcastReceiver();
        definePagerListeners();
        // Used as optimization to reduce the CPU drain since I will have only 3 ListViews to deal with
        centerBar.setOffscreenPageLimit(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomBarProtocol = null;
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        // This ensures that there are no memory leaks associated DataProvider...
        dataProvider.destroy();
    }
    private void defineBroadcastReceiver() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_GLOBAL_EDIT);
        filter.addAction(Constants.INTENT_FILTER_COMPLEXGOAL_EDIT);
        filter.addAction(Constants.INTENT_FILTER_STOP_EDITING);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_FILTER_GLOBAL_EDIT)) {
                    boolean requestModeDone = false;
                    if (editTaskBottomBar == null) {
                        editTaskBottomBar = new EditTaskBottomBar();
                    } else {
                        // This ensures that we reload new data
                        requestModeDone = true;
                    }
                    int taskID = intent.getIntExtra(Constants.INTENT_FILTER_TASK_ID, -1);
                    int eventID = intent.getIntExtra(Constants.INTENT_FILTER_EVENT_ID, -1);
                    TaskObject task;
                    RepeatingEvent event = null;
                    if (eventID == -1) {
                        task = dataProvider.searchForTask(taskID).getTask();
                    } else {
                        TaskEventHolder holder = dataProvider.searchForEvent(eventID);
                        event = holder.getEvent();
                        task = holder.getTask();
                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(bottomBarHolder.getId(), editTaskBottomBar, Constants.EDIT_TASK_BOTTOM_BAR);
                    transaction.commit();
                    editTaskBottomBar.displayEditTask(EditTaskBottomBar.EditTaskState.editTask, task, event, bottomBarProtocol, bottomBarHolder.getWidth());
                    if (requestModeDone) {
                        editTaskBottomBar.modDone();
                    }
                } else if (intent.getAction().equals(Constants.INTENT_FILTER_COMPLEXGOAL_EDIT)) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    editComplexGoal = new EditComplexGoal_BottomBar();
                    transaction.replace(bottomBarHolder.getId(), editComplexGoal, Constants.EDIT_GOAL_BOTTOM_BAR);
                    ComplexGoal goal = dataProvider.searchForComplexGoal(intent.getIntExtra(
                            Constants.INTENT_FILTER_COMPLEXGOAL_ID, -1));
                    if (goal != null) {
                        editComplexGoal.defineMe(goal);
                    }
                    transaction.commit();

                } else if (intent.getAction().equals(Constants.INTENT_FILTER_STOP_EDITING)) {
                    // We have been asked to remove the bottom bar editing
                    switch (currentPage) {
                        case 0:
                            removeBottomBar();
                            break;
                        case 1:
                            produceCreateNewTask();
                            break;
                        case 2:
                            produceCreateNewGoal();
                            break;
                    }
                }
            }
        };

        localBroadcastManager.registerReceiver(broadcastReceiver, filter);
    }
    private void definePagerListeners() {
        centerBar.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:// we are moving to Completed Tasks

                        break;
                    case 1:// we are moving to Current Tasks
                        if (currentPage == 0) {
                            // we are moving from Completed Tasks
                        } else if (currentPage == 2){
                            // we are moving from Complex Tasks
                        }
                        break;
                    case 2:// we are moving to Completed Tasks
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                switch (currentPage) {
                    case 0:
                        removeBottomBar();
                        break;
                    case 1:
                        produceCreateNewTask();
                        break;
                    case 2:
                        produceCreateNewGoal();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void defineLiveDataCalls() {
        DataProviderNewProtocol dataMaster = LocalStorage.getInstance(this);
        dataMaster.getAllTaskObjects().observe(this, dataProvider.getTaskObserver());
        dataMaster.getAllRepeatingEvents().observe(this, dataProvider.getEventObserver());
        dataMaster.getAllComplexGoals().observe(this, dataProvider.getGoalObserver());
    }
    // List View Page adapter:
    private class ListViewAdapter extends FragmentStatePagerAdapter {

        ListViewAdapter(FragmentManager fm) {super(fm);}

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Completed
                    CompletedList completed = new CompletedList();
                    completed.defineMe(dataProvider);
                    return completed;
                case 2: // Complex
                    ComplexList complex = new ComplexList();
                    complex.defineMe(dataProvider);
                    return complex;
                default: // Current
                    UpcomingList upcoming = new UpcomingList();
                    upcoming.defineMe(dataProvider);
                    return upcoming;
            }

        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    protected enum bracketType {
        completed, overdue, undefined, upcoming
    }

    //Bottom Bar implementation:
    private void produceCreateNewTask() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        editTaskBottomBar = new EditTaskBottomBar();
        transaction.replace(bottomBarHolder.getId(), editTaskBottomBar, Constants.EDIT_TASK_BOTTOM_BAR);
        transaction.commit();
        editTaskBottomBar.displayEditTask(EditTaskBottomBar.EditTaskState.createTask,
                null,
                null,
                this, bottomBarHolder.getWidth());
    }
    private void produceCreateNewGoal() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        editComplexGoal = new EditComplexGoal_BottomBar();
        transaction.replace(bottomBarHolder.getId(), editComplexGoal, Constants.EDIT_GOAL_BOTTOM_BAR);
        transaction.commit();
    }
    private void removeBottomBar() {
        Fragment fragmentToRemove = getSupportFragmentManager().findFragmentByTag(Constants.EDIT_TASK_BOTTOM_BAR);
        if (fragmentToRemove != null) {
            getSupportFragmentManager().beginTransaction().remove(fragmentToRemove).commit();
        }
    }

    // Implementation of Bottom Bar protocol:
    @Override
    public void reportDeleteTask(TaskObject objectToDelete) {
        dataProvider.deleteTask(objectToDelete);
        produceCreateNewTask();
    }

    @Override
    public void reportDeleteEvent(RepeatingEvent eventToDelete) {
        dataProvider.deleteEvent(eventToDelete);
        produceCreateNewTask();
    }
}
