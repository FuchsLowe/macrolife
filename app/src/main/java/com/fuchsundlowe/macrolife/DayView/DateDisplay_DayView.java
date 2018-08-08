package com.fuchsundlowe.macrolife.DayView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DayView.CalendarButton;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.Interfaces.DayViewTopFragmentCallback;
import com.fuchsundlowe.macrolife.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
// The class that holds DatesDisplay and provides all the functionality of that fragment
public class DateDisplay_DayView extends Fragment implements View.OnClickListener{


    private Calendar selectedDay;
    private Calendar weekWeDisplay;
    private ViewGroup baseView;
    private DayViewTopFragmentCallback callback;
    private CalendarButton pos1, pos2, pos3, pos4, pos5, pos6, pos7;
    private TextView monthDisplay;
    private int firstDayOfWeek;
    private SharedPreferences preferences;

    public DateDisplay_DayView() {
        // Required empty public constructor
    }

    // LifeCycle:
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        baseView = (ViewGroup) inflater.inflate(R.layout.day_view_top_bar,
               container,false);

        monthDisplay =  baseView.findViewById(R.id.DV_TopBar_CurrentMonthAndYear);

        pos1 = baseView.findViewById(R.id.pos1);
        pos1.setOnClickListener(this);

        pos2 = baseView.findViewById(R.id.pos2);
        pos2.setOnClickListener(this);

        pos3 = baseView.findViewById(R.id.pos3);
        pos3.setOnClickListener(this);

        pos4 = baseView.findViewById(R.id.pos4);
        pos4.setOnClickListener(this);

        pos5 = baseView.findViewById(R.id.pos5);
        pos5.setOnClickListener(this);

        pos6 = baseView.findViewById(R.id.pos6);
        pos6.setOnClickListener(this);

        pos7 = baseView.findViewById(R.id.pos7);
        pos7.setOnClickListener(this);

        return baseView;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK,
                weekWeDisplay.getFirstDayOfWeek());

        // Defines the day buttons
        assignDates(weekWeDisplay);

        // Sets the Month and year in top bar ar String, locale sensitive
        defineMonthYearRepresentation();
    }

    // Methods:
    //Called by outside class to fill in the taskPresented for the Fragment
    public void defineTopBar(DayViewTopFragmentCallback protocol, Calendar weekToDisplay) {
        this.callback = protocol;
        this.weekWeDisplay = weekToDisplay;
    }
    // This class fills in the button fields...
    private void assignDates(Calendar weekInfo) {
        // Determine the first day of the week
        int firstDay = preferences.getInt(Constants.FIRST_DAY_OF_WEEK, weekInfo.getFirstDayOfWeek());

        Calendar rolled = (Calendar) weekInfo.clone();
        rolled.set(Calendar.DAY_OF_WEEK, 1); // setting it to be Sunday
        // This might be a problem?
        Calendar currentDay = Calendar.getInstance();
        boolean isSelectedMonth;
        boolean isSelected = false;
        boolean isCurrentDay;

     //   while (rolled.get(Calendar.WEEK_OF_YEAR) == weekInfo.get(Calendar.WEEK_OF_YEAR)) {
        for (int i = 1; i<=7; i++) {

            isSelectedMonth = rolled.get(Calendar.MONTH) == weekInfo.get(Calendar.MONTH);

            isSelected = rolled.get(Calendar.DAY_OF_YEAR) == weekInfo.get(Calendar.DAY_OF_YEAR);

            isCurrentDay = rolled.get(Calendar.DAY_OF_YEAR) == currentDay.get(Calendar.DAY_OF_YEAR);
            Calendar toPass = (Calendar) rolled.clone();
            switch (firstDay) {
                case 2:
                    switch (i){
                        case 1:
                            pos7.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 2:
                            pos1.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 3:
                            pos2.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 4:
                            pos3.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 5:
                            pos4.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 6:
                            pos5.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 7:
                            pos6.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                    }
                    break;

                default: // assumes US system Sunday is the first day
                    switch (i){
                        case 1:
                            pos1.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 2:
                            pos2.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 3:
                            pos3.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 4:
                            pos4.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 5:
                            pos5.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 6:
                            pos6.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                        case 7:
                            pos7.defineButton(toPass, isSelected, isCurrentDay, isSelectedMonth);
                            break;
                    }
                    break;
            }
            rolled.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
    private void defineMonthYearRepresentation() {
        String monthYearRepresentation = weekWeDisplay.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault()) + ", " + weekWeDisplay.get(Calendar.YEAR);
        monthDisplay.setText(monthYearRepresentation);
    }
    public Calendar getWeekWeDisplay() {
        return weekWeDisplay;
    }
    @Deprecated
    private String getDay() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        String value = format.format(this.selectedDay.getTime());
        return value;
    }
    @Deprecated
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String value = format.format(weekWeDisplay.getTime());
        return  value;
    }

    // OnClick Interface:
    @Override
    public void onClick(View v) {
        if (v instanceof CalendarButton) {
            changeSelector((CalendarButton) v);
            weekWeDisplay = ((CalendarButton) v).getTimeValue();
            defineMonthYearRepresentation();
            callback.setNewSelectedDay(((CalendarButton) v).getTimeValue());
        }
    }
    // Changes the selector indicator on the button
    private void changeSelector(CalendarButton thisButton) {
        if (thisButton.equals(pos1)) {
            pos1.toggleSelected(true);
        } else { pos1.toggleSelected(false); }

        if (thisButton.equals(pos2)) {
            pos2.toggleSelected(true);
        } else { pos2.toggleSelected(false); }

        if (thisButton.equals(pos3)) {
            pos3.toggleSelected(true);
        } else { pos3.toggleSelected(false); }

        if (thisButton.equals(pos4)) {
            pos4.toggleSelected(true);
        } else { pos4.toggleSelected(false); }

        if (thisButton.equals(pos5)) {
            pos5.toggleSelected(true);
        } else { pos5.toggleSelected(false); }

        if (thisButton.equals(pos6)) {
            pos6.toggleSelected(true);
        } else { pos6.toggleSelected(false); }

        if (thisButton.equals(pos7)) {
            pos7.toggleSelected(true);
        } else { pos7.toggleSelected(false); }

    }

    // Changes the selected day
    public void changeSelection(Calendar newTimeSelected) {
        if (pos1.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos1.toggleSelected(true);
        } else { pos1.toggleSelected(false);}

        if (pos2.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos2.toggleSelected(true);
        } else { pos2.toggleSelected(false);}

        if (pos3.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos3.toggleSelected(true);
        } else { pos3.toggleSelected(false);}

        if (pos4.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos4.toggleSelected(true);
        } else { pos4.toggleSelected(false);}

        if (pos5.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos5.toggleSelected(true);
        } else { pos5.toggleSelected(false);}

        if (pos6.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos6.toggleSelected(true);
        } else { pos6.toggleSelected(false);}

        if (pos7.getTimeValue().get(Calendar.DAY_OF_WEEK) == newTimeSelected.get(Calendar.DAY_OF_WEEK)) {
            pos7.toggleSelected(true);
        } else { pos7.toggleSelected(false);}
    }

}
