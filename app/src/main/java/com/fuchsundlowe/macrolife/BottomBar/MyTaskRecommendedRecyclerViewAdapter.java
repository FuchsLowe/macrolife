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
        data.add(taskToAdd);
        notifyDataSetChanged();
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

        public void defineMe(TaskObject taskToRepresent) {
            this.data = taskToRepresent;
            toPutNameInto.setText(taskToRepresent.getTaskName());
            me.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] MIME_Type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData.Item dataItem = new ClipData.Item(String.valueOf(data.getHashID()));
                    ClipData data = new ClipData(Constants.TASK_OBJECT, MIME_Type, dataItem);
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(me);
                    me.startDrag(data,shadowBuilder, data,0);
                }
            });
        }
    }
}
