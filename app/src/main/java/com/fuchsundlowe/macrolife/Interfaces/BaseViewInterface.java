package com.fuchsundlowe.macrolife.Interfaces;

import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListMaster;
import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.OrdinaryEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventMaster;
import com.fuchsundlowe.macrolife.DataObjects.RepeatingEventsChild;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;

import java.util.Set;

/**
 * Created by macbook on 1/26/18.
 */

public interface BaseViewInterface {
    // Needs to listen to the updates from dataClass
    // Holds a data to be displayed for the purpose of the View subclasses
    // Should it contain a class calls for location management? Like to call to check if its too close

    /*
     * What will this interface communicate?
     * Delivery of data
     * Deletion of data
     */

    public Set<ComplexGoalMaster> getComplexGoals();
    public void deleteObject(ComplexGoalMaster object);

    public Set<ListMaster>getAllListMasters();
    public void deleteObject(ListMaster object);

    public Set<ListObject>getListObjects();
    public void deleteObject(ListObject object);

    public Set<OrdinaryEventMaster>getAllOrdinaryEventMasters();
    public void deleteObject(OrdinaryEventMaster object);

    public Set<RepeatingEventMaster>getAllRepeatingEventMasterss();
    public void deleteObject(RepeatingEventMaster object);

    public Set<RepeatingEventsChild>getAllRepeatingEventChildren();
    public void deleteObject(RepeatingEventsChild object);

    public Set<SubGoalMaster>getAllSubGoalMasters();
    public void deleteObject(SubGoalMaster object);

    public void closeDatabase();
}
