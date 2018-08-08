package com.fuchsundlowe.macrolife.BottomBar;

import android.arch.lifecycle.Observer;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.DayView.Task_DayView;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import org.joda.time.Period;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class RecommendationBar extends Fragment {

    private DataProviderNewProtocol dataProvider;
    private MyTaskRecommendedRecyclerViewAdapter adapter;
    private View baseView;

    // LifeCycle:
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider = LocalStorage.getInstance(getContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taskrecommended_list, container, false);
        if (dataProvider == null) {
            dataProvider = LocalStorage.getInstance(view.getContext());
        }
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            final GridLayoutManager gridLayoutManager = new GridLayoutManager(context,
                    2, GridLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);

            adapter = new MyTaskRecommendedRecyclerViewAdapter();
            recyclerView.setAdapter(adapter);

            // Defining Live data injection mechanism:
            dataProvider.getLiveDataForRecommendationBar().observe(this, new Observer<List<TaskObject>>() {
                @Override
                public void onChanged(@Nullable List<TaskObject> objects) {
                    if (objects.size() > 1) {
                        gridLayoutManager.setSpanCount(2);
                    } else {
                        gridLayoutManager.setSpanCount(1);
                    }
                    adapter.addNewData(objects);
                }
            });

            // Now we define the drag and drop functionality
        }
        baseView = view;

        defineDragAndDropListeners();
        return view;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        dataProvider = null;
    }

    // Methods:
    private void defineDragAndDropListeners() {
        baseView.setOnDragListener(new RecommendationBar_DragListener());
    }
    public class RecommendationBar_DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Yes we can accept these values...
                    /*
                     * TODO: Establish if we are showing self to the screen, if not, we need to
                     * present at least some area to enable the task to drop...
                     */
                    return event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT)
                            ||
                            event.getClipDescription().getLabel().equals(Constants.REPEATING_EVENT);
                case DragEvent.ACTION_DRAG_LOCATION:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DROP:
                    Object dropData = event.getLocalState();
                    if (dropData instanceof TaskObject) {
                        ((TaskObject) dropData).setTimeDefined(TaskObject.TimeDefined.noTime);
                        adapter.addTask((TaskObject) dropData);
                        dataProvider.saveTaskObject((TaskObject) dropData);
                    } else if (dropData instanceof RepeatingEvent) {
                        // TODO: So what do we do then? Should we accept them? Or Just show that they aren't welcome?
                    }
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
            }
            return true;
        }
    }
}
