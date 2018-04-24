package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;

public class ComplexTaskChevron extends View {

    private SubGoalMaster data;
    private ComplexTaskInterface protocol;
    private Context context;

    private int DEFAULT_H = 240;
    private int DEFAULT_W = 90;
    private int DEFAULT_TEXT = 32;
    private int DEFAULT_PADDING = 4;
    private int DEFAULT_Z = 2;
    private int DEFAULT_BOX_LINE = 2;
    private int workWordCountedLenght;
    private int currentState = 0;
    private float DEFUALT_TEXT_CUTTER = 0.8f; // distance between text and boundaries of view
    private float textWidth, textHeight, p;
    private boolean canAcceptValue = false;
    private String workWord;
    private Paint textMarker;
    private Paint boundsMarker;
    private Rect mBounds;

    public ComplexTaskChevron(Context context, SubGoalMaster data, ComplexTaskInterface protocol) {
        super(context);
        this.data = data;
        this.protocol = protocol;
        this.context = context;

        DEFAULT_TEXT = dpToPixConverter(DEFAULT_TEXT); // These three produce dp
        DEFAULT_BOX_LINE = dpToPixConverter(DEFAULT_BOX_LINE);
        DEFAULT_PADDING = dpToPixConverter(DEFAULT_PADDING);

        workWord = data.getTaskName();

        // TODO: Coloring should be defined by state...
        textMarker = new Paint();
        textMarker.setColor(Color.BLACK);
        textMarker.setTextSize(DEFAULT_TEXT);
        boundsMarker = new Paint();
        boundsMarker.setColor(Color.BLACK);
        boundsMarker.setStrokeWidth(dpToPixConverter(3));
        boundsMarker.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        textWidth = textMarker.measureText(workWord);
        textHeight = DEFAULT_TEXT;
        workWord = data.getTaskName();

        workWordCountedLenght = textMarker.breakText(workWord, true,
                getWidth() * DEFUALT_TEXT_CUTTER, null);

        /*
         * If all chars fit, draw them
         * else: draw those -1 that fit and concat the string
         *  do all fit in second line?
         *   YES: we draw them all
         *     NO: we cut those that are extra and just draw remainder
         */
        if (workWordCountedLenght > 0) {
            if (workWordCountedLenght >= workWord.length()) {
                canvas.drawText(workWord, (getWidth() - textWidth) / 2,
                        getHeight() / 2 + textHeight / 4, textMarker);
                // Creates two lines of text
            } else {
                // Drawing first line
                textWidth = textMarker.measureText (
                        workWord.substring(0, workWordCountedLenght) + "-");
                canvas.drawText(workWord.substring(0, workWordCountedLenght) + "-",
                        (getWidth() - textWidth) / 2, getHeight() / 2, textMarker);

                // Drawing the second line
                workWord = workWord.substring(workWordCountedLenght);
                workWordCountedLenght = textMarker.breakText(
                        workWord, true,
                        getWidth() * DEFUALT_TEXT_CUTTER, null
                );

                if (workWordCountedLenght >= workWord.length()) {
                    // Means the whole second part fits
                    textWidth = textMarker.measureText(workWord);
                    canvas.drawText(workWord, getWidth() /2 - textWidth / 2,
                            getHeight() / 2 + textHeight, textMarker);
                } else {
                    // Means we just have to cut some last bits
                    workWord = workWord.substring(0,workWordCountedLenght -1);
                    workWord += "...";
                    textWidth = textMarker.measureText(workWord);
                    canvas.drawText(workWord, (getWidth() - textWidth)/2,
                            getHeight() /2 + textHeight, textMarker);
                }

            }
        } else {
            Log.e("Size of text:", "IS SMALLER ZERO");
        }

        /*
        // Text size is not greater than the box
        if (workWordCountedLenght >= workWord.length()) {
            canvas.drawText(workWord, (getWidth() - textWidth) / 2,
                    getHeight()/2 + textHeight / 4 , textMarker);
            // Creates two lines of text
        } else {
            // Text is greater than the box...
            wordHolder = workWord.split(" ", COUNTER_SIZE);
            workWordCountedLenght = 0;
            workWord = wordHolder[0];
            // now we check how much can we type into it before we hit wall
           for (int i = 0; i<COUNTER_SIZE; i++) {
               workWordCountedLenght = textMarker.breakText(workWord, true,
                       getWidth() * DEFUALT_TEXT_CUTTER, null);
               if (workWordCountedLenght >= workWord.length()) {
                   if (i > 0) {
                       //workWord += wordHolder[i];
                   }
               } else {

                   wordsThatFit = i;
                   break;
               }


            }// Now we got to write that first set of words...
            workWord = "";
            for (int i = 0; i <= wordsThatFit - 1; i++) {
                workWord += wordHolder[i];
            }
            textWidth = textMarker.measureText(workWord);
            canvas.drawText(
                    workWord, (getWidth() - textWidth) / 2,
                    getHeight() / 2, textMarker
                    );
            // Now that we have drawn the first part of the sentence, we need to proceed with rest...
            /*
            workWord = wordHolder[wordsThatFit - 1];
            for (int i = wordsThatFit; i <= wordHolder.length; i ++) {

            }

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
                        // Cut the word the wordCountLenght

                    // Its barely enough
                    } else {
                        // Just type it in on 1/2 of the screen.
                    }

                // Means that there is more than one word...
                } else {

            }
        }

        */
        // This part draws the bounds of the view...
        // TODO: Draw the line based box...
        if (mBounds == null) {
            mBounds = new Rect(0,0,getWidth(), getHeight());
        }
        canvas.drawRect(mBounds,boundsMarker);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
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
        if (newX != null && newX >= 0) { data.setMX(newX);}
        if (newY != null && newY >= 0) {data.setMY(newY);}
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

    /* These flags define what should this View draw.
     * 0 = draw normal box
     * 1 = globalEdit is signed, so draw altered box
     * 3 = we can't accept connection. Do note 2 is a request doesn't have to be granted!!!
     * 4 = this task is completed
     */
    public void setStateFlag(int flag) {
        if (flag == 0) {
          iSetFlag(0);
        } else if (flag == 1) {
            // TODO: Determine if we can accept the connection
            if (!data.isTaskCompleted()) { // Task is not completed, we can move on
                if (data.getParentSubGoal() == 0) {

                }

            } else {
                // Means this one is completed, thus no need for management of these colors at all
            }

        }
        invalidate();
    }

    private void iSetFlag(int flag) {
        switch (flag){
            case 0:
                boundsMarker.setColor(Color.BLACK);
                boundsMarker.setAlpha(255);
                textMarker.setColor(Color.BLACK);
                textMarker.setAlpha(255);
                canAcceptValue = false;
                break;
            case 1:
                boundsMarker.setColor(Color.GREEN);
                boundsMarker.setAlpha(127);
                textMarker.setColor(Color.BLACK);
                textMarker.setAlpha(255);
                canAcceptValue = true;
                break;
            case 3:
                boundsMarker.setColor(Color.DKGRAY);
                boundsMarker.setAlpha(200);
                textMarker.setColor(Color.DKGRAY);
                textMarker.setAlpha(200);
                canAcceptValue = false;
                break;
        }
        currentState = flag;
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


