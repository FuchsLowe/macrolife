package com.fuchsundlowe.macrolife.CustomViews;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;

public class TailView extends View {

    private TailViewProtocol mInterface;
    private View fromView, toView; // as in from this view the tail starts and to view the tail goes.
    private RectF tRect, buttonArea;
    private Point tC, topHorizon, bottomHorizon, aCenterInCanvas, bCenterInCanvas;
    private Paint tailPen, connectionHeadPen, cancelBodyPen, cancelLinePen;
    private Path mPath;
    private int quadrant = 0;
    private float currentX, curretY;
    private float calVal = 255 / 60;
    private int TAIL_PEN_WIDTH = 2;
    private int connectionHeadRadius = 10;
    private int CANCEL_LINE_PEN_WIDTH = 3;
    private float cancelLineStartingPoint;
    private int CANCEL_BUTTON_RADIUS = 35;
    private int currentLineProgress = 0;
    private float cancelLineIncrements;
    private drawingState currentDrawingState;
    private boolean isButtonTouched = false;

    public TailView(TailViewProtocol protocol, View fromView, View toView)  {
        super(protocol.getContext());
        mInterface = protocol;
        this.fromView = fromView;
        this.toView = toView;
        tailPen = new Paint();
        tRect = new RectF();
        tC = new Point();
        topHorizon = new Point();
        bottomHorizon = new Point();
        aCenterInCanvas = new Point();
        bCenterInCanvas = new Point();
        mPath = new Path();

        tailPen.setColor(Color.BLACK);
        tailPen.setStyle(Paint.Style.STROKE);
        tailPen.setStrokeWidth(dpToPixConverter(TAIL_PEN_WIDTH));

        connectionHeadPen = new Paint();
        connectionHeadPen.setColor(Color.BLACK);
        connectionHeadPen.setStyle(Paint.Style.FILL);

        cancelBodyPen = new Paint();
        cancelBodyPen.setColor(Color.RED);
        cancelBodyPen.setStyle(Paint.Style.FILL);

        cancelLinePen = new Paint();
        cancelLinePen.setColor(Color.WHITE);
        cancelLinePen.setStyle(Paint.Style.STROKE);
        CANCEL_LINE_PEN_WIDTH = dpToPixConverter(CANCEL_LINE_PEN_WIDTH);
        cancelLinePen.setStrokeWidth(CANCEL_LINE_PEN_WIDTH);

        CANCEL_BUTTON_RADIUS = dpToPixConverter(CANCEL_BUTTON_RADIUS);
        connectionHeadRadius = dpToPixConverter(connectionHeadRadius);
        cancelLineIncrements = (CANCEL_BUTTON_RADIUS * 0.8f) / 40 ;
        currentDrawingState = drawingState.notAppearing;
        setBackgroundColor(Color.TRANSPARENT);
    }
    /*
     * Will update layout manually for location on screen in dependence to the sub and masterView
     */
    // Instructs that we should draw cancelation sign in the middle of the tail view
    public void createCancelSign() {
        currentDrawingState = drawingState.appearing;
        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                if ((int)(animation.getAnimatedValue()) == 100) {
                    // Means we are done with animating
                    currentDrawingState = drawingState.appeared;
                }
                invalidate();
            }
        });
        animator.setDuration(750);
        animator.start();
    }
    private void clickedOnCancel() {
        // TODO: Create some animation that will dissmiss the button and then call removeTail...
        removeTailLinkedge();
    }
    // Called when we need to destroy the tailConnection between views A and B globally and
    private void removeTailLinkedge() {
        if (toView instanceof ComplexTaskChevron) {
            ((ComplexTaskChevron) toView).removeTailLink(this);
        }
        if (fromView instanceof ComplexTaskChevron) {
            ((ComplexTaskChevron) fromView).setConnection(0); // Removes it from data
            ((ComplexTaskChevron) fromView).removeTailLink(this);
        }
        mInterface.removeATail(this);
        mInterface = null;
        fromView = null;
        toView = null;
    }
    // User stops with the need to cancel the connection
    public void removeCancelSign() {
        if (currentDrawingState == drawingState.appeared) {
            currentDrawingState = drawingState.dismissing;
            // We make the animation
            ValueAnimator animator = ValueAnimator.ofInt(0,100);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    if ((int)(animation.getAnimatedValue()) == 100) {
                        // Means we are done with animating
                        currentDrawingState = drawingState.notAppearing;
                    }
                }
            });
        }
    }
     //returns the center point for the toView-View
    private Point toViewCenterInLocalCanvas() {
        tC.set( (int)(toView.getWidth()/2 + toView.getX()),
                (int)(toView.getHeight()/2 + toView.getY()) );
        //tDot.setLocation(tC.x, tC.y);
        return tC;
    }
    /**
     * Returns the quadrant in which is toView-View, in relation to fromView-View
     * Quadrant location is as following:
     * 1 is right, 2 top, 3 left, 4 bottom, 5 center
     */
    private int provideQuadrant() {
        toViewCenterInLocalCanvas();
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
    /**
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
    private boolean isTouchOnButton(float x, float y) {
        if (buttonArea == null) {
            buttonArea = new RectF();
            buttonArea.left = (getWidth() - CANCEL_BUTTON_RADIUS) /2;
            buttonArea.right = (getWidth() + CANCEL_BUTTON_RADIUS) /2;
            buttonArea.top = (getHeight() - CANCEL_BUTTON_RADIUS) /2;
            buttonArea.bottom = (getHeight() + CANCEL_BUTTON_RADIUS) /2;
        }
        boolean isInWidth, isInHeight;
        isInWidth = x >= buttonArea.left && x <= buttonArea.right;
        isInHeight = y >= buttonArea.top && y <= buttonArea.bottom;

        if (isInWidth && isInHeight) {
            isButtonTouched = true;
            return true;
        } else {
            isButtonTouched = false;
            return false;
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // We accept touches only if we have button displayed
        if (currentDrawingState != drawingState.notAppearing) {
            currentX = event.getX();
            curretY = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (isTouchOnButton(currentX,curretY)) {
                        currentDrawingState = drawingState.clickOnCancelDown;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (isTouchOnButton(currentX, curretY)) {
                        // If release is on button then we call the action
                        clickedOnCancel();
                    } else {
                        currentDrawingState = drawingState.appeared;
                        isButtonTouched = false;
                    }
                    buttonArea = null;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    buttonArea = null;
                    currentDrawingState = drawingState.appeared;
                    break;
            }
        } else { return false; }
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // For drawing the red thing, can I skip this somehow?
        calculateConnectionPoints(); // Defines the connection points.
        mPath.reset();
        mPath.moveTo(aCenterInCanvas.x, aCenterInCanvas.y);
        switch (quadrant) {
            case 1:
            case 3:
                mPath.cubicTo(getWidth() / 2, aCenterInCanvas.y,
                        getWidth() / 2, bCenterInCanvas.y, bCenterInCanvas.x, bCenterInCanvas.y);
                break;
            case 2:
            case 4:
                mPath.cubicTo(aCenterInCanvas.x, getHeight() / 2,
                        bCenterInCanvas.x, getHeight() / 2,
                        bCenterInCanvas.x, bCenterInCanvas.y
                );
                break;

        }

        // Draws the tailPen
        canvas.drawPath(mPath, tailPen);

        // Draws the connection head
        canvas.drawCircle(bCenterInCanvas.x, bCenterInCanvas.y, connectionHeadRadius,
                connectionHeadPen);
        //canvas.save();

        switch (currentDrawingState) {
            case appearing:
                break;
            case appeared:
                canvas.drawCircle(getWidth()/2,getHeight()/2,
                        CANCEL_BUTTON_RADIUS, cancelBodyPen);
                break;
            case dismissing:
                break;
            case clickOnCancelDown:
                break;
            case clickOnCancelReleased:
                break;
            case notAppearing:
                break;
        }
    }
    // This is my method for custom layout update
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
    // From View can be null, and if it is means we just changed the destination
    public void reuseTailView(View newFromView, View newToView) {
        if (newFromView != null) {
            this.fromView = newFromView;
        }
        this.toView = newToView;
        updateLayout();
    }
    private int dpToPixConverter(float dp) {
        float scale = mInterface.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private enum drawingState {
        appearing, appeared, clickOnCancelDown, clickOnCancelReleased, dismissing, notAppearing
    }
}
