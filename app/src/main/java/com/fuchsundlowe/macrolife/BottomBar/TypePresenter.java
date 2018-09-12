package com.fuchsundlowe.macrolife.BottomBar;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TypePresenter extends FrameLayout {


    public TypePresenter(@NonNull Context context) {
        super(context);
    }
    public TypePresenter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public TypePresenter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public TypePresenter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void defineMe(RepeatingEventEditor.RepeatType type) {
        TextView typeName = findViewById(R.id.textView_typePresenter);
        Context c = getContext();
        switch (type) {
            case everyDay:
                typeName.setText(c.getString(R.string.daily));
                break;
            case customWeek:
                typeName.setText(c.getString(R.string.weekCustom));
                break;
            case twoWeeks:
                typeName.setText(c.getString(R.string.twoWeeks));
                break;
            case monthly:
                typeName.setText(c.getString(R.string.monthly));
                break;
            case yearly:
                typeName.setText(c.getString(R.string.yearly));
                break;
        }
    }

}
