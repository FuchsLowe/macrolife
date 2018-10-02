package com.fuchsundlowe.macrolife.ListView;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.Interfaces.LDCToFragmentListView;
import com.fuchsundlowe.macrolife.R;

import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComplexList extends Fragment implements ComplexLiveDataProtocol{

    private View baseView;
    private TextView title;
    private List<ComplexGoal> goals;
    private RecyclerView recyclerView;
    private Map<Integer, Integer> statsCompleted, statsIncomplete;
    private Map<Integer, TaskEventHolder> nextTask;
    private Observer<List<ComplexGoal>> liveDataObserver;

    public ComplexList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView =  inflater.inflate(R.layout.simple_list_list_view_fragments, container, false);

        title = baseView.findViewById(R.id.description_simpleListView);
        title.setText(R.string.ComplexList_title);

        recyclerView = baseView.findViewById(R.id.recyclerView_simpleListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(baseView.getContext()));
        recyclerView.setAdapter(new ComplexGoalListAdapter());

        defineOnClick();

        return baseView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void defineMe(LDCProtocol dataProviderProtocol) {
        dataProviderProtocol.subscribeToCompleted(new DataInterface());
        dataProviderProtocol.subscribeToComplexLiveData(this);
    }

    // ComplexLiveDataProtocol implementation:
    public void complexGoalLiveData(LiveData<List<ComplexGoal>> data) {
        liveDataObserver = new Observer<List<ComplexGoal>>() {
            @Override
            public void onChanged(@Nullable List<ComplexGoal> complexGoals) {
                goals = complexGoals;
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
        data.observeForever(liveDataObserver);
    }

    // Subscribes to receive new statistics that belong to ComplexGoals...
    private class DataInterface extends LDCToFragmentListView {
        @Override
        public void deliverComplexTasksStatistics(Map<Integer, Integer> newCompleted, Map<Integer, Integer> newIncomplete, Map<Integer, TaskEventHolder> nextTasks) {
            super.deliverComplexTasksStatistics(newCompleted, newIncomplete, nextTasks);
            statsCompleted = newCompleted;
            statsIncomplete = newIncomplete;
            nextTask = nextTasks;
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    // Recycler used
    private class ComplexGoalListAdapter extends RecyclerView.Adapter<ComplexGoalListAdapter.GoalHolder> {

        @NonNull
        @Override
        public GoalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GoalHolder holder = new GoalHolder(new ComplexTask(parent.getContext()));
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull GoalHolder holder, int position) {
            Point stats = new Point();
            ComplexGoal goal = goals.get(position);
            if (statsCompleted != null && statsIncomplete != null) {
                stats.x = statsCompleted.get(goal.getHashID());
                stats.y = statsIncomplete.get(goal.getHashID());
            }
            holder.task.defineMe(goal, stats, nextTask.get(goal.getHashID()));
        }

        @Override
        public int getItemCount() {
            if (goals != null) {
                return goals.size();
            } else {
                return 0;
            }
        }

        class GoalHolder extends RecyclerView.ViewHolder {
            ComplexTask task;
            public GoalHolder(View itemView) {
                super(itemView);
                if (itemView instanceof ComplexTask) {
                    task = (ComplexTask) itemView;
                }

            }
        }
    }

    private void defineOnClick() {
        // Used to dismiss bottom Bar edit if its active:
        baseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseView.getContext());
                Intent removeBottomBar = new Intent();
                removeBottomBar.setAction(Constants.INTENT_FILTER_STOP_EDITING);
                manager.sendBroadcast(removeBottomBar);
            }
        });
    }
}
