package com.fuchsundlowe.macrolife.TestCases;


import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Space;

import com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar;
import com.fuchsundlowe.macrolife.BottomBar.EditingView_BottomBar;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

public class TestActivity3 extends AppCompatActivity  {

    FrameLayout a;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        a= findViewById(R.id.frameLayout_a);

        a.setBackgroundColor(Color.BLUE);

    }

    @Override
    protected void onResume() {
        super.onResume();

        View alpha, beta, gamma;
        alpha = new View(this);
        alpha.setBackgroundColor(Color.GREEN);
        a.addView(alpha);

        beta = new View(this);
        beta.setBackgroundColor(Color.DKGRAY);
        a.addView(beta);

        alpha.setLayoutParams(new FrameLayout.LayoutParams(40, 40));
        beta.setLayoutParams(new FrameLayout.LayoutParams(60, 40));

        alpha.setX(25);
        beta.setX(75);

        beta.animate().translationX(300).setDuration(2700).start();

    }
}
