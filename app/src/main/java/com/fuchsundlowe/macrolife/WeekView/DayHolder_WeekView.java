package com.fuchsundlowe.macrolife.WeekView;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayHolder_WeekView extends Fragment {

    private ViewGroup baseView;
    private FrameLayout topBar;
    private LinearLayout taskBar;
    private TextView dayDescription;
    private Calendar dayThisHolderPresents;
    private DataProviderNewProtocol dataProvider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and find views
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_day_holder__week_view, container, false);
        topBar = baseView.findViewById(R.id.topBar_dayHolder_weekView);
        taskBar = baseView.findViewById(R.id.linearLayout_dayHolder_weekView);
        dayDescription = baseView.findViewById(R.id.dayDescription_dayHolder_weekView);

        dataProvider = LocalStorage.getInstance(container.getContext());

        return baseView;
    }

    // This is called to insert data
    public void defineMe(Calendar dayIPresent) {
        /*
         * This guy needs to provide for each task a taskObject, repeating events if any and uniform
         * timeCapsule.
         */
        this.dayThisHolderPresents = dayIPresent;

        /*
         * Now I need to find all tasks and events that exist and create weekTasks out of them
         *
         * So how should this filter data?
         */


        // Creation of WeekTasks...

    }

}
