package com.fuchsundlowe.macrolife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuchsundlowe.macrolife.TestCases.TestActivity;


public class MasterScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);

    }

    public void toDay(View view) {
        Intent toDay = new Intent(this, DayView.class);
        startActivity(toDay);
    }

    public void toList(View view) {
        Intent toList = new Intent(this, ListView.class);
        startActivity(toList);
    }

    public void toTest(View view) {
        Intent toTest = new Intent(this, TestActivity.class);
        startActivity(toTest);
    }



}
