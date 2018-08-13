package com.fuchsundlowe.macrolife.WeekView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

/**
 * The functional part of the WeekView Display... Holder of all things.
 */
public class WeekDisplay_WeekView extends Fragment {

    private ViewGroup baseView;
    private DataProviderNewProtocol dataProvider;
    /*
     * TODO: How should I store the day-reference of week here?
     *
     * Maybe It can be a Calendar instance that holds the first day of week... Like having it
     * be Monday or Sunday... And then based on that it searches for data and presents it...
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_week_display__week_view, container, false);
        dataProvider = LocalStorage.getInstance(container.getContext());

        return baseView;
    }

    /*
     * TODO: Define the data insertion class that will re-distribute data to children
     *
     */
    // Calendar is assigned with first day of the week
    public void defineMe(Calendar weekIRepresent) {
        int daysInAWeek = 7;
        for (int i = 0; i < daysInAWeek; i++) {
            DayHolder_WeekView mDay = new DayHolder_WeekView();
            // Add it to view hierarchy
            baseView.addView(mDay);
            // Assign the appropriate value
            Calendar dayToInsert = (Calendar) weekIRepresent.clone();
            dayToInsert.add(Calendar.DAY_OF_WEEK, i);
            mDay.defineMe(dayToInsert);
        }
    }
}
