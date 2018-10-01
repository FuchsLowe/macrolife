package com.fuchsundlowe.macrolife.TestCases;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.BottomBar.RepeatEventSystem.RepeatingEventEditor;
import com.fuchsundlowe.macrolife.R;

public class TestActivity3 extends AppCompatActivity  {

    FrameLayout a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        a = findViewById(R.id.octy);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RepeatingEventEditor ed = new RepeatingEventEditor(this);
        a.addView(ed);
    }
}
