package com.fuchsundlowe.macrolife;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.CustomViews.InfinitePaper;
import com.fuchsundlowe.macrolife.CustomViews.PopUpCreator;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.Interfaces.PopUpProtocol;
import com.fuchsundlowe.macrolife.SupportClasses.HScroll;
import com.fuchsundlowe.macrolife.SupportClasses.VScroll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplexTaskActivity extends AppCompatActivity implements ComplexTaskInterface, PopUpProtocol {

    private int masterID;
    private float mx, my;
    private float curX, curY;
    private float scaleFactor;
    private float MAX_SCALE = 0.7f, MIN_SCALE = 2.0f;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;
    private List<ComplexTaskChevron> wrapped;

    private VScroll vScroll;
    private HScroll hScroll;
    private InfinitePaper container;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mGestureDetector;
    private ComplexTaskChevron viewManaged; // This is a holder for current view that's dragged
    private PopUpCreator bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);
        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);
        //newTask = findViewById(R.id.CTA_newTask);

        scaleFactor = 1.0f; // Defines the default scale factor to start with
        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);
        data = StorageMaster.getInstance(this);
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());
        mGestureDetector = new GestureDetectorCompat(this, new LongPressDetector());

        addPaper();
        getData();
        defineBottomBar();
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
    private void defineBottomBar() {
        bottomBar = new PopUpCreator(PopUpCreator.COMPLEX_TASK_ACTIVITY, this);
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

    // Interface part:
    public float getScale() {
        return scaleFactor;
    }

    public LinearLayout getLinearBox() {
        return findViewById(R.id.bottom_layout);
    }

    public Context getContext() {
        return this;
    }

    public void newTask(String name, Calendar start, Calendar end, Integer x, Integer y, int updateKey) {
        if (updateKey == 0) {
            SubGoalMaster temp = new SubGoalMaster(0, name, start,
                    end, Calendar.getInstance(), false, SourceType.local,
                    masterID, 0, x, y);
            temp.updateMe();
            ComplexTaskChevron chev = new ComplexTaskChevron(this, temp, this);
            wrapped.add(chev);
            container.addView(chev);
            chev.animationPresentSelf();
        }
    }

    public AppCompatActivity getActivity() {
        return this;
    }

    // Touch events:
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);

        if (viewManaged == null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mx = event.getX();
                    my = event.getY();
                    childLookUp(mx, my); // Looks if there is a child
                    bottomBar.releaseFields();
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
            if (scaleFactor > MIN_SCALE) {
                scaleFactor = MIN_SCALE;
            } else if (scaleFactor < MAX_SCALE) {
                scaleFactor = MAX_SCALE;
            }
            container.requestLayout();
            // TODO: By implementing on scale begein adn end you can stop view from accepting other touch events
            return true;
        }
    }

    private class LongPressDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            childLookUp(e.getX(),e.getY());
            if (viewManaged != null) {
                // We Edit existing one
                bottomBar.editChevronInComplexActivity(viewManaged);
                viewManaged = null;
            } else {
                // Create new One
                bottomBar.setNewTask(e.getX(), e.getY());
            }
        }
    }

}