package com.fuchsundlowe.macrolife.Interfaces;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by macbook on 4/4/18.
 */

public interface TailViewProtocol {

    Context getContext();
    ViewGroup getContainer();
    void removeATail(View tailToBeRemoved);
    //BubbleView getParent();

}
