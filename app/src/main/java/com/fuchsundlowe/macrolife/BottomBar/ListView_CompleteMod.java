package com.fuchsundlowe.macrolife.BottomBar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.fuchsundlowe.macrolife.DataObjects.ListObject;
import com.fuchsundlowe.macrolife.DataObjects.TaskObject;
import com.fuchsundlowe.macrolife.EngineClasses.LocalStorage;
import com.fuchsundlowe.macrolife.Interfaces.DataProviderNewProtocol;
import com.fuchsundlowe.macrolife.Interfaces.EditTaskProtocol;
import com.fuchsundlowe.macrolife.R;

import java.util.Calendar;

public class ListView_CompleteMod extends FrameLayout {

    private View baseLayout;
    private ListView_RecyclerView recyclerView;
    private FrameLayout recyclerViewHolder;
    private TextView taskName;
    private EditText newTaskName;
    private DataProviderNewProtocol localData;
    private TaskObject ownerOfList;

    public ListView_CompleteMod(@NonNull Context context, final TaskObject ownerOfList, final EditTaskProtocol protocol) {
        super(context);
        this.ownerOfList = ownerOfList;

        localData = LocalStorage.getInstance(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        baseLayout = inflater.inflate(R.layout.listview_mod, this, true);

        recyclerViewHolder = baseLayout.findViewById(R.id.listViewHolder_RecyclerView);

        recyclerView = new ListView_RecyclerView(context);
        recyclerViewHolder.addView(recyclerView);
        recyclerView.defineMe(ownerOfList.getHashID());

        taskName = baseLayout.findViewById(R.id.taskName_listMod);
        taskName.setText(ownerOfList.getTaskName());

        newTaskName = baseLayout.findViewById(R.id.newTaskField_ListMod);
        newTaskName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (v.getText().length() > 0) {
                        // Create a new task, add it to shit and save it
                        int nextID = localData.findNextFreeHashIDForList();
                        ListObject newListTask = new ListObject(v.getText().toString(), false,
                                ownerOfList.getHashID(), nextID, Calendar.getInstance());
                        localData.saveListObject(newListTask);
                        recyclerView.addListObject(newListTask);
                        newTaskName.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        Button doneButton = baseLayout.findViewById(R.id.doneButton_listMod);
        doneButton.setText("DONE");
        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collapse the whole thing and produce
                protocol.modDone();
            }
        });
    }


}
