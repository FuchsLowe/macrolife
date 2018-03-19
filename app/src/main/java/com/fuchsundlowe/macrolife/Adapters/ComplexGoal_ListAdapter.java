package com.fuchsundlowe.macrolife.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.R;

import java.util.List;

/**
 * Created by macbook on 3/9/18.
 */

public class ComplexGoal_ListAdapter extends RecyclerView.Adapter<ComplexGoal_ListAdapter.ViewHolder> {

    public ComplexGoal_ListAdapter() {
    }

    public void updateDataBase(List<ComplexGoalMaster> newData){
        dataList = newData;
    }

    private List<ComplexGoalMaster> dataList;

    @NonNull
    @Override
    public ComplexGoal_ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complexgoal_card, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ComplexGoal_ListAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(dataList.get(position).getTaskName()); // Only sets Name
    }

    @Override
    public int getItemCount() {
        List<ComplexGoalMaster> list = dataList;
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public void toComplexTaskActivity() {

    }


    // In charge of displaying the Views...
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textToDisplay;

        public ViewHolder(View itemView) {
            super(itemView);
            textToDisplay = (TextView) itemView.findViewById(R.id.complexgoal_taskname_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }

        public TextView getTextView() {
            return this.textToDisplay;
        }
    }
}
