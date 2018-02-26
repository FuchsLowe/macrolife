package com.fuchsundlowe.macrolife.FragmentModels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.R;

/**
 * Created by macbook on 2/22/18.
 */

public class SLVTopBarFragment extends Fragment {

    private TextView label;

    // View Lifecycle:


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        label = getView().findViewById(R.id.slv_TopBarText);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.slv_top_bar_layout, container, false);
    }

    public void setLabel(String input) {
        label.setText(input);
    }
}
