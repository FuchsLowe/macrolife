package com.fuchsundlowe.macrolife.EngineClasses;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoal;
import com.fuchsundlowe.macrolife.DataObjects.Constants;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEvent;
import com.fuchsundlowe.macrolife.DataObjects.RoomDataBaseObject;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.MonthView.MonthViewDataProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.YEAR;
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
 * Looks like depending on the platform or system version the observe forever work on some systems,
 * specifically it worked on Samsung Galaxy S8 but noted it didn't work on Emulators ( used different
 * Android Software, 22 & 27 but same device, Google Pixel XL )
 */

// TODO Implement; maybe after you have red a little about search systems.
/*
 * For now, my ideas was that I can sort the results before they arrive via Live data by
 * Decreasing HashID... < reasons for decreasing hash id is that you will always be more int
 * eresetd in most recent data you have, and less about latter inserted data>
 * Then for search system I would use binary search to find the hashID
 */
public class LocalStorage implements DataProviderNewProtocol, MonthViewDataProvider {

    private static LocalStorage self;
    public RoomDataBaseObject dataBase;
    public LiveData<List<TaskObject>> taskObjectHolder; // TODO: Return to private
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


    // TaskObject Goal:
    @Override
    public LiveData<List<TaskObject>> getAllTaskObjects() {
        return dataBase.newDAO().getAllTaskObjects();
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
    @Override
    public LiveData<List<TaskObject>>getLiveDataForRecommendationBar(){
        return dataBase.newDAO().getTasksForRecommendationFetcher();
    }
    @Override
    public LiveData<List<TaskObject>> getTaskThatIntersects(Calendar day) { // Need to add timeDefined attribute
        // Get the long values of start and end of day...
        long[] results = returnStartAndEndTimesForDay(day);

        return dataBase.newDAO().getTaskThatIntersects(results[0], results[1]);
    }
    @Override
    public LiveData<List<TaskObject>>getTasksForWeekView(Calendar forDay) {
        // Get the long values of start and end of day...
        long[] results = returnStartAndEndTimesForDay(forDay);

        return dataBase.newDAO().getTaskThatIntersectsDayWithAnyTimeValue(results[0], results[1]);
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
    public LiveData<TaskObject> getTaskObjectWithCreationTime(Calendar creationTime){
        return dataBase.newDAO().getTaskObjectWithCreationTime(creationTime.getTimeInMillis());
    }
    @Override
    public void saveTaskObject(final TaskObject task) {
        if (taskObjectHolder.getValue() != null) {
            for (TaskObject taskObject : taskObjectHolder.getValue()) {
                if (taskObject.getHashID() == task.getHashID()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            task.setLastTimeModified(Calendar.getInstance());
                            // If task is set as repeatable object, delete existing repEvents and institute new ones
                            if (task.isThisRepeatingEvent()) {
                                // Establish if this task has the same value in descriptor as stored one...
                                TaskObject fromDB = dataBase.newDAO().findTaskObject(task.getHashID());
                                if (!fromDB.getRepeatDescriptor().equals(task.getRepeatDescriptor())
                                        || !fromDB.getTaskStartTime().equals(task.getTaskStartTime())
                                        || !fromDB.getTaskEndTime().equals(task.getTaskEndTime())) {
                                    // Removing the existing repeating events:
                                    deleteAllRepeatingEvents(task.getHashID());
                                    // Inserting new Values:
                                    dataBase.newDAO().insertRepeatingEvent(composeRepeatingEvents(task));
                                } else {
                                   String n = "Nothing has changed Milord.";
                                }
                            }
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
                        // Just institute new ones as this is new task and it should not have any repEvents
                    }
                }
            }).start();
        }
    }
    // A helper method that converts schema into repeating objects and
    private RepeatingEvent[] composeRepeatingEvents(TaskObject object) {
        // Define the objects start and end times to be the start and end times of the day:
        long[] newStartTime = returnStartAndEndTimesForDay(object.getTaskStartTime());
        long[] newEndTime = returnStartAndEndTimesForDay(object.getTaskEndTime());
        object.getTaskStartTime().setTimeInMillis(newStartTime[0] - 1);
        object.getTaskEndTime().setTimeInMillis(newEndTime[1] + 1);

        // Implement the creation of Repeating Events
        Calendar end, start;
        String[] results = object.getRepeatDescriptor().split("\\|");
        Integer type = null;
        ArrayList<RepeatingEvent> toReturn = new ArrayList<>();
        TaskObject.CheckableStatus objectsCheckableStatus = object.getIsTaskCompleted();
        if (objectsCheckableStatus == TaskObject.CheckableStatus.completed) {
            objectsCheckableStatus = TaskObject.CheckableStatus.incomplete;
        }
        TaskObject.CheckableStatus eventsCheckableStatus;
        Calendar currentTime = Calendar.getInstance();
        for (Integer i = 0; i<results.length; i++) {
            if (i == 0) {
                // Declaring type:
                type = Integer.valueOf(results[i]);
            } else {
                // Create new start and end value:
                start = (Calendar) object.getTaskStartTime().clone();
                end = (Calendar) object.getTaskEndTime().clone();
                String[] timeResult = results[i].split("-");
                if (timeResult.length == 1) {
                    // We have only one thus its a event
                    while (start.before(end)){
                        RepeatingEvent event;
                        switch (type) {
                            case 1: // every Day
                                // if checkable status is assigned to a time before now, we will set it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), (Calendar) start.clone(), null, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                // increment start
                                start.add(Calendar.DAY_OF_YEAR, 1);
                                break;
                            case 2: // custom Week
                                Calendar workValue = Calendar.getInstance();
                                workValue.setTimeInMillis(Long.valueOf(timeResult[0]));
                                workValue.set(YEAR, start.get(YEAR));
                                workValue.set(Calendar.WEEK_OF_YEAR, start.get(Calendar.WEEK_OF_YEAR));
                                // Create event:
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (workValue.after(object.getTaskStartTime()) && workValue.before(object.getTaskEndTime())) {
                                    if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                        if (start.before(currentTime)) {
                                            eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                        } else {
                                            eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                        }
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                    }
                                    event = new RepeatingEvent(object.getHashID(), workValue, null, 0, Calendar.getInstance(), eventsCheckableStatus);
                                    toReturn.add(event);
                                }
                                    // increment start
                                start.add(Calendar.WEEK_OF_YEAR, 1);
                                break;
                            case 3: // every 2 weeks
                                // Create Event:
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), (Calendar) start.clone(), null, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                start.add(Calendar.WEEK_OF_YEAR, 2);
                                // increment start
                                break;
                            case 4: // every month
                                // Create Event:
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), (Calendar) start.clone(), null, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                // increment start
                                start.add(Calendar.MONTH, 1);
                                break;
                            case 5: // every year
                                // Create Event:
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), (Calendar) start.clone(), null, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                // increment start
                                start.add(YEAR, 1);
                                break;
                        }
                    }
                } else if (timeResult.length == 2) {
                    // we have full fledged time frame
                    while(start.before(end)) {
                        RepeatingEvent event;
                        Calendar eventStartTime, eventEndTime;
                        eventStartTime = (Calendar) start.clone();
                        eventStartTime.setTimeInMillis(Long.valueOf(timeResult[0]));
                        long diff;
                        diff = Long.valueOf(timeResult[1]) - Long.valueOf(timeResult[0]);
                        switch (type) {
                            case 1:
                                eventStartTime.set(start.get(YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
                                eventEndTime = (Calendar) eventStartTime.clone();
                                eventEndTime.add(Calendar.MILLISECOND, (int) diff);
                                // Create RepEvent
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), eventStartTime, eventEndTime, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                // increment start
                                start.add(Calendar.DAY_OF_YEAR, 1);
                                break;
                            case 2:
                                // Implementation is different as it has specific requirements...
                                eventStartTime.setTimeInMillis(Long.valueOf(timeResult[0]));
                                eventStartTime.set(YEAR, start.get(YEAR));
                                eventStartTime.set(Calendar.WEEK_OF_YEAR, start.get(Calendar.WEEK_OF_YEAR));

                                eventEndTime = (Calendar) eventStartTime.clone();
                                eventEndTime.add(Calendar.MILLISECOND,(int) diff);
                                if (eventStartTime.after(object.getTaskStartTime())
                                        && eventStartTime.before(object.getTaskEndTime())) {
                                    // Create RepEvent
                                    // if checkable status is assigned to a time before now, we will mark it completed.
                                    if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                        if (start.before(currentTime)) {
                                            eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                        } else {
                                            eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                        }
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                    }
                                    event = new RepeatingEvent(object.getHashID(), eventStartTime, eventEndTime, 0, Calendar.getInstance(), eventsCheckableStatus);
                                    toReturn.add(event);
                                }
                                // increment start
                                start.add(Calendar.WEEK_OF_YEAR, 1);
                                break;
                            case 3:
                                eventStartTime.set(start.get(YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
                                eventEndTime = (Calendar) eventStartTime.clone();
                                eventEndTime.add(Calendar.MILLISECOND, (int) diff);
                                // Create RepEvent
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), eventStartTime, eventEndTime, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                // increment start
                                start.add(Calendar.WEEK_OF_YEAR, 2);
                                break;
                            case 4:
                                eventStartTime.set(start.get(YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
                                eventEndTime = (Calendar) eventStartTime.clone();
                                eventEndTime.add(Calendar.MILLISECOND, (int) diff);
                                // Create RepEvent

                                // increment start
                                // if checkable status is assigned to a time before now, we will mark it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), eventStartTime, eventEndTime, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                start.add(Calendar.MONTH, 1);
                                break;
                            case 5:
                                eventStartTime.set(start.get(YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
                                eventEndTime = (Calendar) eventStartTime.clone();
                                eventEndTime.add(Calendar.MILLISECOND, (int) diff);
                                // Create RepEvent

                                // increment start
                                // if checkable status is assigned to a time before now, we will set it completed.
                                if (objectsCheckableStatus != TaskObject.CheckableStatus.notCheckable) {
                                    if (start.before(currentTime)) {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.completed;
                                    } else {
                                        eventsCheckableStatus = TaskObject.CheckableStatus.incomplete;
                                    }
                                } else {
                                    eventsCheckableStatus = TaskObject.CheckableStatus.notCheckable;
                                }
                                event = new RepeatingEvent(object.getHashID(), eventStartTime, eventEndTime, 0, Calendar.getInstance(), eventsCheckableStatus);
                                toReturn.add(event);
                                start.add(YEAR, 1);
                                break;
                        }
                    }
                }
            }
        }
        RepeatingEvent[] toSend = new RepeatingEvent[toReturn.size()];
        toReturn.toArray(toSend);
        return toSend;
    }
    @Override
    public void deleteTask(final TaskObject objectToDelete) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Delete the children of type Repeating Events
                ArrayList<RepeatingEvent> eventsToDelete = getAllEventsBy(objectToDelete.getHashID());
                if (eventsToDelete.size() > 0) {
                    RepeatingEvent[] tempStorage = new RepeatingEvent[eventsToDelete.size()];
                    eventsToDelete.toArray(tempStorage);
                    dataBase.newDAO().removeRepeatingEvent(tempStorage);
                }
                dataBase.newDAO().removeTask(objectToDelete);
            }
        }).start();
    }
    @Override
    public LiveData<List<TaskObject>> getTasksForRemindersView(Calendar forDay) {

        Calendar dayStartTime = (Calendar) forDay.clone();
        dayStartTime.set(Calendar.HOUR_OF_DAY, 0);
        dayStartTime.set(Calendar.MINUTE, 0);
        dayStartTime.set(Calendar.SECOND, 0);
        dayStartTime.set(Calendar.MILLISECOND, 0);

        Calendar dayEndTime = (Calendar) forDay.clone();
        dayEndTime.set(Calendar.HOUR_OF_DAY, 23);
        dayEndTime.set(Calendar.MINUTE, 59);
        dayEndTime.set(Calendar.SECOND, 59);
        dayEndTime.set(Calendar.MILLISECOND, 999);

        return dataBase.newDAO().getReminderTasksForDay(dayStartTime.getTimeInMillis(), dayEndTime.getTimeInMillis());
    }


    // Repeating event:
    private ArrayList<RepeatingEvent> getAllEventsBy(int masterID) {
        ArrayList<RepeatingEvent> toReturn= new ArrayList<>();
        if (repeatingEventHolder.getValue() != null) {
            for (RepeatingEvent event: repeatingEventHolder.getValue()) {
                if (event.getParentID() == masterID) {
                    toReturn.add(event);
                }
            }
        }
        return toReturn;
    }
    @Override
    @Deprecated // This one doesn't provide right info back
    public LiveData<List<RepeatingEvent>> getEventsThatIntersect(Calendar day) {
            // Get the long values of start and end of day...
            long[] results = returnStartAndEndTimesForDay(day);

            return dataBase.newDAO().getEventThatIntersects(results[0], results[1]);
    }
    @Override
    public LiveData<List<RepeatingEvent>> getEventsForWeekView(Calendar forDay) {
        long[] results = returnStartAndEndTimesForDay(forDay);

        return dataBase.newDAO().getEventThatIntersects(results[0], results[1]);
    }
    @Override
    public RepeatingEvent getEventWith(int hashID) {
        if (repeatingEventHolder.getValue() != null) {
            for (RepeatingEvent event: repeatingEventHolder.getValue()) {
                if (event.getHashID() == hashID) {
                    return event;
                }
            }
        }
        return null;
    }
    @Override
    public LiveData<List<RepeatingEvent>> getAllRepeatingEvents() {
        return dataBase.newDAO().getAllRepeatingEvents();
    }
    @Override
    public void saveRepeatingEvent(final RepeatingEvent event) {
        if (repeatingEventHolder.getValue() != null) {
            for (final RepeatingEvent object : repeatingEventHolder.getValue()) {
                if (object.getHashID() == event.getHashID()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            event.setLastTimeModified(Calendar.getInstance());
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
    public void reSaveRepeatingEventsFor(int masterHashID) {
        // Find them all
        ArrayList<RepeatingEvent> list = new ArrayList<>();
        if (repeatingEventHolder.getValue() != null) {
            for (RepeatingEvent event : repeatingEventHolder.getValue()) {
                if (event.getParentID() == masterHashID) {
                    event.setLastTimeModified(Calendar.getInstance());
                    list.add(event);
                }
            }
            final RepeatingEvent[] toSaveInBulk = new RepeatingEvent[list.size()];
            list.toArray(toSaveInBulk);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataBase.newDAO().saveRepeatingEvent(toSaveInBulk);
                }
            }).start();
        }
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
    public void deleteAllRepeatingEvents(final int forMasterID) {
        ArrayList<RepeatingEvent> toDelete = getAllEventsBy(forMasterID);
        final RepeatingEvent[] toSend = new RepeatingEvent[toDelete.size()];
        toDelete.toArray(toSend);
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBase.newDAO().removeRepeatingEvent(toSend);
            }
        }).start();
    }
    @Override
    public LiveData<List<RepeatingEvent>> getEventsForRemindersView(Calendar forDay) {
        Calendar dayStartTime = (Calendar) forDay.clone();
        dayStartTime.set(Calendar.HOUR_OF_DAY, 0);
        dayStartTime.set(Calendar.MINUTE, 0);
        dayStartTime.set(Calendar.SECOND, 0);
        dayStartTime.set(Calendar.MILLISECOND, 0);

        Calendar dayEndTime = (Calendar) forDay.clone();
        dayEndTime.set(Calendar.HOUR_OF_DAY, 23);
        dayEndTime.set(Calendar.MINUTE, 59);
        dayEndTime.set(Calendar.SECOND, 59);
        dayEndTime.set(Calendar.MILLISECOND, 999);

        return dataBase.newDAO().getReminderEventsForDay(dayStartTime.getTimeInMillis(), dayEndTime.getTimeInMillis());
    }

    // List Objects:
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
    @Override // If there is one it will update it if not it will create new
    public void saveListObject(final ListObject objectToSave) {
        if (listObjectHolder.getValue() != null) {
            if (objectToSave.getHashID() != 0) {
                for (final ListObject object : listObjectHolder.getValue()) {
                    if (object.getHashID() == objectToSave.getHashID()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                objectToSave.setLastTimeModified(Calendar.getInstance());
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



    // Complex Goals Objects:
    @Override
    public void saveComplexGoal(final ComplexGoal goalToSave) {
        if (complexGoalHolder.getValue() != null) {
            for (ComplexGoal goal: complexGoalHolder.getValue()) {
                if (goal.getHashID() ==  goalToSave.getHashID()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            goalToSave.setLastTimeModified(Calendar.getInstance());
                            dataBase.newDAO().saveComplexTask(goalToSave);
                        }
                    }).start();
                    return;
                }
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dataBase.newDAO().insertComplexTask(goalToSave);
                }
            }).start();
        }
    }
    @Override
    public LiveData<List<ComplexGoal>>getAllComplexGoals() {
        return dataBase.newDAO().getAllComplexGoals();
    }
    @Override
    public ComplexGoal findComplexGoal(int byID) {
        if (complexGoalHolder.getValue() != null) {
            for (ComplexGoal goal: complexGoalHolder.getValue()) {
                if (goal.getHashID() == byID) { return goal; }
            }
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
    public void deleteComplexGoal(final ComplexGoal goal) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataBase.newDAO().removeComplexGoal(goal);
            }
        }).start();
    }

    // Month View Data provider additional Calls:
    public LiveData<List<TaskObject>> tasksForAYear(short year) {
        Calendar defCal = Calendar.getInstance();
        defCal.set(YEAR, year);
        long[] values = returnFirstDayAndLastDayOfYear(defCal);
        return dataBase.newDAO().getTaskThatIntersects(values[0], values[1]);
    }
    public LiveData<List<RepeatingEvent>> eventsForAYear(short year) {
        Calendar defCal = Calendar.getInstance();
        defCal.set(YEAR, year);
        long[] values = returnFirstDayAndLastDayOfYear(defCal);
        return dataBase.newDAO().getEventThatIntersects(values[0], values[1]);
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
    private long[] returnFirstDayAndLastDayOfYear(Calendar yearRef) {
        long[] toReturn = new long[2];
        yearRef.set(yearRef.get(YEAR),1, 1,0,0,0);
        toReturn[0] = yearRef.getTimeInMillis();
        yearRef.set(yearRef.get(YEAR), 12, 31,23,59,59);
        toReturn[1] = yearRef.getTimeInMillis();
        return toReturn;
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
    @Override
    public int findNextFreeHashIDForGoal() {
        if (complexGoalHolder.getValue() !=null && complexGoalHolder.getValue().size() > 0) {
            int biggestID = 0;
            for (ComplexGoal goal: complexGoalHolder.getValue()) {
                biggestID = Math.max(biggestID, goal.getHashID());
            }
            return biggestID +1;
        }
        return 0;
    }

    /* TODO: Consistency manager implementation:
    * The goal of this implementation is to in background do consistency check on relationships and
    * logic of the DataBase. The relationships between objects, cleanup and other things would be
    * evaluated and changes would be committed.
    *
    * This would also check with server to establish the consistency of both data-bases.
    *
    */
   private class ConsistencyManager {

       // Called to start the manager
       void start() {

       }
       /* This method checks the relationships between repeating event and task object and removes
        * ones that are not in sync.
        */
       void determineRepeatingTaskConsistency() {

       }
       /*
        * This method checks for consistency of mods in task Objects and adjusts the status of
        * those mods accordingly
        */
       void determineTaskObjectModConsistency() {

       }
       /*
        * This method checks for existence of complex goals defined in TaskObject and makes
        * changes to ensure consistency
        */
       void determineTaskObjectComplexGoalRelationships() {

       }
       /*
        * This method makes calls to server and establishes consistency between the data in server
        * and data in phone.
        */
       void syncWithServer() {

       }
       /*
        * For checkable tasks that are past due, I need to remove checkable attribute and put in
        * undefined tasks if after specific time period.
        */
       void updateTaskCheckableStatus() {}
   }

}
