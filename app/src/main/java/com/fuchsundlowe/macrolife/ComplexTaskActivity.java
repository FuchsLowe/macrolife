package com.fuchsundlowe.macrolife;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.CustomViews.InfinitePaper;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.SupportClasses.HScroll;
import com.fuchsundlowe.macrolife.SupportClasses.VScroll;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplexTaskActivity extends AppCompatActivity implements ComplexTaskInterface {

    private int masterID;
    private float mx, my;
    private float curX, curY;
    float scaleFactor;
    private float MAX_SCALE = 0.5f, MIN_SCALE = 5.0f;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;
    private List<ComplexTaskChevron> wrapped;

    private VScroll vScroll;
    private HScroll hScroll;
    private InfinitePaper container;
    private EditText newTask;

    private ScaleGestureDetector mScaleDetector;
    private ComplexTaskChevron viewManaged; // This is a holder for current view that's dragged


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);
        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);

        newTask = findViewById(R.id.CTA_newTask);

        scaleFactor = 1.0f; // Defines the default scale factor to start with

        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);

        addPaper();

        data = StorageMaster.getInstance(this);
        getData();
        
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());

        defineBottomBar();

        display = findViewById(R.id.luda); // For tep recording of location of click


    }


    // Touch events:
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);

        if (viewManaged == null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mx = event.getX();
                    my = event.getY();
                    childLookUp(mx, my); // Looks if there is a child
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = event.getX();
                    curY = event.getY();
                    vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    mx = curX;
                    my = curY;
                    break;
                case MotionEvent.ACTION_UP:
                    curX = event.getX();
                    curY = event.getY();
                    vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        } else { // executed only if child is set thus moves it

            switch (event.getAction()) {

                case (MotionEvent.ACTION_MOVE):
                    curX = event.getX();
                    curY = event.getY();
                    viewManaged.setTranslationX(viewManaged.getTranslationX() + curX - mx);
                    viewManaged.setTranslationY(viewManaged.getTranslationY() + curY - my);
                    mx = curX;
                    my = curY;
                    break;
                case (MotionEvent.ACTION_UP):
                    viewManaged.updateNewCoordinates();
                    viewManaged = null;
                    break;
                case (MotionEvent.ACTION_CANCEL):
                    viewManaged.updateNewCoordinates();
                    viewManaged = null;
                    break;
            }
        }
        return true;

    }


    private class Scaler extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            container.requestLayout();
            // TODO: By implementing on scale begein adn end you can stop view from accepting other touch events
            return true;
        }
    }

    private void childLookUp(float x, float y) {

        for (ComplexTaskChevron object : wrapped) {
            Rect hit = new Rect();
            object.getGlobalVisibleRect(hit);
            if (hit.contains((int)x, (int)y)) {
                viewManaged = object;
                break;
            }
        }

    }

    private void addPaper() {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        container = new InfinitePaper(this, this);
        container.setLayoutParams(p);
        hScroll.addView(container);
    }

    // Data Management:

    private void getData() {
        data.findAllChildren(masterID).observe(this, new Observer<List<SubGoalMaster>>() {
            @Override
            public void onChanged(@Nullable List<SubGoalMaster> subGoalMasters) {
                allChildren = subGoalMasters;
                updateData();
            }
        });
    }


    private void updateData() {
       if (wrapped != null) {
           // Do nothing...
       } else {
           wrapped = new ArrayList<>();
            for (SubGoalMaster child: allChildren) {
                ComplexTaskChevron temp = new ComplexTaskChevron(this, child, this);
                wrapped.add(temp);
                container.addView(temp);
                temp.animationPresentSelf();
            }
       }

    }

    // Bottom Bar Implementation:
    private void defineBottomBar() {
        newTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        createNewTask();
                    }
                }
                return true;
            }
        });
    }

    private void createNewTask(){
        if (newTask.getText().length() > 0) { // Only if it has a name ;)
            SubGoalMaster temp = new SubGoalMaster(0, newTask.getText().toString(), null,
                    null, Calendar.getInstance(), false, SourceType.local,
                    masterID, 0, 0, 0);
            temp.updateMe();
            ComplexTaskChevron chev = new ComplexTaskChevron(this, temp, this);
            wrapped.add(chev);
            container.addView(chev);
            chev.animationPresentSelf();

        }
    }

    /* If view is null, will re-layout self, if view is passed and is touching the bounds, will
     * request layout
     */
    private void layout(@Nullable View v) {
        if (v == null) {
            container.requestLayout();
        } else {
            float h,y = 0;
            h = v.getWidth() + v.getX();
            y = v.getHeight() + v.getY();
            if (h >= container.getWidth() || y >= container.getHeight()) {
                container.requestLayout();
            }
        }
    }

    // TODO: Temp Test

    public void increaseSize(View view) {
        text("Click A");
        ViewGroup.LayoutParams k = container.getLayoutParams();
        k.height = 1600;
        k.width = 1400;
        container.setLayoutParams(k);
        container.requestLayout();
    }


    public void secondaryClick(View view) {
        text("Click B");
        ViewGroup.LayoutParams k = container.getLayoutParams();
        k.height = 600;
        k.width = 400;
        container.setLayoutParams(k);
        container.requestLayout();
    }
    EditText display;

    public void text(String i) {
        display.append("\n" + i);
    }

    // TODO: End of test calls...

    // Interface part:
    public float getScale() {
        return scaleFactor;
    }

}