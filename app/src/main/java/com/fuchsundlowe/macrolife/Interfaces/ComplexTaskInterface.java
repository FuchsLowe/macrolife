package com.fuchsundlowe.macrolife.Interfaces;

import android.content.Context;

import com.fuchsundlowe.macrolife.DepreciatedClasses.ComplexTaskChevron;

/**
 * Created by macbook on 3/26/18.
 */

public interface ComplexTaskInterface {
    Context getContext();
    ComplexTaskChevron findChevWithID(int iDentification);
}
