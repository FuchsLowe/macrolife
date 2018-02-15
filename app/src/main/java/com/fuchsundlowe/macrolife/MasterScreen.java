package com.fuchsundlowe.macrolife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fuchsundlowe.macrolife.FragmentModels.DayViewFragment;


public class MasterScreen extends AppCompatActivity {



    DayViewFragment day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        // This is the earlyest stage of app lifecycle so I should create
        // my database insatnce here

        // Adding the fragment for testing now:
        day =  new DayViewFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.masterContainer,
                day).commit();



    }


}
