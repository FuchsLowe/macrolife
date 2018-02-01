package com.fuchsundlowe.macrolife;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fuchsundlowe.macrolife.RoomElementsScapped.DataProvider;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;

import java.util.Calendar;

public class MasterScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_screen);
       // tempActionMethod();
    }

    private void tempActionMethod() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Creates database;
                DataProvider dbInstance = Room.databaseBuilder(getApplicationContext(),
                        DataProvider.class,
                        "testOne").build();

                // This part inserts values into database:
                populateDatabase(dbInstance);
                // This part returns data from databse:
                 OrdinaryEventMaster[] results = getAllTaksFromDatabase(dbInstance);


            }
        }).start();


    }
    private void populateDatabase(DataProvider db) {
        OrdinaryEventMaster task = new OrdinaryEventMaster();
        task.setHashID(1);
        task.setTaskCompleted(false);
        task.setTaskCreatedTimeStamp(Calendar.getInstance());
        task.setTaskName("First ever to return to other side");
        db.daoObject().insertTask(task);
    }

    private OrdinaryEventMaster[] getAllTaksFromDatabase(DataProvider db) {
        return db.daoObject().getAllTasks();
    }
}
