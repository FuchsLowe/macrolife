package com.fuchsundlowe.macrolife;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fuchsundlowe.macrolife.DataObjects.DAO;
import com.fuchsundlowe.macrolife.DataObjects.DataProvider;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;

import java.util.Calendar;
import java.util.List;

public class MasterScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        // This is the earlyest stage of app lifecycle so I should create
        // my database insatnce here
    }

}
