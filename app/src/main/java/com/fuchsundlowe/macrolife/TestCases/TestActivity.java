package com.fuchsundlowe.macrolife.TestCases;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.fuchsundlowe.macrolife.ComplexGoal.ComplexTaskActivity;
import com.fuchsundlowe.macrolife.DepreciatedClasses.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        name = findViewById(R.id.enetrName);
        descriptor = findViewById(R.id.Descriptor);

    }

    EditText name;
    EditText descriptor;

    public void onSave(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OrdinaryEventMaster em = new OrdinaryEventMaster(0, name.getText().toString(),null,
                        null, Calendar.getInstance(), false, SourceType.local);

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



    public void toScroll(View view) {
        Intent toScroll = new Intent(this, TestActivity2.class);
        startActivity(toScroll);
    }

    public void toTest3(View view) {
        Intent test3 = new Intent (this, TestActivity3.class);
        startActivity(test3);
    }

    public void toTest4(View view) {
        Intent test4 =  new Intent(this, ComplexTaskActivity.class);
        startActivity(test4);
    }

}
