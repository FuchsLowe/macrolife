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

import com.fuchsundlowe.macrolife.R;

public class ListView extends AppCompatActivity {

    private FrameLayout bottomBar;
    private RecyclerView centerBar;
    private Button complete, current, complex;
    private ListDataController dataProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        dataProvider = new ListDataController(this);
        // Linking views:
        bottomBar = findViewById(R.id.bottomBar_listView);
        centerBar = findViewById(R.id.centerBar_listView);

        complete = findViewById(R.id.completed_listView);
        complete.setTag(ButtonTags.complete);

        current = findViewById(R.id.current_listView);
        current.setTag(ButtonTags.current);

        complex = findViewById(R.id.complex_listVIew);
        complex.setTag(complex);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This ensures that there are no memory leaks associated with LiveData.
        dataProvider.destroy();
    }

    public void onButtonClick(View view) {
        // determine which view was called:
        if (view instanceof Button) {
            if (view.getTag().equals(ButtonTags.complete)) {

            } else if (view.getTag().equals(current)) {

            } else if (view.getTag().equals(complex)) {

            }
        }

        // Change elevation maybe for selected one?
    }

    // List View Page adapter:
    private class ListViewAdapter extends FragmentStatePagerAdapter {

        ListViewAdapter(FragmentManager fm) {super(fm);}

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    // TODO: Bottom Bar implementation:

    // a simple enum for tags for buttons:
    private enum ButtonTags {
    // todo Implement!
        complete("completeTag"), current("currentTag"), complex("ComplexTag");
        String textID;

        ButtonTags(String val) {
            textID = val;
        }

        String getTag() {
            return textID;
        }

    }
}
