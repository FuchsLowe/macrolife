package com.fuchsundlowe.macrolife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Scroller;

import com.fuchsundlowe.macrolife.SupportClasses.HScroll;
import com.fuchsundlowe.macrolife.TestCases.Test4;
import com.fuchsundlowe.macrolife.TestCases.TestActivity;
import com.fuchsundlowe.macrolife.TestCases.TestActivity2;
import com.fuchsundlowe.macrolife.TestCases.TestActivity3;
import com.fuchsundlowe.macrolife.TestCases.TestActivity4;


public class MasterScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
        //toDay(null);
        test2();

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

    public void test2() {
        Intent toTest2 = new Intent(this, ComplexTaskActivity.class);
        startActivity(toTest2);
    }

    void test3() {
        Intent toTest3 = new Intent(this, TestActivity4.class);
        startActivity(toTest3);
    }


}
