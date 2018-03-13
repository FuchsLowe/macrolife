package com.fuchsundlowe.macrolife.Adapters;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.R;
import java.util.List;

/**
 * Created by macbook on 3/12/18.
 */

public class RegularTask_ListAdapter extends RecyclerView.Adapter<RegularTask_ListAdapter.ViewHolder>{

    public RegularTask_ListAdapter(LiveData<List<OrdinaryEventMaster>> dataList) {
        this.dataList = dataList;
    }

    private LiveData<List<OrdinaryEventMaster>> dataList;


    @NonNull
    @Override
    public RegularTask_ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.regulartask_card, parent, false);

        return new RegularTask_ListAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull RegularTask_ListAdapter.ViewHolder holder, int position) {
        holder.getName().setText(dataList.getValue().get(position).getTaskName());
    }

    @Override
    public int getItemCount() {
        List<OrdinaryEventMaster> list = dataList.getValue();
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
            name = (TextView) v.findViewById(R.id.regulartask_taskname_card);
        }

        public TextView getName() { return name;}

    }

}
