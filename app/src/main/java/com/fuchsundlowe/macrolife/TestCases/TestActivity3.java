package com.fuchsundlowe.macrolife.TestCases;


import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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

    LinearLayout lay;
    ImageButton ia, ib, ic;
    Space s1,s2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        lay = findViewById(R.id.uArea);

        ia = new ImageButton(this);
        ia.setLayoutParams(new LinearLayout.LayoutParams(40,40));
        ia.setImageResource(R.drawable.repeat_24px);

        ib = new ImageButton(this);
        ib.setLayoutParams(new LinearLayout.LayoutParams(40,40));
        ib.setImageResource(R.drawable.check_circle_24px);

        ic = new ImageButton(this);
        ic.setLayoutParams(new LinearLayout.LayoutParams(40,40));
        ic.setImageResource(R.drawable.list_alt_24px);

        s1 = new Space(this);
        s2 = new Space(this);
        int p = 80;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(p,p);
        s1.setLayoutParams(lp);
        s2.setLayoutParams(lp);

        lay.addView(ia);
        lay.addView(s1);
        lay.addView(ib);
        lay.addView(s2);
        lay.addView(ic);
    }

    int padding = 0;
    int multiplier = 10;
    public void onCLick(View view) {
        int p = padding * multiplier;
        padding +=1;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(p,p);
        s1.setLayoutParams(lp);
        s2.setLayoutParams(lp);
        lay.requestLayout();
    }

}
