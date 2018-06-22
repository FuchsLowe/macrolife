package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
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
        dataProvider = LocalStorage.getInstance(null);

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
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new MyTaskRecommendedRecyclerViewAdapter(dataProvider.getDataForRecommendationBar());
            recyclerView.setAdapter(adapter);
        }
        baseView = view;

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        defineDragAndDropListeners();
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
                    if (event.getClipData().getDescription().getLabel() == Constants.TASK_OBJECT) {
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
                        adapter.addTask((TaskObject) dropData);
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
