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
import android.util.Log;
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
    private boolean layoutIsChangin = false;
    private int INCREASE_PAPER_BY = 50;

    private List<SubGoalMaster> allChildren;
    private DataProviderProtocol data;
    private List<ComplexTaskChevron> wrappedChildrenInChevrons;
    private List<TailView> allTails;

    private VScroll vScroll;
    private HScroll hScroll;
    private InfinitePaper container;
    private BubbleView mBubble;
    private TailView mTail;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mGestureDetector;
    private View viewManaged; // This is a holder for current view that's dragged
    private PopUpCreator bottomBar;
    private ValueAnimator animator;
    private Rect hit;
    private ComplexTaskChevron connectionCandidate;

    TextView flasher;
    TextView scchng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complex_task_activity);
        vScroll = findViewById(R.id.vScroll);
        hScroll = findViewById(R.id.hScroll);

        // Currently only Testing of implemnetation here
        vScroll.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                stopChangesToLayoutTemp();
            }
        });
        hScroll.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                stopChangesToLayoutTemp();
            }
        });

        allTails = new ArrayList<>();
        hit = new Rect();
        scaleFactor = 1.0f; // Defines the default scale factor to start with
        masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, -1);
        data = StorageMaster.getInstance(this);
        mScaleDetector = new ScaleGestureDetector(this, new Scaler());
        mGestureDetector = new GestureDetectorCompat(this, new LongPressDetector());
        INCREASE_PAPER_BY = dpToPixConverter(INCREASE_PAPER_BY);

        addPaper();
        getData();
        defineBottomBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    //scchng.setBackgroundColor(Color.YELLOW);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                       // scchng.setBackgroundColor(Color.WHITE);
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
            globalEdit = true; // Just indicator
            mBubble = new BubbleView(this, (ComplexTaskChevron) viewManaged,
                    BubbleView.ConnectorState.initiated);
            container.addView(mBubble);
            // Signal to all Chevrons to redraw themselves for editing.
            setChevronFlag(1);

        } else {
            globalEdit = false;
            cancelBubble();
            bottomBar.releaseFields();
            viewManaged = null;
            setChevronFlag(0);
        }

    }

    private boolean childLookUp(float x, float y) {
        for (ComplexTaskChevron object : wrappedChildrenInChevrons) {
            object.getGlobalVisibleRect(hit);
            if (hit.contains((int) x, (int) y)) {
                viewManaged = object;
                return true;
            }
        }
        return false;
    }

    // This one checks if bubble can establish connection with Chevron at specific location
    private boolean canConnect(int x, int y) {
        for (ComplexTaskChevron object : wrappedChildrenInChevrons) {
            object.getGlobalVisibleRect(hit);
            if (hit.contains(x,y)) {
                if (object.canAccepConnection()) {
                    connectionCandidate = object;
                    return true;
                }
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

    private void setChevronFlag(int flag) {
        for (ComplexTaskChevron chev: wrappedChildrenInChevrons) {
            if (chev != viewManaged) {
                switch (flag){
                    case 0:
                        chev.setStateFlag(ComplexTaskChevron.ChevronStates.normal,0);
                        break;
                    case 1:
                        chev.setStateFlag(ComplexTaskChevron.ChevronStates.globalEdit,
                                ((ComplexTaskChevron)viewManaged).getDataID());
                        break;

                    // The View will invalidate himself
                }
            }
        }
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
       if (wrappedChildrenInChevrons == null) {
           wrappedChildrenInChevrons = new ArrayList<>();
       }
           // Reusing the chevrons:
        if (wrappedChildrenInChevrons.size() > 0) {
           if (wrappedChildrenInChevrons.size() > allChildren.size()) {
               // We have more wrappers than children, thus we remove some
               int excessWrappers = wrappedChildrenInChevrons.size() - allChildren.size();
               for (; excessWrappers>0; excessWrappers--) {
                   container.removeView(wrappedChildrenInChevrons.get(0));
                   wrappedChildrenInChevrons.remove(0);
               }
               // Now we reassign the the remainder
               for (int counter = 0; counter <= allChildren.size(); counter++) {
                   wrappedChildrenInChevrons.get(counter).reuseChevron(allChildren.get(counter));
               }
           } else if (wrappedChildrenInChevrons.size() == allChildren.size()) {
               // Number of wrappers is exact as the number of children to be wrapped
               for (int counter = 0; counter <= allChildren.size(); counter++) {
                   wrappedChildrenInChevrons.get(counter).reuseChevron(allChildren.get(counter));
               }
           } else {
               // Means that we have more children than wrappers
               int counter = 0;
               for (SubGoalMaster master: allChildren) {
                   if (counter <= wrappedChildrenInChevrons.size()) {
                       wrappedChildrenInChevrons.get(counter).reuseChevron(master);
                   } else {
                       ComplexTaskChevron temp = new ComplexTaskChevron(master, this);
                       wrappedChildrenInChevrons.add(temp);
                       container.addView(temp);
                       temp.animationPresentSelf();
                   }
               }
           }
        } else {
           // Means that there are no chevrons to be reused, so we have to create new ones
            for (SubGoalMaster child: allChildren) {
                ComplexTaskChevron temp = new ComplexTaskChevron(child, this);
                wrappedChildrenInChevrons.add(temp);
                container.addView(temp);
                temp.animationPresentSelf();
            }
        }
       setAllTails(); // When we do the update then the tails are adjusted... Thast whats calling this
    }

    // A function that iterates over all wraped items and connects tails for given ones
    private void setAllTails() {
        // We first clear the tails so they don't duplicate if there are any
        if ((allTails !=null) || (allTails.size() > 0)) {
            for (TailView tailToBeRemoved : allTails) {
                container.removeView(tailToBeRemoved);
            }
            allTails.clear();
        }
        for (ComplexTaskChevron chev: wrappedChildrenInChevrons) {
            if (chev.getSubGoal() > 0) {
                makeATail(chev, findChevWithID(chev.getSubGoal()));
            }
        }

        // Reusing the tails:

    }

    private void makeATail(View tailOut, View tailIn) {
        TailView temp = new TailView(this, tailOut, tailIn);
        ((ComplexTaskChevron)tailOut).setOutTail(temp);
        ((ComplexTaskChevron)tailIn).addInTail(temp);
        allTails.add(temp);
        container.addView(temp);
        temp.invalidate();
    }
    private void defineBottomBar() {
        bottomBar = new PopUpCreator(PopUpCreator.COMPLEX_TASK_ACTIVITY, this);
    }

    // Interface part:
    public void stopChangesToLayoutTemp(){
        //trackerOfBoolTest(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("Calling sleep", "NOW");
                    Thread.sleep(1000);
                    trackerOfBoolTest(false);
                } catch (InterruptedException e) {
                    trackerOfBoolTest(false);
                    Log.e("Error", "Sleep interuoted");
                }
            }
        });
    }

    // Looks for Chevron with requested ID. Returns null if there is none.
    @Override
    public ComplexTaskChevron findChevWithID(int iDentification) {
        for (ComplexTaskChevron chev: wrappedChildrenInChevrons) {
            if (chev.getDataID() == iDentification) {
                return chev;
            }
        }
        return null;
    }

    private void trackerOfBoolTest(boolean test) {
        //Log.e("TEst is set to:", String.valueOf(test));
        //layoutIsChangin=test;

    }

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
            ComplexTaskChevron chev = new ComplexTaskChevron(temp, this);
            wrappedChildrenInChevrons.add(chev);
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

        if (!layoutIsChangin) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mx = event.getX();
                my = event.getY();

                if (globalEdit) {
                    //if we have clicked at bubble, we move it subsequnetly, else we dismiss everything
                    if (isItABubble(mx, my)) {
                        // We leave everything as it is so bubble can be moved...
                        movingBubble = true;
                        mTail = new TailView(this, mBubble.getMaster(), mBubble);
                        mTail.setAlpha(0.5f);
                        // Layout? TODO: Should I use standar procedure?
                        container.addView(mTail);
                        mTail.layout(
                                mBubble.getLeft(), mBubble.getBottom(),
                                mBubble.getRight(), mBubble.getMaster().getTop()
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

                            if ( canConnect( (int) (curX), (int) (curY)) ){
                               mBubble.setConnectionOpportunity(1);
                               //TEST:
                                scchng.setBackgroundColor(Color.GREEN);
                            } else {
                                mBubble.setConnectionOpportunity(0);
                                //TEST:
                                scchng.setBackgroundColor(Color.RED);

                            }

                            mTail.updateLayout();
                        } else if (viewManaged != null) {
                            // we do the view
                            transX = (int) (viewManaged.getTranslationX() + curX - mx);
                            transY = (int) (viewManaged.getTranslationY() + curY - my);

                            viewManaged.setTranslationX(transX);
                            viewManaged.setTranslationY(transY);

                            ((ComplexTaskChevron) viewManaged).invalidateTails();

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
                        if (movingBubble) {
                            if (canConnect((int)(curX),(int) (curY))) {
                                if (connectionCandidate != null) {
                                    ((ComplexTaskChevron) viewManaged).setConnection
                                            (connectionCandidate.getDataID());
                                    // Cancel Bubble
                                    cancelBubble();
                                    // Establish a new Permanent Connector
                                    makeATail(viewManaged, connectionCandidate);
                                }
                            } else {
                                if (viewManaged!= null) {
                                    ((ComplexTaskChevron) viewManaged).updateNewCoordinates();
                                }
                                bubbleToParent();
                            }
                        }
                       // viewManaged = null;
                        movingBubble = false;
                        connectionCandidate = null;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (viewManaged instanceof ComplexTaskChevron) {
                            ((ComplexTaskChevron) viewManaged).updateNewCoordinates();
                        }
                        if (movingBubble) {
                            bubbleToParent();
                        }
                        //viewManaged = null;
                        movingBubble = false;
                        connectionCandidate = null;
                        break;
                }
            }
        }

        return true;
    }

    private class Scaler extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            stopChangesToLayoutTemp();
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
            stopChangesToLayoutTemp();
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