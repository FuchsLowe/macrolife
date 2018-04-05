package com.fuchsundlowe.macrolife.TestCases;


import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import com.fuchsundlowe.macrolife.R;

public class TestActivity3 extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        image = findViewById(R.id.imageView);


    }

    public void action1(View view) {
        Animatable j = (Animatable) image.getDrawable();
        j.start();
    }


}
