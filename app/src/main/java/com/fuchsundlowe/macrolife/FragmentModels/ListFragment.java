package com.fuchsundlowe.macrolife.FragmentModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.Adapters.ComplexGoal_ListAdapter;
import com.fuchsundlowe.macrolife.Adapters.ListGoal_ListAdapter;
import com.fuchsundlowe.macrolife.Adapters.RegularTask_ListAdapter;
import com.fuchsundlowe.macrolife.Adapters.RepeatingEvent_ListAdapter;
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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by macbook on 2/22/18.
 */

public class ListFragment extends Fragment implements DateAndTimeProtocol {

    private TextView label;
    private RecyclerView list;
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
        // Init of the main elements of Fragment

        number = getArguments().getInt(Constants.LIST_VIEW_TYPE_TO_DISPLAY); // Defines parameters
        // for this views style, data it loads and etc.

        // TOP BAR:
        setLabel(number);

        // CENTER BAR:
        defineListRecyclerView();
        subscribeToData(number);
        // BOTTOM BAR:
        defineNameTextField();

        buttonBar = getView().findViewById(R.id.buttonBar_TaskCreator); // Holds all buttons
        purpose = getView().findViewById(R.id.setPurpose_TaskCreatorComplex); // purpose field

        defineButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setLabel(int input) {
        label = getView().findViewById(R.id.slv_TopBarText);
        switch (input){
            case 0: label.setText(R.string.ComplexGoalTxt);
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

    // ListView implementation:

    private RecyclerView.Adapter adapter;

    private void defineListRecyclerView() {
        list = getView().findViewById(R.id.RecyclerView_ListLayout); // Finds handle to Recycler View
        list.setHasFixedSize(true); // TODO: Establish if this is even good to set?

        // Defines the layout of the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(linearLayoutManager); // Thus it shall be linear layout

        // Defines Adapter:
        switch (number) {
            case 0:
                adapter = new ComplexGoal_ListAdapter();
                list.setAdapter(adapter);
                break;
            case 1:
                adapter = new RegularTask_ListAdapter();
                list.setAdapter(adapter);
                break;
            case 2:
                adapter = new ListGoal_ListAdapter();
                list.setAdapter(adapter);
                break;
            case 3:
                adapter = new RepeatingEvent_ListAdapter();
                list.setAdapter(adapter);
                break;
        }
    }

    //TODO: Unknow if this is still needed?
    public void implementListAdapter(List<Map<String,String>> resource) {
        String fromArray[] = {"name"};
        int to[] = {R.id.textViewBase};
        SimpleAdapter adapter = new SimpleAdapter(
                getActivity(), resource, R.layout.text_layout_for_list, fromArray, to
        );

    }

    /*
    // Converter methods TODO: DO rest of the converters and implement them and change Implementation here
    private List<Map<String,String>> converterOfComplexGoals() {
        List<ComplexGoalMaster> set = StorageMaster.optionalStorageMaster().getComplexGoals().getValue();
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
    */



    // Bottom Bar implementation:

    private EditText name;
    private EditText purpose;
    private LinearLayout buttonBar;
    private Button startDateButton;
    private Button endDateButton;
    private Button doneButton;
    private Calendar startDate;
    private Calendar endDate;


    // If this view displays ComplexGoal, this will provide purpose field. Otherwise will skip it.
    public void providePurposeInput() {
        if (number==0) {
            purpose.setVisibility(View.VISIBLE);
        }
    }

    // Makes button bar container visible.
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

    // Provides implementation for displaying and softKey return when invoked...
    private void defineNameTextField() {
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

    // Defines buttons and their actions
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

    // Displays time input view and based on isStartTimeValue it defines where it will return value,
    // to either startTime or endTime.
    private void provideStartTime(boolean isStartTime) {
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        // For reporting back the values picked
        timePickerFragment.toReport = this;
        // For knowing which one to call of the values for updates
        timePickerFragment.isStartTime = isStartTime;
        timePickerFragment.show(getFragmentManager(),"TimePickerFragment");
    }

    // Displays date input view and based on isStartTimeValue it defines where it will return value,
    // to either startTime or endTime.
    private void provideDatePicker(boolean isStartTime) {
        DatePickerFragment datePicker = new DatePickerFragment();
        // For reporting back the values picked
        datePicker.toReport = this;
        // For knowing which one to call of the values for updates
        datePicker.isStartTime = isStartTime;
        datePicker.show(getFragmentManager(), "DatePickerFragment");
    }


    // Implementation for responding to done editing the new Task. TODO: Needs to dissmiss and clear input Keyboard
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

    // Updates startDate date
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
    // Updates endDate date
    @Override
    public void setEndDate(int year, int month, int day) {
        if (endDate == null) {
            endDate = Calendar.getInstance();
            endDate.set(1971,1,1,0,0,0);
            endDate.set(Calendar.MILLISECOND,0);
        }
        endDate.set(year, month, day);

    }
    // Updates startDate time
    @Override
    public void setStartTime(int hour, int minute, int second) {
        startDate.set(Calendar.HOUR, hour);
        startDate.set(Calendar.MINUTE, minute);
        startDate.set(Calendar.SECOND, second);
    }
    // Updates endDate Time
    @Override
    public void setEndTime(int hour, int minute, int second) {
        endDate.set(Calendar.HOUR, hour);
        endDate.set(Calendar.MINUTE, minute);
        endDate.set(Calendar.SECOND, second);
    }

    // Data Management part:

    private void subscribeToData(int number) {
        switch (number){
            case 0:
                dataMaster.subscribeObserver_ComplexGoal(this, new Observer<List<ComplexGoalMaster>>() {
                    @Override
                    public void onChanged(@Nullable List<ComplexGoalMaster> complexGoalMasters) {
                        if (list.getAdapter() != null) {
                            ComplexGoal_ListAdapter adapter = (ComplexGoal_ListAdapter)list.getAdapter();
                            adapter.updateDataBase(complexGoalMasters);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case 1:
                dataMaster.subscribeObserver_OrdinaryEvent(this, new Observer<List<OrdinaryEventMaster>>() {
                    @Override
                    public void onChanged(@Nullable List<OrdinaryEventMaster> ordinaryEventMasters) {
                        if (list.getAdapter() != null) {
                            RegularTask_ListAdapter adapter = (RegularTask_ListAdapter) list.getAdapter();
                            adapter.updateDatabase(ordinaryEventMasters);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case 2:
                dataMaster.subscribeObserver_ListMaster(this, new Observer<List<ListMaster>>() {
                    @Override
                    public void onChanged(@Nullable List<ListMaster> listMasters) {
                        if (list.getAdapter() != null) {
                            ListGoal_ListAdapter adapter = (ListGoal_ListAdapter) list.getAdapter();
                            adapter.updateDataBase(listMasters);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case 3:
                dataMaster.subscribeObserver_RepeatingMaster(this, new Observer<List<RepeatingEventMaster>>() {
                    @Override
                    public void onChanged(@Nullable List<RepeatingEventMaster> repeatingEventMasters) {
                        if (list.getAdapter() != null) {
                            RepeatingEvent_ListAdapter adapter = (RepeatingEvent_ListAdapter) list.getAdapter();
                            adapter.updateDataBase(repeatingEventMasters);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
        }
    }




}
