package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;

public class TailView extends View {

    private TailViewProtocol mInterface;
    private View fromView, toView; // as in from this view the tail starts and to view the tail goes.
    private RectF tRect;
    private Point tC, topHorizon, bottomHorizon, aCenterInCanvas, bCenterInCanvas;
    private Paint line, connectionHeadPen;
    private Path mPath;
    private int quadrant = 0;
    private int lineWidth = 2;
    private int connectionHeadRadius = 10;
    private static int counter = 0;

    //TestDot dOne,dTwo, dThree, dFour, tDot;

    static void callCounter() {
        counter+=1;
        Log.e("Current count is:", String.valueOf(counter));
    }

    public TailView(TailViewProtocol protocol, View fromView, View toView)  {
        super(protocol.getContext());
        callCounter();
        mInterface = protocol;
        this.fromView = fromView;
        this.toView = toView;
        line = new Paint();
        line.setColor(Color.BLACK);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(dpToPixConverter(lineWidth));
        connectionHeadPen = new Paint();
        connectionHeadPen.setColor(Color.BLACK);
        connectionHeadPen.setStyle(Paint.Style.FILL);
        tRect = new RectF();
        tC = new Point();
        topHorizon = new Point();
        bottomHorizon = new Point();
        aCenterInCanvas = new Point();
        bCenterInCanvas = new Point();
        mPath = new Path();
        connectionHeadRadius = dpToPixConverter(connectionHeadRadius);
        setBackgroundColor(Color.TRANSPARENT);
        /*
        dOne = new TestDot(protocol.getContext());
        dTwo = new TestDot(protocol.getContext());
        dThree = new TestDot(protocol.getContext());
        dFour = new TestDot(protocol.getContext());[]
        tDot = new TestDot(protocol.getContext());

        part of test, shows dots, can be deleted when done testing
        mInterface.getContainer().addView(dOne);
        mInterface.getContainer().addView(dTwo);
        mInterface.getContainer().addView(dThree);
        mInterface.getContainer().addView(dFour);
        mInterface.getContainer().addView(tDot);


        tDot.pointSize = 35;
        tDot.setBackgroundColor(Color.RED);
        dThree.setBackgroundColor(Color.RED);
        dFour.setBackgroundColor(Color.GREEN);
        */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        calculateConnectionPoints(); // Defines the connection points.
        mPath.reset();
        mPath.moveTo(aCenterInCanvas.x, aCenterInCanvas.y);
        switch (quadrant){
            case 1:
            case 3:
                mPath.cubicTo(getWidth()/2, aCenterInCanvas.y,
                        getWidth()/2, bCenterInCanvas.y, bCenterInCanvas.x, bCenterInCanvas.y);
                break;
            case 2:
            case 4:
                mPath.cubicTo(aCenterInCanvas.x, getHeight()/2,
                        bCenterInCanvas.x, getHeight()/2,
                        bCenterInCanvas.x, bCenterInCanvas.y
                        );
                break;

        }

        canvas.drawPath(mPath, line);

        // Drawing of the direction indicator

        // Circle point implementation:
        canvas.drawCircle(bCenterInCanvas.x, bCenterInCanvas.y, connectionHeadRadius,
                connectionHeadPen);
    }
    /*
     * Will update layout manually for location on screen in dependence to the sub and masterView
     */
    public void updateLayout() {
        switch (provideQuadrant()) {
            case 0: // Is OVERLAYING the fromView
                tRect.left = 0;
                tRect.top = 0;
                tRect.right = 0;
                tRect.bottom = 0;
                break;
            case 1: // is LEFT of fromView
                tRect.left = toView.getX() + toView.getWidth();
                tRect.top = Math.min(fromView.getY(), toView.getY());
                tRect.right = fromView.getX();
                tRect.bottom = Math.max(fromView.getY() + fromView.getHeight(), toView.getY() + toView.getHeight());
                break;
            case 2: // is ABOVE fromView
                tRect.left = Math.min(toView.getX(), fromView.getX());
                tRect.top = toView.getY() + toView.getHeight();
                tRect.right = Math.max(toView.getX() + toView.getWidth(), fromView.getX() + fromView.getWidth());
                tRect.bottom = fromView.getY();
                break;
            case 3: // is RIGHT of fromView
                tRect.left = fromView.getX() + fromView.getWidth();
                tRect.top = Math.min(fromView.getY(), toView.getY());
                tRect.right = toView.getX();
                tRect.bottom = Math.max(fromView.getY() + fromView.getHeight(), toView.getY() + toView.getHeight());
                break;
            case 4: // is UNDER fromView
                tRect.left = Math.min(fromView.getX(), toView.getX());
                tRect.top = fromView.getY() + fromView.getHeight();
                tRect.right = Math.max(fromView.getX() + fromView.getWidth(), toView.getX() + toView.getWidth());
                tRect.bottom = toView.getY();
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
     * returns the center point for the toView-View
     */
    private Point bCenter() {
        tC.set( (int)(toView.getWidth()/2 + toView.getX()),
                (int)(toView.getHeight()/2 + toView.getY()) );
        //tDot.setLocation(tC.x, tC.y);
        return tC;
    }

    /*
     * Returns the quadrant in which is toView-View, in relation to fromView-View
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
                (int)(fromView.getX() - (fromView.getY() - tC.y)),
                (int)(fromView.getX() + fromView.getWidth() + fromView.getY() - tC.y)
        );
        bottomHorizon.set(
                (int)(fromView.getX() - (tC.y - fromView.getY() - fromView.getHeight())),
                (int)(fromView.getX() + fromView.getWidth() + (tC.y - fromView.getY() - fromView.getHeight()))
        );

        /*
        dOne.setLocation(topHorizon.x, (int) toView.getY());
        dTwo.setLocation(topHorizon.y, (int) toView.getY());
        dThree.setLocation(bottomHorizon.x, (int) toView.getY());
        dFour.setLocation(bottomHorizon.y, (int) toView.getY());
        */

        quadrant = -1; // Control Number for testing

        if (tC.y < fromView.getY()) {
            // means its above the topLine
            mInterface.displayText(1);
            if (tC.x < topHorizon.x) {
                // Its quadrant 1
                quadrant = 1;
            } else if (tC.x > topHorizon.y) {
                // Its quadrant 3
                quadrant = 3;
            } else {
                // Its in quadrant 2
                quadrant = 2;
            }
        } else if (tC.y > (fromView.getY() + fromView.getHeight())) {
            // Means its under bottomLine
            mInterface.displayText(2);
            if (tC.x < bottomHorizon.x) {
                // Its quadrant 1
                quadrant = 1;
            } else if (tC.x > bottomHorizon.y) {
                // Its quadrant 3
                quadrant = 3;
            } else {
                // Its quadrant 4
                quadrant = 4;
            }
        } else {
            // its between the topLine and bottomLine
            mInterface.displayText(3);
            if (tC.x < fromView.getX()) {
                // Its quadrant 1
                quadrant = 1;
            } else if (tC.x > (fromView.getX() + fromView.getWidth())) {
                // Its quadrant 3
                quadrant = 3;
            } else {
                // It must be over fromView
                quadrant = 0;
            }
        }
        //mInterface.displayText(quadrant); // TEST - TO BE DELETED
        return quadrant;
    }

    /*
     * Calculates coordinates in Canvas system for connector lines for
     * fromView view and toView view. You access the points by calling aCenterInCanvas and
     * bCenterInCanvas respectively
     */
    private void calculateConnectionPoints() {
        switch (quadrant) {
            case 1:
                aCenterInCanvas.set(
                        getWidth(),
                        (int) (fromView.getY() + fromView.getHeight()/2 - this.getY())
                );
                bCenterInCanvas.set(
                        0,
                        (int) (toView.getY() + toView.getHeight()/2 - this.getY())
                );
                break;
            case 2:
                aCenterInCanvas.set(
                        (int) (fromView.getX() + fromView.getWidth()/2 - this.getX()),
                        getHeight()
                );
                bCenterInCanvas.set(
                        (int)(toView.getX() + toView.getWidth()/2 - this.getX()),
                        0
                );
                break;
            case 3:
                aCenterInCanvas.set(
                        0,
                        (int)(fromView.getY() + fromView.getHeight()/2 - this.getY())
                );
                bCenterInCanvas.set(
                        getWidth(),
                        (int)(toView.getY() + toView.getHeight()/2 - this.getY())
                );
                break;
            case 4:
                aCenterInCanvas.set(
                        (int)(fromView.getX() + fromView.getWidth()/2 - this.getX()),
                        0
                );
                bCenterInCanvas.set(
                        (int)(toView.getX() + toView.getWidth()/2 - this.getX()),
                        getHeight()
                        );
                ;
                break;
                default: // Some error has occurred, or views are overlapping.
                    aCenterInCanvas.set(-1,-1);
                    bCenterInCanvas.set(-1,-1);
        }

    }

    private int dpToPixConverter(float dp) {
        float scale = mInterface.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }

    public void reuseTailView(View newFromView, View newToView) {
        this.fromView = newFromView;
        this.toView = newToView;
        updateLayout();
    }

}
