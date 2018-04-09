package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.ConnectorViewProtocol;

public class BubbleView extends View {

    private ConnectorViewProtocol mProtocol;
    private ComplexTaskChevron master;
    private float SIZE_OF_BALL = 0.5f;
    private int WIDTH_OF_STROKE = 2; // this is recalculated to DP
    private Paint penStroke;

    private float mX, mY;


    public BubbleView(ConnectorViewProtocol protocol, ComplexTaskChevron master) {
        super(protocol.getContext());
        this.mProtocol = protocol;
        this.master = master;

        WIDTH_OF_STROKE = dpToPixConverter(WIDTH_OF_STROKE);

        penStroke = new Paint();
        penStroke.setColor(Color.BLUE);
        penStroke.setStrokeWidth(WIDTH_OF_STROKE);



    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2,
                (getHeight() / 2) - 5, penStroke);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (int) (master.getWidth() * SIZE_OF_BALL) + 1;
        setMeasuredDimension(size, size);
    }

    public void animateToOriginalPosition() {
        this.animate().translationX(master.getLeft())
                .translationY(master.getTop() - this.getMeasuredHeight()).setDuration(200).start();
    }

    private int dpToPixConverter(float dp) {
        float scale = mProtocol.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    public View getChevron() {
        return master;
    }
}
