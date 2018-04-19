package com.fuchsundlowe.macrolife;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.CustomViews.BubbleView;
import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.CustomViews.InfinitePaper;
import com.fuchsundlowe.macrolife.CustomViews.PopUpCreator;
import com.fuchsundlowe.macrolife.CustomViews.TailView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.Interfaces.PopUpProtocol;
import com.fuchsundlowe.macrolife.SupportClasses.HScroll;
import com.fuchsundlowe.macrolife.SupportClasses.VScroll;

import java.io.IOError;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplexTaskActivity extends AppCompatActivity implements ComplexTaskInterface,
        PopUpProtocol, TailViewProtocol {

    private int masterID, transX, transY;
    private float curX, curY, mx, my, scaleFactor;
    private float MAX_SCALE = 0.5f, MIN_SCALE = 2.0f;
    private boolean globalEdit = false;
    private boolean movingBubble = false;
    private int INCREASE_PAPER_BY = 50;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;
    private List<ComplexTaskChevron> wrapped;
    private List<TailView> connectors;

    private VScroll vScroll;
    private HScroll hScroll;
    private InfinitePaper container;
    private BubbleView mBubble;
    private TailView mTail;
    private Rect managedViewsRect;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mGestureDetector;
    private View viewManaged; // This is a holder for current view that's dragged
    private PopUpCreator bottomBar;
    private ValueAnimator animator;

    TextView flasher;
    TextView scchng;

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
        managedViewsRect = new Rect();
        INCREASE_PAPER_BY = dpToPixConverter(INCREASE_PAPER_BY);

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
        /*
        flasher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // What flasher does on click?

                bubbleToParent();
            }
        });
        */


    }

    private void signalGlobalEdit(boolean editStart) {
        if (editStart) {
            // Create a Bubble view and ConnectorView adjecent to the current ViewManaged
            globalEdit = true; // Just indicator
            mBubble = new BubbleView(this, (ComplexTaskChevron) viewManaged,
                    BubbleView.ConnectorState.initiated);
            container.addView(mBubble);
            mBubble.setBackgroundColor(Color.RED);
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
            container.removeView(mTail);
            mTail = null;
        }
    }

    private void addPaper() {
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        container = new InfinitePaper(this, this);
        container.setLayoutTransition(new LayoutTransition());
        container.setLayoutParams(p);
        hScroll.addView(container);
        container.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
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
       if (wrapped == null) {
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
                    mTail = new TailView(this, mBubble.getMaster(), mBubble);
                    mTail.setBackgroundColor(Color.CYAN);
                    mTail.setAlpha(0.5f);
                    // Layout?
                    container.addView(mTail);
                    mTail.layout(
                            mBubble.getLeft(),mBubble.getBottom(),
                            mBubble.getRight(),mBubble.getMaster().getTop()
                    );
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
                        // This is the default way so far of managing the location of the bubble
                        mBubble.setTranslationX(mBubble.getTranslationX() + curX - mx);
                        mBubble.setTranslationY(mBubble.getTranslationY() + curY - my);

                        // This part is about moving the tail View:
                        left(String.valueOf(mBubble.getY() + mBubble.getHeight()));
                        right(String.valueOf(mBubble.getMaster().getY()));
                        /*
                        // TOP Container
                        if ((mBubble.getY() + mBubble.getHeight()) <= mBubble.getMaster().getY()) {
                            displayText(0);
                            // Defining the side of container
                            if (mBubble.getX() >= mBubble.getStartPosition().left) {
                                // We add to the right margin
                                mTail.layout(
                                        mBubble.getStartPosition().left,
                                        (int)(mBubble.getHeight() + mBubble.getY()),
                                        (int)(mBubble.getWidth() + mBubble.getX()),
                                        mBubble.getStartPosition().bottom
                                );
                            } else {
                                mTail.layout(
                                        (int)(mBubble.getX()),
                                        (int)(mBubble.getY() + mBubble.getWidth()),
                                        mBubble.getStartPosition().right,
                                        mBubble.getStartPosition().bottom
                                );
                            }

                        // RIGHT Container
                        } else if ((mBubble.getX()) >= mBubble.getMaster().getRight()) {
                            this.displayText(1);

                            // Lower Half
                            if ((mBubble.getHeight() + mBubble.getY()) >=
                                    (mBubble.getMaster().getBottom() - mBubble.getHeight()/2)) {
                                mTail.setBackgroundColor(Color.YELLOW);
                                mTail.layout(
                                        mBubble.getMaster().getRight(),
                                        mBubble.getMaster().getTop() + mBubble.getHeight()/2,
                                        (int)(mBubble.getX()),
                                        (int)(mBubble.getY() + mBubble.getHeight())
                                );

                             //Upper Half
                            } else {
                                mTail.setBackgroundColor(Color.GREEN);
                                mTail.layout(
                                        mBubble.getMaster().getRight(),
                                        (int)(mBubble.getY()),
                                        (int)(mBubble.getX()),
                                        mBubble.getMaster().getBottom() - mBubble.getHeight()/2

                                );
                            }

                        // LEFT Container
                        } else if ((mBubble.getX() + mBubble.getWidth()) <= mBubble.getMaster().getLeft() ) {
                            this.displayText(2);
                            // Lower Half
                            if ((mBubble.getHeight() + mBubble.getY()) >=
                                    (mBubble.getMaster().getBottom() - mBubble.getHeight()/2)) {
                                mTail.setBackgroundColor(Color.YELLOW);
                                mTail.layout(
                                        (int)mBubble.getX() + mBubble.getWidth(),
                                        mBubble.getMaster().getTop() + mBubble.getHeight()/2,
                                        mBubble.getMaster().getLeft(),
                                        (int)(mBubble.getY() + mBubble.getHeight())
                                );
                                //Upper Half
                            } else {
                                mTail.setBackgroundColor(Color.GREEN);
                                mTail.layout(
                                        (int)(mBubble.getX() + mBubble.getWidth()),
                                        (int)(mBubble.getY()),
                                        mBubble.getMaster().getLeft(),
                                        mBubble.getMaster().getBottom() - mBubble.getHeight()/2
                                );
                            }
                        // BOTTOM Container
                        } else if (mBubble.getY() >= mBubble.getMaster().getBottom()) {
                            this.displayText(3);
                            // Checks if its in right margin
                            if (mBubble.getX() >= mBubble.getStartPosition().left) {
                                mTail.layout(
                                        mBubble.getStartPosition().left,
                                        mBubble.getMaster().getBottom(),
                                        (int)(mBubble.getX() + mBubble.getWidth()),
                                        (int)(mBubble.getY())
                                );
                            // Left margin
                            } else {
                                mTail.layout(
                                        (int)(mBubble.getX()),
                                        mBubble.getMaster().getBottom(),
                                        mBubble.getStartPosition().right,
                                        (int)(mBubble.getY())
                                );

                            }
                        // CENTER:
                        } else {
                            this.displayText(4);

                        }

                        mTail.invalidate();
                        */
                        mTail.updateLayout2();
                    } else if (viewManaged != null) {
                        // we do the view
                        transX = (int)(viewManaged.getTranslationX() + curX - mx); // Cancels going
                        transY = (int) (viewManaged.getTranslationY() + curY - my);// our of bounds
                        //transX = Math.max(transX, 0);
                        //transY = Math.max(transY,0);

                        viewManaged.setTranslationX(transX);
                        viewManaged.setTranslationY(transY);
                        // if view goes out of bounds we increase the bounds...
                        if (viewManaged.getX() + viewManaged.getWidth() > container.getWidth()) {
                           animator = ValueAnimator.ofInt(container.getMeasuredWidth(),
                                   INCREASE_PAPER_BY);
                           animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                               @Override
                               public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                   int val = (Integer) valueAnimator.getAnimatedValue();
                                   ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
                                   layoutParams.width = val;
                                   container.setLayoutParams(layoutParams);
                               }
                           });
                           animator.setDuration(200);
                           animator.start();
                        }
                        if (viewManaged.getY() + viewManaged.getHeight() > container.getHeight()) {
                            // We do the same thing for Height...
                            animator = ValueAnimator.ofInt(container.getMeasuredHeight(),
                                    INCREASE_PAPER_BY);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    int val = (Integer) valueAnimator.getAnimatedValue();
                                    ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
                                    layoutParams.height = val;
                                    container.setLayoutParams(layoutParams);
                                }
                            });
                            animator.setDuration(200);
                            animator.start();
                        }
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
            // That's good enough...
            container.setScaleY(scaleFactor);
            container.setScaleX(scaleFactor);
            return true;
        }
    }

    private class LongPressDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            if (childLookUp(e.getX(),e.getY())) {
                // We have clicked on view
                if (viewManaged != null) {
                    if (viewManaged instanceof ComplexTaskChevron) {
                        bottomBar.editChevronInComplexActivity((ComplexTaskChevron) viewManaged);
                        signalGlobalEdit(true); // this one calls views to redraw themselves for editing
                    }
                }
            } else {
                // Click was on empty location
                // Click should present coordinates in parent view system... These are absolute...
                Point nativeLock = container.clickLocation();
                bottomBar.setNewTask(nativeLock.x, nativeLock.y);
            }

        }
    }
    @Override
    public void displayText(int val){
           switch (val){
               case -1:
                   break;
               case 0:flasher.setBackgroundColor(Color.BLUE);
                break;
               case 1: flasher.setBackgroundColor(Color.RED);
                break;
               case 2: flasher.setBackgroundColor(Color.GREEN);
                break;
               case 3: flasher.setBackgroundColor(Color.YELLOW);
                break;
               case 4: flasher.setBackgroundColor(Color.BLACK);
                break;
           }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            flasher.setBackgroundColor(Color.WHITE);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }); // TODO: NO START HERE
    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }

    private int dpToPixConverter(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    void left(String val) {
        flasher.setText(val);
    }
    void right(String val) {
        scchng.setText(val);
    }

}