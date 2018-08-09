package com.fuchsundlowe.macrolife.WeekView;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.R;

public class WeekView extends AppCompatActivity {

    private ViewPager centralBar;
    private FrameLayout bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        centralBar = findViewById(R.id.centralBar_weekView);
        bottomBar = findViewById(R.id.bottomBar_weekView);

    }

    // Todo: Create the PageAdapter class for centralBar
}
