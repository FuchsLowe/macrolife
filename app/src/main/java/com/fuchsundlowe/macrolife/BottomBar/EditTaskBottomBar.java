package com.fuchsundlowe.macrolife.BottomBar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Space;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.BottomBarCommunicationProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;
import com.fuchsundlowe.macrolife.TestCases.TestOfAlpha;
import java.util.Calendar;
import java.util.HashMap;


import static com.fuchsundlowe.macrolife.BottomBar.EditTaskBottomBar.EditTaskState.editTask;

// This class manages the Bottom Bar in edit task or creating a new task... Holder of 3 Layouts
public class EditTaskBottomBar extends Fragment implements EditTaskProtocol {

    //Variables and instances:
    private DataProviderNewProtocol dataProvider;
    private BottomBarCommunicationProtocol parentProtocol;
    private ViewGroup baseView;
    private LinearLayout dynamicArea;
    private LinearLayout modAreaOne, modAreaTwo;
    private int MAX_BUTTON_SIZE = 80;
    private int MIN_PADDING_BETWEEN_BUTTONS = 10;
    private HashMap<TaskObject.Mods, ModButton> modButtons;
    private TaskObject taskObject;
    private LayoutInflater inflater;
    private ModButton.SpecialtyButton modSelected = null; // Should be either start or end
    private Context context;
    private EditTaskBottomBar self;
    private EditTaskState state;
    private EditingView_BottomBar editView;
    private int sizeOfParent;
    private Calendar startValue, endValue;
    private HashMap<ModButton.SpecialtyButton, ModButton> bottomBarButtons;

    // Lifecycle:
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        context = getContext();
        self = this;

        baseView = (ViewGroup) inflater.inflate(R.layout.edit_task_bottom_bar, container, false);

        dataProvider = LocalStorage.getInstance(getContext());

        MAX_BUTTON_SIZE = dpToPixConverter(MAX_BUTTON_SIZE);
        MIN_PADDING_BETWEEN_BUTTONS = dpToPixConverter(MIN_PADDING_BETWEEN_BUTTONS);

        dynamicArea = baseView.findViewById(R.id.dynamicArea_editTask);
        modAreaOne = baseView.findViewById(R.id.modAreaOne_editTAsk);
        modAreaTwo = baseView.findViewById(R.id.modAreaTwo_editTask);

        // TODO: TEST Phase
        baseView.setBackgroundColor(Color.GRAY);
        dynamicArea.setBackgroundColor(Color.GREEN);
        modAreaOne.setBackgroundColor(Color.CYAN);
        modAreaTwo.setBackgroundColor(Color.YELLOW);
        // End test

