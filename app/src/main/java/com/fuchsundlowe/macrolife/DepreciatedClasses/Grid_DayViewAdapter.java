package com.fuchsundlowe.macrolife.DepreciatedClasses;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by macbook on 3/14/18.
 */
@Deprecated
public class Grid_DayViewAdapter extends BaseAdapter {
// TODO: This is testing!!!

    private Context mContext;
    private DataProviderProtocol data;
    public Grid_DayViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resource.length;

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
        TextView toDisplay;
        if (convertView == null) {
            toDisplay = new TextView(mContext);
            //toDisplay.setLayoutParams(new GridView.LayoutParams(85,85));
            toDisplay.setPadding(8,8,8,8);
        } else {
            toDisplay = (TextView) convertView;
        }
        toDisplay.setText(resource[position]);
        return toDisplay;
    }

    String[] resource = {"Alpha", "Jack", "Sam", "Donny", "Jasmine", "Leonardo", "Alexander",
    "Peruzzi", "Gian-Franco", "Emanuel", "Sarah", "Devon", "Natalie", "Petrarco"};



}
