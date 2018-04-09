package com.fuchsundlowe.macrolife.CustomViews;

import android.graphics.Canvas;
import android.view.View;

import com.fuchsundlowe.macrolife.Interfaces.ConnectorViewProtocol;


public class ConnectorView extends View {

    ConnectorViewProtocol mInterface;

    public ConnectorView(ConnectorViewProtocol protocol) {
        super(protocol.getContext());
        mInterface = protocol;
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

}
