package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

// This is the master presenter of the Repeating events
public class RepeatingEventEditor extends ConstraintLayout{

    // Base View:
    private ViewGroup baseView, editorHolder, buttonBar;
    private HashMap<ModButton.SpecialtyButton, ModButton> bottomBarButtons;
    // Implementation for repeating event editor:
    // Top Bar:
    private ImageButton saveButton, deleteButton;
    private TextView taskName;
    // Reminder Bar:
    private FrameLayout reminderBarHolder;
    // Left Side holder:
    private LinearLayout leftSideHolder;
    private HashMap<Integer, SideButton_RepeatEditor> weekButtons;
    // Central Bar - Representing the chronoView:
    private CronoViewFor_RepeatEditor dayView;
    private ScrollView dayViewHolder;
    // Bottom Bar Holder;
    private ViewPager bottomBarHolder;
    // Other variables:
    private LayoutInflater inflater;
    private TaskObject editedObject;
    private DataProviderNewProtocol localStorage;
    private SharedPreferences preferences;
    private OnClickListener buttonClickListener;
    private EditTaskProtocol protocol;
    private Calendar startTime, endTime;
    private RepeatType currentType;
    private RepeatingEventEditor self;


    public RepeatingEventEditor(Context context, EditTaskProtocol protocol) {
        super(context);
        this.protocol = protocol;
        this.self = this;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseView = (ViewGroup) inflater.inflate(R.layout.re, this, true); // Not this...
        editorHolder = baseView.findViewById(R.id.editorHolder_repeatingBase); // holds the editor View
        buttonBar = baseView.findViewById(R.id.buttonBar_repeatingBase); // holds the button bar

        // Adding the editorView to the editor holder but hiding it at initial presentation
        ConstraintLayout editorView = (ConstraintLayout) inflater.inflate(R.layout.repeating_event_editor, this, false);
        editorHolder.addView(editorView);
        // Two buttons that sit at the top of editorView and the TextView displaying the task name:
        taskName = editorView.findViewById(R.id.taskName_RepeatEditor);
        saveButton = editorView.findViewById(R.id.save_editorBase);
        deleteButton = editorView.findViewById(R.id.delete_editorBase);
        // Holder for the ChronoView implementation
        dayViewHolder = editorView.findViewById(R.id.DayViewHolder_RepeatEditor);
        dayView = new CronoViewFor_RepeatEditor(getContext());
        dayViewHolder.addView(dayView);
        leftSideHolder = editorView.findViewById(R.id.leftSideHolder_RepeatEditor);
        editorView.setVisibility(GONE);
        // Bottom bar holder, presenter of type of the repeating event:
        bottomBarHolder = editorView.findViewById(R.id.bottomBar_RepeatEditor);
        defineViewPager();
        //defineBottomBarButtonHolder();

        // Other implementations:
        localStorage = LocalStorage.getInstance(context);
        weekButtons = new HashMap<>();
        defineButtonClickListener();

    }
    // Methods:
    private void defineBottomBarButtonHolder() {
        // TODO: REMOVE?
        bottomBarButtons = new HashMap<>();
        bottomBarButtons.put(ModButton.SpecialtyButton.repeating, (ModButton) findViewById(R.id.type_editorBase));
        bottomBarButtons.put(ModButton.SpecialtyButton.save, (ModButton) findViewById(R.id.save_editorBase));
        bottomBarButtons.put(ModButton.SpecialtyButton.startValues, (ModButton) findViewById(R.id.startTime_editorBase));
        bottomBarButtons.put(ModButton.SpecialtyButton.endValues, (ModButton) findViewById(R.id.endTime_editorBase));
        bottomBarButtons.put(ModButton.SpecialtyButton.delete, (ModButton) findViewById(R.id.delete_editorBase));

        for (ModButton button: bottomBarButtons.values()) {
            button.defineMe(null);
        }
    }
    private void defineViewPager() {
        final SimplePageAdapter adapter = new SimplePageAdapter();
        bottomBarHolder.setAdapter(adapter);
        bottomBarHolder.setCurrentItem(0);
        // Listener so we could record the changes made...
        bottomBarHolder.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentType = adapter.presentingType[position];
                if (currentType == RepeatType.customWeek) {
                    leftSideHolder.setVisibility(VISIBLE);
                } else if (leftSideHolder.getVisibility() == VISIBLE) {
                    leftSideHolder.setVisibility(GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    // Simple adapter that should present the TypePresenter instead of Fragment
    private class SimplePageAdapter extends PagerAdapter {

        final RepeatType[] presentingType;

        SimplePageAdapter() {
            presentingType = new RepeatType[]{RepeatType.everyDay, RepeatType.customWeek, RepeatType.twoWeeks,
                    RepeatType.monthly, RepeatType.yearly};
        }

        View getView(int position, ViewPager pager) {
            View frag = inflater.inflate(R.layout.type_presenter, bottomBarHolder);
            if (frag instanceof TypePresenter) {
                ((TypePresenter) frag).defineMe(presentingType[position]);
            }
            return frag;
        }

        @Override
        public int getCount() {
            return presentingType.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ViewPager pager = (ViewPager) container;
            View view = getView(position, pager);

            pager.addView(view);

            return view;
        }
    }
    // This function manages receiving of data and infuses fields and methods with it
    public void defineMe(TaskObject objectWeEdit) {
        // TODO: Change!
        editedObject = objectWeEdit;
        taskName.setText(objectWeEdit.getTaskName());
        // So first I need to define how many buttons do I need to display...
        if (!editedObject.isThisRepeatingEvent()) {
            // we only have 4 buttons to present
            bottomBarButtons.get(ModButton.SpecialtyButton.save).setVisibility(GONE);
        } else {
            bottomBarButtons.get(ModButton.SpecialtyButton.startValues).setSpecialtyState(true);
            bottomBarButtons.get(ModButton.SpecialtyButton.endValues).setSpecialtyState(true);
            bottomBarButtons.get(ModButton.SpecialtyButton.type).setSpecialtyState(true);
            startTime = objectWeEdit.getTaskStartTime();
            endTime = objectWeEdit.getTaskEndTime();
        }
        defineLeftSideHolder();
    }

    //Presents or removes the left side of the bar.
    private void defineLeftSideHolder() {
        // grab buttons
        weekButtons.put(0, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_1));
        weekButtons.put(1, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_2));
        weekButtons.put(2, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_3));
        weekButtons.put(3, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_4));
        weekButtons.put(4, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_5));
        weekButtons.put(5, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_6));
        weekButtons.put(6, (SideButton_RepeatEditor) leftSideHolder.findViewById(R.id.sideButton_7));

        preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        int firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK,
                Calendar.getInstance().getFirstDayOfWeek());

        DayOfWeek[] daysOfWeek;
        switch (firstDayOfWeek) {
            case 1: // US - Sunday first day of week
                daysOfWeek = Constants.AMERICAN_WEEK_DAYS;
                break;
            default: // Europe - Monday first day of week
                daysOfWeek = Constants.EUROPEAN_WEEK_DAYS;
                break;
        }
        // Assigning phase
        for (int i = 0; i < 7; i++) {
            weekButtons.get(i).defineMe(daysOfWeek[i], buttonClickListener);
        }
    }

    private int dpToPixConverter(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private void defineButtonClickListener() {
        buttonClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof SideButton_RepeatEditor) {
                    // this is called by side button
                } else if (v instanceof ModButton) {
                    switch (((ModButton) v).reportButtonType()) {
                        case startValues:
                            // Produce the Date Calendar with starting the values
                            com.fuchsundlowe.macrolife.BottomBar.DatePickerFragment dateFragment =
                                    new com.fuchsundlowe.macrolife.BottomBar.DatePickerFragment();
                            dateFragment.defineMe(editedObject, protocol,true);
                            dateFragment.show(getContext().requireFragmentManager(), "DateFragment");
                            break;
                        case endValues:
                            // Produce the Date Calendar
                            break;
                        case delete:
                            // Remove all repeating tasks and make this taskObject
                            break;
                        case save:
                            // Delete existing events and re-create tasks...
                            protocol.modDone();
                            /*
                             * I am re-saving the children knowingly that there are no changes made
                             * to them, but rather to signal change in ChrnoView
                             * TODO: This migth not be needed, because we will re-create the new tasks
                             */
                            localStorage.reSaveRepeatingEventsFor(editedObject.getHashID());
                            break;
                        case repeating:
                            // TODO: Implement! Produce the event
                            break;
                    }
                }
            }
        };
        // Save Button Implementation:
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: Implement
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo: implement!
            }
        });
    }
    public enum RepeatType {
        everyDay, customWeek, twoWeeks, monthly, yearly
    }


}
