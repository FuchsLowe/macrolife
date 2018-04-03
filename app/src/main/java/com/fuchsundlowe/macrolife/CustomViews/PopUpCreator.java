package com.fuchsundlowe.macrolife.CustomViews;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.fuchsundlowe.macrolife.Interfaces.PopUpProtocol;

/**
 * Created by macbook on 4/3/18.
 */

public class PopUpCreator {
    // Types it can create
    public static final int COMPLEX_TASK_ACTIVITY = 0;

    // possible fields
    private EditText name;

    private PopUpProtocol mInterface;
    private LinearLayout mParent;

    public PopUpCreator(int defineType, PopUpProtocol protocol) {
        mInterface = protocol;
        mParent = mInterface.getLinearBox();

        switch (defineType) {
            case COMPLEX_TASK_ACTIVITY:
                setComplexTaskActivity();
                break;
        }
    }


    private void setComplexTaskActivity() {
        name = new EditText(mInterface.getContext());
        mParent.addView(name);
    }
}
