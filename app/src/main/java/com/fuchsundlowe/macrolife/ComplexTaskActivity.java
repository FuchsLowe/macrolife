package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.Observer;
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
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplexTaskActivity extends AppCompatActivity implements ComplexTaskInterface{

    private float mx, my;
    private float curX, curY;
    float scaleFactor;
    private float MAX_SCALE = 0.5f, MIN_SCALE = 5.0f;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;
    private List<ComplexTaskChevron> wrapped;

    private ScrollView vScroll;
    private HorizontalScrollView hScroll;
    private ViewGroup layoutContainer; // This one is parent for all children
    private EditText newTask;

    private ScaleGestureDetector mScaleDetector;

    private int masterID;

    // TODO: Test Layout for scaling:

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);
        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);
        layoutContainer = findViewById(R.id.container_complexTask);
        newTask = findViewById(R.id.CTA_newTask);

        scaleFactor = 1.0f; // Defines the default scale factor to start with

        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);

        data = StorageMaster.getInstance(this);
        getData();
        
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());

        defineBottomBar();

    }

    // Touch events:

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);

            float curX, curY;

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
                invalidate();
            return true;
        }
    }

    private void invalidate() {
        // Invalidate the views for update...

    }

    // Data Management:

    private void getData() {
        data.findAllChildren(masterID).observe(this, new Observer<List<SubGoalMaster>>() {
            @Override
            public void onChanged(@Nullable List<SubGoalMaster> subGoalMasters) {
                allChildren = subGoalMasters;
                Log.d("Children SIze: ", String.valueOf(allChildren.size()));
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
                //ViewGroup.LayoutParams parms = new FrameLayout.LayoutParams(child.getMX(),
                //        child.getMY());
                layoutContainer.addView(temp);
                temp.animationPresentSelf();
            }
       }
       invalidate();
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
        SubGoalMaster temp = new SubGoalMaster(0, newTask.getText().toString(), null,
                null, Calendar.getInstance(), false, SourceType.local,
                masterID, 0, 0,0);
        temp.updateMe();
    }

    // TODO: Temp Test
    public void increaseSize(View view) {
        Log.e("CLick Recorder", " True");
        wrapped.get(0).animate().z(16f).setDuration(1000).start();
    }
    // TODO: End of test calls...

    // Interface part:
    public float getScale() {
        return scaleFactor;
    }

}