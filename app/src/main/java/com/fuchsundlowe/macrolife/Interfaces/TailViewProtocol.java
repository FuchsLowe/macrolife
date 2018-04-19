package com.fuchsundlowe.macrolife.Interfaces;

import android.content.Context;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.CustomViews.BubbleView;

/**
 * Created by macbook on 4/4/18.
 */

public interface TailViewProtocol {

    Context getContext();
    void displayText(int val);
    ViewGroup getContainer();
    //BubbleView getParent();

}
