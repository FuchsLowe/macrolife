package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

public class NotePad extends FrameLayout {

    private EditText textArea;
    private ImageButton saveButton;
    private ImageButton deleteButton;
    private TaskObject taskManipulated;
    private EditTaskProtocol protocol;
    private LayoutInflater inflater;

    public NotePad(@NonNull Context context, TaskObject taskManipulated, EditTaskProtocol protocol) {
        super(context);
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        addView(inflater.inflate(R.layout.note_pad, null));

        this.protocol = protocol;
        this.taskManipulated = taskManipulated;
        textArea = findViewById(R.id.textArea_notePad);
        saveButton = findViewById(R.id.saveButton_notePad);
        deleteButton = findViewById(R.id.deleteButton_notePad);
        defineClickListeners();

        textArea.setText(taskManipulated.getNote());
    }

    private void defineClickListeners() {
        saveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = textArea.getText().toString();
                if (newText.length() > 0) {
                    taskManipulated.addMod(TaskObject.Mods.note);
                    taskManipulated.setNote(newText);
                } else {
                    // We have no text thus no note
                    taskManipulated.removeAMod(TaskObject.Mods.note);
                    taskManipulated.setNote("");
                    // Launch toast
                    Toast noNoteToast = Toast.makeText(getContext(), R.string.Toast_Empty_Note, Toast.LENGTH_SHORT);
                    noNoteToast.show();
                }

                protocol.saveTask(taskManipulated, null);
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send Warrning!
                deleteWarning();
            }
        });
    }
    // https://stackoverflow.com/questions/23464232/how-would-you-create-a-popover-view-in-android-like-facebook-comments
    private void deleteWarning() {
        View warrningBox = inflater.inflate(R.layout.delete_warrning, null, false);
        // TODO: Make sure this values make sense. Do you need to calculate them by self?

        TextView tittle = findViewById(R.id.tittle_deleteWarning);
        tittle.setText(R.string.Toast_Tittle_WARNING);
        TextView subtitle = findViewById(R.id.subtitle_deleteWarning);
        subtitle.setText(R.string.Toast_Subtitle);

        final PopupWindow popupWindow = new PopupWindow(warrningBox, warrningBox.getWidth(), warrningBox.getHeight());
        popupWindow.setFocusable(true);        // TODO: Define animation
        popupWindow.showAtLocation(protocol.getBaseView(), Gravity.CENTER,0,0);

        Button deleteButton = findViewById(R.id.deleteButton_deleteWarning);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // We initiate delete
                textArea.setText("");
                taskManipulated.setNote("");
                taskManipulated.removeAMod(TaskObject.Mods.note);
                popupWindow.dismiss(); // Make delayed dismiss if this path isn't working
                protocol.saveTask(taskManipulated, null); // Does this get called?
            }
        });
        Button cancelButton = findViewById(R.id.cancelButton_deleteWarning);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the whole Charade
                popupWindow.dismiss();
            }
        });
    }

}
