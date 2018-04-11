package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;


public class ComplexTaskChevron extends View {

    private SubGoalMaster data;
    private ComplexTaskInterface protocol;
    private Context context;

    private int DEFAULT_H = 240;
    private int DEFAULT_W = 90;
    private int DEFAULT_TEXT = 36;
    private int DEFAULT_PADDING = 4;
    private int DEFAULT_Z = 2;
    private int DEFAULT_BOX_LINE = 2;
    private int COUNTER_SIZE = 6;
    private int workWordCountedLenght;
    private int wordsThatFit = 0;
    private int remainingWords = 0;
    private float DEFUALT_TEXT_CUTTER = 0.8f; // distance between text and boundaries of view
    private float scale, textWidth, textHeight, p;
    private float[] linePoints;
    private String workerString;
    private String[] wordHolder;
    private Paint textMarker;
    private Paint boundsMarker;


    private TextView text;

    public ComplexTaskChevron(Context context, SubGoalMaster data, ComplexTaskInterface protocol) {
        super(context);
        this.data = data;
        this.protocol = protocol;
        this.context = context;

        workerString = data.getTaskName();
        textMarker = new Paint();
        textMarker.setColor(Color.BLACK);
        boundsMarker = new Paint();
        boundsMarker.setColor(Color.BLACK);

        DEFAULT_TEXT = dpToPixConverter(DEFAULT_TEXT); // These three produce dp
        DEFAULT_BOX_LINE = dpToPixConverter(DEFAULT_BOX_LINE);
        DEFAULT_PADDING = dpToPixConverter(DEFAULT_PADDING);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: Draw text in accordance to the size of the box...
        scale = protocol.getScale();
        textMarker.setTextSize(DEFAULT_TEXT * scale);
        textWidth = textMarker.measureText(workerString);
        textHeight = DEFAULT_TEXT * scale;
        workerString = data.getTaskName();
        workWordCountedLenght = textMarker.breakText(workerString, true,
                getWidth() * DEFUALT_TEXT_CUTTER, null);
        // Text size is not greater than the box
        if (workWordCountedLenght >= workerString.length()) {
            canvas.drawText(workerString, (getWidth() - textWidth) / 2,
                    getHeight()/2 + textHeight / 4 , textMarker);
            // Creates two lines of text
        } else {
            // Text is greater than the box...
            wordHolder = workerString.split(" ", COUNTER_SIZE);
            workWordCountedLenght = 0;
            workerString = wordHolder[0];
            // now we check how much can we type into it before we hit wall
           for (int i = 0; i<COUNTER_SIZE; i++) {
               workWordCountedLenght = textMarker.breakText(workerString, true,
                       getWidth() * DEFUALT_TEXT_CUTTER, null);
               if (workWordCountedLenght >= workerString.length()) {
                   if (i > 0) {
                       workerString += wordHolder[i];
                   }
               } else {
                   // TODO: Does this break for loop?
                   wordsThatFit = i;
                   break;
               }


            }// Now we got to write that first set of words...
            workerString = "";
            for (int i = 0; i <= wordsThatFit - 1; i++) {
                workerString += wordHolder[i];
            }
            textWidth = textMarker.measureText(workerString);
            canvas.drawText(
                    workerString, (getWidth() - textWidth) / 2,
                    getHeight() / 2, textMarker
                    );
            // Now that we have drawn the first part of the sentence, we need to proceed with rest...
            /*
            workerString = wordHolder[wordsThatFit];
            for (int i = wordsThatFit; i <= wordHolder.length; i ++) {
                // TODO: This is where am I now
            }
            */
                // This means that first word is to long or its just enough size
                // Is it right size?
                if (workWordCountedLenght == 0) {
                   // Measure text and determine if we print it or we cut it...
                    workWordCountedLenght = textMarker.breakText(
                            wordHolder[0],
                            true,
                            getWidth() * DEFUALT_TEXT_CUTTER, null
                            );
                    // YES words is too long
                    if (workWordCountedLenght >= wordHolder[0].length()) {

                    // Its barely enough
                    } else {

                    }

                // Means that there is more than one word...
                } else {

            }
        }


        // This part draws the bounds of the view...
        // TODO: Draw the line based box...

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {;
       float scale = protocol.getScale();

       float minX = dpToPixConverter(DEFAULT_W * scale);
       float minY = dpToPixConverter(DEFAULT_H * scale);

       setMinimumHeight((int)minY + 1);
       setMinimumWidth((int) minX + 1);


        setMeasuredDimension((int) (minX + dpToPixConverter(DEFAULT_PADDING)),
                (int) (minY + dpToPixConverter(DEFAULT_PADDING)));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setOutlineProvider(new CustomOutline(w, h));
    }

    // Utility method calls:
    private int dpToPixConverter(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }

    public int getDataID() {
        return data.getHashID();
    }

    public int getXFromData() {
        return data.getMX();
    }

    public int getYFromData() {
        return data.getMY();
    }

    public String getTaskName() { return data.getTaskName();}

    public void setNewValues(String newName, Integer newX, Integer newY,
                             Integer parentSubGoal, Boolean completed) {
        if (newName != null) { data.setTaskName(newName);}
        if (newX != null) { data.setMX(newX);}
        if (newY != null) {data.setMY(newY);}
        if (parentSubGoal != null) {data.setParentSubGoal(parentSubGoal); }
        if (completed != null) {data.setTaskCompleted(completed);}

        this.requestLayout();

    }

    // Animation Calls:

    public void animationDestroy() {
        data.deleteMe();
        this.animate().alpha(0f).setDuration(200).start();

    }

    public void animationPresentSelf() {
        /* An old way of doing things
        ObjectAnimator forX = ObjectAnimator.ofFloat(this, "x", data.getMX());
        ObjectAnimator forY = ObjectAnimator.ofFloat(this, "y", data.getMY());

        AnimatorSet set = new AnimatorSet();
        set.playTogether(forX, forY);
        set.start();
        */

        // Simple animation of things:
        this.animate().x(data.getMX()).y(data.getMY()).translationZ(DEFAULT_Z).setDuration(200).start();
    }

    // Touch Events management:

    public void updateNewCoordinates() {
        this.data.setMX((int)getX());
        this.data.setMY((int) getY());
        this.data.updateMe();
    }




    // Outline Provider:

    private class CustomOutline extends ViewOutlineProvider {

        int width;
        int height;

        CustomOutline(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public void getOutline(View view, Outline outline) {
            //int cPadding = dpToPixConverter(DEFAULT_PADDING * scale);
            int cPadding = 0;
            outline.setRect(0 + cPadding, cPadding, width - cPadding, height - cPadding);
        }
    }

    private class MyDrawable extends Drawable {

        Paint backColor;
        Paint edgeMarker;

        public MyDrawable() {
            backColor = new Paint();
            backColor.setColor(Color.WHITE);

            edgeMarker = new Paint();
            edgeMarker.setColor(Color.BLACK);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            setBackgroundColor(Color.WHITE);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

}


