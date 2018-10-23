package com.fuchsundlowe.macrolife.MonthView;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopBarFrag_MonthView extends Fragment {

    private Map<Integer,Button> buttonMap;
    private Calendar yearPresented;

    public TopBarFrag_MonthView() {
        // Required empty public constructor
    }

    public void defineMe(Calendar yearToPresent) {
        this.yearPresented = yearToPresent;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View base = inflater.inflate(R.layout.fragment_top_bar_frag__month_view, container, false);

        TextView yearLabel = base.findViewById(R.id.yearLabel_topBar_monthView);
        yearLabel.setText(yearPresented.get(Calendar.YEAR));

        buttonMap = new HashMap<>(17); // because default load factor is 0.75

        buttonMap.put(1, (Button) base.findViewById(R.id.january));
        buttonMap.put(2, (Button) base.findViewById(R.id.february));
        buttonMap.put(3, (Button) base.findViewById(R.id.march));
        buttonMap.put(4, (Button) base.findViewById(R.id.april));
        buttonMap.put(5, (Button) base.findViewById(R.id.may));
        buttonMap.put(6, (Button)  base.findViewById(R.id.june));
        buttonMap.put(7, (Button) base.findViewById(R.id.july));
        buttonMap.put(8, (Button)  base.findViewById(R.id.august));
        buttonMap.put(9, (Button)  base.findViewById(R.id.september));
        buttonMap.put(10, (Button)  base.findViewById(R.id.october));
        buttonMap.put(11, (Button)  base.findViewById(R.id.november));
        buttonMap.put(12, (Button)  base.findViewById(R.id.december));

        defineOnClickListeners();

        return base;
    }



    private void defineOnClickListeners(){
        // Defining the click listener:
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO  Implement:
                /*
                 * Change selection this specific one to be selected, and remove
                 * potential selection to others...
                 */
            }
        };

        // Assigning the Listener:
        for (Button mButton: buttonMap.values()) {
            mButton.setOnClickListener(buttonClickListener);
        }

        //TODO Implement:  In addition, select the current month:
        buttonMap.get(yearPresented.get(Calendar.MONTH));
    }
}
