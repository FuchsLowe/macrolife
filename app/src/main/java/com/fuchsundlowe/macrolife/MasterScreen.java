package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.ComplexGoal.ComplexTaskActivity;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.DayView.DayView;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.TestCases.Test4;
import com.fuchsundlowe.macrolife.TestCases.TestActivity3;
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
        toListView(null);
        defineDisplay();
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

    public void toListView(View view) {
        Intent list = new Intent(this, com.fuchsundlowe.macrolife.ListView.ListView.class);
        startActivity(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dataBaseMaster.isDataBaseOpen()) {
            dataBaseMaster.closeDataBase();
        }
    }
    public void create_rep_event(View view) {
        RepeatingEvent event = new RepeatingEvent(
                dataBaseMaster.findNextFreeHashIDForTask() -1, null, null, DayOfWeek.thursday, dataBaseMaster.findNextFreeHashIDForEvent(), Calendar.getInstance()
        );
        dataBaseMaster.saveRepeatingEvent(event);

        Handler h = new Handler(getMainLooper());
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                defineDisplay();
            }
        }, 750);
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

        dataBaseMaster.getAllEvents().observe(this, new Observer<List<RepeatingEvent>>() {
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
            print("EVENT: " + ev.getHashID() + " ID: "+ ev.getParentID() + " " + ev.getDayOfWeek());
        }
    }

    public void remove_events(View view) {
        dataBaseMaster.deleteAllRepeatingEvents(dataBaseMaster.findNextFreeHashIDForTask() -1, TaskObject.Mods.repeatingMultiValues);
    }

    void print(String val) {
        display.append(val + "\n");
    }

}
