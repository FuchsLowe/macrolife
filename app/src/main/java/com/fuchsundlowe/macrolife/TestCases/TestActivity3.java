package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fuchsundlowe.macrolife.CustomViews.ComplexTaskChevron;
import com.fuchsundlowe.macrolife.DataObjects.ComplexGoalMaster;
import com.fuchsundlowe.macrolife.DataObjects.SourceType;
import com.fuchsundlowe.macrolife.DataObjects.SubGoalMaster;
import com.fuchsundlowe.macrolife.Interfaces.ComplexTaskInterface;
import com.fuchsundlowe.macrolife.R;
import java.io.IOException;
import java.net.URL;

public class TestActivity3 extends AppCompatActivity {

    ViewGroup master;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        master = findViewById(R.id.master_test);
        SubGoalMaster task = new SubGoalMaster(0,"Yalla",null, null, null,false, SourceType.local, 01, 10, 10, 10);

    }

    public float getScale() {
        return 1f;
    }
}
