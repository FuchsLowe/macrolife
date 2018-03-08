package com.fuchsundlowe.macrolife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;


public class MasterScreen extends AppCompatActivity {



    private DayView day;
    private ListView list;
    private DataProviderProtocol dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        initiateDatabase();
        // This is the earlyest stage of app lifecycle so I should create
        // my database insatnce here


        //toDay();
        toList();
    }
    private void initiateDatabase() {
        dataBase = StorageMaster.getInstance(this);
    }

    private void toDay() {
        Intent day = new Intent(this, DayView.class);

        startActivity(day);
    }

    private void toList() {
        Intent list = new Intent(this, ListView.class);
        startActivity(list);
    }

}
