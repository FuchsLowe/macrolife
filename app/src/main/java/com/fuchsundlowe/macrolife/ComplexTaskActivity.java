package com.fuchsundlowe.macrolife;

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

public class ComplexTaskActivity extends AppCompatActivity implements ComplexTaskInterface, View.OnTouchListener{

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
    //private FrameLayout container;
    private EditText newTask;

    private ScaleGestureDetector mScaleDetector;

    private int masterID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);
        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);

        newTask = findViewById(R.id.CTA_newTask);

        scaleFactor = 1.0f; // Defines the default scale factor to start with

        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);

        //frameContainer();
        addPaper();

        data = StorageMaster.getInstance(this);
        getData();
        
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());

        defineBottomBar();

        display = findViewById(R.id.luda); // For tep recording of location of click

    }

    void frameContainer() {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //container = new FrameLayout(this);
        container.setLayoutParams(p);
        hScroll.addView(container);

    }

    // Touch events:
    private ComplexTaskChevron viewManaged;

    // This one is used by views
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        text("Special Click");
            switch (event.getAction()) {
                case (MotionEvent.ACTION_DOWN):
                    mx = event.getX();
                    my = event.getY();
                    viewManaged = (ComplexTaskChevron) v;
                    break;
                case (MotionEvent.ACTION_MOVE):
                    curX = event.getX();
                    curY = event.getY();

                    viewManaged.setTranslationX(curX - mx);
                    viewManaged.setTranslationY(curY - my);

                    mx = curX;
                    my = curY;
                    text("X: " + mx + " Y: " + my);
                    break;
                case (MotionEvent.ACTION_UP):
                    viewManaged.updateNewCoordinates();
                    viewManaged = null;
                    text("SPecial UP");
                    break;
                case (MotionEvent.ACTION_CANCEL):
                    viewManaged.updateNewCoordinates();
                    viewManaged = null;
                    text("SPecial CANCEL");
                    break;

            }


        return true;
    }


    // This one is called by layout to scroll or scale
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        text("Regular click");
        mScaleDetector.onTouchEvent(event);

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mx = event.getX();
                    my = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = event.getX();
                    curY = event.getY();
                    vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    mx = curX;
                    my = curY;
                    text("X: " + mx + " Y: " + my);
                    break;
                case MotionEvent.ACTION_UP:
                    curX = event.getX();
                    curY = event.getY();
                    vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    break;
            }

        return true;

    }

    private class Scaler extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(MIN_SCALE, Math.min(MAX_SCALE, scaleFactor));

                Log.d("Scale Factor", String.valueOf(detector.getScaleFactor()));
                layout(null);
            return true;
        }
    }

    private ComplexTaskChevron childLookUp(int x, int y) {

        for (ComplexTaskChevron object : wrapped) {
            Rect hit = new Rect();
            object.getHitRect(hit);
            if (hit.contains(x,y)) {
                return object;
            }
        }
        return null;
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
                text("Children SIze: " +String.valueOf(allChildren.size()));
                updateData();
            }
        });
    }

    private void updateData() {
       if (wrapped != null) {
          for (SubGoalMaster child: allChildren) {
              for (ComplexTaskChevron chevron: wrapped) {
                  // How can I intersect the new and old sets?
              }
          }
       } else {
           wrapped = new ArrayList<>();
            for (SubGoalMaster child: allChildren) {
                ComplexTaskChevron temp = new ComplexTaskChevron(this, child, this);
                wrapped.add(temp);
                temp.setOnTouchListener(this);
                container.addView(temp);
                temp.animationPresentSelf();
            }
       }
       //layout(null);
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
        text("New Task Created");
        SubGoalMaster temp = new SubGoalMaster(0, newTask.getText().toString(), null,
                null, Calendar.getInstance(), false, SourceType.local,
                masterID, 0, 0,0);
        temp.updateMe();
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

    int count = 1;
    public void increaseSize(View view) {
      container.animate().scaleXBy(1).scaleYBy(1).setDuration(1000).start();
      count +=1;
    }

    public void secondaryClick(View view) {
        container.animate().scaleYBy(0.5f).scaleXBy(0.5f).setDuration(1000).start();
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



    class ComplexTaskViewGroup extends ViewGroup {

        public ComplexTaskViewGroup(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }
    }

}