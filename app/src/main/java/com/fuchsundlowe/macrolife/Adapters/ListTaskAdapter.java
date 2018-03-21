package com.fuchsundlowe.macrolife.Adapters;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.List;



public class ListTaskAdapter extends RecyclerView.Adapter<ListTaskAdapter.BaseAdapter> {

    private DataProviderProtocol dataMaster;
    private int parentID;
    private List<ListObject> data;
    private Context mContext;

    public ListTaskAdapter(int parentID, Context context) {
        this.parentID = parentID;
        this.mContext = context;
        implementLiveData();
    }

    @NonNull
    @Override
    public BaseAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View card = LayoutInflater.from(mContext).inflate(R.layout.list_task_card, parent, false);

        return new BaseAdapter(card);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter holder, int position) {
        holder.box.setText(data.get(position).getTaskName());
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    private void implementLiveData() {
        dataMaster = StorageMaster.optionalStorageMaster(); // Init's the dataMaster
        dataMaster.getListObjectsByParent(this.parentID).observeForever(new Observer<List<ListObject>>() {
            @Override
            public void onChanged(@Nullable List<ListObject> listObjects) {
                data = listObjects;
                notifySetChange();
            }
        });
    }

    private void notifySetChange() {
        this.notifyDataSetChanged();
    }

    class BaseAdapter extends  RecyclerView.ViewHolder{

        CheckBox box;

        BaseAdapter(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.card_checkBox);
        }
    }
}
