package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.LiveData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.EngineClasses.StorageMaster;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderProtocol;

import java.util.Calendar;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        name = findViewById(R.id.enetrName);
        descriptor = findViewById(R.id.Descriptor);
        data = StorageMaster.getInstance(this);

    }

    DataProviderProtocol data;
    EditText name;
    EditText descriptor;

    public void onSave(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OrdinaryEventMaster em = new OrdinaryEventMaster(0, name.getText().toString(),null,
                        null, Calendar.getInstance(), false, SourceType.local);
                data.insertObject(em);
            }
        }).start();

        //descriptor.append("Object To Be added: \n"+ "Name: " + em.getTaskName() + "\nID: " + em.getHashID() + "\n" );

    }

    public void onLoad(View view) {
        LiveData<List<OrdinaryEventMaster>> fetched = data.getAllOrdinaryEvents();
        List<OrdinaryEventMaster> parsed = fetched.getValue();
        if (parsed != null) {
            for (OrdinaryEventMaster values: parsed) {
                descriptor.append(values.getTaskName() + "\n");
            }
        } else {
            descriptor.setText("Nothing Found :(");
        }
    }
}
