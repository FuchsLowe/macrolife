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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.R;

public class ListView extends AppCompatActivity implements BottomBarCommunicationProtocol {

    private FrameLayout bottomBarHolder;
    private RecyclerView centerBar;
    private LDCProtocol dataProvider;
    private EditTaskBottomBar editTaskBottomBar;
    private BottomBarCommunicationProtocol bottomBarProtocol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        centerBar = findViewById(R.id.centerBar_listView);
        bottomBarHolder = findViewById(R.id.bottomBar_listView);

        dataProvider = new ListDataController(this);
        bottomBarProtocol = this;

        defineBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bottomBarProtocol = null;
        // This ensures that there are no memory leaks associated DataProvider...
        dataProvider.destroy();
    }
    private void defineBroadcastReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.INTENT_FILTER_GLOBAL_EDIT);

        manager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.INTENT_FILTER_GLOBAL_EDIT)) {
                    if (editTaskBottomBar == null) {
                        editTaskBottomBar = new EditTaskBottomBar();
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
                    transaction.replace(bottomBarHolder.getId(), editTaskBottomBar);
                    transaction.commit();
                    editTaskBottomBar.displayEditTask(EditTaskBottomBar.EditTaskState.editTask, task, event, bottomBarProtocol, bottomBarHolder.getWidth());
                }
            }
        }, filter);
    }
    // List View Page adapter:
    private class ListViewAdapter extends FragmentStatePagerAdapter {

        ListViewAdapter(FragmentManager fm) {super(fm);}

        @Override
        public Fragment getItem(int position) {
            // TODO: Update to reflect the values being inseted into it...
            switch (position) {
                case 0: // Completed
                    CompletedList frag = new CompletedList();
                    return frag;
                case 2: // Complex
                    return null;
                default: // Current
                    return null;
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
    // TODO: Bottom Bar implementation:

    // Implementation of Bottom Bar protocol:
    @Override
    public void reportDeleteTask(TaskObject objectToDelete) {
        dataProvider.deleteTask(objectToDelete);
        // TODO Not done
    }

    @Override
    public void reportDeleteEvent(RepeatingEvent eventToDelete) {
        dataProvider.deleteEvent(eventToDelete);
        // TODO Not done
    }

}
