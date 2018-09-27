package com.fuchsundlowe.macrolife.ListView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.fuchsundlowe.macrolife.Interfaces.LDCProtocol;
import com.fuchsundlowe.macrolife.R;

public class ListView extends AppCompatActivity {

    private FrameLayout bottomBar;
    private RecyclerView centerBar;
    private LDCProtocol dataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        centerBar = findViewById(R.id.centerBar_listView);
        dataProvider = new ListDataController(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This ensures that there are no memory leaks associated DataProvider...
        dataProvider.destroy();
    }



    // List View Page adapter:
    private class ListViewAdapter extends FragmentStatePagerAdapter {

        ListViewAdapter(FragmentManager fm) {super(fm);}

        @Override
        public Fragment getItem(int position) {
            // TODO: Update to reflect the values being inseted into it...
            switch (position) {
                case 0: // Completed
                    Completed_ListView frag = new Completed_ListView();
                    return frag;
                case 2: // Complex
                    return null;
                default: // Current
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    protected enum bracketType {
        completed, overdue, undefined, upcoming
    }
    // TODO: Bottom Bar implementation:

}
