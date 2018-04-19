package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;
import com.fuchsundlowe.macrolife.TestCases.TestDot;


public class TailView extends View {

    private TailViewProtocol mInterface;
    private View a,b;
    private RectF tRect;
    private Point tC, topHorizon, bottomHorizon, aCenterInCanvas, bCenterInCanvas;
    private int tQ = 0;
    private Paint line;
    TestDot dOne,dTwo, dThree, dFour, tDot;

    public TailView(TailViewProtocol protocol, View a, View b) {
        super(protocol.getContext());
        mInterface = protocol;
        this.a = a;
        this.b = b;
        line = new Paint();
        line.setColor(Color.BLACK);
        line.setStrokeWidth(5);
        tRect = new RectF();
        tC = new Point();
        topHorizon = new Point();
        bottomHorizon = new Point();
        aCenterInCanvas = new Point();
        bCenterInCanvas = new Point();
        setBackgroundColor(Color.GREEN);
        dOne = new TestDot(protocol.getContext());
        dTwo = new TestDot(protocol.getContext());
        dThree = new TestDot(protocol.getContext());
        dFour = new TestDot(protocol.getContext());
        tDot = new TestDot(protocol.getContext());

        /*  part of test, shows dots, can be deleted when done testing
        mInterface.getContainer().addView(dOne);
        mInterface.getContainer().addView(dTwo);
        mInterface.getContainer().addView(dThree);
        mInterface.getContainer().addView(dFour);
        mInterface.getContainer().addView(tDot);
        */

        tDot.pointSize = 35;
        tDot.setBackgroundColor(Color.RED);
        dThree.setBackgroundColor(Color.RED);
        dFour.setBackgroundColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*
         * Based on Quadrant I determine the aConnection Point and bConnectionPoint
         * For start Test I will just draw a line...
         * I need to translate locations of a and b from parent to local canvas.
         */
        calculateConnectionPoints();
        canvas.drawCircle(aCenterInCanvas.x,aCenterInCanvas.y, 50, line);
        canvas.drawCircle(bCenterInCanvas.x,bCenterInCanvas.y, 50, line);

    }
    /*
     * Will update layout manually for location on screen in dependence to the sub and masterView
     */
    public void updateLayout2() {
        switch (provideQuadrant()) {
            case 0: // Is OVERLAYING the a
                tRect.left = 0;
                tRect.top = 0;
                tRect.right = 0;
                tRect.bottom = 0;
                break;
            case 1: // is LEFT of a
                tRect.left = b.getX() + b.getWidth();
                tRect.top = Math.min(a.getY(), b.getY());
                tRect.right = a.getX();
                tRect.bottom = Math.max(a.getY() + a.getHeight(), b.getY() + b.getHeight());
                break;
            case 2: // is ABOVE a
                tRect.left = Math.min(b.getX(), a.getX());
                tRect.top = b.getY() + b.getHeight();
                tRect.right = Math.max(b.getX() + b.getWidth(), a.getX() + a.getWidth());
                tRect.bottom = a.getY();
                break;
            case 3: // is RIGHT of a
                tRect.left = a.getX() + a.getWidth();
                tRect.top = Math.min(a.getY(), b.getY());
                tRect.right = b.getX();
                tRect.bottom = Math.max(a.getY() + a.getHeight(), b.getY() + b.getHeight());
                break;
            case 4: // is UNDER a
                tRect.left = Math.min(a.getX(), b.getX());
                tRect.top = a.getY() + a.getHeight();
                tRect.right = Math.max(a.getX() + a.getWidth(), b.getX() + b.getWidth());
                tRect.bottom = b.getY();
                break;
        }
        layout(
                (int) tRect.left,
                (int) tRect.top,
                (int) tRect.right,
                (int) tRect.bottom
        );
        invalidate(); // Makes it redraw the lines
    }

    /*
     * returns the center point for the b-View
     */
    private Point bCenter() {
        tC.set( (int)(b.getWidth()/2 + b.getX()),
                (int)(b.getHeight()/2 + b.getY()) );
        tDot.setLocation(tC.x, tC.y);
        return tC;
    }

