package com.fuchsundlowe.macrolife.TestCases;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.R;

import java.text.SimpleDateFormat;

public class Test4 extends AppCompatActivity {


    LocalStorage database;
    TextView console;
    EditText numerical;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test4);
        database = LocalStorage.getInstance(this);
        console = findViewById(R.id.console_test);
        numerical = findViewById(R.id.numerical_input_test);
    }

    public void searchEvent(View view) {
    }

    public void searchTask(View view) {
        String val = numerical.getText().toString();
        Integer number = Integer.valueOf(val);
        TaskObject object = database.findTaskObjectBy( number);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm 'at' dd-MM" );

        if (object != null) {
            String toSend = "Name: " + object.getTaskName();
            toSend += "\n" + "HashID: " + object.getHashID();
            toSend += "\n" + "startTime: " + f.format(object.getTaskStartTime().getTime()) + "\nendTime: " + f.format(object.getTaskEndTime().getTime());


            writeToConsole(toSend);
        } else {
            writeToConsole("No Such Task");
        }
    }

    void writeToConsole(String val) {
        console.append(val + "\n" + ">>>0<<<" + "\n");
    }

    public void clearConsole(View v) {
        console.setText("");
    }

}