        return baseView;
    }
    @Override
    public void onStart() {
        super.onStart();
        setState(state);
        registerEventPickerBroadcastReceiver();
    }

    // Editing of object appearance:
    public void defineMe(final EditTaskState setState, @Nullable TaskObject taskManipulated,
                         final BottomBarCommunicationProtocol parentProtocol, int sizeToWorkWith) {
        this.state = setState;
        this.taskObject = taskManipulated;
        this.parentProtocol = parentProtocol;
        this.sizeOfParent = sizeToWorkWith;
    }
    protected void setState(EditTaskState newState){
        this.state = newState;

        switch (newState) {
            case createTask:
                baseView.removeAllViews();
                // define TextView and wait
                TextView justTextView = new TextView(getContext());
                dynamicArea.addView(justTextView);
                dynamicArea.setVisibility(View.VISIBLE);
                justTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (v.getText().length() > 0) {
                                // TODO: return this to whoever needs it...
                                TaskObject newTaskCreated = createNewTask(v.getText().toString());
                                // TODO: If this is not possible, return new object to Actviity and resend it here.
                                setState(editTask);
                            }
                            baseView.requestLayout();
                            return true;
                        }
                        return false;
                    }
                });
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.setVisibility(View.GONE);
                dynamicArea.requestLayout();
                return;
            case editTask:
                if (taskObject != null) {
                    dynamicArea.removeAllViews();
                    dynamicArea.setVisibility(View.VISIBLE);
                    editView = new EditingView_BottomBar(getContext());
                    dynamicArea.addView(editView);
                    editView.insertData(taskObject, null, this);
                    defineModButtons();
                    /* TODO:
                     * Should I Change the color of the button on possible mod? Problem is
                     * that change of color is expected when button gets clicked and having
                     * this selected will impede selection of the button. One solutuion owuld be
                     * to have 4 different states of button. One for selected w/o click and other
                     * two are unselected with w/o click.
                     *
                     * Known issue:
                     * There is no repeatingMultiValues equivalent button in the modButtons...
                     * Solution is to make a if statement that will check for that and possibly
                     * other un-defined possibilities and activate appropriate buttons in such case
                    List<TaskObject.Mods> modsToImplement = taskObject.getAllMods();
                    for (TaskObject.Mods mod : modsToImplement) {
                        modButtons.get(mod).setModActive(true);
                    }
                    */
                    dynamicArea.requestLayout();
                    return;
                } else {return;}
            case test1:
                TestOfAlpha m = new TestOfAlpha(getContext());
                dynamicArea.addView(m);
                break;
        }
    }
    /*
     * This function just defines the look of buttons, their spacing and fills the ModButton objects
     * with relevant info for put method of the ModButton class
     */
    private void defineModButtons() {
        // Should define all mods so that
        // Size; SHould have max size just in case...

        modAreaOne.removeAllViews();
        modAreaTwo.removeAllViews();

        int NUMBER_OF_MODS_FIRST_ROW = 4;
        int NUMBER_OF_MODS_SECOND_ROW = 2;

        if (NUMBER_OF_MODS_FIRST_ROW > 0) {
            modAreaOne.setVisibility(View.VISIBLE);
        }

        if (NUMBER_OF_MODS_SECOND_ROW > 0) {
            modAreaTwo.setVisibility(View.VISIBLE);
        }

        //===!!!MAKE SURE NUMBER OF MODS IN FIRST AND SECOND ROW == TOTAL NUMBER OF MODS!!!===//

        if (modButtons == null) {
            modButtons = new HashMap<>(NUMBER_OF_MODS_FIRST_ROW + NUMBER_OF_MODS_SECOND_ROW);
        }

        int[] buttonAndPaddingResults = calculatePaddingAndButtonHeight(NUMBER_OF_MODS_FIRST_ROW,
                NUMBER_OF_MODS_SECOND_ROW);
        Space space = new Space(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5);
        lp.weight = 1;
        space.setLayoutParams(lp);
        modAreaOne.addView(space);
        for (int i = 1; i <= NUMBER_OF_MODS_FIRST_ROW; i++) {
            ModButton mod;
            switch (i) {
                case 1:
                    mod = new ModButton(getContext(), TaskObject.Mods.dateAndTime, this);
                    modButtons.put(TaskObject.Mods.dateAndTime, mod);
                    break;
                case 2:
                    mod = new ModButton(getContext(), TaskObject.Mods.repeating, this);
                    modButtons.put(TaskObject.Mods.repeating, mod);
                    break;
                case 3:
                    mod = new ModButton(getContext(), TaskObject.Mods.list, this);
                    modButtons.put(TaskObject.Mods.list, mod);
                    break;
                default:
                    mod = new ModButton(getContext(), TaskObject.Mods.delete, this);
                    modButtons.put(TaskObject.Mods.delete, mod);
                    break;
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonAndPaddingResults[0], buttonAndPaddingResults[0]);
            layoutParams.gravity = 0;
            mod.setLayoutParams(layoutParams);
            modAreaOne.addView(mod);

            Space padding = new Space(getContext());
            padding.setLayoutParams(lp);
            modAreaOne.addView(padding);

        }
        Space lowerPart = new Space(getContext());
        lowerPart.setLayoutParams(lp);
        modAreaTwo.addView(lowerPart);
        for (int i = 1; i<= NUMBER_OF_MODS_SECOND_ROW; i++) {
            ModButton mod;
            switch (i) {
                case 1:
                    mod = new ModButton(getContext(), TaskObject.Mods.note, this);
                    modButtons.put(TaskObject.Mods.note, mod);
                    break;
                default:
                    mod = new ModButton(getContext(), TaskObject.Mods.checkable, this);
                    modButtons.put(TaskObject.Mods.checkable, mod);
                    break;
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(buttonAndPaddingResults[0], buttonAndPaddingResults[0]);
            layoutParams.gravity = 0;
            mod.setLayoutParams(layoutParams);
            modAreaTwo.addView(mod);

            Space padding = new Space(getContext());
            padding.setLayoutParams(lp);
            modAreaTwo.addView(padding);
        }
    }
    private TaskObject createNewTask(String taskName) {
        TaskObject newTask = new TaskObject(0, 0, 0, taskName, Calendar.getInstance(),
                null, null, Calendar.getInstance(), TaskObject.CheckableStatus.notCheckable,
                null, 0, 0, null, TaskObject.TimeDefined.noTime, "");
        return newTask;
    }
    private int dpToPixConverter(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private void presentDeleteWarning() {
        View warningBox = inflater.inflate(R.layout.delete_warrning, null, false);
        float WIDTH_BY_SCREEN_PERCENTAGE = 0.8f;
        float HEIGHT_BY_SCREEN_PERCENTAGE = 0.25f;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int calculatedWidth = (int) (displayMetrics.widthPixels * WIDTH_BY_SCREEN_PERCENTAGE);
        int calculatedHeight = (int) (displayMetrics.heightPixels * HEIGHT_BY_SCREEN_PERCENTAGE);

        TextView tittle = warningBox.findViewById(R.id.tittle_deleteWarning);
        tittle.setText(R.string.Toast_Tittle_WARNING);
        TextView subtitle = warningBox.findViewById(R.id.subtitle_deleteWarning);
        subtitle.setText(R.string.Toast_Subtitle);

        final PopupWindow popupWindow = new PopupWindow(warningBox, calculatedWidth, calculatedHeight);
        popupWindow.setFocusable(true);        // TODO: Define animation
        popupWindow.showAtLocation(baseView, Gravity.CENTER,0,0);

        Button deleteButton = warningBox.findViewById(R.id.deleteButton_deleteWarning);
        deleteButton.setText("DELETE");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // We report back to DayView to complete the deletion Progress and decide what to do
                // whith this view
                popupWindow.dismiss();
                parentProtocol.reportDeleteTask(taskObject);
            }
        });
        Button cancelButton = warningBox.findViewById(R.id.cancelButton_deleteWarning);
        cancelButton.setText("CANCEL");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the whole Charade
                popupWindow.dismiss();
            }
        });
    }
    // Returns maxSize for buttons, padding for first row and then padding for second row
    private int[] calculatePaddingAndButtonHeight(int buttonsInFirstFow, int buttonsInSecondRow) {
        int maxNumberInRows = Math.max(buttonsInFirstFow, buttonsInSecondRow);
        int maxCalculatedButtonSize = Math.min(((sizeOfParent - maxNumberInRows * MIN_PADDING_BETWEEN_BUTTONS)
                / maxNumberInRows), MAX_BUTTON_SIZE);
        int paddingRowOne = (sizeOfParent - (maxCalculatedButtonSize * buttonsInFirstFow)) /
                (buttonsInFirstFow +1);
        int paddingRowTwo = (sizeOfParent - (maxCalculatedButtonSize * buttonsInSecondRow)) /
                (buttonsInSecondRow +1);
        int[] toReturn =  {maxCalculatedButtonSize, paddingRowOne, paddingRowTwo};
        return toReturn;
    }
    // Register broadcast receiver for events from EventDatePicker
    private void registerEventPickerBroadcastReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseView.getContext());
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.START_VALUE_DONE);
        filter.addAction(Constants.END_VALUE_DONE);
        filter.addAction(Constants.TYPE_DEFINED);
        filter.addAction(Constants.TYPE_NOT_DEFINED);
        manager.registerReceiver(new BroadcastReceiver() {
            // note: This is the part that coordinates the appearance of the bottomBarButtons:
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.START_VALUE_DONE)) {
                    // the startValue is defined:
                    if (bottomBarButtons != null) {
                        // We select it to indicate that we have defined that value
                        bottomBarButtons.get(ModButton.SpecialtyButton.startValues).setSpecialtyState(true);
                        bottomBarButtons.get(ModButton.SpecialtyButton.endValues).setUnavailable(true);
                    }
                } else if (intent.getAction().equals(Constants.END_VALUE_DONE)) {
                    // the endValue is defined:
                    if (bottomBarButtons != null) {
                        // We select it to indicate that we have defined that value
                        bottomBarButtons.get(ModButton.SpecialtyButton.endValues).setSpecialtyState(true);
                        //if the save button didn't appear so far, now we can present it:
                        if (bottomBarButtons.get(ModButton.SpecialtyButton.repeating).getSpecialtyState()) {
                            bottomBarButtons.get(ModButton.SpecialtyButton.save).setVisibility(View.VISIBLE);
                        }
                    }
                } else if (intent.getAction().equals(Constants.TYPE_DEFINED)) {
                    if (bottomBarButtons != null) {
                        // Present back the buttons and remove the RepeatEventEditor
                        modAreaOne.removeAllViews();
                        modAreaOne.setVisibility(View.GONE);
                        dynamicArea.setVisibility(View.VISIBLE);
                        // We select it to indicate that we have defined that value
                        bottomBarButtons.get(ModButton.SpecialtyButton.repeating).setSpecialtyState(true);
                        //if the save button didn't appear so far, now we can present it:
                        if (bottomBarButtons.get(ModButton.SpecialtyButton.endValues).getSpecialtyState()) {
                            bottomBarButtons.get(ModButton.SpecialtyButton.save).setVisibility(View.VISIBLE);
                        }
                    }
                } else if (intent.getAction().equals(Constants.TYPE_NOT_DEFINED)) {
                    if (bottomBarButtons != null) {
                        // We un-select the repeating specialty button and remove save since we are
                        // incomplete now...
                        modAreaOne.removeAllViews();
                        modAreaOne.setVisibility(View.GONE);
                        dynamicArea.setVisibility(View.VISIBLE);
                        bottomBarButtons.get(ModButton.SpecialtyButton.repeating).setSpecialtyState(false);
                        bottomBarButtons.get(ModButton.SpecialtyButton.save).setVisibility(View.GONE);
                    }
                }
            }
        }, filter);
    }

    //EditTaskProtocol implementation:
    @Override
    public void saveTask(TaskObject task, @Nullable RepeatingEvent event) {
        dataProvider.saveTaskObject(task);
        if (event != null) {
            dataProvider.saveRepeatingEvent(event);
        }
    }
    @Override
    public void clickOnMod(final TaskObject.Mods mod) {
        /* We Respond on mod click
         * If its one of the mods the task can have we
         */
        final int screenHeight = getResources().getDisplayMetrics().heightPixels;
        switch (mod) {
            case repeating:
                /*
                 * New Implementation:
                 * So present the list of buttons and manage their functionality
                 * If called the type, report with repeating editor
                 */
                dynamicArea.removeAllViews();
                // Defining the buttons and inserting layout:
                View base = inflater.inflate(R.layout.repeating_event_base, dynamicArea, true);
                bottomBarButtons = new HashMap<>();
                bottomBarButtons.put(ModButton.SpecialtyButton.repeating, (ModButton) base.findViewById(R.id.type_editorBase));
                bottomBarButtons.put(ModButton.SpecialtyButton.save, (ModButton) base.findViewById(R.id.save_editorBase));
                bottomBarButtons.put(ModButton.SpecialtyButton.startValues, (ModButton) base.findViewById(R.id.startTime_editorBase));
                bottomBarButtons.put(ModButton.SpecialtyButton.endValues, (ModButton) base.findViewById(R.id.endTime_editorBase));
                bottomBarButtons.put(ModButton.SpecialtyButton.delete, (ModButton) base.findViewById(R.id.delete_editorBase));

                // Click listener:
                View.OnClickListener listener =  new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof ModButton) {
                            switch (((ModButton) v).reportButtonType()) {
                                case startValues:
                                    // Present the date
                                    EventDatePicker dateFragment = new EventDatePicker();
                                    startValue = (Calendar)taskObject.getTaskStartTime().clone();
                                    dateFragment.defineMe(startValue, null, getContext());
                                    dateFragment.show(self.requireFragmentManager(), "DateFragment");
                                    break;
                                case endValues:
                                    // Present the date
                                    EventDatePicker datePicker = new EventDatePicker();
                                    endValue = (Calendar) taskObject.getTaskEndTime().clone();
                                    datePicker.defineMe(endValue, startValue, getContext());
                                    datePicker.show(self.requireFragmentManager(), "DateFragment");
                                    break;
                                case repeating:
                                    // Produce Editor
                                    /*
                                     * NOTE: Since I need to be able to revert back to the start, end
                                     * and other values, I would like then to just hide the dynamic area
                                     * that shows them and present editor in maybe modArea one.
                                     * Then When I call done it will just remove the mod area one views
                                     * and will make dynamic area visible again.
                                     */
                                    dynamicArea.setVisibility(View.GONE);
                                    modAreaOne.removeAllViews();
                                    modAreaOne.setVisibility(View.VISIBLE);
                                    RepeatingEventEditor editor = new RepeatingEventEditor(getContext(), self);
                                    float howMuchShouldIOccupyScreen = 0.8f;
                                    ConstraintLayout.LayoutParams param = new ConstraintLayout.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            (int)(screenHeight * howMuchShouldIOccupyScreen)
                                    );
                                    editor.setLayoutParams(param);
                                    modAreaOne.addView(editor);
                                    editor.defineMe(taskObject);
                                    break;
                                case save:
                                    // Save new implementation
                                    taskObject.setTaskStartTime(startValue);
                                    taskObject.setTaskEndTime(endValue);
                                    dataProvider.saveTaskObject(taskObject);
                                    modDone();
                                    break;
                                case delete:
                                    /*
                                     * if the task has not set yet set the end time, then it
                                     * can be recovered... So goal is to retrieve the prior state
                                     * if possible
                                     */
                                    if (!taskObject.isThisRepeatingEvent()) {
                                        startValue = null;
                                        endValue = null;
                                        modDone();
                                    } else {
                                        // delete the other repeating events if any for this masterHash:
                                        dataProvider.deleteAllRepeatingEvents(taskObject.getHashID());
                                        taskObject.removeAMod(TaskObject.Mods.repeating);
                                        dataProvider.saveTaskObject(taskObject);
                                        modDone();
                                    }
                                    break;
                            }
                        }
                    }
                };

                for (ModButton button: bottomBarButtons.values()) {
                    button.defineMe(listener);
                }
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.setVisibility(View.GONE);
                break;
            case note:
                dynamicArea.removeAllViews();
                NotePad note = new NotePad(this.context, taskObject,this);
                dynamicArea.addView(note);
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.setVisibility(View.GONE);
                break;
            case list:
                dynamicArea.removeAllViews();
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.setVisibility(View.GONE);
                ListView_CompleteMod modToAdd = new ListView_CompleteMod(this.context, taskObject, this);
                float percentage_H = 0.6f;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)(screenHeight * percentage_H)
                );
                modToAdd.setLayoutParams(params);
                dynamicArea.addView(modToAdd);
                break;
            case checkable:
                if (editView != null) {
                    boolean isCheckable = editView.toggleCheckBoxExistance();
                    if (isCheckable) {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.incomplete);
                    } else {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.notCheckable);
                    }
                    saveTask(taskObject, null);
                }
                break;
            case delete:
                presentDeleteWarning();
                break;
            case dateAndTime:
                dynamicArea.setVisibility(View.GONE);
                modAreaOne.removeAllViews();
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.removeAllViews();
                // Defining the click listeners for buttons that are presented for this mod
                View.OnClickListener localClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v instanceof ModButton) {
                            switch (((ModButton) v).reportButtonType()) {
                                case startValues:
                                    modAreaOne.setVisibility(View.VISIBLE);
                                    modSelected = ModButton.SpecialtyButton.startValues;
                                    break;
                                case endValues:
                                    modAreaOne.setVisibility(View.VISIBLE);
                                    modSelected = ModButton.SpecialtyButton.endValues;
                                    break;
                                case clear:
                                    // Detect which value to delete
                                    if (modSelected == ModButton.SpecialtyButton.startValues) {
                                        taskObject.setTimeDefined(TaskObject.TimeDefined.noTime);
                                    } else {
                                        taskObject.setTimeDefined(TaskObject.TimeDefined.onlyDate);
                                        taskObject.setTaskEndTime(null);
                                    }
                                    dataProvider.saveTaskObject(taskObject);
                                    break;
                                case delete:
                                    // Should there be warning?
                                    taskObject.setTimeDefined(TaskObject.TimeDefined.noTime);
                                    break;
                                case time:
                                    TimePickerFragment timeFragment = new TimePickerFragment();
                                    if (modSelected == ModButton.SpecialtyButton.startValues) {
                                        timeFragment.defineMe(taskObject, self, true);
                                    } else if (modSelected == ModButton.SpecialtyButton.endValues) {
                                        timeFragment.defineMe(taskObject, self, false);
                                    }
                                    timeFragment.show(requireFragmentManager(), "TimeFragment");
                                    break;
                                case date:
                                    com.fuchsundlowe.macrolife.BottomBar.DatePickerFragment dateFragment =
                                            new com.fuchsundlowe.macrolife.BottomBar.DatePickerFragment();
                                    if (modSelected == ModButton.SpecialtyButton.startValues) {
                                        dateFragment.defineMe(taskObject, self,true);
                                    } else if (modSelected == ModButton.SpecialtyButton.endValues) {
                                        dateFragment.defineMe(taskObject, self, false);
                                    }
                                    dateFragment.show(requireFragmentManager(), "DateFragment");
                                    break;
                                case save:
                                    saveTask(taskObject, null);
                                    modDone();
                                    break;
                            }
                        }
                    }
                };

                // Defining the buttons:
                ModButton.SpecialtyButton[] firstRowButtons = {ModButton.SpecialtyButton.date,
                        ModButton.SpecialtyButton.time, ModButton.SpecialtyButton.clear};
                ModButton.SpecialtyButton[] secondRow = {ModButton.SpecialtyButton.startValues,
                        ModButton.SpecialtyButton.endValues}; //, ModButton.SpecialtyButton.delete, ModButton.SpecialtyButton.clear};
                int[] paddingAndButtonValues = calculatePaddingAndButtonHeight(firstRowButtons.length, secondRow.length);

                // Creating the first row buttons and adding them along with space for even look
                for (ModButton.SpecialtyButton value: firstRowButtons) {
                    ModButton button = new ModButton(getContext(), value, localClickListener);
                    button.setLayoutParams(new ConstraintLayout.LayoutParams(paddingAndButtonValues[0],
                            paddingAndButtonValues[0]));
                    Space space = new Space(context);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5);
                    lp.weight = 1;
                    space.setLayoutParams(lp);
                    modAreaOne.addView(space);
                    modAreaOne.addView(button);
                }
                // This one is added at the end to make everything even
                Space spaceUpper = new Space(context);
                LinearLayout.LayoutParams lpu = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5);
                lpu.weight = 1;
                spaceUpper.setLayoutParams(lpu);
                modAreaOne.addView(spaceUpper);

                // Creating the second row buttons and adding them along with space for even look
                for (ModButton.SpecialtyButton value: secondRow) {
                    ModButton button = new ModButton(getContext(), value, localClickListener);
                    //button.setPadding(paddingAndButtonValues[2],0,0,0);
                    button.setLayoutParams(new ConstraintLayout.LayoutParams(paddingAndButtonValues[0],
                            paddingAndButtonValues[0]));
                    Space space = new Space(context);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5);
                    lp.weight = 1;
                    space.setLayoutParams(lp);
                    modAreaTwo.addView(space);
                    modAreaTwo.addView(button);
                }
                Space spaceLower = new Space(context);
                LinearLayout.LayoutParams lpl = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 5);
                lpl.weight = 1;
                spaceLower.setLayoutParams(lpl);
                modAreaTwo.addView(spaceLower);
                // Done:
                break;
        }
    }
    @Override
    public void modDone() { // What we do when we have done working with a mod
        setState(editTask);
    }
    @Override
    public View getBaseView(){
        return this.baseView;
    }

    // Place for Enums:
    public enum EditTaskState {
        createTask, editTask, test1
    }
}
