package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;


public class InfinitePaper extends ViewGroup {

    private Context mContext;
    private ComplexTaskInterface mInterface;
    private int MIN_SIZE_X = 1200;
    private int MIN_SIZE_Y = 800;
    private int MIN_PADDING = 20;
    private boolean firstAppearance = true;


    public InfinitePaper(@NonNull Context context, ComplexTaskInterface mInterface) {
        super(context);
        mContext = context;
        this.mInterface = mInterface;
        recalculateCanvasSize();
        setWillNotDraw(false);
        this.setBackgroundColor(Color.GRAY);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    // Should set minWidth & height for Children size
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float scale = mInterface.getScale();

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        Point forMin = getMinSize();
        setMinimumHeight((int) (forMin.y * scale));
        setMinimumWidth((int) (forMin.x * scale));

        float regX, regY;
        regX = Math.max(MIN_SIZE_X, forMin.x + dpToPixConverter(MIN_PADDING)) * scale;
        regY =  Math.max(MIN_SIZE_Y, forMin.y + dpToPixConverter(MIN_PADDING)) * scale;

        setMeasuredDimension(
                (int)regX,
                (int) regY
        );

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        ComplexTaskChevron kid;

        if (firstAppearance) { // We bring them on screen for the first time
            firstAppearance = false;

            for (int i = 0; i< getChildCount(); i++) {
                kid = (ComplexTaskChevron) getChildAt(i);

                kid.layout(20,50, kid.getWidth() + 20, kid.getHeight() + 50);

            }

        } else { // They just need resizing or something
            for (int i =0; i< getChildCount(); i++) {
                kid = (ComplexTaskChevron) getChildAt(i);

                kid.layout(kid.getXFromData(), kid.getYFromData(),
                        kid.getXFromData() + kid.getMeasuredHeight(),
                        kid.getYFromData() + kid.getMeasuredWidth());
            }
        }

    }

    // returns minimum size so that children are always included in the view
    private Point getMinSize() {
        Point temp = new Point();
        float maxX = 0;
        float maxY = 0;

        for (int i = 0; i< this.getChildCount(); i++) {
            View v = this.getChildAt(i);
            maxX = Math.max(maxX, v.getWidth() + v.getX());
            maxY = Math.max(maxY, v.getHeight() + v.getY());
        }

        temp.set((int) maxX +1, (int) maxY +1);

        return temp;
    }


    private int dpToPixConverter(float dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }

    private void recalculateCanvasSize() {
        MIN_SIZE_X = dpToPixConverter(MIN_SIZE_X);
        MIN_SIZE_Y = dpToPixConverter(MIN_SIZE_Y);
    }
}
