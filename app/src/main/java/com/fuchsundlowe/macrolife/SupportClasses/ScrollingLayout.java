package com.fuchsundlowe.macrolife.SupportClasses;


import android.content.Context;

import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ScrollingLayout extends RelativeLayout {

    private float mPosX = 0, mPosY = 0;

    public ScrollingLayout(Context context) {
        super(context);
    }

    public ScrollingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ScrollingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:

            {
                mPosX = getX() - ev.getRawX();
                mPosY = getY() - ev.getRawY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Find the index of the active pointer and fetch its position
                // Calculate the distance moved
                setX(ev.getRawX() + mPosX);
                setY(ev.getRawY() + mPosY);
                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                break;
            }
        }
        return true;
    }
}