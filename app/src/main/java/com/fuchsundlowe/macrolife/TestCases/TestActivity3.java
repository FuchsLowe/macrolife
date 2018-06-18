package com.fuchsundlowe.macrolife.TestCases;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fuchsundlowe.macrolife.CustomViews.EditingView_BottomBar;
import com.fuchsundlowe.macrolife.CustomViews.NotePad;
import com.fuchsundlowe.macrolife.CustomViews.Task_DayView;
import com.fuchsundlowe.macrolife.R;
import com.fuchsundlowe.macrolife.SupportClasses.ColorFilterGenerator;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity3 extends AppCompatActivity {

    FrameLayout lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        lay = findViewById(R.id.masterFrame);
        lay.addView(new NotePad(this));

    }



}
