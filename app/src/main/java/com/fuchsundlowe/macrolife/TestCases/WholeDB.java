package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.R;

import java.text.SimpleDateFormat;

public class WholeDB extends AppCompatActivity {

    LinearLayout ll;
    LocalStorage ls;
    String n = "\n";
    LinearLayout.LayoutParams lp;
    SimpleDateFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whole_db);
        ll = findViewById(R.id.layout);
        ls = LocalStorage.getInstance(this);
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        format = new SimpleDateFormat("HH:mm 'at' dd-MM-yyyy");
        //once();
    }


    public void presentTask(View view) {
        ll.removeAllViews();
        for (TaskObject t : ls.taskObjectHolder.getValue()) {
            String toPass = formatTask(t);

            TextView tV = new TextView(this);
            tV.setText(toPass);
            tV.setBackgroundColor(Color.BLACK);
            tV.setTextColor(Color.CYAN);

            tV.setLayoutParams(lp);

            ll.addView(tV);

        }
    }

    String formatTask(TaskObject task) {
        String t = "";
       // HashId
        t+= "HashID: " + task.getHashID() + n;
        // Name
        t+= "Name: " + task.getTaskName() +n;
        // StartTime
        t+= "StartTime: " + format.format(task.getTaskStartTime().getTime()) + n;
        // End TIme
        t+= "End Time: " + format.format(task.getTaskEndTime().getTime()) + n;
        // Mods
        t += "Mods: " + task.getMods() + n;
        // RepeatDescriptor
        t += "Descriptor: " + task.getRepeatDescriptor() + n;

        return t;

    }

    public void presentEvent(View view) throws InterruptedException {
        ll.removeAllViews();
        final RepeatingEvent[] events = new RepeatingEvent[120];
        new Thread(new Runnable() {
            @Override
            public void run() {
                int c = 0;
                for (RepeatingEvent event: ls.dataBase.newDAO().TEST_repeatingEvents()) {
                    events[c] = event;
                    c++;
                }
            }
        }).start();
        Thread.sleep(1000);
        for (RepeatingEvent e : events) {
            if (e != null) {
                String toPass = formatEvent(e);

                TextView tV = new TextView(this);
                tV.setText(toPass);
                tV.setBackgroundColor(Color.BLACK);
                tV.setTextColor(Color.GREEN);

                tV.setLayoutParams(lp);

                ll.addView(tV);
            }
        }

    }

    String formatEvent(RepeatingEvent event) {
        String t = "";
        // hashDI
        t+= "HashID: " + event.getHashID() + n;
        // Parent
        t+= "Parent ID: " + event.getParentID() + n;
        // StartTime
        t+= "StartTime: " + format.format(event.getStartTime().getTime())+n;
        // EndTIme
        t+= "End Time: " + format.format(event.getEndTime().getTime())+n;
        return t;
    }

    void once() {
        ls.deleteAllRepeatingEvents(9);
        ls.deleteAllRepeatingEvents(10);
    }
}
