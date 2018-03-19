package com.fuchsundlowe.macrolife.Adapters;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.PopUpData;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.List;

/**
 * Created by macbook on 3/14/18.
 */

public class PopUpGridAdapter extends BaseAdapter {

    private Context mContext;
    private DataProviderProtocol data;
    private List<PopUpData> dataHolder;
    public PopUpGridAdapter(Context mContext) {
        this.mContext = mContext;
        data = StorageMaster.getInstance(mContext);
        loadData();
    }

    @Override
    public int getCount() {
        if (dataHolder != null) {
            return dataHolder.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridCard;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            gridCard = new View(mContext);
            gridCard = inflater.inflate(R.layout.grid_view, null);
            TextView setName = (TextView) gridCard.findViewById(R.id.taskName_gridCard);
            setName.setText(dataHolder.get(position).name);

        } else {
            gridCard = convertView;
            TextView setName = (TextView) gridCard.findViewById(R.id.taskName_gridCard);
            setName.setText(dataHolder.get(position).name);
        }

        return gridCard;
    }

    private void loadData() {
        data.loadPopUpValues().observeForever(new Observer<List<PopUpData>>() {
            @Override
            public void onChanged(@Nullable List<PopUpData> popUpData) {
                dataHolder = popUpData;
                dataHasChanged();
            }
        });
    }

    private void dataHasChanged() {
        this.notifyDataSetChanged();
    }
}