    /*
     * Returns the quadrant in which is b-View, in relation to a-View
     * Quadrant location is as following:
     * 1 is right, 2 top, 3 left, 4 bottom, 5 center
     */
    private int provideQuadrant() {
        bCenter();
        /*
         * Top:
         * Get hold of tC.y
         * Calculate topHorizon for tC.y
         * if tC.x between topHorizon, else we have right or left.
         * Bottom:
         * same procedure
         * same logic, you can detect ends
         */
        topHorizon.set(
                (int)(a.getX() - (a.getY() - tC.y)),
                (int)(a.getX() + a.getWidth() + a.getY() - tC.y)
        );
        bottomHorizon.set(
                (int)(a.getX() - (tC.y - a.getY() - a.getHeight())),
                (int)(a.getX() + a.getWidth() + (tC.y - a.getY() - a.getHeight()))
        );

        dOne.setLocation(topHorizon.x, (int) b.getY());
        dTwo.setLocation(topHorizon.y, (int) b.getY());
        dThree.setLocation(bottomHorizon.x, (int) b.getY());
        dFour.setLocation(bottomHorizon.y, (int) b.getY());

        tQ = -1; // Control Number for testing

        if (tC.y < a.getY()) {
            // means its above the topLine
            mInterface.displayText(1);
            if (tC.x < topHorizon.x) {
                // Its quadrant 1
                tQ = 1;
            } else if (tC.x > topHorizon.y) {
                // Its quadrant 3
                tQ = 3;
            } else {
                // Its in quadrant 2
                tQ = 2;
            }
        } else if (tC.y > (a.getY() + a.getHeight())) {
            // Means its under bottomLine
            mInterface.displayText(2);
            if (tC.x < bottomHorizon.x) {
                // Its quadrant 1
                tQ = 1;
            } else if (tC.x > bottomHorizon.y) {
                // Its quadrant 3
                tQ = 3;
            } else {
                // Its quadrant 4
                tQ = 4;
            }
        } else {
            // its between the topLine and bottomLine
            mInterface.displayText(3);
            if (tC.x < a.getX()) {
                // Its quadrant 1
                tQ = 1;
            } else if (tC.x > (a.getX() + a.getWidth())) {
                // Its quadrant 3
                tQ = 3;
            } else {
                // It must be over a
                tQ = 0;
            }
        }
        //mInterface.displayText(tQ); // TEST - TO BE DELETED
        return tQ;
    }

    /*
     * Calculates coordinates in Canvas system for connector lines for
     * a view and b view. You access the points by calling aCenterInCanvas and
     * bCenterInCanvas respectively
     */
    private void calculateConnectionPoints() {
        switch (tQ) {
            case 1:
                aCenterInCanvas.set(
                        getWidth(),
                        (int) (a.getY() + a.getHeight()/2 - this.getY())
                );
                bCenterInCanvas.set(
                        0,
                        (int) (b.getY() + b.getHeight()/2 - this.getY())
                );
                break;
            case 2:
                aCenterInCanvas.set(
                        (int) (a.getX() + a.getWidth()/2 - this.getX()),
                        getHeight()
                );
                bCenterInCanvas.set(
                        (int)(b.getX() + b.getWidth()/2 - this.getX()),
                        0
                );
                break;
            case 3:
                aCenterInCanvas.set(
                        0,
                        (int)(a.getY() + a.getHeight()/2 - this.getY())
                );
                bCenterInCanvas.set(
                        getWidth(),
                        (int)(b.getY() + b.getHeight()/2 - this.getY())
                );
                break;
            case 4:
                aCenterInCanvas.set(
                        (int)(a.getX() + a.getWidth()/2 - this.getX()),
                        0
                );
                bCenterInCanvas.set(
                        (int)(b.getX() + b.getWidth()/2 - this.getX()),
                        getHeight()
                        );
                ;
                break;
                default: // Some error has occurred, or views are overlapping.
                    aCenterInCanvas.set(-1,-1);
                    bCenterInCanvas.set(-1,-1);
        }

    }

}
