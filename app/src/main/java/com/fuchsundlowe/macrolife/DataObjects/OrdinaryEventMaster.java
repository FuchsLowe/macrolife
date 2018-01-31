package com.fuchsundlowe.macrolife.DataObjects;

import android.arch.persistence.room.Entity;

import java.util.Calendar;

/**
 * Created by macbook on 1/29/18.
 * This is a holder class for ordinary Tasks
 */
@Entity(primaryKeys = {"hashID"})
public class OrdinaryEventMaster extends DataMasterClass {
    public OrdinaryEventMaster() {
        // Empty constructor as I expect that Room will find way to populate it via
        // base class.
    }

    /*
    TODO: Unclear if this needs any extra implementation...
    Maybe if I do isEvent then I can hold some recommendations for such day or event...
     */
}
