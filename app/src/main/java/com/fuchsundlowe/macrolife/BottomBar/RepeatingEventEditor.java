package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.Image;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

// This is the master presenter of the Repeating events
public class RepeatingEventEditor extends ConstraintLayout {

    private LinearLayout leftSideHolder;
    private TextView taskName;
    private CronoViewFor_RepeatEditor dayView;
    private ScrollView dayViewHolder;
    private ConstraintLayout bottomBarHolder;
    private LayoutInflater inflater;
    private ConstraintLayout baseView;
    private LinearLayout modsHolder;
    private HashMap<TaskObject.Mods, ImageView> imageButtonModsHolder;
    private TaskObject editedObject;
    private DataProviderNewProtocol localStorage;
    private int leftHolderWidthByPercentageOfTotalWidth = 10;
    private HashMap<Integer, SideButton_RepeatEditor> weekButtons;
    private SharedPreferences preferences;
    private OnClickListener buttonClickListener;
    private int MIN_PADDING_BETWEEN_BUTTONS = 10;
    private int MAX_BUTTON_SIZE = 40;
    private EditTaskProtocol protocol;


    public RepeatingEventEditor(Context context, EditTaskProtocol protocol) {
        super(context);
        this.protocol = protocol;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        baseView = (ConstraintLayout) inflater.inflate(R.layout.repeating_event_editor, this, true);

        bottomBarHolder = baseView.findViewById(R.id.bottomBar_RepeatEditor);

        dayViewHolder = baseView.findViewById(R.id.DayViewHolder_RepeatEditor);
        dayView = new CronoViewFor_RepeatEditor(getContext());
        dayViewHolder.addView(dayView);

        modsHolder = baseView.findViewById(R.id.modsHolder_eventEditor);
        imageButtonModsHolder = new HashMap<>();
        imageButtonModsHolder.put(TaskObject.Mods.note, (ImageView) findViewById(R.id.modNote_eventEditor));
        imageButtonModsHolder.put(TaskObject.Mods.list, (ImageView) findViewById(R.id.modList_eventEditor));
        imageButtonModsHolder.put(TaskObject.Mods.repeating, (ImageView) findViewById(R.id.modUniversal_repeatEditor));
        imageButtonModsHolder.put(TaskObject.Mods.repeatingMultiValues, (ImageView) findViewById(R.id.modMultiValues_repeatEditor));

        taskName = baseView.findViewById(R.id.taskName_RepeatEditor);

        leftSideHolder = baseView.findViewById(R.id.leftSideHolder_RepeatEditor);

        weekButtons = new HashMap<>();

        localStorage = LocalStorage.getInstance(context);

        MIN_PADDING_BETWEEN_BUTTONS = dpToPixConverter(MIN_PADDING_BETWEEN_BUTTONS);
        MAX_BUTTON_SIZE = dpToPixConverter(MAX_BUTTON_SIZE);

        defineButtonClickListener();

    }

    // Methods:

