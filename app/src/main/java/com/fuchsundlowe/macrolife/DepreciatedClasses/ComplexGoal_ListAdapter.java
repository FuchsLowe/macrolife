package com.fuchsundlowe.macrolife.DepreciatedClasses;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.ComplexGoal.ComplexTaskActivity;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.R;

import java.util.List;

/**
 * Created by macbook on 3/9/18.
 */
@Deprecated
public class ComplexGoal_ListAdapter extends RecyclerView.Adapter<ComplexGoal_ListAdapter.ViewHolder> {

    public ComplexGoal_ListAdapter(Context context) {
        mContext = context;
    }

    public void updateDataBase(List<ComplexGoal> newData){
        dataList = newData;
    }

    private List<ComplexGoal> dataList;
    private static Context mContext;
    @NonNull
    @Override
    public ComplexGoal_ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.complexgoal_card, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ComplexGoal_ListAdapter.ViewHolder holder, int position) {
        holder.getTextView().setText(dataList.get(position).getTaskName()); // Only sets Name
        holder.masterID = dataList.get(position).getHashID();

    }

    @Override
    public int getItemCount() {
        List<ComplexGoal> list = dataList;
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }


    // In charge of displaying the Views...
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textToDisplay;
        private int masterID;

        public ViewHolder(View itemView) {
            super(itemView);
            textToDisplay = (TextView) itemView.findViewById(R.id.complexgoal_taskname_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toIntent();
                }
            });

        }

        public TextView getTextView() {
            return this.textToDisplay;
        }

        // Opens the Activity
        void toIntent() {
            Intent toTask = new Intent(mContext, ComplexTaskActivity.class);
            toTask.putExtra(Constants.LIST_VIEW_MASTER_ID, masterID);
            mContext.startActivity(toTask);
        }
    }
}
