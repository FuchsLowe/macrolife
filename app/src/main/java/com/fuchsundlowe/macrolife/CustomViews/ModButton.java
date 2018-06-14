package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.support.annotation.Nullable;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
/*
 * Defines button to be used in EditTaskBottomBar to show Mods
 */

public class ModButton extends android.support.v7.widget.AppCompatImageButton {

    private TaskObject.Mods definedMod;
    private EditTaskProtocol protocol;

    public ModButton(Context context, TaskObject.Mods mod, EditTaskProtocol protocol) {
        super(context);
        definedMod = mod;
        this.protocol = protocol;
        // Set image for mod...
        switch (mod) {
            case list:
                break;
            case note:
                break;
            case repeating:
                break;
            case dateAndTime:
                break;
            case delete:
                break;
            case checkable:
                break;
        }
    }

    public void setModActive(boolean isActive) {
        // Define how you will set the mod to be active color...
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        protocol.clickOnMod(this.definedMod);
    }
}
