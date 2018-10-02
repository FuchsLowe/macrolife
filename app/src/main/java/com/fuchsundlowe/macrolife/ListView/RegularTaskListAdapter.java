package com.fuchsundlowe.macrolife.ListView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;

import java.util.List;

// A standard adapter that is used in Recycler views for UpcomingList and CompletedList
public class RegularTaskListAdapter extends RecyclerView.Adapter<RegularTaskListAdapter.TaskHolder> {

    private List<TaskEventHolder> dataToDisplay;
    private final ListView.bracketType type;

    RegularTaskListAdapter(List<TaskEventHolder> dataToDisplay, ListView.bracketType type) {
        this.dataToDisplay = dataToDisplay;
        this.type = type;
    }

    @NonNull
    @Override
    public RegularTaskListAdapter.TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegularTaskListAdapter.TaskHolder(new RegularTask(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull RegularTaskListAdapter.TaskHolder holder, int position) {
        holder.task.defineMe(dataToDisplay.get(position), type);
    }

    @Override
    public int getItemCount() {
        if (dataToDisplay!= null) {
            return dataToDisplay.size();
        } else { return 0; }
    }

    class TaskHolder extends RecyclerView.ViewHolder {
        RegularTask task;
        public TaskHolder(View itemView) {
            super(itemView);
            if (itemView instanceof RegularTask) {
                task = (RegularTask) itemView;
            }
        }
    }
}
