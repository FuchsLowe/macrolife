package com.fuchsundlowe.macrolife.BottomBar;

import android.content.ClipData;
import android.content.ClipDescription;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.R;
import java.util.ArrayList;


public class MyTaskRecommendedRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecommendedRecyclerViewAdapter.ViewHolder> {

    private ArrayList<TaskObject> data;
    public MyTaskRecommendedRecyclerViewAdapter(ArrayList<TaskObject> dataToPresent) {
        this.data = dataToPresent;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // So how should this look like?
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_taskrecommended, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.defineMe(data.get(position));
    }

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

    public void removeTask(TaskObject objectToRemove, int adapterPosition) {
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
                    return false;
                    /*
                     * TODO: Possible solution:
                     * Maybe you can remove the task after it has been confirmed attached to the
                     * other view...
                     * Maybe you can send signal to this object that it is no longer the noTime,
                     * lets give him a date
                     */
                }
            });
        }
    }
}
