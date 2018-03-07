package com.fuchsundlowe.macrolife.FragmentModels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
/*
 *
 */

public class TopBarFragment_DayView extends Fragment {


    private Calendar day;
    private TextView topLabel;
    private TextView bottomLabel;

    public TopBarFragment_DayView() {
        // Required empty public constructor
    }

    public void setDay(Calendar day) {
        this.day = day;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.day_view_top_bar,
               container,false);
       return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        topLabel = getView().findViewById(R.id.topLabel);
        bottomLabel = getView().findViewById(R.id.bottomLabel);
        if (day != null) {
            topLabel.setText(getDay());
            bottomLabel.setText(getDate());
        }

    }

    @Override
    public void onStart() {
        super.onStart();

//        topLabel.setText(getDay());
//        bottomLabel.setText(getDate());
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    // TODO: To define the size of text and overall position metrics, color etc
    // Implememntation of support methods:
    private String getDay() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        String value = format.format(this.day.getTime());
        return value;
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        String value = format.format(this.day.getTime());
        return  value;
    }


}
