package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;

public class BubbleView extends View {

    private TailViewProtocol mProtocol;
    private ComplexTaskChevron master;
    private float SIZE_OF_BALL = 0.2f;
    private int WIDTH_OF_STROKE = 2; // this is recalculated to DP
    private int DRAW_DURATION = 750;
    private float draw_progress;
    private Paint penStroke;
    private ConnectorState mState;
    private float mX, mY;
    private int startLeft, startRight, startTop, startBottom;
    private int currentState;
    private Rect bubbleRect;
    private boolean isAnimating = false;
    private boolean canConnect = false;
    private View tailView;

    public BubbleView(TailViewProtocol protocol, ComplexTaskChevron master,
                      ConnectorState state) {
        super(protocol.getContext());
        this.mProtocol = protocol;
        this.master = master;
        mState = state;

        WIDTH_OF_STROKE = dpToPixConverter(WIDTH_OF_STROKE);
        draw_progress = 0.0f;

        penStroke = new Paint();
        setBubbleState(0);
        penStroke.setStrokeWidth(WIDTH_OF_STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.e("Bubble VIew", "DRAW CALLED W:" + getWidth() + " H: " + getHeight());
        canvas.drawCircle(getWidth() / 2, getHeight() / 2,
                (getHeight() / 2) - 5, penStroke); // Here for now just to show something...
        switch (mState) {
            case initiated:
                // Saves the initial state
                startLeft = getLeft();
                startRight = getRight();
                startTop = getTop();
                startBottom = getBottom();

                //Draws the circle
                /* This method is postponed for now, until I decide how will I do this...
                    Current idea is to go via Vector Drawable because its the android way... Also
                    I need to decide what will I draw eventually
                bubbleRect.set(getWidth() / 2 + getHeight() / 2, 0,
                        getWidth() / 2 + getHeight() / 2, getHeight()); // Donno if this is right...
                canvas.drawArc(bubbleRect, 0.0f, 360 * draw_progress,
                        false, penStroke);
                // Guess it calls animator?

                */
                mState = ConnectorState.onMove;
                break;
            case onMove:
                // We should draw the head...

                // Now we calculate the drawing of the body

                break;
            case connected:
                break;
            case canceling:
                // TODO: Drawing of the cancel bubble
                break;
        }
    }

    public Rect getStartPosition() {
        if (bubbleRect != null) {
            return bubbleRect;
        } else {
            bubbleRect = new Rect(startLeft, startTop, startRight, startBottom);
            return bubbleRect;
        }
    }

    public View getMaster() {
        return master;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) (master.getWidth() * SIZE_OF_BALL) + 1;
        setMeasuredDimension(size, size);
    }

    public void animateToOriginalPosition() {
        this.layout(startLeft, startTop, startRight, startBottom);
        //requestLayout(); I don't think it requires this
    }

    // 1 means we can accept current connection, 0 we can't
    private void setBubbleState(int state) {
        switch (state) {
            case 0:
                penStroke.setColor(Color.BLUE);
                break;
            case 1:
                penStroke.setColor(Color.GREEN);
                break;
        }
        currentState = state;
        invalidate();
    }

    private int dpToPixConverter(float dp) {
        float scale = mProtocol.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }

    // 0 is false, 1 is true
    public void setConnectionOpportunity(int val) {
        // If they are the same we don't make change
        if (currentState!=val) {
            setBubbleState(val);
        }
    }

    public enum ConnectorState {
        initiated, onMove, onConnect, connected, canceling;
    }
}
