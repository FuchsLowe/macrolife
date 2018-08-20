package com.fuchsundlowe.macrolife.DepreciatedClasses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.R;

import java.util.List;
@Deprecated
public class ListGoal_ListAdapter extends RecyclerView.Adapter<ListGoal_ListAdapter.ViewHolder>{

    public void updateDataBase(List<ListMaster> newData) {
        dataList = newData;
    }

    private List<ListMaster> dataList;
    private static Context mContext;

    public ListGoal_ListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public ListGoal_ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listgoal_card, parent, false);

        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ListGoal_ListAdapter.ViewHolder holder, int position) {
        holder.getName().setText(dataList.get(position).getTaskName());
        holder.masterID = dataList.get(position).getHashID();

    }

    @Override
    public int getItemCount() {
        List<ListMaster> list = dataList;
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private int masterID;


        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.listgoal_taskname_card);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shootIntent();
                }
            });
        }

        public TextView getName() { return name;}

        void shootIntent() {

        }
    }

}
