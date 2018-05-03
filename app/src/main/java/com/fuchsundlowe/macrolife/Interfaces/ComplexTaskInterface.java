package com.fuchsundlowe.macrolife.Interfaces;

import android.content.Context;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;

/**
 * Created by macbook on 3/26/18.
 */

public interface ComplexTaskInterface {
    float getScale();
    void stopChangesToLayoutTemp();
    Context getContext();
    ComplexTaskChevron findChevWithID(int iDentification);
}
