package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
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
    private void setComplexTaskActivity() {
        name = new EditText(mInterface.getContext());
        name.setHint("New Task");
        name.setSingleLine(true);
        name.setImeOptions(EditorInfo.IME_ACTION_DONE);
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        mInterface.newTask(v.getText().toString(), null, null,
                                DEFAULT_SPAWN_X, DEFAULT_SPAWN_Y, 0);
                    }
                }
                return true;
            }
        });

        mParent.addView(name);
    }

    public void editChevronInComplexActivity(final ComplexTaskChevron object) {

        // Adds the start and end time modificators
        if (holder == null) {
            name.setText(object.getTaskName());
            holder = new LinearLayout(mInterface.getContext());
            holder.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.setLayoutParams(parms);

            Button a = new Button(mInterface.getContext());
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.setBackgroundColor(Color.BLUE);
                }
            });
            a.setText("Date");

            Button b = new Button(mInterface.getContext());
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            b.setText("Time");

            mParent.addView(holder);

            ViewGroup.LayoutParams buttonParams = new ViewGroup.LayoutParams(mParent.getWidth() / 2,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            a.setLayoutParams(buttonParams);
            b.setLayoutParams(buttonParams);

            holder.addView(a);
            holder.addView(b);

            // TODO: here we define the Boolean for defining if the task is done...

            final Button doneButton = new Button(mInterface.getContext());
            doneButton.setText("DONE");
            doneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (name.length() > 0) {
                        object.setNewValues(name.getText().toString(),null, null,
                                null,null );
                    }
                    releaseFields();
                }
            });
            mParent.addView(doneButton);

            final Button delete = new Button(mInterface.getContext());
            delete.setText("DELETE");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    object.animationDestroy();
                    releaseFields();
                }
            });
            mParent.addView(delete);
        } else {
            name.setText(object.getTaskName());
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

    public void setNewTask(float x, float y) {
        name.setText("");
        InputMethodManager imm = (InputMethodManager)
                mInterface.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        Log.d("Result", String.valueOf(name.requestFocus()));
        imm.showSoftInput(name, 0);
        clickLocation = new Point((int)x, (int)y);

    }

}
