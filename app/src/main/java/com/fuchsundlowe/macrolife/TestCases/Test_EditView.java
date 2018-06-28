package com.fuchsundlowe.macrolife.TestCases;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.R;

public class Test_EditView extends FrameLayout {
    public Test_EditView(@NonNull Context context) {
        super(context);
        View base = inflate(context, R.layout.test_edit_task, this);

    }

}
