package com.fuchsundlowe.macrolife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MasterScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        //toList();
        toTest();
    }


    private void toList() {

        Intent toList = new Intent(this, ListView.class);
        startActivity(toList);
    }

    private void toTest() {
        Intent toTest = new Intent(this, TestActivity.class);
        startActivity(toTest);
    }



}
