package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;


public class TailView extends View {

    private TailViewProtocol mInterface;
    private View a,b;
    private int qL;

    public TailView(TailViewProtocol protocol, View a, View b) {
        super(protocol.getContext());
        mInterface = protocol;
        this.a = a;
        this.b = b;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /* You need to know in which quadrant you are, and location of middle of the Bubble and Master
         * Or I just need to know the 1/2 width & Height of the mBubble
         * WHat would be the difference between drawing on command and drawing on static lines?
         * If I pass onDrawCanvas, will it use it by defualt or do I need to constrcut two separate
         * logics, one for layot and one for drawing?
         */

    }
    /*
     * Will update layout manually for location on screen in dependence to the sub and masterView
     *
     */
    public void updateLayout() {

    }

    /*
     * Quadrant location is as following:
     * 0 is right, 1 top, 2 left, 3 bottom, 4 center
     */
    public void setQuadrant(int qL) {
        this.qL = qL;
    }

}
