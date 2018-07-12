package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

// This is the master presenter of the Repeating events
public class RepeatingEventEditor extends ConstraintLayout {

    private LinearLayout leftSideHolder;
    private TextView taskName;
    private CronoViewFor_RepeatEditor dayView;
    private ScrollView dayViewHolder;
    private LinearLayout bottomBarHolder;
    private LayoutInflater inflater;
    private ConstraintLayout baseView;
    private TaskObject editedObject;
    private DataProviderNewProtocol localStorage;
    private int leftHolderWidthByPercentageOfTotalWidth = 10;
    private Button[] weekButtons;
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

        taskName = baseView.findViewById(R.id.taskName_RepeatEditor);

        leftSideHolder = baseView.findViewById(R.id.leftSideHolder_RepeatEditor);
        //int leftSideWidth = getResources().getDisplayMetrics().widthPixels / leftHolderWidthByPercentageOfTotalWidth;
        //leftSideHolder.setLayoutParams(new Constraints.LayoutParams(leftSideWidth, LayoutParams.MATCH_CONSTRAINT));


        localStorage = LocalStorage.getInstance(context);

        MIN_PADDING_BETWEEN_BUTTONS = dpToPixConverter(MIN_PADDING_BETWEEN_BUTTONS);
        MAX_BUTTON_SIZE = dpToPixConverter(MAX_BUTTON_SIZE);

        defineButtonClickListener();
    }

    // This class manages recevieng of data and infuses fields and methods with it, as layouting
    public void defineMe(TaskObject objectWeEdit) {
        editedObject = objectWeEdit;
        taskName.setText(objectWeEdit.getTaskName());
        TaskObject.Mods repeatingModWeHave = objectWeEdit.getRepeatingMod();
        //If task is set with single/repeating
        if (repeatingModWeHave == null) {
            leftSideHolder.setVisibility(GONE);
            // TODO: SHould we delete the tasks associated with repeaitng event? Like delete them if this gets reseted?
        } else if (repeatingModWeHave == TaskObject.Mods.repeating){
            defineLeftSideHolder(true);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.universal);
        } else if (repeatingModWeHave == TaskObject.Mods.repeatingMultiValues) {
            defineLeftSideHolder(false);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.monday);
        }
        defineBottomButtons();
    }
    // Determines if side bar is required, makes it visible or invisible depending on implementation
    // and defines buttons as days depending on users preference for the first day of week
    private void defineLeftSideHolder(boolean isUniversal) {
        if (isUniversal) { // meaning that there is no need for side to define specific day
            leftSideHolder.setVisibility(GONE);
        } else {
            leftSideHolder.setVisibility(VISIBLE);
            leftSideHolder.removeAllViews();

            int widthOfButton = 200;
            int heightOfButton = 0;

            weekButtons  = new Button[7];
            preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            int firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK,
                    Calendar.getInstance().getFirstDayOfWeek());
            switch (firstDayOfWeek) {
                case 1: // US System Sunday
                    for (int i = 0; i<7; i++) {
                        SideButton_RepeatEditor sideButton = new SideButton_RepeatEditor(getContext(),Constants.AMERICAN_WEEK_DAYS[i]
                                , buttonClickListener);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthOfButton,
                                heightOfButton);
                        params.weight = 1;
                        sideButton.setLayoutParams(params);
                        leftSideHolder.addView(sideButton);
                        weekButtons[i] = sideButton;
                    }
                    break;
                case 2: // European System Monday
                    for (int i = 1; i<=7; i++) {
                        SideButton_RepeatEditor sideButton = new SideButton_RepeatEditor(getContext(),Constants.EUROPEAN_WEEK_DAYS[i]
                                , buttonClickListener);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthOfButton,
                                heightOfButton);
                        params.weight = 1;
                        sideButton.setLayoutParams(params);
                        leftSideHolder.addView(sideButton);
                        weekButtons[i] = sideButton;
                    }
                    break;
            }
        }
    }
    private void defineBottomButtons() {
        bottomBarHolder.removeAllViews();
        int NUMBER_OF_BUTTONS_IN_ROW = 3; // KEEP THIS UPDATED!!!
        Point buttonValues = calculatePaddingAndButtonHeight(NUMBER_OF_BUTTONS_IN_ROW);
        for (int i = 1; i<=NUMBER_OF_BUTTONS_IN_ROW; i++) {
            ModButton mod;
            switch (i) {
                case 1:
                    mod = new ModButton(getContext(), ModButton.SpecialtyButton.delete, buttonClickListener);
                    break;
                case 2:
                    boolean repeatModNotNull = editedObject.getRepeatingMod() != null;
                    if (editedObject.getRepeatingMod() != null &&
                            editedObject.getRepeatingMod() == TaskObject.Mods.repeatingMultiValues) {
                        mod = new ModButton(getContext(), ModButton.SpecialtyButton.complex, buttonClickListener);
                    } else {
                        // produce single value
                        mod = new ModButton(getContext(), ModButton.SpecialtyButton.universal, buttonClickListener);
                    }
                    break;
                default:
                    mod = new ModButton(getContext(), ModButton.SpecialtyButton.save, buttonClickListener);
                    break;
            }
            // add space
            Space padding = new Space(getContext());
            padding.setLayoutParams(new ViewGroup.LayoutParams(buttonValues.x, buttonValues.y));
            bottomBarHolder.addView(padding);
            ViewGroup.LayoutParams parms = new ViewGroup.LayoutParams(buttonValues.y, buttonValues.y);
            mod.setLayoutParams(parms);
            bottomBarHolder.addView(mod);
        }

    }
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
                            editedObject.removeAMod(TaskObject.Mods.repeating);
                            editedObject.removeAMod(TaskObject.Mods.repeatingMultiValues);
                            // TODO: Remove the Repeating events asociated or no? How to save?
                            protocol.modDone();
                            break;
                        case save:
                            // save self and collapse to edit
                            // TODO: WHo should be in charge of saving?
                            protocol.modDone();
                            break;
                        case complex:
                            ((ModButton) v).toggleButton();
                            editedObject.addMod(TaskObject.Mods.repeating);
                            defineMe(editedObject);
                            break;
                        case universal:
                            ((ModButton) v).toggleButton();
                            editedObject.addMod(TaskObject.Mods.repeatingMultiValues);
                            defineMe(editedObject);
                            break;
                    }
                }
            }
        };
    }
}
