package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
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
        saveButton.setImageResource(R.drawable.save_24px);
        deleteButton = findViewById(R.id.deleteButton_notePad);
        deleteButton.setImageResource(R.drawable.delete_24px);
        defineClickListeners();

        textArea.setText(taskManipulated.getNote());
        defineTextArea(Resources.getSystem().getDisplayMetrics().heightPixels);
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
                protocol.modDone();
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send Warning!
                deleteWarning();
            }
        });
    }
    // https://stackoverflow.com/questions/23464232/how-would-you-create-a-popover-view-in-android-like-facebook-comments
    private void deleteWarning() {
        View warningBox = inflater.inflate(R.layout.delete_warrning, null, false);
        float WIDTH_BY_SCREEN_PERCENTAGE = 0.8f;
        float HEIGHT_BY_SCREEN_PERCENTAGE = 0.25f;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int calculatedWidth = (int) (displayMetrics.widthPixels * WIDTH_BY_SCREEN_PERCENTAGE);
        int calculatedHeight = (int) (displayMetrics.heightPixels * HEIGHT_BY_SCREEN_PERCENTAGE);

        TextView tittle = warningBox.findViewById(R.id.tittle_deleteWarning);
        tittle.setText(R.string.Toast_Tittle_WARNING);
        TextView subtitle = warningBox.findViewById(R.id.subtitle_deleteWarning);
        subtitle.setText(R.string.Toast_Subtitle);

        final PopupWindow popupWindow = new PopupWindow(warningBox, calculatedWidth, calculatedHeight);
        popupWindow.setFocusable(true);        // TODO: Define animation
        popupWindow.showAtLocation(protocol.getBaseView(), Gravity.CENTER,0,0);

        Button deleteButton = warningBox.findViewById(R.id.deleteButton_deleteWarning);
        deleteButton.setText("DELETE");
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // We initiate delete
                textArea.setText("");
                taskManipulated.setNote("");
                taskManipulated.removeAMod(TaskObject.Mods.note);
                popupWindow.dismiss(); // Make delayed dismiss if this path isn't working
                protocol.saveTask(taskManipulated, null);

                // Now we need to close the note... And return to regular edit...
                protocol.modDone();
            }
        });
        Button cancelButton = warningBox.findViewById(R.id.cancelButton_deleteWarning);
        cancelButton.setText("CANCEL");
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the whole Charade
                popupWindow.dismiss();
            }
        });
    }

    private void defineTextArea(int screenHeight) {
        // Define size to be 60% of the screen height
        float HEIGHT_VALUE = 0.5f;
        textArea.getLayoutParams().height = (int) (screenHeight * HEIGHT_VALUE);
    }
}
