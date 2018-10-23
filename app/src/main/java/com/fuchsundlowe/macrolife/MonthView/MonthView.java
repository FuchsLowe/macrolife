package com.fuchsundlowe.macrolife.MonthView;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.R;

public class MonthView extends AppCompatActivity {

    private MonthDataControllerProtocol model;
    private ViewPager topBar, centralBar;
    private ViewGroup bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_view);

        model = new MonthViewModel(this, this);

        topBar = findViewById(R.id.topBar_MonthView);
        centralBar = findViewById(R.id.centerBar_listView);
        bottomBar = findViewById(R.id.bottomBar_MonthView);


    }

    // View Pager Listeners

    // View Pager Transformers for Sliding

}
