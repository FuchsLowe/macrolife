package com.fuchsundlowe.macrolife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fuchsundlowe.macrolife.DataObjects.DataProvider;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.FragmentModels.DayViewFragment;
import com.fuchsundlowe.macrolife.FragmentModels.SimpleListFragment;
import com.fuchsundlowe.macrolife.Interfaces.BaseViewInterface;


public class MasterScreen extends AppCompatActivity {



    private DayViewFragment day;
    private SimpleListFragment list;
    private BaseViewInterface dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        dataBase = StorageMaster.getInstance(getApplicationContext());
        // This is the earlyest stage of app lifecycle so I should create
        // my database insatnce here

        // Adding the fragment for testing now:
        //day =  new DayViewFragment();
        list = new SimpleListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.masterContainer,
                list).commit();

    }
    private void initiateDatabase() {

    }

}
