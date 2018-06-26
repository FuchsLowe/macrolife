package com.fuchsundlowe.macrolife;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fuchsundlowe.macrolife.ComplexGoal.ComplexTaskActivity;
import com.fuchsundlowe.macrolife.DayView.DayView;
import com.fuchsundlowe.macrolife.DepreciatedClasses.ListView;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.TestCases.TestActivity;
import com.fuchsundlowe.macrolife.TestCases.TestActivity3;


public class MasterScreen extends AppCompatActivity {

    private DataProviderNewProtocol dataBaseMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseMaster = LocalStorage.getInstance(this);
        setContentView(R.layout.activity_master_screen);
        //test2();
        //test3();
        toDay(null);
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
        Intent toTest3 = new Intent(this, TestActivity3.class);
        startActivity(toTest3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBaseMaster.isDataBaseOpen()) {
            dataBaseMaster.closeDataBase();
        }
    }
}
