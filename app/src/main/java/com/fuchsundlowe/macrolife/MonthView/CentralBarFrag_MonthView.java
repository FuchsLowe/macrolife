package com.fuchsundlowe.macrolife.MonthView;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;


public class CentralBarFrag_MonthView extends Fragment {

    private View baseView;
    private TextView[] dayTittles;
    private Button[] dayButtons;
    private Calendar monthDisplayed;
    private static View.OnClickListener calendarButtonAction;

    public CentralBarFrag_MonthView() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        baseView =  inflater.inflate(R.layout.fragment_central_bar_frag__month_view, container, false);

        // Init of TextViews used for displaying dayTittles of the week
        dayTittles = new TextView[7];
        dayTittles[0] = baseView.findViewById(R.id.day_1);
        dayTittles[1] = baseView.findViewById(R.id.day_2);
        dayTittles[2] = baseView.findViewById(R.id.day_3);
        dayTittles[3] = baseView.findViewById(R.id.day_4);
        dayTittles[4] = baseView.findViewById(R.id.day_5);
        dayTittles[5] = baseView.findViewById(R.id.day_6);
        dayTittles[6] = baseView.findViewById(R.id.day_7);

        // Initiating buttons array:
        dayButtons = new Button[42];
        // ROW 1
        dayButtons[0] = baseView.findViewById(R.id.R1_1);
        dayButtons[1] = baseView.findViewById(R.id.R1_2);
        dayButtons[2] = baseView.findViewById(R.id.R1_3);
        dayButtons[3] = baseView.findViewById(R.id.R1_4);
        dayButtons[4] = baseView.findViewById(R.id.R1_5);
        dayButtons[5] = baseView.findViewById(R.id.R1_6);
        dayButtons[6] = baseView.findViewById(R.id.R1_7);
        // ROW 2
        dayButtons[7] = baseView.findViewById(R.id.R2_1);
        dayButtons[8] = baseView.findViewById(R.id.R2_2);
        dayButtons[9] = baseView.findViewById(R.id.R2_3);
        dayButtons[10] = baseView.findViewById(R.id.R2_4);
        dayButtons[11] = baseView.findViewById(R.id.R2_5);
        dayButtons[12] = baseView.findViewById(R.id.R2_6);
        dayButtons[13] = baseView.findViewById(R.id.R2_7);
        // ROW 3
        dayButtons[14] = baseView.findViewById(R.id.R3_1);
        dayButtons[15] = baseView.findViewById(R.id.R3_2);
        dayButtons[16] = baseView.findViewById(R.id.R3_3);
        dayButtons[17] = baseView.findViewById(R.id.R3_4);
        dayButtons[18] = baseView.findViewById(R.id.R3_5);
        dayButtons[19] = baseView.findViewById(R.id.R3_6);
        dayButtons[20] = baseView.findViewById(R.id.R3_7);
        // ROW 4
        dayButtons[21] = baseView.findViewById(R.id.R4_1);
        dayButtons[22] = baseView.findViewById(R.id.R4_2);
        dayButtons[23] = baseView.findViewById(R.id.R4_3);
        dayButtons[24] = baseView.findViewById(R.id.R4_4);
        dayButtons[25] = baseView.findViewById(R.id.R4_5);
        dayButtons[26] = baseView.findViewById(R.id.R4_6);
        dayButtons[27] = baseView.findViewById(R.id.R4_7);
        // ROW 5
        dayButtons[28] = baseView.findViewById(R.id.R5_1);
        dayButtons[29] = baseView.findViewById(R.id.R5_2);
        dayButtons[30] = baseView.findViewById(R.id.R5_3);
        dayButtons[31] = baseView.findViewById(R.id.R5_4);
        dayButtons[32] = baseView.findViewById(R.id.R5_5);
        dayButtons[33] = baseView.findViewById(R.id.R5_6);
        dayButtons[34] = baseView.findViewById(R.id.R5_7);
        // ROW 6
        dayButtons[35] = baseView.findViewById(R.id.R6_1);
        dayButtons[36] = baseView.findViewById(R.id.R6_2);
        dayButtons[37] = baseView.findViewById(R.id.R6_3);
        dayButtons[38] = baseView.findViewById(R.id.R6_4);
        dayButtons[39] = baseView.findViewById(R.id.R6_5);
        dayButtons[40] = baseView.findViewById(R.id.R6_6);
        dayButtons[41] = baseView.findViewById(R.id.R6_7);

