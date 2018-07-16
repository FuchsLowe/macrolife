package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.DayOfWeek;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.RoomDataBaseObject;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/*
 * Known Issue: The Observe Forever for live data used to produce in memory holding of Tasks ( via
 * function call defineInMemoryDataBaseCalls ) used for
 * search and comparisons use is known not to provide updates to dataHolders after initial update.
 * To overcome this issue, whenever a task is saved or deleted, manually the holders are updated.
 *
 * Newest implementation:
 * Data object Holders like taskObjectHolder & etc. are always accessed and transferrd to
 * array in order to do in memory checks of data.
 * For this to work, I need to register at least one observer and observe forever is thus doing
 * that job even if it doesn't report back to in all occasions changes.
 *
 * If possible find the cause and replace the faulty system!
 *
 * Looks like depending on the platform or system version the observe forever work on some?
 */

public class LocalStorage implements DataProviderNewProtocol {

    private static LocalStorage self;
    private RoomDataBaseObject dataBase;
    private LiveData<List<TaskObject>> taskObjectHolder;
    private LiveData<List<ListObject>> listObjectHolder;
    private LiveData<List<RepeatingEvent>> repeatingEventHolder;
    private LiveData<List<ComplexGoal>> complexGoalHolder;


    // Constructor implementation:
    public static  @Nullable LocalStorage getInstance(@Nullable Context context) {
        if (self != null) {
            return self;
        } else if (context != null) {
            self = new LocalStorage(context);
            return self;
        } else { return null; }
    }
    private LocalStorage(Context context) {
        // Deals with database initialization ofc
        dataBase = Room.databaseBuilder(context, RoomDataBaseObject.class,
                Constants.DATA_BASE_NAME).build();
        defineInMemoryDatabaseCalls();
    }

