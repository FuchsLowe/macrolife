package com.fuchsundlowe.macrolife.TestCases;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.CustomViews.DayViewBackLayout;
import com.fuchsundlowe.macrolife.R;

public class Test4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);
        ViewGroup holder = findViewById(R.id.parent);
    }
}
