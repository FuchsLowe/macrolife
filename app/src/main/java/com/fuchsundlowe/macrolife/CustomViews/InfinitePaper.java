package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.Interfaces.ScaleInterface;

public class InfinitePaper extends ViewGroup implements ScaleInterface {

    private Context mContext;
    private ComplexTaskInterface mInterface;
    private int MIN_WIDTH;
    private int MIN_HEIGHT;
    private int MIN_PADDING = 20;
    private Point clickLocation;
    private float currentScale = 1;

    public InfinitePaper(@NonNull Context context, ComplexTaskInterface mInterface) {
        super(context);
        mContext = context;
        this.mInterface = mInterface;
        setWillNotDraw(false);
        this.setBackgroundColor(Color.GRAY);
        clickLocation = new Point();
        defineCanvasSize();

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        clickLocation.set((int)ev.getX(), (int)ev.getY());
        return false;
    }
    // Should set minWidth & height for Children size
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        Point forMin = getMinSize();
        forMin.set((int) Math.max(forMin.x, MIN_WIDTH),
                    (int) Math.max(forMin.y, MIN_HEIGHT));

        setMinimumHeight(forMin.x);
        setMinimumWidth(forMin.y);

        float regX, regY;
        regX = Math.max(MIN_WIDTH, forMin.x + dpToPixConverter(MIN_PADDING));
        regY = Math.max(MIN_HEIGHT, forMin.y + dpToPixConverter(MIN_PADDING));

        setMeasuredDimension(
                (int) (regX * currentScale),
                (int) (regY * currentScale)
        );

    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            Log.e("onLayout", "Changed = True");
        }
        ComplexTaskChevron kid;
            for (int i =0; i< getChildCount(); i++) {
               if (getChildAt(i) instanceof ComplexTaskChevron) {
                   kid = (ComplexTaskChevron) getChildAt(i);
                   if (kid.getX() < 0.5 || kid.getY() <0.5) {
                       kid.layout(
                               (int) (kid.getXFromData()),
                               (int) (kid.getYFromData()),
                               (int) ((kid.getXFromData() + kid.getMeasuredHeight())),
                               (int) ((kid.getYFromData() + kid.getMeasuredWidth())));
                   } else {
                       /* TODO: Does this work?
                       kid.layout(
                               (int)(kid.getX()),
                               (int)(kid.getY()),
                               (int)(kid.getX() + kid.getMeasuredHeight()),
                               (int)(kid.getY() + kid.getMeasuredWidth()));
                               */
                   }

               } else if (getChildAt(i) instanceof BubbleView) {

                   BubbleView object = (BubbleView) getChildAt(i);
                   View parent = object.getMaster();

                   object.layout(
                           (int)(parent.getX() +
                                   ((parent.getWidth() - object.getMeasuredWidth()) / 2)),
                           (int)(parent.getY() - object.getMeasuredHeight()),
                           (int)(parent.getX() + parent.getWidth() -
                                   ((parent.getWidth() - object.getMeasuredWidth()) / 2)),
                           (int)(parent.getY())
                   );

               }
            }
            // now try and set the tails?
            for (int i = 0; i<getChildCount(); i++) {
               if ( getChildAt(i) instanceof TailView) {
                   ((TailView) getChildAt(i)).updateLayout();
               }
            }
    }
    // Part of ScaleInterface, will work to adjust self to new dimentions
    @Override
    public void setNewScale(float newScale) {
        // SHould increase own size... both X and Y
        this.currentScale = newScale;
        this.requestLayout();
    }

    // returns minimum size so that children are always included in the view
    private Point getMinSize() {
        Point temp = new Point();
        float maxX = 0;
        float maxY = 0;
        View viewExamined;
        for (int i = 0; i< this.getChildCount(); i++) {
            viewExamined = this.getChildAt(i);
            if (viewExamined instanceof ComplexTaskChevron) {
                maxX = Math.max(maxX, viewExamined.getMeasuredWidth() +
                        ((ComplexTaskChevron) viewExamined).getXFromData());
                maxY = Math.max(maxY, viewExamined.getMeasuredHeight() +
                        ((ComplexTaskChevron) viewExamined).getYFromData());
            }
        }

        temp.set((int) maxX +1, (int) maxY +1);

        return temp;
    }
    private int dpToPixConverter(float dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private void defineCanvasSize() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        MIN_WIDTH = size.x * 2;
        MIN_HEIGHT = size.y;

    }
    public Point clickLocation() {
        return clickLocation;
    }
}
