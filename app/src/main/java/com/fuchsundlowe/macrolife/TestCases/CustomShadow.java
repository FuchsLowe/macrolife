package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by macbook on 3/22/18.
 */

public class CustomShadow extends ViewOutlineProvider {
    @Override
    public void getOutline(View view, Outline outline) {
        outline.setRect(0,0, view.getWidth(), view.getHeight());
    }
}
