package com.fuchsundlowe.macrolife.CustomViews;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuchsundlowe.macrolife.R;

/**
 * Created by macbook on 2/26/18. TODO: Probably not gonna use it
 */

public class ComplexTaskCreatorPopUp extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_creator__complex,container);
    }
}
