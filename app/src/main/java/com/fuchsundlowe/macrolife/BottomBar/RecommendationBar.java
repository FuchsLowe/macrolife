package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;


public class RecommendationBar extends Fragment {

    private int mColumnCount = 2;
    private DataProviderNewProtocol dataProvider;
    private MyTaskRecommendedRecyclerViewAdapter adapter;
    private View baseView;

    public RecommendationBar() {
    }

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
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(context, mColumnCount, GridLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(gridLayoutManager);
            }
            // TODO: This is called on update of dataset... Can I remove the thing from it...
            adapter = new MyTaskRecommendedRecyclerViewAdapter(dataProvider.getDataForRecommendationBar());
            recyclerView.setAdapter(adapter);
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

    private void defineDragAndDropListeners() {
        baseView.setOnDragListener(new RecommendationBar_DragListener());
    }

    public class RecommendationBar_DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().getLabel().equals(Constants.TASK_OBJECT)
                            ||
                            event.getClipDescription().getLabel().equals(Constants.REPEATING_EVENT)) {
                        // Yes we can accept these values...
                        /*
                         * TODO: Establish if we are showing self to the screen, if not, we need to
                         * present at least some area to enable the task to drop...
                         */
                        return true;
                    } else {
                        return false;
                    }
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
