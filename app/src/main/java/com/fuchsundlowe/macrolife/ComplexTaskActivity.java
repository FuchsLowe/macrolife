package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fuchsundlowe.macrolife.CustomViews.BubbleView;
import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.CustomViews.ConnectorView;
import com.fuchsundlowe.macrolife.CustomViews.InfinitePaper;
import com.fuchsundlowe.macrolife.CustomViews.PopUpCreator;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.Interfaces.ConnectorViewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.Interfaces.PopUpProtocol;
import com.fuchsundlowe.macrolife.SupportClasses.HScroll;
import com.fuchsundlowe.macrolife.SupportClasses.VScroll;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplexTaskActivity extends AppCompatActivity implements ComplexTaskInterface,
        PopUpProtocol, ConnectorViewProtocol {

    private int masterID;
    private float mx, my;
    private float curX, curY;
    private float scaleFactor;
    private float MAX_SCALE = 1.0f, MIN_SCALE = 2.0f;
    private boolean globalEdit = false;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;
    private List<ComplexTaskChevron> wrapped;

    private VScroll vScroll;
    private HScroll hScroll;
    private InfinitePaper container;
    private BubbleView mBubble;
    private boolean movingBubble = false;
    private List<ConnectorView> connectors;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mGestureDetector;
    private View viewManaged; // This is a holder for current view that's dragged
    private PopUpCreator bottomBar;

    ViewGroup flasher;
    ViewGroup scchng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);
        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);

        connectors = new ArrayList<>();
        scaleFactor = 1.0f; // Defines the default scale factor to start with
        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);
        data = StorageMaster.getInstance(this);
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());
        mGestureDetector = new GestureDetectorCompat(this, new LongPressDetector());

        addPaper();
        getData();
        defineBottomBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    scchng.setBackgroundColor(Color.YELLOW);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        scchng.setBackgroundColor(Color.WHITE);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                }
            });
        }

        flasher = findViewById(R.id.flasher);
        scchng = findViewById(R.id.scchng);


    }

    private void signalGlobalEdit(boolean editStart) {
        if (editStart) {
            // Create a Bubble view and ConnectorView adjecent to the current ViewManaged
            globalEdit = true;
            mBubble = new BubbleView(this, (ComplexTaskChevron) viewManaged);
            container.addView(mBubble);
            // Signal to all Chevrons to redraw themselves for editing.
        } else {
            globalEdit = false;
            cancelBubble();
            bottomBar.releaseFields();
        }

    }

    private boolean childLookUp(float x, float y) {
        Rect hit = new Rect();
        for (ComplexTaskChevron object : wrapped) {
            object.getGlobalVisibleRect(hit);
            if (hit.contains((int) x, (int) y)) {
                viewManaged = object;
                return true;
            }
        }
        return false;
    }

    private boolean isItABubble(float x, float y) {
        if (mBubble != null) {
            Rect hit = new Rect();
            mBubble.getGlobalVisibleRect(hit);
            if (hit.contains((int) x, (int) y)) {
                return true;
            }
        }
        return false;
    }

    private void cancelBubble() {
        movingBubble = false;
        container.removeView(mBubble);
        mBubble = null;
    }

    // Will spring back bubble to original position, due to not hooking up with other goal
    private void bubbleToParent() {
        if (mBubble != null) {
            mBubble.animateToOriginalPosition();
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

    @Override
    public void globalEditDone() {
        signalGlobalEdit(false);
    }

    public AppCompatActivity getActivity() {
        return this;
    }

    // Touch events:
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event); // checks if its a long press...

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mx = event.getX();
            my = event.getY();

            if (globalEdit) {
                //if we have clicked at bubble, we move it subsequnetly, else we dismiss everything
                if (isItABubble(mx, my)) {
                    // We leave everything as it is so bubble can be moved...
                    movingBubble = true;
                } else {
                   signalGlobalEdit(false);
                }
            } else {
                // if we have clicked at task, we move it, else we move background..
                childLookUp(mx, my);
            }
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    curX = event.getX();
                    curY = event.getY();

                    if (movingBubble) {
                        mBubble.setTranslationX(mBubble.getTranslationX() + curX - mx);
                        mBubble.setTranslationY(mBubble.getTranslationY() + curY - my);
                    } else if (viewManaged != null) {
                        // we do the view
                        viewManaged.setTranslationX(viewManaged.getTranslationX() + curX - mx);
                        viewManaged.setTranslationY(viewManaged.getTranslationY() + curY - my);
                    } else {
                        // we move the background...
                        vScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                        hScroll.scrollBy((int) (mx - curX), (int) (my - curY));
                    }

                    mx = curX;
                    my = curY;
                    break;
                case MotionEvent.ACTION_UP:
                    if (viewManaged instanceof ComplexTaskChevron) {
                        ((ComplexTaskChevron) viewManaged).updateNewCoordinates();
                    }

                    if (movingBubble) {
                        bubbleToParent();
                    }
                    viewManaged = null;
                    movingBubble = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    if (viewManaged instanceof ComplexTaskChevron) {
                        ((ComplexTaskChevron) viewManaged).updateNewCoordinates();
                    }
                    if (movingBubble) {
                        bubbleToParent();
                    }
                    viewManaged = null;
                    movingBubble = false;
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
                if (viewManaged instanceof ComplexTaskChevron) {
                    bottomBar.editChevronInComplexActivity((ComplexTaskChevron) viewManaged);
                    signalGlobalEdit(true); // this one calls views to redraw themselves for editing
                }
                viewManaged = null;
            } else {
                // Create new One
                bottomBar.setNewTask(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void displayText(int val){
           switch (val){
               case 0:flasher.setBackgroundColor(Color.BLUE);
                break;
               case 1: flasher.setBackgroundColor(Color.GREEN);
                break;
               case 2: flasher.setBackgroundColor(Color.RED);
                break;
           }
    }

}