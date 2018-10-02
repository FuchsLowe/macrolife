package com.fuchsundlowe.macrolife.ListView;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.Interfaces.LDCToFragmentListView;
import com.fuchsundlowe.macrolife.R;

import java.util.List;

/**
 * Holder for listViews tasks that are completed!
 */
public class CompletedList extends Fragment {

    private View baseView;
    private RecyclerView recyclerView;
    private List<TaskEventHolder> dataToDisplay;
    private TextView title;

    public CompletedList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         baseView =  inflater.inflate(R.layout.simple_list_list_view_fragments, container, false);
         title = baseView.findViewById(R.id.description_simpleListView);
         title.setText(R.string.CompletedList_title);

         recyclerView = baseView.findViewById(R.id.recyclerView_simpleListView);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
         recyclerView.setAdapter(new RegularTaskListAdapter(dataToDisplay, ListView.bracketType.completed));

         defineOnClick();

         return baseView;
    }

    public void defineMe(LDCProtocol dataProviderProtocol) {
        dataProviderProtocol.subscribeToCompleted(new DataInterface());
    }

    // Used to receive the new infusion of data and then does the data update.
    private class DataInterface extends LDCToFragmentListView {
        @Override
        public void deliverCompleted(List<TaskEventHolder> newHolders) {
            super.deliverCompleted(newHolders);
            dataToDisplay = newHolders;
            if (recyclerView != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
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
