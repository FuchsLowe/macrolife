package com.fuchsundlowe.macrolife.FragmentModels;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.BaseViewInterface;
import com.fuchsundlowe.macrolife.R;



/**
 * Created by macbook on 2/22/18.
 */

public class SimpleListFragment extends Fragment {

    // Lifecycle
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.simple_list_view, container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        viewPager = getView().findViewById(R.id.TopBarSimpleListView);
        pagerAdapter = new MasterPageAdapter(getFragmentManager());
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
            SLVTopBarFragment barFragment = new SLVTopBarFragment();
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
        tempFragContainer = getView().findViewById(R.id.bottomContainer);
        tempFragContainer.setVisibility(View.VISIBLE);
        complexTaskCreator = new TaskCreator_Complex();
        getFragmentManager().beginTransaction().add(R.id.bottomContainer, complexTaskCreator).commit();

    }



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
