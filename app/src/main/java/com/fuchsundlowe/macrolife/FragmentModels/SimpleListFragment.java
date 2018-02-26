package com.fuchsundlowe.macrolife.FragmentModels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.BaseViewInterface;
import com.fuchsundlowe.macrolife.R;

/**
 * Created by macbook on 2/22/18.
 */

public class SimpleListFragment extends ListFragment {

    // Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPager = getView().findViewById(R.id.TopBarSimpleListView);
        pagerAdapter = new MasterPageAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        this.setListAdapter(provideListAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.simple_list_view, container,false);
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
            SLVTopBarFragment barFragment = new SLVTopBarFragment();
            switch (position) {
                // TODO: To be replaced with internationalized strings
                case 0: barFragment.setLabel(TaskNames.ComplexGoals.getValue());
                case 1: barFragment.setLabel(TaskNames.OrdinaryTasks.getValue());
                case 2: barFragment.setLabel(TaskNames.ListTasks.getValue());
                default: barFragment.setLabel(TaskNames.RepeatingEvents.getValue());
            }
            return barFragment;
        }

        @Override
        public int getCount() {
            return COUNT_OF_ITEMS;
        }
    }

    // Center Bar:
    private ListAdapter provideListAdapter() {
        // Find out what task it is...
        int position = viewPager.getCurrentItem();

    }




    // PopUp Task Creator:


    // Local methods:

    private BaseViewInterface getDatabase() {
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
