package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import com.fuchsundlowe.macrolife.R;


public class TestActivity4 extends AppCompatActivity {

    ScrollView scv;
    RelativeLayout re;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test5);

        scv = findViewById(R.id.scv);
        re = findViewById(R.id.rel);

        re.setBackgroundColor(Color.BLACK);
    }

    public void a(View view) {
        buttonMania();
    }

    public void b(View view) {
        moveLittleNuggets();
    }

    int count = 1;
    public void c(View view) {
        re.setMinimumHeight(1000 * count);
        re.setMinimumWidth(800 * count);
        count+= 1;
        invalidateRe();
        invalidateScroll();
    }
    //======================//

    void buttonMania() {
        int count = 0;
        int countR = 0;
        int w = 250;
        int h = 120;

        for (int i=0; i<6; i++) {
            Button clyde = new Button(this);
            clyde.layout(w * count, h * countR, w * count + w, h * countR + h);
            re.addView(clyde);
            count += 1;
            countR += 1;
        }
    }

    void invalidateScroll() {
        scv.requestLayout();
    }

    void invalidateRe() {
        re.requestLayout();
    }

    void moveLittleNuggets() {
        float x = 1f;
        float y = 1f;
        for (int i = 0; i < re.getChildCount() ; i++) {
            View a = re.getChildAt(i);
            a.animate().x(a.getX() * x + 1).y(a.getY() * y + 1).setDuration(200).start();
            x += 1f;
            y += 0.5f;
        }
    }



}
