package com.fuchsundlowe.macrolife.TestCases;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

/**
 * Created by macbook on 4/19/18.
 */

public class TestDot extends View {

    public int pointSize = 20;
    public TestDot(Context context) {
        super(context);
        setBackgroundColor(Color.BLACK);
    }

    public void setLocation(int x, int y) {
        layout(x,y,x+pointSize,y+pointSize);
    }


}
