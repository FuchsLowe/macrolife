package com.fuchsundlowe.macrolife.CustomViews;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.R;
import java.util.Calendar;

// This is the master presenter of the Repeating events
public class RepeatingEventEditor extends ConstraintLayout {

    private LinearLayout leftSideHolder;
    private TextView taskName;
    private DayView_RepeatEditor dayView;
    private ScrollView dayViewHolder;
    private LinearLayout bottomBarHolder;
    private LayoutInflater inflater;
    private ConstraintLayout baseView;
    private TaskObject editedObject;
    private DataProviderNewProtocol localStorage;
    private int leftHolderWidthByPercentageOfTotalWidth = 10;
    private Button[] weekButtons;
    private SharedPreferences preferences;
    private OnClickListener buttonClickListener;


    public RepeatingEventEditor(Context context) {
        super(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        baseView = (ConstraintLayout) inflater.inflate(R.layout.repeating_event_editor, this, false);

        bottomBarHolder = baseView.findViewById(R.id.bottomBar_RepeatEditor);

        dayViewHolder = baseView.findViewById(R.id.DayViewHolder_RepeatEditor);
        dayView = new DayView_RepeatEditor(getContext());
        dayViewHolder.addView(dayView);

        taskName = baseView.findViewById(R.id.taskName_RepeatEditor);

        leftSideHolder = baseView.findViewById(R.id.leftSideHolder_RepeatEditor);

        localStorage = LocalStorage.getInstance(context);

        defineButtonClickListener();
        defineBottomButtons();
    }

    public void defineMe(TaskObject objectWeEdit) {
        editedObject = objectWeEdit;
        TaskObject.Mods repeatingModWeHave = objectWeEdit.getRepeatingMod();
        //If task is set with single/repeating
        if (repeatingModWeHave == null) {
            leftSideHolder.setVisibility(GONE);
            // TODO: SHould we delete the tasks associated with repeaitng event?
        } else if (repeatingModWeHave == TaskObject.Mods.repeating){
            defineLeftSideHolder(true);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.universal);
        } else if (repeatingModWeHave == TaskObject.Mods.repeatingMultiValues) {
            defineLeftSideHolder(false);
            dayView.populateViewWithTasks(objectWeEdit, DayOfWeek.monday);
        }
    }

    private void defineLeftSideHolder(boolean isUniversal) {
        if (isUniversal) {
            leftSideHolder.setVisibility(GONE);
        } else {
            leftSideHolder.setVisibility(VISIBLE);
            int widthOfButton = this.getWidth() / leftHolderWidthByPercentageOfTotalWidth;
            int heightOfButton = this.getHeight() / 7; // because we have 7 days in week
            weekButtons  = new Button[7];
            preferences = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
            int firstDayOfWeek = preferences.getInt(Constants.FIRST_DAY_OF_WEEK,
                    Calendar.getInstance().getFirstDayOfWeek());
            switch (firstDayOfWeek) {
                case 1: // US System Sunday
                    for (int i = 1; i<=7; i++) {
                        SideButton_RepeatEditor sideButton = new SideButton_RepeatEditor(getContext(),Constants.AMERICAN_WEEK_DAYS[i]
                                , buttonClickListener);
                        ViewGroup.LayoutParams params = sideButton.getLayoutParams();
                        params.height = heightOfButton;
                        params.width = widthOfButton;
                        sideButton.setLayoutParams(params);
                        leftSideHolder.addView(sideButton);
                        weekButtons[i] = sideButton;
                    }
                    break;
                case 2: // European System Monday
                    for (int i = 1; i<=7; i++) {
                        SideButton_RepeatEditor sideButton = new SideButton_RepeatEditor(getContext(),Constants.EUROPEAN_WEEK_DAYS[i]
                                , buttonClickListener);
                        ViewGroup.LayoutParams params = sideButton.getLayoutParams();
                        params.height = heightOfButton;
                        params.width = widthOfButton;
                        sideButton.setLayoutParams(params);
                        leftSideHolder.addView(sideButton);
                        weekButtons[i] = sideButton;
                    }
                    break;
            }
        }
    }
    private void defineBottomButtons() {

    }
    private void defineButtonClickListener() {
        buttonClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof SideButton_RepeatEditor) {
                    dayView.populateViewWithTasks(editedObject, ((SideButton_RepeatEditor) v).dayOfWeek);
                }
            }
        };
    }
}
