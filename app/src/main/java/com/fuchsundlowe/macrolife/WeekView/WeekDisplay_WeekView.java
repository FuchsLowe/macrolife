package com.fuchsundlowe.macrolife.WeekView;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;
import java.text.DateFormat;
import java.util.Calendar;
/**
 * The functional part of the WeekView Display... Holder of all things.
 */
public class WeekDisplay_WeekView extends Fragment {

    private ViewGroup baseView;
    private LinearLayout listLayout;
    private TextView weekNumber, weekDescription;
    private Calendar weekIPresent;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        baseView = (ViewGroup) inflater.inflate(R.layout.fragment_week_display__week_view, container, false);
        listLayout = baseView.findViewById(R.id.listView_weekDisplay);

        weekNumber = baseView.findViewById(R.id.weekNumber_weekView);
        String weekNumberText = getString(R.string.week) + " " + weekIPresent.get(Calendar.WEEK_OF_YEAR);
        weekNumber.setText(weekNumberText);

        weekDescription = baseView.findViewById(R.id.weekDescription_weekView);
        DateFormat daysFormatter = DateFormat.getDateInstance(DateFormat.LONG);
        String firstDay = daysFormatter.format(weekIPresent.getTime());
        Calendar lastDayTime = (Calendar) weekIPresent.clone();
        lastDayTime.add(Calendar.DAY_OF_WEEK, 6);
        String lastDay = daysFormatter.format(lastDayTime.getTime());
        String wholeText = firstDay + " " +  getString(R.string.to) + " " + lastDay;
        weekDescription.setText(wholeText);
        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initiateData();
    }

    // Calendar is assigned with first day of the week
    public void defineMe(Calendar weekIRepresent) {
        this.weekIPresent = weekIRepresent;
    }

    private void initiateData() {
        int daysInAWeek = 7;
        for (int i = 0; i < daysInAWeek; i++) {
            DayHolder_WeekView mDay = new DayHolder_WeekView(baseView.getContext());
            // Add it to view hierarchy
            listLayout.addView(mDay);
            // Assign the appropriate value
            Calendar dayToInsert = (Calendar) weekIPresent.clone();
            dayToInsert.add(Calendar.DAY_OF_WEEK, i);
            mDay.defineMe(dayToInsert);
        }
    }
}
