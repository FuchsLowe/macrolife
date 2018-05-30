package com.fuchsundlowe.macrolife.FragmentModels;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.CustomViews.CalendarButton;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.Interfaces.DayViewTopFragmentCallback;
import com.fuchsundlowe.macrolife.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        String monthYearRepresentation = weekWeDisplay.getDisplayName(Calendar.MONTH, Calendar.LONG,
                Locale.getDefault()) + ", " + weekWeDisplay.get(Calendar.YEAR);
        monthDisplay.setText(monthYearRepresentation);
    }

    // Methods:
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
        Calendar currentDay = Calendar.getInstance();
        boolean isSelectedMonth;
        boolean isSelected = false;
        boolean isCurrentDay;

     //   while (rolled.get(Calendar.WEEK_OF_YEAR) == weekInfo.get(Calendar.WEEK_OF_YEAR)) {
        for (int i = 1; i<=7; i++) {

            if (rolled.get(Calendar.MONTH) == weekInfo.get(Calendar.MONTH)) {
                isSelectedMonth = true;
            } else { isSelectedMonth = false; }

            if (rolled.get(Calendar.DAY_OF_YEAR) == weekInfo.get(Calendar.DAY_OF_YEAR)) {
                isSelected = true;
            } else { isSelected = false; }

            if (rolled.get(Calendar.DAY_OF_YEAR) == currentDay.get(Calendar.DAY_OF_YEAR)) {
                isCurrentDay = true;
            } else {
                isCurrentDay = false;
            }
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
            rolled.roll(Calendar.DAY_OF_MONTH, 1);
        }
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
            Log.d("Value Asked: ", "" + ((CalendarButton) v).getTimeValue().get(Calendar.DAY_OF_MONTH));
            describeDatesInButtons();
            changeSelector((CalendarButton) v);
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

    // Testing:
    String describeDatesInButtons() {
        Log.e("CentralTime: ", " " + getDate());
        Log.d("XXXX: ", " " + pos1.getTimeValue().get(Calendar.DAY_OF_MONTH));
        Log.d("XXXX: ", " " + pos2.getTimeValue().get(Calendar.DAY_OF_MONTH));
        Log.d("XXXX: ", " " + pos3.getTimeValue().get(Calendar.DAY_OF_MONTH));
        Log.d("XXXX: ", " " + pos4.getTimeValue().get(Calendar.DAY_OF_MONTH));
        Log.d("XXXX: ", " " + pos5.getTimeValue().get(Calendar.DAY_OF_MONTH));
        Log.d("XXXX: ", " " + pos6.getTimeValue().get(Calendar.DAY_OF_MONTH));
        Log.d("XXXX: ", " " + pos7.getTimeValue().get(Calendar.DAY_OF_MONTH));
        return null;
    }
}
