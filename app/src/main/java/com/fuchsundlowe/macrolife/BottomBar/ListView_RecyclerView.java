package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.ArrayList;
import java.util.List;

public class ListView_RecyclerView extends RecyclerView {

    private LayoutManager layoutManager;
    private DataProviderNewProtocol localStorage;
    private TaskAdapter adapter;

    public ListView_RecyclerView(Context context) {
        super(context);
    }

    public void defineMe(int taskMasterID) {
        layoutManager = new LinearLayoutManager(getContext());
        this.setLayoutManager(layoutManager);

        ViewGroup.LayoutParams parms = this.getLayoutParams();
        parms.width = ViewGroup.LayoutParams.MATCH_PARENT;
        parms.height = ViewGroup.LayoutParams.MATCH_PARENT;
        this.setLayoutParams(parms);

        this.setHasFixedSize(true);

        localStorage = LocalStorage.getInstance(getContext());

        adapter = new TaskAdapter(localStorage.findListFor(taskMasterID));

        this.setAdapter(adapter);
        swipeFunctionality();
    }

    public void addListObject(ListObject newListObject) {
        adapter.dataToDisplay.add(newListObject);
        adapter.notifyDataSetChanged();
    }
    // https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
    private void swipeFunctionality() {
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int direction) {
                adapter.removeAt(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                /*
                View itemView = viewHolder.itemView;
                int itemHeight = itemView.getHeight();
                itemView.getBackground().setBounds(
                        itemView.getRight() + (int)dX,
                        itemView.getTop(),
                        itemView.getRight(),
                        itemView.getBottom()
                        );
                itemView.getBackground().draw(c);

                int percentOfMargin = 10;
                int margin = itemHeight / percentOfMargin;

                int iconTop = margin;
                int iconRight = itemView.getRight() - margin;
                int iconLeft = iconRight; // add minus the width of the icon...
                int iconBottom = itemView.getBottom() - margin;

                // TODO: Draw the icon...
                */
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        touchHelper.attachToRecyclerView(this);
    }

    class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView taskName;
            public CheckBox checkStatus;
            public ListObject data;

            public ViewHolder(View itemView) {
                super(itemView);
            }

            void insertData(ListObject data) {
                this.data = data;
                taskName = itemView.findViewById(R.id.edit_task_taskName);
                checkStatus = itemView.findViewById(R.id.edit_task_checkBox);

                taskName.setText(data.getTaskName());
                checkStatus.setChecked(data.getTaskStatus());

                // Enables responses to Click events on checkBox and editing Task Name
                defineFunctionality();
            }
            void defineFunctionality() {
                checkStatus.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportSave();
                    }
                });
                taskName.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            reportSave();
                        }
                    }
                });
            }
            void reportSave() {
                data.setTaskName(taskName.getText().toString());
                data.setTaskStatus(checkStatus.isChecked());
                localStorage.saveListObject(data);
            }
        }

        List<ListObject> dataToDisplay;

        public TaskAdapter(List<ListObject> dataSet) {
            this.dataToDisplay = dataSet;
        }
        @NonNull
        @Override
        public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View toBind = LayoutInflater.from(getContext()).inflate(R.layout.edit_task, parent, false);

            return new ViewHolder(toBind);
        }
        @Override
        public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
            holder.insertData(dataToDisplay.get(position));
        }
        @Override
        public int getItemCount() {
            return dataToDisplay.size();
        }

        public void removeAt(int position) {
            localStorage.deleteListObject(dataToDisplay.get(position));
            dataToDisplay.remove(position);
            notifyItemRemoved(position);
        }

        public void updateData(ArrayList<ListObject> dataUpdate) {
            dataToDisplay = dataUpdate;
            notifyDataSetChanged();
        }
    }
}