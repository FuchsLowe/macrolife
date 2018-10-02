package com.fuchsundlowe.macrolife.ListView;


import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.Interfaces.LDCToFragmentListView;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment that displays Upcoming Tasks, Overdue Tasks as well as Unassigned Tasks.
 */
public class UpcomingList extends Fragment {

    private ScrollView base;
    private ViewGroup listBase, oneHolder, twoHolder, threeHolder;
    private TextView containerNameOne,containerNameTwo, containerNameThree;
    private ImageButton expandOne, expandTwo, expandThree;
    private RecyclerView recyclerOne, recyclerTwo, recyclerThree;
    private LDCProtocol dataProtocol;
    private Timer updateTask;

    private List<TaskEventHolder> overdue, unassigned, upcoming;

    public UpcomingList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        base = (ScrollView) inflater.inflate(R.layout.fragment_upcoming_list, container, false);
        /*
        listBase = (ViewGroup) inflater.inflate(R.layout.upcomming_list_base_view, base, true);

        oneHolder = listBase.findViewById(R.id.rowOneHolder_listBase);
        twoHolder = listBase.findViewById(R.id.rowTwoHolder_listBase);
        threeHolder = listBase.findViewById(R.id.rowThreeHolder_listBase);

        containerNameOne = listBase.findViewById(R.id.text_rowOne_listBase);
        containerNameOne.setText(R.string.OverdueList_title);

        containerNameTwo = listBase.findViewById(R.id.text_rowTwo_listBase);
        containerNameTwo.setText(R.string.UpcomingList_title);

        containerNameThree = listBase.findViewById(R.id.text_rowThree_listBase);
        containerNameThree.setText(R.string.UnassignedList_title);

        expandOne = listBase.findViewById(R.id.button_rowOne_listBase);
        expandTwo = listBase.findViewById(R.id.button_rowTwo_listBase);
        expandThree = listBase.findViewById(R.id.button_rowThree_listBase);

        recyclerOne = listBase.findViewById(R.id.recyclerView_rowOne_listBase);
        recyclerTwo = listBase.findViewById(R.id.recyclerView_rowTwo_listBase);
        recyclerThree = listBase.findViewById(R.id.recyclerView_rowThree_listBase);

        defineOnClick();
        defineAdapters();
        */
        return base;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTask = new Timer();
    }
    @Override
    public void onStop() {
        super.onStop();
        updateTask.cancel();
        updateTask = null;
    }

    public void defineMe(LDCProtocol protocol) {
        /*
        DataInterface dataProvider = new DataInterface();
        protocol.subscribeToOverdue(dataProvider);
        protocol.subscribeToUpcoming(dataProvider);
        protocol.subscribeToUnassigned(dataProvider);
        dataProtocol = protocol;
        */
    }

    private void defineOnClick() {
        expandOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oneHolder.getVisibility() == View.VISIBLE) {
                    oneHolder.setVisibility(View.GONE);
                } else {
                    oneHolder.setVisibility(View.VISIBLE);
                }
            }
        });
        expandTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (twoHolder.getVisibility() == View.VISIBLE) {
                    twoHolder.setVisibility(View.GONE);
                } else {
                    twoHolder.setVisibility(View.VISIBLE);
                }
            }
        });
        expandThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (threeHolder.getVisibility() == View.VISIBLE) {
                    threeHolder.setVisibility(View.GONE);
                } else {
                    threeHolder.setVisibility(View.VISIBLE);
                }
            }
        });
        listBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(base.getContext());
                Intent removeBottomBar = new Intent();
                removeBottomBar.setAction(Constants.INTENT_FILTER_STOP_EDITING);
                manager.sendBroadcast(removeBottomBar);
            }
        });
    }
    private void defineAdapters() {
        recyclerOne.setLayoutManager(new LinearLayoutManager(base.getContext()));
        recyclerOne.setAdapter(new RegularTaskListAdapter(overdue, ListView.bracketType.overdue));

        recyclerTwo.setLayoutManager(new LinearLayoutManager(base.getContext()));
        recyclerTwo.setAdapter(new RegularTaskListAdapter(upcoming, ListView.bracketType.upcoming));

        recyclerThree.setLayoutManager(new LinearLayoutManager(base.getContext()));
        recyclerThree.setAdapter(new RegularTaskListAdapter(unassigned, ListView.bracketType.undefined));
    }

    private class DataInterface extends LDCToFragmentListView {

        public void deliverOverdue(List<TaskEventHolder> newHolders) {
            overdue = newHolders;
            recyclerOne.getAdapter().notifyDataSetChanged();
            if (newHolders.size() == 0) {
                oneHolder.setVisibility(View.GONE);
            } else {
                oneHolder.setVisibility(View.VISIBLE);
            }
        }
        public void deliverUnassigned(List<TaskEventHolder> newHolders) {
            unassigned = newHolders;
            recyclerThree.getAdapter().notifyDataSetChanged();
            if (newHolders.size() == 0) {
                twoHolder.setVisibility(View.GONE);
            } else {
                twoHolder.setVisibility(View.VISIBLE);
            }
        }
        public void deliverUpcoming(List<TaskEventHolder> newHolders) {
            upcoming = newHolders;
            recyclerTwo.getAdapter().notifyDataSetChanged();
            if (newHolders.size() == 0) {
                threeHolder.setVisibility(View.GONE);
            } else {
                threeHolder.setVisibility(View.VISIBLE);
            }
            if (newHolders.size() > 0) {
                initiateUpdateTimer(newHolders.get(0));
            }
        }
        /*
         * This method keeps track of next upcoming task and the moment it becomes ____ it triggers
         * auto save so the task would be re-distributed to different bracket...
         */
        private void initiateUpdateTimer(final TaskEventHolder holder) {

            TimerTask taskToExecute = new TimerTask() {
                @Override
                public void run() {
                    dataProtocol.saveTaskEventHolder(holder);
                }
            };
            Date executionTime;
            if (holder.getTimeDefined() == TaskObject.TimeDefined.dateAndTime) {
                executionTime = holder.getEndTime().getTime();
            } else {
                Calendar daysEnd = (Calendar) holder.getStartTime().clone();
                daysEnd.set(Calendar.HOUR_OF_DAY, 23);
                daysEnd.set(Calendar.MINUTE, 59);
                daysEnd.set(Calendar.SECOND, 59);
                executionTime = daysEnd.getTime();
            }
            updateTask.schedule(taskToExecute, executionTime);
        }
    }

}
