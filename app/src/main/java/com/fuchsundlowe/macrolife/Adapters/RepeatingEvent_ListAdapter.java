package com.fuchsundlowe.macrolife.Adapters;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.R;

import java.util.List;

/**
 * Created by macbook on 3/12/18.
 */

public class RepeatingEvent_ListAdapter extends RecyclerView.Adapter<RepeatingEvent_ListAdapter.ViewHolder>{

    public void updateDataBase(List<RepeatingEventMaster> newData) {
        dataList = newData;
    }

    private List<RepeatingEventMaster> dataList;


    @NonNull
    @Override
    public RepeatingEvent_ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.repeatingoal_card, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RepeatingEvent_ListAdapter.ViewHolder holder, int position) {
        holder.getName().setText(dataList.get(position).getTaskName());
    }

    @Override
    public int getItemCount() {

        List<RepeatingEventMaster> list = dataList;
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;

        public ViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.repeatinggoal_taskname_card);
        }

        public TextView getName() { return name;}

    }

}
