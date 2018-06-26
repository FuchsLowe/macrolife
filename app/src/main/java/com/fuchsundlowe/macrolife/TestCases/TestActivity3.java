package com.fuchsundlowe.macrolife.TestCases;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.BottomBar.EditingView_BottomBar;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

public class TestActivity3 extends AppCompatActivity implements EditTaskProtocol {

    FrameLayout lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        lay = findViewById(R.id.base);
    }

    public void onCLick(View view) {
        EditingView_BottomBar bar = new EditingView_BottomBar(this);
        bar.setBackgroundColor(Color.RED);
        lay.addView(bar);

        Calendar now = Calendar.getInstance();
        TaskObject task = new TaskObject(0,0,0,"Subotai",
                now, null, null, now, TaskObject.CheckableStatus.incomplete,
                "", 0, 0, "", TaskObject.TimeDefined.dateAndTime);
        bar.insertData(task, null, this);
        ViewGroup.LayoutParams params = bar.getLayoutParams();
        params.height = 120;
        bar.requestLayout();
    }

    // EDit Task Protocol
    @Override
    public void saveTask(TaskObject task, @Nullable RepeatingEvent event) {

    }
    @Override
    public void clickOnMod(TaskObject.Mods mod) {

    }
    @Override
    public void modDone() {

    }
    @Override
    public View getBaseView() {
        return null;
    }
}
