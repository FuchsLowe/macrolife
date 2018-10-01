package com.fuchsundlowe.macrolife.ListView;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpcomingList extends Fragment {


    public UpcomingList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming_list, container, false);
    }

}
