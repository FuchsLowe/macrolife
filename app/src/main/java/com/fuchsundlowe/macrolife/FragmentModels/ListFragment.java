package com.fuchsundlowe.macrolife.FragmentModels;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.Interfaces.DateAndTimeProtocol;
import com.fuchsundlowe.macrolife.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by macbook on 2/22/18.
 */

public class ListFragment extends Fragment implements DateAndTimeProtocol {

    private TextView label;
    private ListView list;
    private int number;
    private DataProviderProtocol dataMaster;
    // View Lifecycle:

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataMaster = StorageMaster.getInstance(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.list_scene_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        label = getView().findViewById(R.id.slv_TopBarText);
        list = getView().findViewById(R.id.TopBarSimpleListView);
        number = getArguments().getInt(Constants.LIST_VIEW_TYPE_TO_DISPLAY);
        setLabel(number);
        defineName();
        purpose = getView().findViewById(R.id.setPurpose_TaskCreatorComplex);
        buttonBar = getView().findViewById(R.id.buttonBar_TaskCreator);
        defineButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
        //providePopUp();
    }

    public void setLabel(int input) {
        switch (input){
            case 0: label.setText(R.string.ComplexGoalTxt);
           // implementListAdapter(converterOfComplexGoals());
                break;
            case 1: label.setText(R.string.OrdinaryTasksText);
                break;
            case 2: label.setText(R.string.ListTaskText);
                break;
            case 3: label.setText(R.string.RepeatingGoalText);
                break;
        }
        String res = (String) label.getText();
    }

    public void implementListAdapter(List<Map<String,String>> resource) {
        String fromArray[] = {"name"};
        int to[] = {R.id.textViewBase};
        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(), resource, R.layout.text_layout_for_list, fromArray, to
        );
        list.setAdapter(adapter);
    }

    // Converter methods TODO: DO rest of the converters and implement them
    private List<Map<String,String>> converterOfComplexGoals() {
        Set<ComplexGoalMaster> set = StorageMaster.optionalStorageMaster().getComplexGoals();
        List<Map<String, String>> toReport = new ArrayList<>();
        if (set != null) {
            for (ComplexGoalMaster goal : set) {
                Map<String, String> map = new HashMap<>();
                String name = goal.getTaskName();
                map.put("name", name);
                toReport.add(map);
            }
        }
        return toReport;
    }

    /*
    // PopUp Task Creator: TODO: Here I need to implement how will i define popUP based on Type of initiator
    private FrameLayout tempFragContainer;
    private TaskCreator_Complex complexTaskCreator;

    public void providePopUp() {
        //tempFragContainer = getView().findViewById(R.id.bottomContainer);
        //complexTaskCreator = new TaskCreator_Complex();
        //getFragmentManager().beginTransaction().add(R.id.bottomContainer, complexTaskCreator).commit();
    }
    */

    // Providing popUp Inherently...

    private EditText name;
    private EditText purpose;
    private LinearLayout buttonBar;
    private Button startDateButton;
    private Button endDateButton;
    private Button doneButton;
    private Calendar startDate;
    private Calendar endDate;



    public void providePurposeInput() {
        if (number==0) {
            purpose.setVisibility(View.VISIBLE);
        }
    }
    private void provideButtonBar() {
        buttonBar.setVisibility(View.VISIBLE);
    }

    private void enableEndTimeButton() {
        Button endTime = getView().findViewById(R.id.endTime_TaskCreator);
        endTime.setVisibility(View.VISIBLE);
    }


    public void doneEditing() {
        // Do the transition to another screen where creation of complex will be provide.
    }

    private void defineName() {
        name = getView().findViewById(R.id.setName_TaskCreatorComplex);
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    providePurposeInput();
                    provideButtonBar();
                }
                return true;
            }
        });

    }

    private void defineButtons() {
        // StartDate&Time Button:
        startDateButton = getView().findViewById(R.id.startTime_TaskCreator);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For startDateButton
            }
        });

        // EndDate&Time Button:
        endDateButton = getView().findViewById(R.id.endTime_TaskCreator);
        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For endDateButton
            }
        });

        // For Done Button:
        doneButton = getView().findViewById(R.id.done_TaskCreator);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // For Done button implementation
                doneClicked();
            }
        });
    }

    // PopUps implementation:

    private void provideStartTime(boolean isStartTime) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        // For reporting back the values picked
        timePickerFragment.toReport = this;
        // For knowing which one to call of the values for updates
        timePickerFragment.isStartTime = isStartTime;
        timePickerFragment.show(getFragmentManager(),"TimePickerFragment");
    }

    private void provideEndTime(boolean isStartTime) {
        DatePickerFragment datePicker = new DatePickerFragment();
        // For reporting back the values picked
        datePicker.toReport = this;
        // For knowing which one to call of the values for updates
        datePicker.isStartTime = isStartTime;
        datePicker.show(getFragmentManager(), "DatePickerFragment");
    }



    private void doneClicked() {
        DataProviderProtocol dataMaster = StorageMaster.optionalStorageMaster();
        String taskName = name.getText().toString();
        switch (number) {
            // Complex
            case 0:
                String taskPurpose = purpose.getText().toString();
                ComplexGoalMaster goal = new ComplexGoalMaster(0, taskName, startDate, endDate,
                        Calendar.getInstance(), false, SourceType.local, taskPurpose);
                dataMaster.insertObject(goal);
                // TODO: Open a complex creator?
                break;
            // Ordinary
            case 1:
                OrdinaryEventMaster ordinaryEventMaster = new OrdinaryEventMaster(0,taskName,
                        startDate, endDate, Calendar.getInstance(), false,
                        SourceType.local);
                dataMaster.insertObject(ordinaryEventMaster);
                break;
            // List:
            case 2:
                ListMaster listMaster = new ListMaster(0, taskName, startDate, endDate,
                        Calendar.getInstance(), false, SourceType.local);
                dataMaster.insertObject(listMaster);
                break;
            // Repeating
            case 3:
                RepeatingEventMaster repeatingEventMaster = new RepeatingEventMaster(0,
                        taskName, startDate, endDate, Calendar.getInstance(), false,
                        SourceType.local);
                dataMaster.insertObject(repeatingEventMaster);
                // TODO: Lead to Week to assign repeating tasks?
                break;
        }
    }

    @Override
    public void setStartDate(int year, int month, int day) {
        if (startDate == null) {
            // This is done to set no time value... SUbsequent checks for time should check if
            // values are 0,0,0,0 for time

            startDate = Calendar.getInstance();
            startDate.set(1971, 1,1,0,0,0);
            startDate.set(Calendar.MILLISECOND, 0);
        }
        startDate.set(year, month,day);
    }

    @Override
    public void setEndDate(int year, int month, int day) {
        if (endDate == null) {
            endDate = Calendar.getInstance();
            endDate.set(1971,1,1,0,0,0);
            endDate.set(Calendar.MILLISECOND,0);
        }
        endDate.set(year, month, day);

    }

    @Override
    public void setStartTime(int hour, int minute, int second) {
        startDate.set(Calendar.HOUR, hour);
        startDate.set(Calendar.MINUTE, minute);
        startDate.set(Calendar.SECOND, second);
    }

    @Override
    public void setEndTime(int hour, int minute, int second) {
        endDate.set(Calendar.HOUR, hour);
        endDate.set(Calendar.MINUTE, minute);
        endDate.set(Calendar.SECOND, second);
    }
}
