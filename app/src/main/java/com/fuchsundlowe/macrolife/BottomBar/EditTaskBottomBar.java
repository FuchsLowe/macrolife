package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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
import java.util.List;

// This class manages the Bottom Bar in edit task or creating a new task...
public class EditTaskBottomBar extends Fragment implements EditTaskProtocol {

    //Variables and instances:
    private DataProviderNewProtocol dataProvider;
    private BottomBarCommunicationProtocol parentProtocol;
    private ViewGroup baseView;
    private LinearLayout dynamicArea;
    private LinearLayout modAreaOne, modAreaTwo;
    private int MAX_BUTTON_SIZE = 40;
    private int MIN_PADDING_BETWEEN_BUTTONS = 10;
    private HashMap<TaskObject.Mods, ModButton> modButtons;
    private TaskObject taskObject;
    private LayoutInflater inflater;
    private ModButton.SpecialtyButton modSelected = null; // Should be either start or end
    private Context context;
    private EditTaskBottomBar self;
    private EditTaskState state;
    private EditingView_BottomBar editView;

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

        return baseView;
    }

    void defineStuff() {

        dynamicArea = baseView.findViewById(R.id.dynamicArea_editTask);
        modAreaOne = baseView.findViewById(R.id.modAreaOne_editTAsk);
        modAreaTwo = baseView.findViewById(R.id.modAreaTwo_editTask);

        // TODO: TEST Phase
        baseView.setBackgroundColor(Color.GRAY);
        dynamicArea.setBackgroundColor(Color.GREEN);
        modAreaOne.setBackgroundColor(Color.CYAN);
        modAreaTwo.setBackgroundColor(Color.YELLOW);
        // End test
    }

    @Override
    public void onStart() {
        super.onStart();
        defineStuff();
        setState(state, taskObject, parentProtocol);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //Methods:
    /* Based on data it receives it will sprung into action its looks. Return value indicates that
     * operation of creating desired View state is failure or success...
     * Must receive the taskManipulatd if setState == editTask, else returns false
     */
    public void defineMe(final EditTaskState setState, @Nullable TaskObject taskManipulated,
                         final BottomBarCommunicationProtocol parentProtocol) {
        this.state = setState;
        this.taskObject = taskManipulated;
        this.parentProtocol = parentProtocol;

    }
    private boolean setState(final EditTaskState setState, @Nullable TaskObject taskManipulated,
                            final BottomBarCommunicationProtocol parentProtocol) {
        this.parentProtocol = parentProtocol;
        switch (setState) {
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
                                setState(EditTaskState.editTask, newTaskCreated, parentProtocol);
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
                return true;
            case editTask:
                if (taskManipulated != null) {
                    taskObject = taskManipulated;
                    dynamicArea.removeAllViews();
                    dynamicArea.setVisibility(View.VISIBLE);
                    EditingView_BottomBar editView = new EditingView_BottomBar(getContext());
                    Log.d("BASE VIEW WIDH: ", baseView.getWidth() + "");
                    dynamicArea.addView(editView);
                    editView.insertData(taskManipulated, null, this);
                    editView.layout(0,0, dynamicArea.getWidth(), 80);
                    defineModButtons();
                    List<TaskObject.Mods> modsToImplement = taskManipulated.getAllMods();
                    for (TaskObject.Mods mod : modsToImplement) {
                        modButtons.get(mod).setModActive(true);
                    }
                    dynamicArea.requestLayout();
                    return true;
                } else {return false;}
            case test1:
                TestOfAlpha m = new TestOfAlpha(getContext());
                dynamicArea.addView(m);
                break;
        }
        return false;
    }
    private void defineModButtons() {
        // Should define all mods so that
        // Size; SHould have max size just in case...
        modAreaOne.setVisibility(View.VISIBLE);
        modAreaOne.removeAllViews();
        modAreaTwo.setVisibility(View.VISIBLE);
        modAreaTwo.removeAllViews();

        int NUMBER_OF_MODS_FIRST_ROW = 4;
        int NUMBER_OF_MODS_SECOND_ROW = 2;

        //===!!!MAKE SURE NUMBER OF MODS IN FIRST AND SECOND ROW == TOTAL NUMBER OF MODS!!!===//

        if (modButtons == null) {
            modButtons = new HashMap<>(NUMBER_OF_MODS_FIRST_ROW + NUMBER_OF_MODS_SECOND_ROW);
        }

        Point paddingAreaOne = calculatePaddingAndButtonHeight(NUMBER_OF_MODS_FIRST_ROW);
        Point paddingAreaTwo = calculatePaddingAndButtonHeight(NUMBER_OF_MODS_SECOND_ROW);

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
            mod.setPadding(paddingAreaOne.x, 0,0,0);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(paddingAreaOne.y,
                    paddingAreaOne.y);
            mod.setLayoutParams(layoutParams);
            modAreaOne.addView(mod);
        }

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
            mod.setPadding(paddingAreaTwo.x, 0,0,0);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(paddingAreaTwo.y, paddingAreaTwo.y);
            mod.setLayoutParams(layoutParams);
            modAreaTwo.addView(mod);
        }

    }
    private TaskObject createNewTask(String taskName) {
        TaskObject newTask = new TaskObject(0, 0, 0, taskName, Calendar.getInstance(),
                null, null, Calendar.getInstance(), TaskObject.CheckableStatus.notCheckable,
                null, 0, 0, null, TaskObject.TimeDefined.noTime);
        return newTask;
    }
    private int dpToPixConverter(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale * 0.5f);
    }
    private void deleteWarning() {
        View warningBox = inflater.inflate(R.layout.delete_warrning, null, false);
        // TODO: Make sure this values make sense. Do you need to calculate them by self?

        TextView tittle = warningBox.findViewById(R.id.tittle_deleteWarning);
        tittle.setText(R.string.Toast_Tittle_WARNING);
        TextView subtitle = warningBox.findViewById(R.id.subtitle_deleteWarning);
        subtitle.setText(R.string.Toast_Subtitle);

        final PopupWindow popupWindow = new PopupWindow(warningBox, warningBox.getWidth(), warningBox.getHeight());
        popupWindow.setFocusable(true);        // TODO: Define animation
        popupWindow.showAtLocation(baseView, Gravity.CENTER,0,0);

        Button deleteButton = warningBox.findViewById(R.id.deleteButton_deleteWarning);
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
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the whole Charade
                popupWindow.dismiss();
            }
        });
    }
    private Point calculatePaddingAndButtonHeight(int numberOfButtonsInRow) {
        // This function calculates padding between buttons in row only considering one padding parameter
        // that is the padding of the left side
        // Fist value is padding, second is button Size

        int maxCalculatedButtonSize = (modAreaOne.getWidth() -
                ((numberOfButtonsInRow + 1) * MIN_PADDING_BETWEEN_BUTTONS)) / numberOfButtonsInRow;
        int buttonSize = Math.max(MAX_BUTTON_SIZE, maxCalculatedButtonSize);

        return new Point((modAreaOne.getWidth() - (buttonSize * numberOfButtonsInRow)) /
                (numberOfButtonsInRow + 1) ,buttonSize);
    }

    //EditTaskProtocol implementation:
    @Override
    public void saveTask(TaskObject task, @Nullable RepeatingEvent event) {
        // TODO: Implement!
    }
    @Override
    public void clickOnMod(final TaskObject.Mods mod) {
        /* We Respond on mod click
         * If its one of the mods the task can have we
         */
        switch (mod) {
            case repeating:
                dynamicArea.removeAllViews();
                RepeatingEventEditor editor = new RepeatingEventEditor(getContext(), this);
                dynamicArea.addView(editor);
                editor.defineMe(taskObject);
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
                dynamicArea.addView(new ListView_CompleteMod(this.context, taskObject, this));
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.setVisibility(View.GONE);
                break;
            case checkable:
                if (editView != null) {
                    boolean isCheckable = editView.toggleCheckBoxExistance();
                    if (isCheckable) {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.incomplete);
                    } else {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.notCheckable);
                    }
                    // TODO: Save changes to taskObject
                }
                break;
            case delete:
                deleteWarning();
                break;
            case dateAndTime:
                dynamicArea.setVisibility(View.GONE);
                modAreaOne.removeAllViews();
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.removeAllViews();
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
                                    modAreaTwo.setVisibility(View.VISIBLE);
                                    modSelected = ModButton.SpecialtyButton.endValues;
                                    break;
                                case clear:
                                    // Detect which value to delete
                                    taskObject.setTimeDefined(TaskObject.TimeDefined.onlyDate);
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
                ModButton.SpecialtyButton[] firstRowButtons = {ModButton.SpecialtyButton.date,
                        ModButton.SpecialtyButton.time, ModButton.SpecialtyButton.clear};
                Point valuesFirstRow = calculatePaddingAndButtonHeight(firstRowButtons.length);
                for (ModButton.SpecialtyButton value: firstRowButtons) {
                    ModButton button = new ModButton(getContext(), value, localClickListener);
                    button.setPadding(valuesFirstRow.x, 0, 0, 0);
                    button.getLayoutParams().height = valuesFirstRow.y;
                    button.getLayoutParams().width = valuesFirstRow.y;
                    modAreaOne.addView(button);
                }
                ModButton.SpecialtyButton[] secondRow = {ModButton.SpecialtyButton.startValues,
                ModButton.SpecialtyButton.endValues, ModButton.SpecialtyButton.delete, ModButton.SpecialtyButton.clear};
                Point valuesSecondRow = calculatePaddingAndButtonHeight(secondRow.length);
                for (ModButton.SpecialtyButton value: secondRow) {
                    ModButton button = new ModButton(getContext(), value, localClickListener);
                    button.setPadding(valuesSecondRow.x,0,0,0);
                    button.getLayoutParams().width = valuesSecondRow.y;
                    button.getLayoutParams().height = valuesSecondRow.y;
                    modAreaTwo.addView(button);
                }
                break;
        }
    }

    @Override
    public void modDone() { // What we do when we have done working with a mod
        setState(EditTaskState.editTask, taskObject, parentProtocol);
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
