package com.fuchsundlowe.macrolife.CustomViews;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.Chevronable;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;
import java.util.HashMap;


public class EditTaskBottomBar extends Fragment implements EditTaskProtocol {

    //Variables and instances:
    private DataProviderNewProtocol dataProvider;
    private ViewGroup baseView;
    private FrameLayout dynamicArea;
    private LinearLayout modAreaOne, modAreaTwo;
    private int MAX_BUTTON_SIZE = 40;
    private int MIN_PADDING_BETWEEN_BUTTONS = 10;
    private HashMap<TaskObject.Mods, ModButton> modButtons;
    private TaskObject taskObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseView = (ViewGroup) inflater.inflate(R.layout.edit_task_bottom_bar, container, false);
        dynamicArea = baseView.findViewById(R.id.dynamicArea_editTask);
        modAreaOne = baseView.findViewById(R.id.modAreaOne_editTAsk);
        modAreaTwo = baseView.findViewById(R.id.modAreaTwo_editTask);

        dataProvider = LocalStorage.getInstance(getContext());

        MAX_BUTTON_SIZE = dpToPixConverter(MAX_BUTTON_SIZE);
        MIN_PADDING_BETWEEN_BUTTONS = dpToPixConverter(MIN_PADDING_BETWEEN_BUTTONS);

        return baseView;
    }

    //Methods:
    /* Based on data it receives it will sprung into action its looks. Return value indicates that
     * operation of creating desired View state is failure or success...
     * Must receive the taskManipulatd if setState == editTask, else returns false
     */
    public boolean setState(final EditTaskState setState, @Nullable TaskObject taskManipulated) {
        switch (setState) {
            case createTask:
                baseView.removeAllViews();
                // define TextView and wait
                TextView justTextView = new TextView(getContext());
                dynamicArea.addView(justTextView);
                justTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            if (v.getText().length() > 0) {
                                // TODO: return this to whoever needs it...
                                TaskObject newTaskCreated = createNewTask(v.getText().toString());
                                // TODO: If this is not possible, return new object to Actviity and resend it here.
                                setState(EditTaskState.editTask, newTaskCreated);
                            }
                            return true;
                        }
                        return false;
                    }
                });
                modAreaOne.setVisibility(View.GONE);
                modAreaTwo.setVisibility(View.GONE);
                return true;
            case editTask:
                if (taskManipulated != null) {
                    taskObject = taskManipulated;
                    dynamicArea.removeAllViews();
                    EditingView_BottomBar editView = new EditingView_BottomBar(getContext());
                    dynamicArea.addView(editView);
                    editView.insertData(taskManipulated, null, this);
                    defineModButtons();
                    TaskObject.Mods[] modsToImplement = taskManipulated.getAllMods();
                    for (TaskObject.Mods mod : modsToImplement) {
                        modButtons.get(mod).setModActive(true);
                    }
                    return true;
                } else {return false;}
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

        int maxCalculatedButtonSize = (modAreaOne.getWidth() -
                ((NUMBER_OF_MODS_FIRST_ROW + 1) * MIN_PADDING_BETWEEN_BUTTONS)) / NUMBER_OF_MODS_FIRST_ROW;
        int buttonSize = Math.max(MAX_BUTTON_SIZE, maxCalculatedButtonSize);

        if (modButtons == null) {
            modButtons = new HashMap<>(NUMBER_OF_MODS_FIRST_ROW + NUMBER_OF_MODS_SECOND_ROW);
        }

        int paddingAreaOne = (modAreaOne.getWidth() - (buttonSize * NUMBER_OF_MODS_FIRST_ROW)) /
                (NUMBER_OF_MODS_FIRST_ROW + 1);
        int paddingAreaTwo = (modAreaTwo.getWidth() - (buttonSize * NUMBER_OF_MODS_SECOND_ROW)) /
                (NUMBER_OF_MODS_SECOND_ROW + 1);

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
            mod.setPadding(paddingAreaOne, 0,0,0);
            ViewGroup.LayoutParams layoutParams = mod.getLayoutParams();
            layoutParams.height = buttonSize;
            layoutParams.width = buttonSize;
            mod.setLayoutParams(layoutParams);
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
            mod.setPadding(paddingAreaTwo, 0,0,0);
            ViewGroup.LayoutParams layoutParams = mod.getLayoutParams();
            layoutParams.height = buttonSize;
            layoutParams.width = buttonSize;
            mod.setLayoutParams(layoutParams);
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

    //EditTaskProtocol implementation:
    @Override
    public void saveTask(TaskObject task, @Nullable RepeatingEvent event) {
        // TODO: Implementation depends on the system...
    }
    @Override
    public void clickOnMod(TaskObject.Mods mod) {
        /* We Respond on mod click
         * If its one of the mods the task can have we
         */
        switch (mod) {
            case repeating:
                /*
                 * Replaces flexibleArea with Repeating Master
                 * Collapses modAreaTwo
                 * replaces the items with its desired buttons
                 */
                break;
            case note:
                /*
                 * Replaces flexible Area with big TextEditor
                 * Colapses modAreaOne
                 * replaces the items with its desired buttons
                 */
                break;
            case list:
                /* TODO: Can this be used accross the platform? To reuse this functionality?
                 * Replaces flexibleArea with List view
                 * Collapese modAreaTwo
                 * replaces the items with its desired buttons
                 */
                break;
            case checkable:
                /*
                 * Toggles the checkBox Status and reports the new change to taskObject
                 */
                View editingView = dynamicArea.getChildAt(0);
                if (editingView instanceof EditingView_BottomBar) {
                    boolean isCheckable = ((EditingView_BottomBar) editingView).toggleCheckBoxExistance();
                    if (isCheckable) {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.incomplete);
                    } else {
                        taskObject.setIsTaskCompleted(TaskObject.CheckableStatus.notCheckable);
                    }
                    // TODO: Save changes to taskObject
                }
                break;
            case delete:
                /*
                 * Repalces the flexible area with Warning label
                 * Collapses modAreaTwo
                 * repalces the items with Cancel and Delete Buttons
                 */
                break;
            case dateAndTime:
                /*
                 * Replaces the flexible area with date and time chooser
                 * collapses modAreaTwo
                 * replaces the items with its buttons
                 */
                break;
        }
    }

    // Place for Enums:
    public enum EditTaskState {
        createTask, editTask
    }
}
