package com.fuchsundlowe.macrolife.FragmentModels;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.CustomViews.SimpleChronoView;
import com.fuchsundlowe.macrolife.R;

/**
 * This class provides day view fragment for app. Main features expected here are:
 *  Chronological view of the daily duties.
 *  Requests information to fill in this specific day based on day atribute.
 *  TODO: Add a darn regualr view for test purposes to see if it can be done...
 */
public class DayViewFragment extends Fragment {


    //private OnFragmentInteractionListener mListener;

    private ScrollView center;
    private FrameLayout topBar;

    public DayViewFragment() {
        // Required empty public constructor
    }

    // MyImpplementation:
    private void addGraphics() {
        center.addView(new SimpleChronoView(getContext()));

    }

    public void scrollTo(int hour) {
        if (hour >= 0 && hour <=24) {
            int slices = center.getHeight() / 24;
            center.scrollTo(0,slices * hour);
        }
    }


    // Life-cycle events:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.day_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        center = getView().findViewById(R.id.center);
        topBar = getView().findViewById(R.id.top_bar);
        addGraphics();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
     */

    // Touch events:




}
