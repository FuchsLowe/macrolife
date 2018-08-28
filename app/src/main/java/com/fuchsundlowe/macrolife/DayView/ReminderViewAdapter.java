package com.fuchsundlowe.macrolife.DayView;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.R;
import java.util.List;
import com.fuchsundlowe.macrolife.DayView.DayDisplay_DayView.TaskEventHolder;

public class ReminderViewAdapter extends RecyclerView.Adapter<ReminderViewAdapter.ReminderViewHolder>{

    protected List<TaskEventHolder> data;

    // LifeCycle:
    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_view_task, parent, false);
        return new ReminderViewHolder((view));
    }
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        holder.defineMe(data.get(position));
    }

    // Data Manipulation:
    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }
    private void removeItem(TaskEventHolder taskToRemove, int position) {
        data.remove(taskToRemove);
        notifyItemRemoved(position);
    }
    public void addNewData(List<TaskEventHolder> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    // Task Wrapper Class
    public class ReminderViewHolder extends RecyclerView.ViewHolder {
        TaskEventHolder reminderPresented;
        View baseView;
        CheckBox checkBox;
        TextView name;
        LinearLayout imageHolder;
        ImageView noteImage, listImage;

        private ReminderViewHolder(View itemView) {
            super(itemView);
            this.baseView = itemView;
            checkBox = baseView.findViewById(R.id.checkBox_reminderViewTask);
            name = baseView.findViewById(R.id.nameLabel_reminderViewTask);
            imageHolder = baseView.findViewById(R.id.imageHolder_reminderViewTask);
            noteImage = baseView.findViewById(R.id.noteImage_reminderView);
            listImage = baseView.findViewById(R.id.listImage_reminderView);
        }

        private TaskEventHolder getReminderPresented() {
            return this.reminderPresented;
        }

        public void defineMe(TaskEventHolder taskToRepresent) {
            this.reminderPresented = taskToRepresent;

            // Implementation of reminderPresented to View values:
            switch (reminderPresented.getCompletionState()) {
                case incomplete:
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(false);
                    break;
                case completed:
                    checkBox.setVisibility(View.VISIBLE);
                    checkBox.setChecked(true);
                    break;
                case notCheckable:
                    checkBox.setVisibility(View.GONE);
                    break;
            }

            name.setText(reminderPresented.getName());

            if (reminderPresented.getAllMods().contains(TaskObject.Mods.note) ) {
                noteImage.setVisibility(View.VISIBLE);
                noteImage.setImageResource(R.drawable.note_add_24px);
            } else {
                noteImage.setVisibility(View.GONE);
            }

            if (reminderPresented.getAllMods().contains(TaskObject.Mods.list)) {
                listImage.setVisibility(View.VISIBLE);
                listImage.setImageResource(R.drawable.list_alt_24px);
            } else {
                listImage.setVisibility(View.GONE);
            }

            // on Long Click we should produce the Drag and drop system
            baseView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] MIME_Type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                    ClipData.Item dataItem = new ClipData.Item(String.valueOf(baseView.getHeight()));
                    final ClipData data = new ClipData(Constants.TASK_OBJECT, MIME_Type, dataItem);
                    final View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(baseView);
                    baseView.startDrag(data, shadowBuilder, getReminderPresented(), 0);
                    removeItem(getReminderPresented(), getAdapterPosition());

                    return true;
                }
            });
            // on click we should open it in editor at bottom
            baseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseView.getContext());
                    Intent intent = new Intent(Constants.INTENT_FILTER_GLOBAL_EDIT);
                    intent.putExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, reminderPresented.getMasterHashID());
                    manager.sendBroadcast(intent);

                }
            });
        }
    }
}
