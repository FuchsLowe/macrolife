package com.fuchsundlowe.macrolife;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.fuchsundlowe.macrolife.CustomViews.SimpleChronoView;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventsChild;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.FragmentModels.TopBarFragment_DayView;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.R;
import com.fuchsundlowe.macrolife.SupportClasses.ZoomOut_PageTransformer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides day view fragment for app. Main features expected here are:
 *  Chronological view of the daily duties.
 *  Requests information to fill in this specific day based on day atribute.
 */
public class DayView extends FragmentActivity {

    private ScrollView center;
    private ViewPager topBar;
    private PagerAdapter adapter;
    private DataProviderProtocol dataMaster;

    public DayView() {
        // Required empty public constructor
        dataMaster = StorageMaster.getInstance(this);
        // TODO: Temp solution, will always go back to today's day
        currentDay = new DayHolder(Calendar.getInstance(), dataMaster);

    }



    // Life-cycle events:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We are expecting a passed day in form of long to be delivered
        if (savedInstanceState != null) {
            this.setMetaData(savedInstanceState.getLong(Constants.DAY_TO_DISPLAY));
        }
        setContentView(R.layout.day_layout);

    }


    @Override
    public void onStart() {
        super.onStart();
        initiateCenterBar();
        initiateTopBar();
    }




    // TopBar Implementation:
    private void initiateTopBar() {
        if (topBar == null) {
            topBar = (ViewPager) findViewById(R.id.top_bar);
        }

        adapter = new PageAdapterMaster(getSupportFragmentManager());
        topBar.setAdapter(adapter);
        topBar.setPageTransformer(true, new ZoomOut_PageTransformer());
        topBar.setCurrentItem(30);
    }

    private class PageAdapterMaster extends FragmentStatePagerAdapter {
        private int NUM_OF_VIEWS  = 60;
        public PageAdapterMaster(FragmentManager fm) {
            super(fm);
        }
        private Integer currentValue;
        private Calendar startTime;
        @Override
        public Fragment getItem(int position) {
            if (currentValue == null) {
                currentValue = position;
                TopBarFragment_DayView barFragment = new TopBarFragment_DayView();
                barFragment.setDay(currentDay.thisDay);
                startTime = currentDay.thisDay;
                return barFragment;
            } else {
                TopBarFragment_DayView barFragment = new TopBarFragment_DayView();
                Calendar tempHolder = Calendar.getInstance();
                tempHolder.setTime(startTime.getTime());
                tempHolder.roll(Calendar.DAY_OF_YEAR, position - currentValue);
                barFragment.setDay(tempHolder);
                return barFragment;
            }

        }

        @Override
        public int getCount() {
            return NUM_OF_VIEWS;
        }


    }


    // CenterBar implementation:
    // This function adds the time layout for center bar
    private void initiateCenterBar() {
        if (center == null) {
            center = findViewById(R.id.center);
        }
        center.addView(new SimpleChronoView(this));
    }

    // Scroling function that accepts values from 0 to 24, otherwise it will not scroll
    public void scrollTo(int hour) {
        if (hour >= 0 && hour <=24) {
            int slices = center.getHeight() / 24;
            center.scrollTo(0,slices * hour);
        }
    }

    //DataBase management: TODO: This will need to be revamped with new implementation

    private DayHolder currentDay;
    private DayHolder nextDay;
    private DayHolder previousDay;

    // Assuming that this is called only from the onCreate method, this function defines day to show
    // and its adjacent days
    private void setMetaData(Long dayInLong) {
        Calendar tempTime = Calendar.getInstance();

        tempTime.setTime(new Date(dayInLong)); // Does adjustment of time
        currentDay = new DayHolder(tempTime, dataMaster);

        tempTime.roll(Calendar.DAY_OF_MONTH,true); // changed the time for 1 day
        nextDay = new DayHolder(tempTime, dataMaster);

        tempTime.roll(Calendar.DAY_OF_MONTH, -2); // changes time for -2 days
        previousDay = new DayHolder(tempTime, dataMaster);
    }

    // Methods for swaping and handling of the days:
    private void nextDay() {
        DayHolder tempHolder = previousDay;
        previousDay = currentDay;
        currentDay = nextDay;
        Calendar next = Calendar.getInstance();
        next.setTime(currentDay.thisDay.getTime());
        next.roll(Calendar.DAY_OF_MONTH,true);
        tempHolder.reuseMe(next);// SHould load the new time
        nextDay = tempHolder;
    }

    private void previousDay() {
        DayHolder tempHolder = nextDay;
        nextDay = currentDay;
        currentDay = previousDay;
        Calendar next = Calendar.getInstance();
        next.setTime(currentDay.thisDay.getTime());
        next.roll(Calendar.DAY_OF_MONTH,false);
        tempHolder.reuseMe(next);
        previousDay = tempHolder;
    }

    // Holders for CurrentDay, nextDay and previousDay
    private class DayHolder {

        protected Calendar thisDay;
        private DataProviderProtocol dataProvider;
        private Set<ComplexGoalMaster> complexGoalMasters;
        private Set<SubGoalMaster> subGoalMasters;
        private Set<ListMaster> listMasters;
        private Set<OrdinaryEventMaster> ordinaryEventMasters;
        private Set<RepeatingEventMaster> repeatingEventMasters;
        private Set<RepeatingEventsChild> repeatingEventsChildren;

        public DayHolder(Calendar forDay, DataProviderProtocol dataBase) {
            // Implements the fetching and loading of the files
            thisDay = forDay;
            dataProvider = dataBase;
            complexGoalMasters = new HashSet<>();
            subGoalMasters = new HashSet<>();
            listMasters = new HashSet<>();
            ordinaryEventMasters = new HashSet<>();
            repeatingEventMasters = new HashSet<>();
            repeatingEventsChildren = new HashSet<>();
            //fillInTheSets(); TODO: Temp Solution
        }

        public void reuseMe(Calendar newDay) {
            thisDay = newDay;
            cleansTheSets();
            fillInTheSets();
        }

        // Manages the calls for populating the sets, assumes sets are initiated, only at
        // creation of the class gets called
        private void fillInTheSets() {
            complexGoalMasters = dataProvider.getComplexGoalsByDay(thisDay);
            for (ComplexGoalMaster master: complexGoalMasters) {
                subGoalMasters.addAll(dataProvider.getAllSubGoalsByMasterId(master.getHashID()));
            }
            listMasters = dataProvider.getAllListMastersByDay(thisDay);
            ordinaryEventMasters = dataProvider.getAllOrdinaryTasksByDay(thisDay);
            repeatingEventMasters = dataProvider.getAllRepeatingEventMastersByDay(thisDay);
            for (RepeatingEventMaster master: repeatingEventMasters) {
                repeatingEventsChildren.addAll(dataProvider.
                        getAllRepeatingChildrenByParent(master.getHashID()));
            }
        }

        private void cleansTheSets() {
            complexGoalMasters.clear();
            subGoalMasters.clear();
            listMasters.clear();
            ordinaryEventMasters.clear();
            repeatingEventsChildren.clear();
            repeatingEventMasters.clear();
        }
    }

}
