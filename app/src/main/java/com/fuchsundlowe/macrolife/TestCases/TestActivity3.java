package com.fuchsundlowe.macrolife.TestCases;


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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fuchsundlowe.macrolife.CustomViews.Task_DayView;
import com.fuchsundlowe.macrolife.R;
import com.fuchsundlowe.macrolife.SupportClasses.ColorFilterGenerator;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity3 extends AppCompatActivity {

    View a, b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        a = findViewById(R.id.id_a);
        a.setBackgroundColor(Color.GRAY);
        b = findViewById(R.id.id_b);
        b.setBackgroundColor(Color.TRANSPARENT);
        defineTimerTest();
    }

    void defineTimerTest() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(timerTask,0, 1000);
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.d("Timer Fired", "TIME: " + Calendar.getInstance().getTimeInMillis());
        }
    };


    public void sklopka(View view) {
        a.getBackground().setColorFilter(ColorFilterGenerator.adjustHue(110));
        a.requestLayout();
    }

}