    // This class manages receving of data and infuses fields and methods with it, as layouting
    public void defineMe(TaskObject objectWeEdit) {
        editedObject = objectWeEdit;
        taskName.setText(objectWeEdit.getTaskName());
        TaskObject.Mods repeatingModWeHave = objectWeEdit.getRepeatingMod();
        //If task is set with single/repeating
         if (repeatingModWeHave == null  ) {
            leftSideHolder.setVisibility(GONE);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.universal);
        } else if (repeatingModWeHave == TaskObject.Mods.repeating){
            defineLeftSideHolder(true);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.universal);
        } else if (repeatingModWeHave == TaskObject.Mods.repeatingMultiValues) {
            defineLeftSideHolder(false);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.monday);
        }
        defineBottomButtons();
        defineMods();
    }
    /*
     * Determines if side bar is required, makes it visible or invisible depending on implementation
     * and defines buttons as days depending on users preference for the first day of week
     */
    private void defineLeftSideHolder(boolean isUniversal) {
        if (isUniversal) { // meaning that there is no need for side to define specific day
            leftSideHolder.setVisibility(GONE);
        } else {
            leftSideHolder.setVisibility(VISIBLE);

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
    }
    private void defineBottomButtons() {
        // I only need to assign them values, nothing about positioning. Its defined statically!
        ModButton one = bottomBarHolder.findViewById(R.id.modButton_1_repatingEventEditor);
        one.defineMe(ModButton.SpecialtyButton.delete, buttonClickListener);

        ModButton two = bottomBarHolder.findViewById(R.id.modButton_2_repatingEventEditor);

        two.defineMe(ModButton.SpecialtyButton.universal, buttonClickListener);
        if (editedObject.getRepeatingMod() != null &&
                editedObject.getRepeatingMod() == TaskObject.Mods.repeatingMultiValues) {
            two.defineMe(ModButton.SpecialtyButton.complex, buttonClickListener);
        } else {
            // produce single value
            two.defineMe(ModButton.SpecialtyButton.universal, buttonClickListener);
        }
        ModButton three = bottomBarHolder.findViewById(R.id.modButton_3_repatingEventEditor);
        three.defineMe(ModButton.SpecialtyButton.save, buttonClickListener);
    }
    // Mod Displayer that sets mods selected state depending on the set of mods in editedTask
    private void defineMods() {
        List<TaskObject.Mods> modsTaskHave = editedObject.getAllMods();
        // Make those mods we need to implement visible and those we don't gone
        for (TaskObject.Mods mod : imageButtonModsHolder.keySet()) {
            if (modsTaskHave.contains(mod)) {
                imageButtonModsHolder.get(mod).setVisibility(VISIBLE);
            } else {
                imageButtonModsHolder.get(mod).setVisibility(GONE);
            }
        }
    }
    @Deprecated
    private void switchBetweenMods(boolean goingToMultiValues) {
        List<TaskObject.Mods> mods = editedObject.getAllMods();
        if (mods.contains(TaskObject.Mods.note)) {
            imageButtonModsHolder.get(TaskObject.Mods.note).setVisibility(VISIBLE);
        } else {
            imageButtonModsHolder.get(TaskObject.Mods.note).setVisibility(GONE);
        }

        if (mods.contains(TaskObject.Mods.list)) {
            imageButtonModsHolder.get(TaskObject.Mods.list).setVisibility(VISIBLE);
        } else {
            imageButtonModsHolder.get(TaskObject.Mods.list).setVisibility(GONE);
        }
        if (goingToMultiValues) {
            leftSideHolder.setVisibility(VISIBLE);
            imageButtonModsHolder.get(TaskObject.Mods.repeating).setVisibility(GONE);
            editedObject.removeAMod(TaskObject.Mods.repeating);

            if (isThereAnyEventForThisMod(TaskObject.Mods.repeatingMultiValues)) {
                imageButtonModsHolder.get(TaskObject.Mods.repeatingMultiValues).setVisibility(VISIBLE);
                editedObject.addMod(TaskObject.Mods.repeatingMultiValues);
                dayView.populateViewWithTasks(editedObject, DayOfWeek.monday);
            } else {
                imageButtonModsHolder.get(TaskObject.Mods.repeatingMultiValues).setVisibility(GONE);
                editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);
            }
        } else {
            leftSideHolder.setVisibility(INVISIBLE);
            imageButtonModsHolder.get(TaskObject.Mods.repeatingMultiValues).setVisibility(GONE);
            editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);

            if (isThereAnyEventForThisMod(TaskObject.Mods.repeating)) {
                imageButtonModsHolder.get(TaskObject.Mods.repeating).setVisibility(VISIBLE);
                editedObject.addMod(TaskObject.Mods.repeating);
                dayView.populateViewWithTasks(editedObject, DayOfWeek.universal);
            } else {
                imageButtonModsHolder.get(TaskObject.Mods.repeating).setVisibility(GONE);
                editedObject.removeAMod(TaskObject.Mods.repeating);
            }
        }

    }
    @Deprecated
    private Point calculatePaddingAndButtonHeight(int numberOfButtonsInRow) {
        // This function calculates padding between buttons in row only considering one padding parameter
        // that is the padding of the left side
        // Fist value is padding, second is button Size

        int maxCalculatedButtonSize = (bottomBarHolder.getWidth() -
                ((numberOfButtonsInRow + 1) * MIN_PADDING_BETWEEN_BUTTONS)) / numberOfButtonsInRow;
        int buttonSize = Math.min(MAX_BUTTON_SIZE, maxCalculatedButtonSize);
        int screenSize =(int) (getResources().getDisplayMetrics().widthPixels * 0.90f); // Temp solution, should get real size

        Point toReturn = new Point((screenSize - (buttonSize * numberOfButtonsInRow)) /
                (numberOfButtonsInRow + 1), buttonSize);

        return toReturn;
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
                    dayView.populateViewWithTasks(editedObject, ((SideButton_RepeatEditor) v).dayOfWeek);
                } else if (v instanceof ModButton) {
                    switch (((ModButton) v).reportButtonType()) {
                        case delete:
                            // The delete procedure will remove all events based on current displayed mod
                            if (leftSideHolder.getVisibility() == VISIBLE) {
                                // means that we have multi-values, xoxo cheat!!!
                                localStorage.deleteAllRepeatingEvents(editedObject.getHashID(),
                                        TaskObject.Mods.repeatingMultiValues);
                                editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);
                                // If there is repeatingEvent then we will fall back to that...
                                if (isThereAnyEventForThisMod(TaskObject.Mods.repeating)) {
                                    editedObject.addMod(TaskObject.Mods.repeating);
                                } else {
                                    editedObject.removeAMod(TaskObject.Mods.repeating);
                                }

                            } else {
                                // means that we have single repeating value
                                localStorage.deleteAllRepeatingEvents(editedObject.getHashID(),
                                        TaskObject.Mods.repeating);
                                editedObject.removeAMod(TaskObject.Mods.repeating);
                                // If there is repeatingMulti-values we will fall back to that
                                if (isThereAnyEventForThisMod(TaskObject.Mods.repeatingMultiValues)) {
                                    editedObject.addMod(TaskObject.Mods.repeatingMultiValues);
                                } else {
                                    editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);
                                }
                            }
                            if (editedObject.getRepeatingMod() == null) {
                                // This task no longer supports repeating mods and shoud therefore
                                // be moved to unassigedn tasks
                                editedObject.setTimeDefined(TaskObject.TimeDefined.noTime);
                            } else {
                                TaskObject.Mods mos = editedObject.getRepeatingMod();
                            }
                            localStorage.saveTaskObject(editedObject);
                            protocol.modDone();
                            break;
                        case save:
                            // save self and collapse to edit
                            if (leftSideHolder.getVisibility() == VISIBLE) {
                                if (isThereAnyEventForThisMod(TaskObject.Mods.repeatingMultiValues)) {
                                    editedObject.addMod(TaskObject.Mods.repeatingMultiValues);
                                } else {
                                    editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);
                                    editedObject.removeAMod(TaskObject.Mods.repeating);
                                }
                            } else {
                                if (isThereAnyEventForThisMod(TaskObject.Mods.repeating)) {
                                    editedObject.addMod(TaskObject.Mods.repeating);
                                } else {
                                    editedObject.removeAMod(TaskObject.Mods.repeating);
                                    editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);
                                }
                            }

                            localStorage.saveTaskObject(editedObject);
                            protocol.modDone();
                            // Request the ChronoView To Re-update itself
                            /*
                             * Reasoning to use this method is to prevent delay between the data being
                             * updated and reported via Live data as well as inability to effectively
                             * communicate between this class and ChronoView in reasonable manner
                             * NOTE: Currently not being used<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                             */
                            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
                            Intent reportChangesInModToContext = new Intent(Constants.INTENT_FILTER_CHANGE_IN_MOD);
                            reportChangesInModToContext.putExtra(Constants.INTENT_FILTER_FIELD_HASH_ID, editedObject.getHashID());
                            TaskObject.Mods currentMod = editedObject.getRepeatingMod();
                            if (currentMod == null) {
                                reportChangesInModToContext.putExtra(Constants.INTENT_FILTER_FIELD_MOD_TYPE, 0);
                            } else if (currentMod == TaskObject.Mods.repeating) {
                                reportChangesInModToContext.putExtra(Constants.INTENT_FILTER_FIELD_MOD_TYPE, 1);
                            } else if (currentMod == TaskObject.Mods.repeatingMultiValues) {
                                reportChangesInModToContext.putExtra(Constants.INTENT_FILTER_FIELD_MOD_TYPE, 2);

                            }

                            /*
                             * I am re-saving the children knowingly that there are no changes made
                             * to them, but rather to signal change in ChrnoView
                             */
                            localStorage.reSaveRepeatingEventsFor(editedObject.getHashID());

                            break;
                        case complex:
                            ((ModButton) v).toggleButton();
                            editedObject.addMod(TaskObject.Mods.repeating);
                            defineMe(editedObject);
                            //switchBetweenMods(false);
                            break;
                        case universal:
                            ((ModButton) v).toggleButton();
                            editedObject.addMod(TaskObject.Mods.repeatingMultiValues);
                            defineMe(editedObject);
                            //switchBetweenMods(true);
                            break;
                    }
                }
            }
        };
    }
    private boolean isThereAnyEventForThisMod(TaskObject.Mods mod) {
        return localStorage.getEventsBy(editedObject.getHashID(), mod).size() > 0;
    }
}