    // Database Calls:
    @Override
    public LiveData<List<TaskObject>> getTasksFor(Calendar day) {
        // Currently Unsuported?
        return null;
    }
    @Override // Static return value
    public ComplexGoal findComplexGoal(int byID) {
        if (complexGoalHolder.getValue() != null) {
            for (ComplexGoal goal: complexGoalHolder.getValue()) {
                if (goal.getHashID() == byID) { return goal; }
            }
        }
        return null;
    }
    @Override
    public TaskObject findTaskObjectBy(int ID) {
        List<TaskObject> transformed = taskObjectHolder.getValue();
        if (transformed != null) {
            for (TaskObject task : transformed) {
                if (task.getHashID() == ID) {
                    return task;
                }
            }
        }
        return null;
    }
    public LiveData<List<TaskObject>> getTaskThatIntersects(Calendar day) {
        // Get the long values of start and end of day...
        long[] results = returnStartAndEndTimesForDay(day);

        return dataBase.newDAO().getTaskThatIntersects(results[0], results[1]);
    }
    @Override
    public LiveData<List<RepeatingEvent>> getEventsThatIntersect(Calendar day) {
        // Get the long values of start and end of day...
        long[] results = returnStartAndEndTimesForDay(day);

        return dataBase.newDAO().getEventThatIntersects(results[0], results[1]);
    }
    @Override
    public List<RepeatingEvent> getEventsBy(int masterID, TaskObject.Mods mod) {
        if (repeatingEventHolder.getValue() != null) {
            List<RepeatingEvent> setToSend= new ArrayList<>();
            if (mod == TaskObject.Mods.repeating) {
                for (RepeatingEvent event : repeatingEventHolder.getValue()) {
                    if (event.getParentID() == masterID && event.getDayOfWeek() == DayOfWeek.universal) {
                        setToSend.add(event);
                    }
                }
            } else {
                for (RepeatingEvent event : repeatingEventHolder.getValue()) {
                    if (event.getParentID() == masterID && event.getDayOfWeek() != DayOfWeek.universal) {
                        setToSend.add(event);
                    }
                }
            }
            return setToSend;
        }
        return null;
    }
    @Override
    public ComplexGoal getComplexGoalBy(int masterID) {
        if (complexGoalHolder.getValue() != null) {
            for (ComplexGoal goal : complexGoalHolder.getValue()) {
                if (goal.getHashID() == masterID) {
                    return goal;
                }
            }
        }
        return null;
    }
    @Override
    public void deleteTask(final TaskObject objectToDelete) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBase.newDAO().removeTask(objectToDelete);
            }
        }).start();
    }
    @Override // If there is one it will update it if not it will create new
    public void saveListObject(final ListObject objectToSave) {
        if (listObjectHolder.getValue() != null) {
            if (objectToSave.getHashID() != 0) {
                for (final ListObject object : listObjectHolder.getValue()) {
                    if (object.getHashID() == objectToSave.getHashID()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dataBase.newDAO().saveListObject(objectToSave);
                            }
                        }).start();
                        return;
                    }
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataBase.newDAO().insertListObject(objectToSave);
                }
            }).start();
        }
    }
    @Override
    public void deleteListObject(final ListObject objectToDelete) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBase.newDAO().removeListObject(objectToDelete);
            }
        }).start();
    }
    @Override
    public List<ListObject> findListFor(int taskObjectID) {
        if (listObjectHolder.getValue() != null) {
            List<ListObject> setToReturn = new ArrayList<>();
            for (ListObject object : listObjectHolder.getValue()) {
                if (object.getMasterID() == taskObjectID) {
                    setToReturn.add(object);
                }
            }
            return setToReturn;
        }
        return null;
    }
    @Override
    public void deleteRepeatingEvent(final RepeatingEvent eventToDelete) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBase.newDAO().removeRepeatingEvent(eventToDelete);
            }
        }).start();
    }
    @Override
    public void saveRepeatingEvent(final RepeatingEvent event) {
        if (repeatingEventHolder.getValue() != null) {
            for (final RepeatingEvent object : repeatingEventHolder.getValue()) {
                if (object.getHashID() == event.getHashID()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dataBase.newDAO().saveRepeatingEvent(event);
                        }
                    }).start();
                    return;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataBase.newDAO().insertRepeatingEvent(event);
                }
            }).start();

            for (RepeatingEvent event1: repeatingEventHolder.getValue()) {
                if (event1.getHashID() == event.getHashID()) {
                    break;
                }
            }
        }


    }
    @Override
    public ArrayList<TaskObject> getDataForRecommendationBar() {
        if (taskObjectHolder.getValue() != null) {
            ArrayList<TaskObject> listToReturn = new ArrayList<>();
            for (TaskObject task : taskObjectHolder.getValue()) {
                if (task.getIsTaskCompleted() == TaskObject.CheckableStatus.notCheckable &&
                        task.getTimeDefined() == TaskObject.TimeDefined.noTime) {
                    listToReturn.add(task);
                }
            }
            return listToReturn;
        }
        return null;
    }
    @Override
    public void saveTaskObject(final TaskObject task) {
        if (taskObjectHolder.getValue() != null) {
            for (TaskObject taskObject : taskObjectHolder.getValue()) {
                if (taskObject.getHashID() == task.getHashID()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dataBase.newDAO().saveTask(task);
                        }
                    }).start();
                    return;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("New Task: ", "" + task.getHashID());
                    if (task.getTaskName().length() > 0) {
                        dataBase.newDAO().insertTask(task);
                    }
                }
            }).start();
        }
    }
    @Override
    public LiveData<TaskObject> getTaskObjectWithCreationTime(Calendar creationTime){
        return dataBase.newDAO().getTaskObjectWithCreationTime(creationTime.getTimeInMillis());
    }
    @Override
    public int findNextFreeHashIDForTask() {
        if (taskObjectHolder.getValue() != null && taskObjectHolder.getValue().size() > 0) {
            int biggestID = 0;
            for (TaskObject task : taskObjectHolder.getValue()) {
                biggestID = Math.max(biggestID, task.getHashID());
            }
            return biggestID +1;
        } else {
            return 0;
        }
    }
    @Override
    public int findNextFreeHashIDForList() {
        if (listObjectHolder.getValue() != null && listObjectHolder.getValue().size() > 0) {
            int bigestID = 0;
            for (ListObject listy : listObjectHolder.getValue()) {
                bigestID = Math.max(bigestID, listy.getHashID());
            }
            return bigestID +1;
        } else {
            return 0;
        }
    }
    @Override
    public int findNextFreeHashIDForEvent() {
        if (repeatingEventHolder.getValue() != null && repeatingEventHolder.getValue().size() > 0) {
            int biggestID = 0;
            for (RepeatingEvent event: repeatingEventHolder.getValue()) {
                biggestID = Math.max(biggestID, event.getHashID());
            }
            return biggestID +1;
        } else {
            return 0;
        }
    }

    // Method calls:
    private long[] returnStartAndEndTimesForDay(Calendar day) {
       Calendar dayToWorkWith = (Calendar) day.clone();

       dayToWorkWith.set(Calendar.HOUR_OF_DAY,0);
       dayToWorkWith.set(Calendar.MINUTE,0);
       dayToWorkWith.set(Calendar.SECOND,0);
       dayToWorkWith.set(Calendar.MILLISECOND,0);
       long startTimeStamp = dayToWorkWith.getTimeInMillis();

       dayToWorkWith.set(Calendar.HOUR_OF_DAY, 23);
       dayToWorkWith.set(Calendar.MINUTE, 59);
       dayToWorkWith.set(Calendar.SECOND, 59);
       long endTimeStamp = dayToWorkWith.getTimeInMillis();

       return new long[]{startTimeStamp, endTimeStamp};
   }
    private void defineInMemoryDatabaseCalls() {
        taskObjectHolder = dataBase.newDAO().getAllTaskObjects();
        taskObjectHolder.observeForever(new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> taskObjects) {
                Log.d("Report Changes", " to database task Objects");
            }
        });
        listObjectHolder = dataBase.newDAO().getAllListObjects();
        listObjectHolder.observeForever(new Observer<List<ListObject>>() {
            @Override
            public void onChanged(@Nullable List<ListObject> listObjects) {
                Log.d("Report Changes", " to List database");
            }
        });
        repeatingEventHolder = dataBase.newDAO().getAllRepeatingEvents();
        repeatingEventHolder.observeForever(new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> repeatingEvents) {
                Log.d("report changes to", " repeating events dB");
            }
        });
        complexGoalHolder = dataBase.newDAO().getAllComplexGoals();
        complexGoalHolder.observeForever(new Observer<List<ComplexGoal>>() {
            @Override
            public void onChanged(@Nullable List<ComplexGoal> complexGoals) {
                Log.d("Report Changes to", " complex goal DB");
            }
        });
   }
    public boolean isDataBaseOpen() {
        return dataBase.isOpen();
   }
    public void closeDataBase() {
        taskObjectHolder = null;
        listObjectHolder = null;
        repeatingEventHolder = null;
        complexGoalHolder = null;

        dataBase.close();
   }


}
