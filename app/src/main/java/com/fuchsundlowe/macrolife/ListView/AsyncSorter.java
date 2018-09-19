package com.fuchsundlowe.macrolife.ListView;

import android.os.AsyncTask;
import android.os.Process;

import com.fuchsundlowe.macrolife.DataObjects.TaskEventHolder;
import com.fuchsundlowe.macrolife.Interfaces.P3;
import com.fuchsundlowe.macrolife.ListView.ListDataController.bracketType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/*
 * Remember, Tasks have normal ID's while Events have inverted ones by value of *-1;
 */
public class AsyncSorter extends AsyncTask<Transporter, Void, Void> {

    private Calendar currentTime;
    private Transporter work;
    private List<TaskEventHolder> completed, overdue, upcoming, undefined;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
        currentTime = Calendar.getInstance();

        completed = new ArrayList<>();
        overdue = new ArrayList<>();
        upcoming = new ArrayList<>();
        undefined = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Transporter... transporters) {
        work = transporters[0];
        if (!isCancelled() && work != null) {
            // I need to create the list of maps...
            work.initiateMaps();

            List<Integer> completedToRemove = new ArrayList<>();
            List<Integer> overdueToRemove = new ArrayList<>();
            List<Integer> upcomingToRemove = new ArrayList<>();
            List<Integer> undefinedToRemove = new ArrayList<>();
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
                            completed.add(holder);
                            completedToRemove.add(key);
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
                            overdue.add(holder);
                            overdueToRemove.add(key);
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
                            upcoming.add(holder);
                            upcomingToRemove.add(key);
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
                            undefined.add(holder);
                            undefinedToRemove.add(key);
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
                } else {
                    return null;
                }
            }

            // TODO: Evaluate if now there are some that have been removed from previous lot
            // Evaluate if all of previous Array are the same as ones in this array:
            if (!isCancelled()) {
                if (!work.editedCompleted) {
                    for (TaskEventHolder oldHolder: work.mCompleted) {
                        /*
                         * Now I am checking if all of the old ones are found in new ones
                         * So how can I do that? How are new ones stored then?
                         */
                        if (completed.contains(oldHolder)) {
                            completed.
                        }

                    }
                }
            } else {
                return null;
            }
            // Done sorting, now we need to determine which ones have been changed...
            if (!isCancelled()) {
                if (work.editedCompleted) {
                    // means that we need to re-define this one...
                    // removing the values:
                    for (Long id: completedToRemove) {
                        // TODO: Wrong... Should Remove the list
                        work.completed.remove(id);
                    }
                    // adding the new set:
                    for (TaskEventHolder holderToAdd: completed) {
                        long key;
                        if (holderToAdd.isTask()) {
                            key = holderToAdd.getActiveID();
                        } else {
                            key = holderToAdd.getActiveID() *-1;
                        }
                        work.completed.put(key, holderToAdd);
                    }
                    // Used to sort in order the List
                    sortCompleted();
                    // Replace lists TODO
                }
                // TODO: Complete the rest...
            } else {
                return null;
            }
        } else {
            return null;
        }
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
            // Reports completed state of whatever did it do...
            P3 reportToParent = work.parent;
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

            reportToParent.flushChanges();
        }
        resetValues();
        // We are done now, ready for another call if arieses.
    }

    // mark Support Methods:
    // This method evaluates the holder and returns its target bracket
    private bracketType evaluate(TaskEventHolder holder) {
        // TODO : Evaluate the time it takes to calculate this...
        long nan = System.nanoTime();
        Calendar currentTime = Calendar.getInstance();
        long nan2 = System.nanoTime();
        long resForCal = nan2 -nan;

        ListDataController.bracketType reportType = null;
        switch (holder.getCompletionState()) {
            case completed:
                reportType = ListDataController.bracketType.completed;
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
                        reportType = ListDataController.bracketType.undefined;
                        break;
                    case onlyDate:
                        // meaning its reminder, has the day passed?
                        if (hasDayPassed(holder.getStartTime())) {
                            reportType = ListDataController.bracketType.overdue;
                        } else {
                            reportType = ListDataController.bracketType.upcoming;
                        }
                        break;
                    case dateAndTime:
                        if (holder.getEndTime().before(currentTime)) {
                            // means that it has passed
                            reportType = ListDataController.bracketType.overdue;
                        } else {
                            reportType = ListDataController.bracketType.upcoming;
                        }
                        break;
                }
                break;
            case notCheckable:
                switch (holder.getTimeDefined()) {
                    case noTime:
                        reportType = ListDataController.bracketType.undefined;
                        break;
                    case onlyDate:
                        if (hasDayPassed(holder.getStartTime())) {
                            reportType = ListDataController.bracketType.completed;
                        } else {
                            reportType = ListDataController.bracketType.upcoming;
                        }
                        break;
                    case dateAndTime:
                        if (holder.getEndTime().before(currentTime)) {
                            reportType = ListDataController.bracketType.completed;
                        } else {
                            reportType = ListDataController.bracketType.upcoming;
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
        int n = completed.size();
        for (int i=1; i<n; ++i)
        {
            TaskEventHolder value = completed.get(i);
            int j = i-1;

            while (j>=0 && evaluateForCompleteSort(completed.get(j), value))
            {
                completed.add(j+1, value);
                j = j-1;
            }
            completed.add(j+1, value);
        }
    }
    // returns true if one should be after two
    private boolean evaluateForCompleteSort(TaskEventHolder one, TaskEventHolder two) {
        // We evaluate the Start time, since some don't have end time
        return one.getStartTime().getTimeInMillis() > two.getStartTime().getTimeInMillis();
    }
    private void sortUnassigned() {
        // TODO Implemnet
    }
    private void sortOverdue() {

    }
    private void sortUpcoming() {

    }
    // called to release memory on objects we hold.
    private void resetValues() {
        work = null;
        currentTime = null;
    }

}