        return baseView;
    }

    public void defineMe(Calendar monthToDisplay) {
        this.monthDisplayed = monthToDisplay;
        setTheCalendar(monthToDisplay);
    }

    private void setTheCalendar(Calendar monthImpression) {
        SharedPreferences preferences = baseView.getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        int firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK, monthImpression.getFirstDayOfWeek());
        switch (firstDayOfWeek) {
            case 1: // The US System where Sunday is the first dayTittles
                dayTittles[0].setText(getString(R.string.Sunday_Short));
                dayTittles[1].setText(getString(R.string.Monday_Short));
                dayTittles[2].setText(getString(R.string.Tuesday_Short));
                dayTittles[3].setText(getString(R.string.Wednesday_Short));
                dayTittles[4].setText(getString(R.string.Thursday_Short));
                dayTittles[5].setText(getString(R.string.Friday_Short));
                dayTittles[6].setText(getString(R.string.Saturday_Short));
                // TODO: Colorize the Sunday
                break;
            default: // The European system where Monday is the first dayTittles of the week
                dayTittles[0].setText(getString(R.string.Monday_Short));
                dayTittles[1].setText(getString(R.string.Tuesday_Short));
                dayTittles[2].setText(getString(R.string.Wednesday_Short));
                dayTittles[3].setText(getString(R.string.Thursday_Short));
                dayTittles[4].setText(getString(R.string.Friday_Short));
                dayTittles[5].setText(getString(R.string.Saturday_Short));
                dayTittles[6].setText(getString(R.string.Sunday_Short));
                // TODO: Colorize the Sunday
        }
        // Defining the onClickListener
        if (calendarButtonAction == null) {
            calendarButtonAction = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v instanceof Button) {
                        Integer number = Integer.valueOf(String.valueOf(((Button) v).getText()));
                        reportButtonClick(number);
                    }
                }
            };
        }
        // Define the buttons:
        int month = monthImpression.get(Calendar.MONTH);
        int weekOfMonth;
        do {
            weekOfMonth = monthImpression.get(Calendar.WEEK_OF_MONTH);
            Button mButton = getButton(weekOfMonth, monthImpression.get(Calendar.DAY_OF_WEEK), firstDayOfWeek);
            mButton.setText(monthImpression.get(Calendar.DAY_OF_MONTH));
            mButton.setOnClickListener(calendarButtonAction);
            monthImpression.add(Calendar.DAY_OF_YEAR, 1);
        } while (monthImpression.get(Calendar.MONTH) == month);

        // We have to set the month back to one we started since it was overshot
        monthImpression.add(Calendar.DAY_OF_YEAR, -1);
        // Evaluate if we have some extra rows to remove.
        weekOfMonth = monthImpression.get(Calendar.WEEK_OF_MONTH);
        if (weekOfMonth == 4) {
            removeFifthAndSixthRow();
        } else if (weekOfMonth == 5) {
            removeSixthRow();
        }
        // Try and select today if found in this month
        selectCurrentDay(monthImpression, firstDayOfWeek);
    }
    private Button getButton(int row, int dayOfWeek, int firstDayOfWeek) {
        switch (firstDayOfWeek) {
            case 1: // Sunday is the first day
                return dayButtons[row * dayOfWeek -1];
            default: // Monday is the first day
                int modifier = 0;
                switch (dayOfWeek) {
                    case (Calendar.MONDAY):
                        modifier = 1;
                        break;
                    case (Calendar.TUESDAY):
                        modifier = 2;
                        break;
                    case (Calendar.WEDNESDAY):
                        modifier = 3;
                        break;
                    case (Calendar.THURSDAY):
                        modifier = 4;
                        break;
                    case (Calendar.FRIDAY):
                        modifier = 5;
                        break;
                    case (Calendar.SATURDAY):
                        modifier = 6;
                        break;
                    case (Calendar.SUNDAY):
                        modifier = 7;
                        break;
                }
                return dayButtons[row * modifier -1];
        }
    }
    private void deselectButtonsOtherThan(int day) {
        for (Button m: dayButtons) {
            Integer number = Integer.valueOf(String.valueOf(m.getText()));
            if (number != day) {
                // TODO Implement the deselect...
            }
        }
    }
    private void removeSixthRow() {
        for (int i = 28; i<= 34; i++) {
            dayButtons[i].setVisibility(View.GONE);
        }
    }
    private void removeFifthAndSixthRow() {
        for (int i = 28; i<= 41; i++) {
            dayButtons[i].setVisibility(View.GONE);
        }
    }
    private void reportButtonClick(int number) {
        deselectButtonsOtherThan(number);
        // TODO: How should we report the click on button?
        Calendar dateToSend = (Calendar) monthDisplayed.clone();
        dateToSend.set(Calendar.DAY_OF_MONTH, number);

    }
    // If today is found in this month, this will colorize it
    private void selectCurrentDay(Calendar currentMonth, int fistDayOfWeek) {
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH)) {
            //TODO: Specify the colorization for this day... Consider how this should change if
            // date is selected / unselected
                getButton(today.get(Calendar.WEEK_OF_MONTH), today.get(Calendar.DAY_OF_WEEK), fistDayOfWeek);
        }
    }
}
