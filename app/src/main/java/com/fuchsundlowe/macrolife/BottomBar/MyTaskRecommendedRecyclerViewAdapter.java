package com.fuchsundlowe.macrolife.BottomBar;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.R;
import java.util.List;


public class MyTaskRecommendedRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecommendedRecyclerViewAdapter.ViewHolder> {

    private List<TaskObject> data; // Holder of data we are presenting

    // LifeCycle:
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_taskrecommended, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.defineMe(data.get(position));
    }

    // Data Manipulation:
    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else { return 0; }
    }
    public void addTask(TaskObject taskToAdd) {
        // Look if we have the taks with the same hashID... means we are just adding same one to ourselves
        for (TaskObject inHolder : data) {
            if (inHolder.getHashID() == taskToAdd.getHashID()) {
                // We don't add it
                return;
            }
        }
        data.add(taskToAdd);
        notifyDataSetChanged();
    }
    public void addNewData(List<TaskObject> dataToPresent) {
        this.data = dataToPresent;
        this.notifyDataSetChanged();
    }
    private void removeTask(TaskObject objectToRemove, int adapterPosition) {
        data.remove(objectToRemove);
        notifyItemRemoved(adapterPosition);
    }

    // This class wraps data with view
    public class ViewHolder extends RecyclerView.ViewHolder {
        TaskObject data;
        View me;
        TextView toPutNameInto;
        protected ViewHolder(View view) {
            super(view);
            this.me = view;
            toPutNameInto = me.findViewById(R.id.taskName_RecomendationTask);
        }

        private TaskObject getDataObject() {
            return data;
        }

        public void defineMe(TaskObject taskToRepresent) {
            this.data = taskToRepresent;
            toPutNameInto.setText(taskToRepresent.getTaskName());
            me.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Test The call of edit...
                    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(me.getContext());
                    Intent intent = new Intent(Constants.INTENT_FILTER_GLOBAL_EDIT);
                    intent.putExtra(Constants.INTENT_FILTER_TASK_ID, data.getHashID());
                    manager.sendBroadcast(intent);
                }
            });

            me.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // We have a long click...
                    String[] MIME_Type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    // Sending the height so I could manage the layout better in whoever is accepting it.
                    ClipData.Item dataItem = new ClipData.Item(String.valueOf(me.getHeight()));
                    final ClipData data = new ClipData(Constants.TASK_OBJECT, MIME_Type, dataItem);
                    final  View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(me);
                    me.startDrag(data, shadowBuilder, getDataObject(), 0);
                    removeTask(getDataObject(), getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
