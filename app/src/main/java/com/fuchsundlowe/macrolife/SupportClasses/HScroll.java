package com.fuchsundlowe.macrolife.SupportClasses;

/**
 * Created by macbook on 3/19/18.
 */

import android.util.AttributeSet;
import android.view.MotionEvent;

        import android.content.Context;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.widget.HorizontalScrollView;

public class HScroll extends HorizontalScrollView {

    public HScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HScroll(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        //super.onSizeChanged(w, h, oldw, oldh);
    }
}

