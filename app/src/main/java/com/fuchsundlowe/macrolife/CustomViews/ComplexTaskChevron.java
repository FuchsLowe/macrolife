package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Created by macbook on 3/20/18.
 */

public class ComplexTaskChevron extends Drawable {

    Paint testColorBlack;


    public ComplexTaskChevron() {
        testColorBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        testColorBlack.setColor(Color.BLACK);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int mX = getBounds().width() - 20;
        int mY = getBounds().height() - 20;

        canvas.drawRect(20,20,mX,mY,testColorBlack);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}


