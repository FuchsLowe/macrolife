package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

/**
 * Created by macbook on 3/20/18.
 */

public class AlternativeChronoView extends View {

    Paint black;

    public AlternativeChronoView(Context context) {
        super(context);
        Log.e("AlternativeChrono", " Public constructor called");
        setWillNotDraw(false);
        black = new Paint(Paint.ANTI_ALIAS_FLAG);
        black.setStrokeWidth(4f);
        black.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("AlternativeChrono", " On Draw called");
        canvas.drawRect(10,10,30,40, black);

    }
}
