package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.Interfaces.PopUpProtocol;
import java.util.Calendar;
/*
 * The Pseudo-Code:
 * Get Initiated and displays demanded view
 * Can transition from one view to other
 *
 */
@Deprecated
public class PopUpCreator {
    // Types it can create
    public static final int COMPLEX_TASK_ACTIVITY = 0;
    private int DEFAULT_SPAWN_X = 20;
    private int DEFAULT_SPAWN_Y = 20;

    private EditText name;
    private PopUpProtocol mInterface;
    private LinearLayout mParent;
    private LinearLayout holder;
    private Point clickLocation;
    private Calendar startT;
    private Calendar endT;

    // Public constructor:
    public PopUpCreator(int defineType, PopUpProtocol protocol) {
        mInterface = protocol;
        mParent = mInterface.getLinearBox();

        switch (defineType) {
            case COMPLEX_TASK_ACTIVITY:
                setComplexTaskActivity();
                break;
        }
    }
    // Method calls:
    // Called when we are creating the tab for complex task activity
    private void setComplexTaskActivity() {
        // Basically this just sets implemnetation for name field
        name = new EditText(mInterface.getContext());
        name.setHint("New Task");
        name.setSingleLine(true);
        name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        if (clickLocation == null) {
                            // TODO: This should be done by this class
                            mInterface.newTask(v.getText().toString(), null, null,
                                    DEFAULT_SPAWN_X, DEFAULT_SPAWN_Y, 0);
                        } else {
                            mInterface.newTask(v.getText().toString(),
                                    null, null,
                                    clickLocation.x, clickLocation.y, 0
                                    );
                        }
                    }
                    softKeyboard(false);
                    mInterface.globalEditDone();
                }
                return true;
            }
        });

        mParent.addView(name);
    }
    // This method edits the existing Chevron object from ComplexActivity like Activity
    public void editChevronInComplexActivity(final ComplexTaskChevron chevronToEdit) {
        // Adds the start and end time mods
        if (holder == null) {
            name.setText(chevronToEdit.getTaskName());
            holder = new LinearLayout(mInterface.getContext());
            holder.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.setLayoutParams(parms);

            Button dateInvokerButton = new Button(mInterface.getContext());
            dateInvokerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.setBackgroundColor(Color.BLUE);
                }
            });
            dateInvokerButton.setText("Date");

            Button timeInvokerButton = new Button(mInterface.getContext());
            timeInvokerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            timeInvokerButton.setText("Time");

            mParent.addView(holder);

            ViewGroup.LayoutParams buttonParams = new ViewGroup.LayoutParams(mParent.getWidth() / 2,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            dateInvokerButton.setLayoutParams(buttonParams);
            timeInvokerButton.setLayoutParams(buttonParams);

            holder.addView(dateInvokerButton);
            holder.addView(timeInvokerButton);

            // TODO: here we define the Boolean for defining if the task is done...

            final Button doneButton = new Button(mInterface.getContext());
            doneButton.setText("DONE");
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (name.length() > 0) {
                        if (clickLocation != null) {
                            chevronToEdit.setNewValues(name.getText().toString(),
                                    clickLocation.x, clickLocation.y,
                                    null, null);
                            clickLocation = null;
                        } else {
                            chevronToEdit.setNewValues(name.getText().toString(),
                                    null, null,
                                    null, null);
                        }
                    }
                    releaseFields();
                    softKeyboard(false);
                    mInterface.globalEditDone();
                }
            });
            mParent.addView(doneButton);

            final Button delete = new Button(mInterface.getContext());
            delete.setText("DELETE");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chevronToEdit.animationDestroy();
                    mInterface.removeViewFromContainer(chevronToEdit);
                    releaseFields();
                    mInterface.globalEditDone();
                }
            });
            mParent.addView(delete);
        } else {
            name.setText(chevronToEdit.getTaskName());
        }

    }
    // Called when we need to relaese editing fields
    public void releaseFields() {
        while (mParent.getChildCount() > 1) {
            mParent.removeViewAt(1);
        }
        clickLocation = null;
        holder = null;
        startT = null;
        endT = null;

        name.setText("");
        name.setHint("New Task");
    }
    // Stores new data for creation of new Chevron at clicked location
    public void setNewTask(float x, float y) {
        name.setText("");
        softKeyboard(true);
        clickLocation = new Point((int)x, (int)y);
    }
    // This method manages appearance and disappearance of the soft keyboard
    public void softKeyboard(boolean appearance) {
        InputMethodManager imm = (InputMethodManager)
                mInterface.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (appearance) {
            name.requestFocus();
            imm.showSoftInput(name, 0);
        } else {
            name.clearFocus();
            imm.hideSoftInputFromInputMethod(name.getWindowToken(),0);

        }
    }

}
