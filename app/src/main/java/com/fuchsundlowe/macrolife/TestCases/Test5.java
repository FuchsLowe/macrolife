package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.CustomViews.BubbleView;
import com.fuchsundlowe.macrolife.R;

public class Test5 extends AppCompatActivity {

    View b;
    ViewGroup c;
    float oldX,oldY, cX, cY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test6);
        b = new View(this);
        b.setBackgroundColor(Color.RED);
        c = findViewById(R.id.twenty_one);
        c.addView(b, 40, 88);
        b.setTranslationY(20);
        b.setTranslationX(40);

    }

    public void clickOne(View view) {
        b.setTranslationY(500);
        b.setTranslationX(200);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cX = event.getX();
        cY = event.getY();

        float x, y;
        x = cX - oldX;
        y = cY - oldY;

        ViewGroup.LayoutParams stub = b.getLayoutParams();

        stub.width += x;
        stub.height += y;

        b.setLayoutParams(stub);

        oldX = cX;
        oldY = cY;

        return true;
    }
}
