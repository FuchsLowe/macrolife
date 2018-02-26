package com.fuchsundlowe.macrolife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fuchsundlowe.macrolife.FragmentModels.DayViewFragment;
import com.fuchsundlowe.macrolife.FragmentModels.SimpleListFragment;


public class MasterScreen extends AppCompatActivity {



    DayViewFragment day;
    SimpleListFragment list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        // This is the earlyest stage of app lifecycle so I should create
        // my database insatnce here

        // Adding the fragment for testing now:
        //day =  new DayViewFragment();
        list = new SimpleListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.masterContainer,
                list).commit();



    }


}
