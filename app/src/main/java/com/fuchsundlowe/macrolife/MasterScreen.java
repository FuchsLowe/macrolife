package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.ComplexGoal.ComplexTaskActivity;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.DayView.DayView;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.MonthView.MonthView;
import com.fuchsundlowe.macrolife.TestCases.Test4;
import com.fuchsundlowe.macrolife.TestCases.TestActivity3;
import com.fuchsundlowe.macrolife.TestCases.WholeDB;
import com.fuchsundlowe.macrolife.WeekView.WeekView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MasterScreen extends AppCompatActivity {

    private DataProviderNewProtocol dataBaseMaster;
    TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseMaster = LocalStorage.getInstance(this);
        setContentView(R.layout.activity_master_screen);
        display = findViewById(R.id.textView);
        display.setBackgroundColor(Color.BLACK);
        display.setTextColor(Color.GREEN);
        display.setText("");
        //test3();
        //toDay(null);
        //toWeekView();
        //toListView(null);
        //defineDisplay();
        //testFunction();
        toMonthView();

    }

    void testFunction() {
        LocalStorage db = (LocalStorage) dataBaseMaster;
        db.dataBase.newDAO().getAllTaskObjects().observe(this, new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> objects) {
                Log.d("TaskCount:", objects.size()+ " of tasks" );
            }
        });

        db.dataBase.newDAO().getAllRepeatingEvents().observe(this, new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> events) {
                Log.d("EventCount:", events.size()+ " of events");
                for (RepeatingEvent event: events) {
                    Calendar current = Calendar.getInstance();
                    if (event.getStartTime().get(Calendar.DAY_OF_YEAR) ==  current.get(Calendar.DAY_OF_YEAR)) {
                        Log.d("Event:", "Has the same day as today");
                    } else {
                        Log.d("Day:",event.getStartTime().get(Calendar.DAY_OF_YEAR) + " day");
                    }
                }
            }
        });
    }

    public void toDay(View view) {
        Intent toDay = new Intent(this, DayView.class);
        startActivity(toDay);
    }
    public void toWeek(View view) {
        Intent toList = new Intent(this, WeekView.class);
        startActivity(toList);
    }
    public void toTest(View view) {
        Intent toTest = new Intent(this, Test4.class);
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
    void toWeekView() {
        Intent week = new Intent(this, WeekView.class);
        startActivity(week);
    }
    public void toWholeDB(View view) {

        Intent wDB = new Intent(this, WholeDB.class);
        startActivity(wDB);
    }

    public void toListView(View view) {
        Intent list = new Intent(this, com.fuchsundlowe.macrolife.ListView.ListView.class);
        startActivity(list);
    }

    public void toMonthView() {
        Intent month = new Intent(this, MonthView.class);
        startActivity(month);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBaseMaster.isDataBaseOpen()) {
            dataBaseMaster.closeDataBase();
        }
    }


    void defineDisplay() {
        o = new ArrayList<>();
        e = new ArrayList<>();
        if (dataBaseMaster instanceof LocalStorage) {
            ((LocalStorage) dataBaseMaster).taskObjectHolder.observe(this, new Observer<List<TaskObject>>() {
                @Override
                public void onChanged(@Nullable List<TaskObject> objects) {
                    valueMaster(objects, null);
                }
            });
        }

        dataBaseMaster.getAllRepeatingEvents().observe(this, new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> repeatingEvents) {
                valueMaster(null, repeatingEvents);
            }
        });
    }

    List<TaskObject> o;
    List<RepeatingEvent> e;
    public void valueMaster(List<TaskObject> objects, List<RepeatingEvent> events) {
        if (objects != null) {
            o = objects;
        }
        if (events != null) {
            e = events ;
        }
        display.setText("");
        for (TaskObject obj : o) {
            print("TASK: " + obj.getTaskName() +" ID: " + obj.getHashID()+ " mods:" + obj.getAllMods());
        }
        for (RepeatingEvent ev : e) {
            print("EVENT: " + ev.getHashID() + " ID: "+ ev.getParentID());
        }
    }

    public void remove_events(View view) {
    }

    void print(String val) {
        display.append(val + "\n");
    }



}
