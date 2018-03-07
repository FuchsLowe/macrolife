package com.fuchsundlowe.macrolife.FragmentModels;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by macbook on 2/22/18.
 */

public class TopBar_ListFragment extends Fragment {

    private TextView label;
    private ListView list;
    private int number;
    // View Lifecycle:




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        Context mike = getActivity();
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



}
