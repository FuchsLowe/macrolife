package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalStorage implements DataProviderNewProtocol {

    private static LocalStorage self;
    private RoomDataBaseObject dataBase;
    private List<TaskObject> taskObjectHolder;
    private List<ListObject> listObjectHolder;
    private List<RepeatingEvent> repeatingEventHolder;
    private List<ComplexGoal> complexGoalHolder;
    private Observer<List<TaskObject>> taskObjectObserver;
    private Observer<List<ComplexGoal>> complexGoalObserver;
    private Observer<List<ListObject>> listObjectObserver;
    private Observer<List<RepeatingEvent>> repeatingEventObserver;

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
        if (complexGoalHolder != null) {
            for (ComplexGoal goal: complexGoalHolder) {
                if (goal.getHashID() == byID) { return goal; }
            }
        }
        return null;
    }
    @Override
    public TaskObject findTaskObjectBy(int ID) {
        if (taskObjectHolder != null) {
            for (TaskObject object : taskObjectHolder) {
                if (object.getHashID() == ID) { return object; }
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
        if (repeatingEventHolder != null) {
            List<RepeatingEvent> setToSend= new ArrayList<>();
            if (mod == TaskObject.Mods.repeating) {
                for (RepeatingEvent event : repeatingEventHolder) {
                    if (event.getParentID() == masterID && event.getDayOfWeek() == DayOfWeek.universal) {
                        setToSend.add(event);
                    }
                }
            } else {
                for (RepeatingEvent event : repeatingEventHolder) {
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
        if (complexGoalHolder != null) {
            for (ComplexGoal goal : complexGoalHolder) {
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
        if (listObjectHolder != null) {
            for (final ListObject object : listObjectHolder) {
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
        if (listObjectHolder != null) {
            List<ListObject> setToReturn = new ArrayList<>();
            for (ListObject object : listObjectHolder) {
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
        if (repeatingEventHolder != null) {
            for (final RepeatingEvent object : repeatingEventHolder) {
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
        }
    }
    @Override
    public ArrayList<TaskObject> getDataForRecommendationBar() {
        if (taskObjectHolder != null) {
            ArrayList<TaskObject> listToReturn = new ArrayList<>();
            for (TaskObject task : taskObjectHolder) {
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
        if (taskObjectHolder != null) {
            for (TaskObject taskObject : taskObjectHolder) {
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
                    dataBase.newDAO().insertTask(task);
                }
            }).start();
        }
    }
    // Method calls:
    // first value is start time and second value is end time
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
        taskObjectObserver = new Observer<List<TaskObject>>() {
            @Override
            public void onChanged(@Nullable List<TaskObject> taskObjects) {
                taskObjectHolder = taskObjects;
            }
        };
        dataBase.newDAO().getAllTaskObjects().observeForever(taskObjectObserver);

        complexGoalObserver = new Observer<List<ComplexGoal>>() {
            @Override
            public void onChanged(@Nullable List<ComplexGoal> complexGoals) {
                complexGoalHolder = complexGoals;
            }
        };
        dataBase.newDAO().getAllComplexGoals().observeForever(complexGoalObserver);

        listObjectObserver = new Observer<List<ListObject>>() {
            @Override
            public void onChanged(@Nullable List<ListObject> listObjects) {
                listObjectHolder = listObjects;
            }
        };
        dataBase.newDAO().getAllListObjects().observeForever(listObjectObserver);

        repeatingEventObserver = new Observer<List<RepeatingEvent>>() {
            @Override
            public void onChanged(@Nullable List<RepeatingEvent> repeatingEvents) {
                repeatingEventHolder = repeatingEvents;
            }
        };
        dataBase.newDAO().getAllRepeatingEvents().observeForever(repeatingEventObserver);
   }
   public boolean isDataBaseOpen() {
        return dataBase.isOpen();
   }
   public void closeDataBase() {

        dataBase.newDAO().getAllTaskObjects().removeObserver(taskObjectObserver);
        taskObjectObserver = null;

        dataBase.newDAO().getAllComplexGoals().removeObserver(complexGoalObserver);
        complexGoalObserver = null;

        dataBase.newDAO().getAllListObjects().removeObserver(listObjectObserver);
        listObjectObserver = null;

        dataBase.newDAO().getAllRepeatingEvents().removeObserver(repeatingEventObserver);
        repeatingEventObserver = null;

        dataBase.close();
   }
}
