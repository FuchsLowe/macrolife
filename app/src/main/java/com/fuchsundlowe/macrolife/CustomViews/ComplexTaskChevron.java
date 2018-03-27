package com.fuchsundlowe.macrolife.CustomViews;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
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

    private Paint textMarker;

    public ComplexTaskChevron(Context context, SubGoalMaster data, ComplexTaskInterface protocol) {
        super(context);
        this.data = data;
        this.protocol = protocol;
        this.context = context;



        textMarker = new Paint();
        textMarker.setColor(Color.BLACK);
        textMarker.setTextSize(dpToPixConverter(DEFAULT_TEXT));

    }



    @Override
    protected void onDraw(Canvas canvas) {
        float scaleFactor = protocol.getScale(); // Could potentially slow things down.

        // Text Drawing:
        textMarker.setTextScaleX(scaleFactor);
        float textSize = textMarker.measureText(data.getTaskName());

        // Tries to put text in the middle, doesn't yet account for text out of bounds...
        canvas.drawText(data.getTaskName(), (getWidth() - textSize) / 2,
                (getHeight() + dpToPixConverter(DEFAULT_TEXT) - 20) / 2, textMarker);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       float scaleFactor = protocol.getScale();

        setMeasuredDimension(dpToPixConverter(DEFAULT_H * scaleFactor),
                dpToPixConverter(DEFAULT_W * scaleFactor));
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

    // Animation Calls:

    public void animationDestroy() {

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
        this.animate().x(data.getMX()).y(data.getMY()).z(8f).setDuration(200).start();
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
}


