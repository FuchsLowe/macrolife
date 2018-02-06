package com.fuchsundlowe.macrolife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MasterScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        // This is the earlyest stage of app lifecycle so I should create
        // my database insatnce here
    }

}
