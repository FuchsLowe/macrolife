package com.fuchsundlowe.macrolife.TestCases;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
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
        String val = numerical.getText().toString();
        Integer number = Integer.valueOf(val);
        RepeatingEvent object = database.getEventWith(number);
        SimpleDateFormat f = new SimpleDateFormat("HH:mm 'at' dd-MM" );

        if (object != null) {
            // ID
            String toPresent = "ID" + object.getHashID();
            // Start time
            toPresent+= "\n" + " Start Time:" + f.format(object.getStartTime().getTime());
            // End time
            toPresent+= "\n" + "End Time:" + f.format(object.getEndTime().getTime());
            // duration:
            long dur = object.getEndTime().getTimeInMillis() - object.getStartTime().getTimeInMillis();
            dur = dur / 1000 / 60; // to get minutes
            toPresent+= "\n" + "Duration is: " + dur + "in minutes";

            writeToConsole(toPresent);
        } else {
            writeToConsole("No Such Event");
        }

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
            toSend += "\n" + "Descriptor info: \n" + object.getRepeatDescriptor() + "\n";
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
