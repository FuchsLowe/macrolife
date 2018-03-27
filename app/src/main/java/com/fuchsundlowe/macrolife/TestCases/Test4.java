package com.fuchsundlowe.macrolife.TestCases;

import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.EditText;
import android.widget.SeekBar;

import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.CustomViews.DayViewBackLayout;
import com.fuchsundlowe.macrolife.R;

public class Test4 extends AppCompatActivity {


    ViewGroup parent;
    EditText console;
    SeekBar barA;
    SeekBar barb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);
        parent = findViewById(R.id.parent);

        console = findViewById(R.id.consoleText);

        barA = findViewById(R.id.seekBarA);
        barb = findViewById(R.id.seekBarB);

        barA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        barb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void onCLick(View view) {

    }

    private void writeToConsole(String text) {
        console.append("\n" +  text);
    }
}
