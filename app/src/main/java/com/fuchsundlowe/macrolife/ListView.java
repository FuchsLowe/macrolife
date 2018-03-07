package com.fuchsundlowe.macrolife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.FragmentModels.DatePickerFragment;
import com.fuchsundlowe.macrolife.FragmentModels.TaskCreator_Complex;
import com.fuchsundlowe.macrolife.FragmentModels.TimePickerFragment;
import com.fuchsundlowe.macrolife.FragmentModels.TopBar_ListFragment;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;


/**
 * Created by macbook on 2/22/18.
 */

public class ListView extends FragmentActivity {

    // Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPager = findViewById(R.id.TopBarSimpleListView);
        pagerAdapter = new MasterPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        providePopUp();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    // Top Bar implementation:
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;


    private class MasterPageAdapter extends FragmentStatePagerAdapter {

        private int COUNT_OF_ITEMS = 4;

        public MasterPageAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            TopBar_ListFragment barFragment = new TopBar_ListFragment();
            Bundle tempHolder = new Bundle();
            switch (position) {
                // TODO: To be replaced with internationalized strings & handle rest functionaity
                case 0: tempHolder.putInt(Constants.LIST_VIEW_TYPE_TO_DISPLAY,0);
                break;
                case 1:  tempHolder.putInt(Constants.LIST_VIEW_TYPE_TO_DISPLAY,1);
                break;
                case 2:  tempHolder.putInt(Constants.LIST_VIEW_TYPE_TO_DISPLAY,2);
                break;
                default:  tempHolder.putInt(Constants.LIST_VIEW_TYPE_TO_DISPLAY,3);
                break;
            }
            barFragment.setArguments(tempHolder);
            return barFragment;
        }

        @Override
        public int getCount() {
            return COUNT_OF_ITEMS;
        }
    }

    // PopUp Task Creator:
    private FrameLayout tempFragContainer;
    private TaskCreator_Complex complexTaskCreator;

    public void providePopUp() {
        tempFragContainer = findViewById(R.id.bottomContainer);
        tempFragContainer.setVisibility(View.VISIBLE);
        complexTaskCreator = new TaskCreator_Complex();
        getFragmentManager().beginTransaction().add(R.id.bottomContainer, complexTaskCreator).commit();

    }

    public void provideStartTime(View view) {
        DialogFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getSupportFragmentManager(),"TimePickerFragment");
    }

    public void provideEndTime(View view) {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "DatePickerFragment");
    }

    public void doneWithInputs(View view) {

    }

    private DataProviderProtocol getDatabase() {
        return StorageMaster.optionalStorageMaster();
    }

    private enum TaskNames {
        ComplexGoals("Complex Goals"),
        OrdinaryTasks("Ordinary Tasks"),
        ListTasks("List Tasks"),
        RepeatingEvents("Repeating Events");

        private final String value;

        TaskNames(String value) {
            this.value = value;
        }
        String getValue() {
            return value;
        }
    }

}
