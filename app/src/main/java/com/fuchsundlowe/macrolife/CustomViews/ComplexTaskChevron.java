package com.fuchsundlowe.macrolife.CustomViews;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;


public class ComplexTaskChevron extends View {

    private SubGoalMaster data;
    private ComplexTaskInterface protocol;
    private Context context;

    private int DEFAULT_H = 240;
    private int DEFAULT_W = 90;
    private int DEFAULT_TEXT = 36;
    private int DEFAULT_PADDING = 8;
    private int DEFAULT_Z = 16;

    private Paint textMarker;

    public ComplexTaskChevron(Context context, SubGoalMaster data, ComplexTaskInterface protocol) {
        super(context);
        this.data = data;
        this.protocol = protocol;
        this.context = context;

        textMarker = new Paint();
        textMarker.setColor(Color.BLACK);
        DEFAULT_TEXT = dpToPixConverter(DEFAULT_TEXT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float scale = protocol.getScale();
        float textSize = textMarker.measureText(data.getTaskName());
        textMarker.setTextSize(DEFAULT_TEXT * scale);

        // Tries to put text in the middle, doesn't yet account for text out of bounds...
        canvas.drawText(data.getTaskName(), (getWidth() - textSize) / 2,
                (getHeight() + (DEFAULT_TEXT * scale) ) / 2, textMarker);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

       float minX = 0;
       float minY = 0;

       float scale = protocol.getScale();

       minX = dpToPixConverter(DEFAULT_W * scale);
       minY = dpToPixConverter(DEFAULT_H * scale);

       setMinimumHeight((int)minY + 1);
       setMinimumWidth((int) minX + 1);


        setMeasuredDimension((int) (minX + dpToPixConverter(DEFAULT_PADDING) * scale),
                (int) (minY + dpToPixConverter(DEFAULT_PADDING) * scale));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setOutlineProvider(new CustomOutline(w, h));
    }

    // Utility method calls:
    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }

    public int getDataID() {
        return data.getHashID();
    }

    public int getXFromData() {
        return data.getMX();
    }

    public int getYFromData() {
        return data.getMY();
    }

    public String getTaskName() { return data.getTaskName();}

    public void setNewValues(String newName, Integer newX, Integer newY,
                             Integer parentSubGoal, Boolean completed) {
        if (newName != null) { data.setTaskName(newName);}
        if (newX != null) { data.setMX(newX);}
        if (newY != null) {data.setMY(newY);}
        if (parentSubGoal != null) {data.setParentSubGoal(parentSubGoal); }
        if (completed != null) {data.setTaskCompleted(completed);}

        this.requestLayout();

    }

    // Animation Calls:

    public void animationDestroy() {
        data.deleteMe();
        this.animate().alpha(0f).setDuration(200).start();
        this.setOnTouchListener(null);
    }

    public void animationPresentSelf() {
        /* An old way of doing things
        ObjectAnimator forX = ObjectAnimator.ofFloat(this, "x", data.getMX());
        ObjectAnimator forY = ObjectAnimator.ofFloat(this, "y", data.getMY());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(forX, forY);
        set.start();
        */

        // Simple animation of things:
        this.animate().x(data.getMX()).y(data.getMY()).translationZ(DEFAULT_Z).setDuration(200).start();
    }

    // Touch Events management:

    public void updateNewCoordinates() {
        this.data.setMX((int)getX());
        this.data.setMY((int) getY());
        this.data.updateMe();
    }

    void l(int val) {
        Log.i("Click Event", " " + val);
    }

    // Outline Provider:

    private class CustomOutline extends ViewOutlineProvider {

        int width;
        int height;

        CustomOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            int cPadding = dpToPixConverter(DEFAULT_PADDING);
            outline.setRect(0 + cPadding, cPadding, width - cPadding, height - cPadding);
        }
    }

    private class MyDrawable extends Drawable {

        Paint backColor;
        Paint edgeMarker;

        public MyDrawable() {
            backColor = new Paint();
            backColor.setColor(Color.WHITE);

            edgeMarker = new Paint();
            edgeMarker.setColor(Color.BLACK);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            setBackgroundColor(Color.WHITE);
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

}


