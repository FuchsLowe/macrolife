package com.fuchsundlowe.macrolife.Interfaces;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * Created by macbook on 4/3/18.
 */

public interface PopUpProtocol {

    LinearLayout getLinearBox();
    Context getContext();
    void newTask(String name, Calendar start, Calendar end, Integer x, Integer y, int updateKey);
    AppCompatActivity getActivity();
    void globalEditDone();

}
