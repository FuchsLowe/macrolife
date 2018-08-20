package com.fuchsundlowe.macrolife.ListView;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

/**
 * Holder for listViews tasks that are completed!
 */
public class Completed_ListView extends Fragment {

    private DataProviderNewProtocol dataMaster;
    private View baseView;

    public Completed_ListView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Instantiation of values part:


        // Inflate the layout for this fragment
         baseView =  inflater.inflate(R.layout.fragment_completed__list_view, container, false);
         return baseView;
    }

    /*
     * A system for grabbing data that will be shown in this list
     *
     * Shall I use recycler view?
     *
     * How should I implement the swipe to delete functionality?
     */


    /*
     * TODO: Drag and drop
     */
    private void defineDragAndDrop() {

    }
}
