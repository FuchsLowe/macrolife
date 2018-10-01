package com.fuchsundlowe.macrolife.ListView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.MemoryFile;
import android.os.Process;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.Interfaces.AsyncSorterCommunication;
import com.fuchsundlowe.macrolife.ListView.ListView.bracketType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * Remember, Tasks have normal ID's while Events have inverted ones by value of *-1;
 */
public class AsyncSorter extends AsyncTask<Transporter, Void, Void> {

    private Calendar currentTime;
    private Transporter work;
    private Map<Integer, TaskEventHolder> newCompleted, newOverdue, newUpcoming, newUndefined;
    private Map<Integer, Integer> completedMasterTasks, incompleteMasterTasks; // TODO This might need to change if I am setting the
    // next task...
    private Map<Integer, TaskEventHolder> nextTask;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        currentTime = Calendar.getInstance();

        newCompleted = new HashMap<>();
        newOverdue = new HashMap<>();
        newUpcoming = new HashMap<>();
        newUndefined = new HashMap<>();

        completedMasterTasks = new HashMap<>();
        incompleteMasterTasks = new HashMap<>();
    }
    @Override
    protected Void doInBackground(Transporter... transporters) {
        work = transporters[0];
        if (!isCancelled() && work != null) {
            // I need to create the list of maps...
            work.initiateMaps();

            List<TaskEventHolder> completedToRemove = new ArrayList<>();
            List<TaskEventHolder> overdueToRemove = new ArrayList<>();
            List<TaskEventHolder> upcomingToRemove = new ArrayList<>();
            List<TaskEventHolder> undefinedToRemove = new ArrayList<>();
            for (int i = 0; i< work.sizeOfList(); i++) {
                if (!isCancelled()) {
                    // Wrapping:
                    TaskEventHolder holder;
                    if (work.areTasks()) {
                        holder = new TaskEventHolder(work.tasksToConvert.get(i), null);
                    } else {
                        holder = new TaskEventHolder(null, work.eventsToConvert.get(i));
                    }
                    // Sorting:
                    int key = holder.getActiveID();
                    if (!holder.isTask()) {
                        key *= -1;
                    }
                    switch (evaluate(holder)) {
                        case completed:
                            newCompleted.put(key, holder);
                            completedToRemove.add(holder);
                            // If we already established that whole set needs to change, then no need
                            // to re-check it again...
                            if (!work.editedCompleted) {
                                // Evaluate if there are changes made or if it even exists in previous List:
                                if (work.completed.containsKey(key)) {
                                    // Establish if the values have been changed:
                                    if (!holdersEquals(holder, work.completed.get(key))) {
                                        // means they are the same
                                        work.editedCompleted = true;
                                    }
                                } else {
                                    // Means that we don't have this one in previous set
                                    work.editedCompleted = true;
                                }
                            }
                            break;
                        case overdue:
                            newOverdue.put(key, holder);
                            overdueToRemove.add(holder);
                            if (!work.editedOverdue) {
                                if (work.overdue.containsKey(key)) {
                                    if (!holdersEquals(holder, work.overdue.get(key))) {
                                        work.editedOverdue = true;
                                    }
                                } else {
                                    work.editedOverdue = true;
                                }
                            }
                            break;
                        case upcoming:
                            newUpcoming.put(key, holder);
                            upcomingToRemove.add(holder);
                            if (!work.editedUpcoming) {
                                if (work.upcoming.containsKey(key)) {
                                    if (!holdersEquals(holder, work.upcoming.get(key))) {
                                        work.editedUpcoming = true;
                                    }
                                } else {
                                    work.editedUpcoming = true;
                                }
                            }
                            break;
                        case undefined:
                            newUndefined.put(key, holder);
                            undefinedToRemove.add(holder);
                            if (!work.editedUnassigned) {
                                if (work.unassigned.containsKey(key)) {
                                    if (!holdersEquals(holder, work.unassigned.get(key))) {
                                        work.editedUnassigned = true;
                                    }
                                } else {
                                    work.editedUnassigned = true;
                                }
                            }
                            break;
                    }
                } else { return null; }
            }
            // Evaluate if all of previous Array are the same as ones in this array:
            if (!isCancelled()) {
                if (!work.editedCompleted) {
                    for (TaskEventHolder oldHolder: work.mCompleted) {
                        int oldKey = oldHolder.getActiveID();
                        if (!oldHolder.isTask()) {
                            oldKey *= -1;
                        }
                        if (newCompleted.containsKey(oldKey)) {
                            if (!holdersEquals(oldHolder, newCompleted.get(oldKey))) {
                                work.editedCompleted = true;
                                break;
                            }
                        } else {
                            work.editedCompleted = true;
                            break;
                        }

                    }
                }
                if (!work.editedUnassigned) {
                    for (TaskEventHolder oldHolder: work.mUnassigned) {
                        int oldKey = oldHolder.getActiveID();
                        if (!oldHolder.isTask()) {
                            oldKey *= -1;
                        }
                        if (newUndefined.containsKey(oldKey)) {
                            if (!holdersEquals(oldHolder, newUndefined.get(oldKey))) {
                                work.editedUnassigned = true;
                                break;
                            }
                        } else {
                            work.editedUnassigned = true;
                            break;
                        }
                    }
                }
                if (!work.editedUpcoming) {
                    for (TaskEventHolder oldHolder: work.mUpcoming) {
                        int oldKey = oldHolder.getActiveID();
                        if (!oldHolder.isTask()) {
                            oldKey *= -1;
                        }
                        if (newUpcoming.containsKey(oldKey)) {
                            if (!holdersEquals(oldHolder, newUpcoming.get(oldKey))) {
                                work.editedUpcoming = true;
                                break;
                            }
                        } else {
                            work.editedUpcoming = true;
                            break;
                        }
                    }
                }
                if (!work.editedOverdue) {
                    for (TaskEventHolder oldHolder: work.mOverdue) {
                        int oldKey = oldHolder.getActiveID();
                        if (!oldHolder.isTask()) {
                            oldKey *= -1;
                        }
                        if (newOverdue.containsKey(oldKey)) {
                            if (!holdersEquals(oldHolder, newOverdue.get(oldKey))) {
                                work.editedOverdue = true;
                                break;
                            }
                        } else {
                            work.editedOverdue = true;
                            break;
                        }
                    }
                }
            } else { return null; }
            // Done sorting, now we need to determine which ones have been changed...
            if (!isCancelled()) {
                if (work.editedCompleted) {
                    // Remove old ones and put new ones...
                    work.mCompleted.removeAll(completedToRemove);
                    work.mCompleted.addAll(newCompleted.values());
                    sortCompleted();
                }
                if (work.editedOverdue) {
                    work.mOverdue.removeAll(overdueToRemove);
                    work.mOverdue.addAll(newOverdue.values());
                    sortOverdue();
                }
                if (work.editedUpcoming) {
                    work.mUpcoming.removeAll(upcomingToRemove);
                    work.mUpcoming.addAll(newUpcoming.values());
                    sortUpcoming();
                }
                if (work.editedUnassigned) {
                    work.mUnassigned.removeAll(undefinedToRemove);
                    work.mUnassigned.addAll(newUndefined.values());
                    sortUnassigned();
                }
            } else { return null; }
            /*
            * Last thing is to count the number of Holders that have master tasks... We are doing this
            * now because until the end we didn't know if lists have changed or not, thus wanna remove
            * any inconsistency that would occurred otherwise.
            */
            for (TaskEventHolder completedHolder :work.mCompleted) {
                Integer complexGoalID = completedHolder.getComplexGoalID();
                if (complexGoalID != null && complexGoalID > 0) {
                    completedMasterTasks.put(complexGoalID, completedMasterTasks.get(complexGoalID) +1);
                }
            }
            for (TaskEventHolder incompleteHolder: work.mUnassigned) {
                Integer complexGoalID = incompleteHolder.getComplexGoalID();
                if (complexGoalID != null && complexGoalID > 0) {
                    incompleteMasterTasks.put(complexGoalID, incompleteMasterTasks.get(complexGoalID) +1);
                }
            }
            for (TaskEventHolder incompleteHolder: work.mUpcoming) {
                Integer complexGoalID = incompleteHolder.getComplexGoalID();
                if (complexGoalID != null && complexGoalID > 0) {
                    incompleteMasterTasks.put(complexGoalID, incompleteMasterTasks.get(complexGoalID) +1);
                    TaskEventHolder mNextTask = nextTask.get(complexGoalID);
                    if (nextTask != null) {
                        if (mNextTask.getStartTime().after(incompleteHolder.getStartTime())) {
                            nextTask.put(complexGoalID, incompleteHolder);
                        }
                    } else {
                        nextTask.put(complexGoalID, incompleteHolder);
                    }
                }
            }
            for (TaskEventHolder incompleteHolder: work.mOverdue) {
                Integer complexGoalID = incompleteHolder.getComplexGoalID();
                if (complexGoalID != null && complexGoalID > 0) {
                    incompleteMasterTasks.put(complexGoalID, incompleteMasterTasks.get(complexGoalID) +1);
                    TaskEventHolder mNextTask = nextTask.get(complexGoalID);
                    if (mNextTask != null) {
                        if (mNextTask.getStartTime().after(incompleteHolder.getStartTime())) {
                            nextTask.put(complexGoalID, incompleteHolder);
                        }
                    } else {
                        nextTask.put(complexGoalID, incompleteHolder);
                    }
                }
            }
        } else { return null; }
        return null;
    }
    @Override
    protected void onCancelled() {
        super.onCancelled();
        resetValues();
    }
    @Override
    protected void onPostExecute(Void v) {
        super.onPostExecute(v);
        if (!isCancelled()) {
            // Reports newCompleted state of whatever did it do...
            AsyncSorterCommunication reportToParent = work.parent;
            // Reporting system:
            if (work.editedCompleted) {
                reportToParent.changedCompleted();
            }
            if (work.editedUnassigned) {
                reportToParent.changedUnassigned();
            }
            if (work.editedOverdue) {
                reportToParent.changedOverdue();
            }
            if (work.editedUpcoming) {
                reportToParent.changedUpcoming();
            }

            if (work.areTasks()) {
                reportToParent.markTasksReady();
            } else {
                reportToParent.markEventsReady();
            }

            reportToParent.deliverNewComplexTotals(completedMasterTasks, incompleteMasterTasks, nextTask);

            reportToParent.flushChanges();
        }
        resetValues();
        // We are done now, ready for another call if arieses.
    }

    // Support methods:

    // This method evaluates the holder and returns its target bracket
    private bracketType evaluate(TaskEventHolder holder) {
        // TODO : Evaluate the time it takes to calculate this...
        long nan = System.nanoTime();
        Calendar currentTime = Calendar.getInstance();
        long nan2 = System.nanoTime();
        long resForCal = nan2 -nan;

        bracketType reportType = null;
        switch (holder.getCompletionState()) {
            case completed:
                reportType = bracketType.completed;
                break;
            case incomplete:
                /*
                 * if its incomplete... establish if its reminder or t/e...
                 * if reminder determine if day has passed
                 *
                 * if t/e establish end time if has passed
                 */
                switch (holder.getTimeDefined()) {
                    case noTime:
                        reportType = bracketType.undefined;
                        break;
                    case onlyDate:
                        // meaning its reminder, has the day passed?
                        if (hasDayPassed(holder.getStartTime())) {
                            reportType = bracketType.overdue;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                    case dateAndTime:
                        if (holder.getEndTime().before(currentTime)) {
                            // means that it has passed
                            reportType = bracketType.overdue;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                }
                break;
            case notCheckable:
                switch (holder.getTimeDefined()) {
                    case noTime:
                        reportType = bracketType.undefined;
                        break;
                    case onlyDate:
                        if (hasDayPassed(holder.getStartTime())) {
                            reportType = bracketType.completed;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                    case dateAndTime:
                        if (holder.getEndTime().before(currentTime)) {
                            reportType = bracketType.completed;
                        } else {
                            reportType = bracketType.upcoming;
                        }
                        break;
                }
                break;
        }


        long total = System.nanoTime();
        long totalResult = total - nan;

        return reportType;
    }
    // a helper method that determines if reminder has passed or not
    private boolean hasDayPassed(Calendar day) {
        if (currentTime == null) {
            currentTime = Calendar.getInstance();
        }
        if (currentTime.get(Calendar.YEAR) == day.get(Calendar.YEAR)) {
            return currentTime.get(Calendar.DAY_OF_YEAR) > day.get(Calendar.DAY_OF_YEAR);
        } else {
            return currentTime.get(Calendar.YEAR) > day.get(Calendar.YEAR);
        }
    }
    // Evaluates if two Holders are equal...
    private boolean holdersEquals(TaskEventHolder first, TaskEventHolder second) {
       return first.getLastTimeModified().getTimeInMillis() == second.getLastTimeModified().getTimeInMillis();
    }
    // Sorting Systems:
    private void sortCompleted() {
        /*
         * For now I am only using insertion sort, and latter I will use insertion sort for up to
         * N elements in array and then I will use QuickSort for over than N elements in array.
         * For now I think that N should be ~1000
         */
        // Insertion Sort System
        List<TaskEventHolder> listToWorkWith = work.mCompleted;
        int n = listToWorkWith.size();
        for (int i=1; i<n; ++i)
        {
            TaskEventHolder value = listToWorkWith.get(i);
            int j = i-1;

            while (j>=0 && compareByStartTime(listToWorkWith.get(j), value))
            {
                listToWorkWith.add(j+1, value);
                j = j-1;
            }
            listToWorkWith.add(j+1, value);
        }
    }
    private void sortUnassigned() {
        /*
         * TODO: Logic... how will this be defined?
         */
    }
    private void sortOverdue() {
        // Insertion Sort System
        List<TaskEventHolder> listToWorkWith = work.mOverdue;
        int n = listToWorkWith.size();
        for (int i=1; i<n; ++i)
        {
            TaskEventHolder value = listToWorkWith.get(i);
            int j = i-1;

            while (j>=0 && compareForOverdue(listToWorkWith.get(j), value))
            {
                listToWorkWith.add(j+1, value);
                j = j-1;
            }
            listToWorkWith.add(j+1, value);
        }

    }
    private void sortUpcoming() {
// Insertion Sort System
        List<TaskEventHolder> listToWorkWith = work.mUpcoming;
        int n = listToWorkWith.size();
        for (int i=1; i<n; ++i)
        {
            TaskEventHolder value = listToWorkWith.get(i);
            int j = i-1;

            while (j>=0 && compareByStartTime(listToWorkWith.get(j), value))
            {
                listToWorkWith.add(j+1, value);
                j = j-1;
            }
            listToWorkWith.add(j+1, value);
        }    }

    // returns true if one should be after two
    private boolean compareByStartTime(TaskEventHolder one, TaskEventHolder two) {
        // We evaluate the Start time, since some don't have end time
        return one.getStartTime().getTimeInMillis() > two.getStartTime().getTimeInMillis();
    }
    /*
     * Uses end time as comparison argument, but if there is no end time uses end of day
     * from start time as argument...
     */
    private boolean compareForOverdue(TaskEventHolder one, TaskEventHolder two) {
        Calendar oneTime, twoTime;
        if (one.getEndTime() != null && one.getEndTime().after(one.getStartTime())) {
            oneTime = one.getEndTime();
        } else {
            oneTime = (Calendar) one.getStartTime().clone();
            changeToEndDay(oneTime);
        }
        if (two.getEndTime() != null && two.getEndTime().after(two.getStartTime())) {
            twoTime = two.getEndTime();
        } else {
            twoTime = (Calendar) two.getStartTime().clone();
            changeToEndDay(twoTime);
        }

        return oneTime.getTimeInMillis() > twoTime.getTimeInMillis();
    }

    private void changeToEndDay(Calendar value) {
        value.set(Calendar.HOUR_OF_DAY, 23);
        value.set(Calendar.MINUTE, 59);
        value.set(Calendar.SECOND, 59);
    }
    // called to release memory on objects we hold.
    private void resetValues() {
        work = null;
        currentTime = null;
    }

}
