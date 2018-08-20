package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

import static com.fuchsundlowe.macrolife.BottomBar.ModButton.SpecialtyButton.complex;
/*
 * Defines button to be used in EditTaskBottomBar to show Mods
 */

public class ModButton extends android.support.v7.widget.AppCompatImageButton {

    private TaskObject.Mods definedMod;
    private EditTaskProtocol protocol;
    private SpecialtyButton buttonType;


    // Layout constructors
    public ModButton(Context context) {
        super(context);
    }
    public ModButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ModButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // Dynamic constructor, when item is not defined in layout
    public ModButton(Context context, TaskObject.Mods mod, final EditTaskProtocol protocol) {
        super(context);
        defineMe(mod, protocol);
    }
    // For Specialty button implementation
    public ModButton(Context context, SpecialtyButton buttonType, OnClickListener listener) {
        super(context);
        defineMe(buttonType, listener);
    }

    public void defineMe(TaskObject.Mods mod, final EditTaskProtocol protocol) {
        definedMod = mod;
        this.protocol = protocol;
        // Set image for mod...
        switch (mod) {
            case list:
                setImageResource(R.drawable.list_alt_24px);
                break;
            case note:
                setImageResource(R.drawable.note_add_24px);
                break;
            case repeating:
                setImageResource(R.drawable.repeat_24px);
                break;
            case dateAndTime:
                setImageResource(R.drawable.date_range_24px);
                break;
            case delete:
                setImageResource(R.drawable.delete_24px);
                break;
            case checkable:
                setImageResource(R.drawable.check_circle_24px);
                break;
        }

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                protocol.clickOnMod(definedMod);
            }
        });
    }
    public void defineMe(SpecialtyButton buttonType, OnClickListener listener) {
        this.buttonType = buttonType;
        setOnClickListener(listener);

        // Defining the button icon:
        switch (buttonType) {
            case universal:
                setImageResource(R.drawable.repeat_one_24px);
                break;
            case save:
                setImageResource(R.drawable.save_24px);
                break;
            case date:
                setImageResource(R.drawable.date_range_24px);
                break;
            case time:
                setImageResource(R.drawable.timer_24px);
                break;
            case delete:
                setImageResource(R.drawable.delete_24px);
                break;
            case clear:
                setImageResource(R.drawable.clear_24px);
                break;
            case startValues:
                setImageResource(R.drawable.start_24px);
                break;
            case endValues:
                setImageResource(R.drawable.end_24px);
                break;
            case complex:
                setImageResource(R.drawable.repeat_24px);
                break;
        }
    }


    // Methods:
    public void setModActive(boolean isActive) {
        // Define how you will set the mod to be active color...
    }
    public void toggleButton() {
        // TODO: Switch the image
        switch (buttonType) {
            case universal:
                setImageResource(R.drawable.repeat_one_24px);
                buttonType = SpecialtyButton.universal;
                break;
            case complex:
                setImageResource(R.drawable.repeat_24px);
                buttonType = complex;
        }
    }
    public SpecialtyButton reportButtonType() {
        return this.buttonType;
    }
    public enum SpecialtyButton {
        delete, save, universal, complex,
        startValues, endValues, date, time, clear
    }

}
