package com.fuchsundlowe.macrolife;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
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
        registerObserver();
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
                descriptor.post(new Runnable() {
                    @Override
                    public void run() {
                        descriptor.append("Object To Be added: \n"+ "Name: " + em.getTaskName() + "\nID: " + em.getHashID() + "\n" );
                    }
                });
            }
        }).start();

        //descriptor.append("Object To Be added: \n"+ "Name: " + em.getTaskName() + "\nID: " + em.getHashID() + "\n" );

    }

    public void onLoad(View view) {

    }

    private void registerObserver() {
        data.subscribeObserver_OrdinaryEvent(this, new Observer<List<OrdinaryEventMaster>>() {
            @Override
            public void onChanged(@Nullable List<OrdinaryEventMaster> ordinaryEventMasters) {
                for (OrdinaryEventMaster data: ordinaryEventMasters) {
                    descriptor.append("New Entry: " + data.getTaskName() +"\nID: " + data.getHashID());
                }
            }
        });
    }
}
