package com.fuchsundlowe.macrolife;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;

import java.util.List;

public class ComplexTaskActivity extends AppCompatActivity {

    private float mx, my;
    private float curX, curY;
    private float scaleFactor;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;

    private ScrollView vScroll;
    private HorizontalScrollView hScroll;

    private ScaleGestureDetector mScaleDetector;

    private int masterID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);

        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);

        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);

        data = StorageMaster.getInstance(this);
        getData();
        
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());

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

            return false;

    }

    private class Scaler extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            return true;
        }
    }

    // Data Management:

    private void getData() {
        data.findAllChildren(masterID).observe(this, new Observer<List<SubGoalMaster>>() {
            @Override
            public void onChanged(@Nullable List<SubGoalMaster> subGoalMasters) {
                // TODO: How do we arrange all this data?
                allChildren = subGoalMasters;
                updateBase();
            }
        });
    }

    private void updateBase() {
        // TODO: this is used to update the views...
    }
}
