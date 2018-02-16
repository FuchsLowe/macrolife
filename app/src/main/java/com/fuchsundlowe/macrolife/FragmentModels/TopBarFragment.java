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


public class TopBarFragment extends Fragment {


    private Calendar day;
    private TextView topLabel;
    private TextView bottomLabel;

    public TopBarFragment() {
        // Required empty public constructor
    }

    public void setDay(Calendar day) {
        this.day = day;
        topLabel.setText(getDate());
        bottomLabel.setText(getDate());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topLabel = getView().findViewById(R.id.topLabel);
        bottomLabel = getView().findViewById(R.id.bottomLabel);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.top_bar,
               container,false);
       return rootView;
    }
    // TODO: To define the size of text and overall position metrics, color etc
    // Implememntation of support methods:
    private String getDay() {
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        return format.format(this.day.getTime());
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("MMMM dd, yyyy");
        return  format.format(this.day.getTime());
    }


}
