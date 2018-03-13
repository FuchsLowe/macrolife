package com.fuchsundlowe.macrolife;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.FragmentModels.ListFragment;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;


/**
 * Created by macbook on 2/22/18.
 */

public class ListView extends FragmentActivity {

    // Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataProviderProtocol dataMaster = StorageMaster.getInstance(this);
        setContentView(R.layout.list_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPager = findViewById(R.id.TopBarSimpleListView);
        pagerAdapter = new MasterPageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

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
            ListFragment barFragment = new ListFragment();
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



    // Other Methods:

    private DataProviderProtocol getDatabase() {
        return StorageMaster.getInstance(this);
    }

    @Deprecated
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
