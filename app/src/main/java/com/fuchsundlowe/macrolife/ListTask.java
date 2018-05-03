package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import com.fuchsundlowe.macrolife.Adapters.ListTaskAdapter;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;

public class ListTask extends AppCompatActivity {

    private RecyclerView centerBar;
    private EditText newTaskText;
    private int masterID;
    private DataProviderProtocol data;
    private TextView masterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);
        data = StorageMaster.getInstance(this);
        masterName = findViewById(R.id.listTask_taskName);

        centerBar = (RecyclerView) findViewById(R.id.listView_centerBar);
        if (getIntent() != null) {
            masterID = getIntent().getIntExtra(Constants.LIST_VIEW_MASTER_ID, 0);
            defineMasterName();
            initiateRecyclerView();
        }
        initiateBottomBar();
    }
    private void defineMasterName() {
        data.getListMasterByID(masterID).observe(this, new Observer<ListMaster>() {
            @Override
            public void onChanged(@Nullable ListMaster listMaster) {
                masterName.setText(listMaster.getTaskName());
            }
        });
    }
    private void initiateRecyclerView() {
        // This defines the way new items will be stored in view
        centerBar.setLayoutManager(new LinearLayoutManager(this));
        centerBar.setAdapter(new ListTaskAdapter(masterID, this));
    }
    private void initiateBottomBar() {
        newTaskText = findViewById(R.id.newTask_card);
        newTaskText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        createNewSubTask();
                    }
                }
                return true;
            }
        });
    }
    private void createNewSubTask() {
        ListObject newTask = new ListObject(newTaskText.getText().toString(), false,
                masterID,0);
        data.insertObject(newTask);
    }
    public void onDone(View view){
        this.finish();
    }
}
