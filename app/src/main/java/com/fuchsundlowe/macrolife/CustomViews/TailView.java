package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.TailViewProtocol;


public class TailView extends View {

    TailViewProtocol mInterface;

    public TailView(TailViewProtocol protocol) {
        super(protocol.getContext());
        mInterface = protocol;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Here we draw the lines...
    }

}
