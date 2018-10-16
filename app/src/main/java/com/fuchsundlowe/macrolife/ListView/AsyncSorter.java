package com.fuchsundlowe.macrolife.ListView;

import android.os.AsyncTask;
import android.os.Process;
import android.util.SparseArray;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.Interfaces.AsyncSorterCommunication;
import com.fuchsundlowe.macrolife.ListView.ListView.bracketType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
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

        nextTask = new HashMap<>();

        completedMasterTasks = new HashMap<>();
        incompleteMasterTasks = new HashMap<>();
    }
    @Override
    protected Void doInBackground(Transporter... transporters) {
        work = transporters[0];
        if (!isCancelled() && work != null) {
            // I need to create the list of maps...
            work.initiateMaps();
            for (int i = 0; i< work.sizeOfList(); i++) {
                if (!isCancelled()) {
                    // Wrapping:
                    TaskEventHolder mHolder;
                    if (work.areTasks()) {
                        mHolder = new TaskEventHolder(work.virginTasks.get(i), null);
                    } else {
                        mHolder = new TaskEventHolder(null, work.virginEvents.get(i));
                    }
                    // Sorting:
                    int mHoldersKey = mHolder.getActiveID();
                    if (!mHolder.isTask()) {
                        mHoldersKey *= -1;
                    }
                    switch (evaluate(mHolder)) {
                        case completed:
                            newCompleted.put(mHoldersKey, mHolder);
                            // If we already established that whole set needs to change, then no need
                            // to re-check it again...
                            if (!work.editedCompleted) {
                                // Evaluate if there are changes made or if it even exists in previous List:
                                if (work.oldCompletedMap.containsKey(mHoldersKey)) {
                                    // Establish if the values have been changed:
                                    if (!holdersEquals(mHolder, work.oldCompletedMap.get(mHoldersKey))) {
                                        // means they are not the same
                                        work.editedCompleted = true;
                                    }
                                } else {
                                    // Means that we don't have this one in previous set
                                    work.editedCompleted = true;
                                }
                            }
                            break;
                        case overdue:
                            newOverdue.put(mHoldersKey, mHolder);
                            if (!work.editedOverdue) {
                                if (work.oldOverdueMap.containsKey(mHoldersKey)) {
                                    if (!holdersEquals(mHolder, work.oldOverdueMap.get(mHoldersKey))) {
                                        work.editedOverdue = true;
                                    }
                                } else {
                                    work.editedOverdue = true;
                                }
                            }
                            break;
                        case upcoming:
                            newUpcoming.put(mHoldersKey, mHolder);
                            if (!work.editedUpcoming) {
                                if (work.oldUpcomingMap.containsKey(mHoldersKey)) {
                                    if (!holdersEquals(mHolder, work.oldUpcomingMap.get(mHoldersKey))) {
                                        work.editedUpcoming = true;
                                    }
                                } else {
                                    work.editedUpcoming = true;
                                }
                            }
                            break;
                        case undefined:
                            newUndefined.put(mHoldersKey, mHolder);
                            if (!work.editedUnassigned) {
                                if (work.oldUnassignedMap.containsKey(mHoldersKey)) {
                                    if (!holdersEquals(mHolder, work.oldUnassignedMap.get(mHoldersKey))) {
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
                    for (TaskEventHolder oldHolder: work.oldCompleted) {
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
                    for (TaskEventHolder oldHolder: work.oldUnassigned) {
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
                    for (TaskEventHolder oldHolder: work.oldUpcoming) {
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
                    for (TaskEventHolder oldHolder: work.oldOverdue) {
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
                    deleteCompleted(work.areTasks());
                    work.oldCompleted.addAll(newCompleted.values());
                    sortCompleted();
                }
                if (work.editedOverdue) {
                    deleteOverdue(work.areTasks());
                    work.oldOverdue.addAll(newOverdue.values());
                    sortOverdue();
                }
                if (work.editedUpcoming) {
                    deleteUpcoming(work.areTasks());
                    work.oldUpcoming.addAll(newUpcoming.values());
                    sortUpcoming();
                }
                if (work.editedUnassigned) {
                    deleteUnassigned(work.areTasks());
                    work.oldUnassigned.addAll(newUndefined.values());
                    sortUnassigned();
                }
            } else { return null; }
            /*
            * Last thing is to count the number of Holders that have master tasks... We are doing this
            * now because until the end we didn't know if lists have changed or not, thus wanna remove
            * any inconsistency that would occurred otherwise.
            */
            for (TaskEventHolder completedHolder :work.oldCompleted) {
                Integer complexGoalID = completedHolder.getComplexGoalID();
                if (complexGoalID != null && complexGoalID > 0) {
                    completedMasterTasks.put(complexGoalID, completedMasterTasks.get(complexGoalID) +1);
                }
            }
            for (TaskEventHolder incompleteHolder: work.oldUnassigned) {
                Integer complexGoalID = incompleteHolder.getComplexGoalID();
                if (complexGoalID != null && complexGoalID > 0) {
                    incompleteMasterTasks.put(complexGoalID, incompleteMasterTasks.get(complexGoalID) +1);
                }
            }
            for (TaskEventHolder incompleteHolder: work.oldUpcoming) {
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
            for (TaskEventHolder incompleteHolder: work.oldOverdue) {
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
        List<TaskEventHolder> listToWorkWith = work.oldCompleted;
        for (int i = 1; i<listToWorkWith.size(); i++) {
            TaskEventHolder mark = listToWorkWith.get(i);
            int toEvalPos = i -1;
            while (toEvalPos > 0 && compareByStartTime(listToWorkWith.get(toEvalPos), mark)) {
                listToWorkWith.set(toEvalPos +1, listToWorkWith.get(toEvalPos));
                toEvalPos --;
            }
            listToWorkWith.set(toEvalPos + 1, mark);
        }
    }
    private void sortUnassigned() {
        /*
         * TODO: Logic... how will this be defined?
         */
    }
    private void sortOverdue() {
        // Insertion Sort System
        List<TaskEventHolder> listToWorkWith = work.oldOverdue;
        for (int i = 1; i< listToWorkWith.size(); i++) {
            TaskEventHolder mark = listToWorkWith.get(i);
            int toEvalPos = i -1;
            while (toEvalPos > 0 && compareForOverdue(listToWorkWith.get(toEvalPos), mark)) {
                listToWorkWith.set(toEvalPos +1, listToWorkWith.get(toEvalPos));
                toEvalPos --;
            }
            listToWorkWith.set(toEvalPos +1, mark);
        }
    }
    private void sortUpcoming() {
        // Insertion Sort System
        List<TaskEventHolder> listToWorkWith = work.oldUpcoming;
        for (int i = 1; i< listToWorkWith.size(); i++) {
            TaskEventHolder mark = listToWorkWith.get(i);
            int toEvalPos = i -1;
            while (toEvalPos > 0 && compareByStartTime(listToWorkWith.get(toEvalPos), mark)) {
                listToWorkWith.set(toEvalPos +1, listToWorkWith.get(toEvalPos));
                toEvalPos --;
            }
            listToWorkWith.set(toEvalPos +1, mark);
        }
    }

    // Removal Systems:
    private void deleteCompleted(boolean deleteTasks) {
        LinkedList<TaskEventHolder> toDelete = new LinkedList<>();
        for (TaskEventHolder holder : work.oldCompleted) {
            if (deleteTasks) {
                if (holder.isTask()) {
                    toDelete.add(holder);
                }
            } else {
                if (!holder.isTask()) {
                    toDelete.add(holder);
                }
            }
        }
        work.oldCompleted.removeAll(toDelete);
    }
    private void deleteUnassigned(boolean deleteTasks) {
        LinkedList<TaskEventHolder> toDelete = new LinkedList<>();
        for (TaskEventHolder holder : work.oldUnassigned) {
            if (deleteTasks) {
                if (holder.isTask()) {
                    toDelete.add(holder);
                }
            } else {
                if (!holder.isTask()) {
                    toDelete.add(holder);
                }
            }
        }
        work.oldUnassigned.removeAll(toDelete);
    }
    private void deleteOverdue(boolean deleteTasks) {
        LinkedList<TaskEventHolder> toDelete = new LinkedList<>();
        for (TaskEventHolder holder : work.oldOverdue) {
            if (deleteTasks) {
                if (holder.isTask()) {
                    toDelete.add(holder);
                }
            } else {
                if (!holder.isTask()) {
                    toDelete.add(holder);
                }
            }
        }
        work.oldOverdue.removeAll(toDelete);
    }
    private void deleteUpcoming(boolean deleteTasks) {
        LinkedList<TaskEventHolder> toDelete = new LinkedList<>();
        for (TaskEventHolder holder : work.oldUpcoming) {
            if (deleteTasks) {
                if (holder.isTask()) {
                    toDelete.add(holder);
                }
            } else {
                if (!holder.isTask()) {
                    toDelete.add(holder);
                }
            }
        }
        work.oldUpcoming.removeAll(toDelete);
    }

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
