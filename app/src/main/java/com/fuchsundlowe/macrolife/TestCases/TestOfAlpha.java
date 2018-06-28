package com.fuchsundlowe.macrolife.TestCases;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fuchsundlowe.macrolife.R;

public class TestOfAlpha extends LinearLayout{

    ConstraintLayout baseView;

    public TestOfAlpha(Context context) {
        super(context);
        inflate(context, R.layout.edit_task,this);
    }
}
