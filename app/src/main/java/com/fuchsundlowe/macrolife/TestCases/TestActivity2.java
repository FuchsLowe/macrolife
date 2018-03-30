package com.fuchsundlowe.macrolife.TestCases;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.fuchsundlowe.macrolife.R;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class TestActivity2 extends AppCompatActivity {

    ImageView image;
    float mScaleFactor;
    ScaleGestureDetector detector;
    ScrollView ns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        loadImage();
    }

    private void loadImage() {
        ns = findViewById(R.id.scroller);
        image = new ImageView(this);
        image.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ns.addView(image);
        final String location = "https://cpb-us-east-1-juc1ugur1qwqqqo4.stackpathdns.com/wp.wwu.edu/dist/5/1453/files/2017/01/the-black-swan-2758q6y.jpg";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(location);
                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    image.post(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageBitmap(bmp);
                        }
                    });
                } catch (IOException error) {
                    Log.e("Failed to load", error.getLocalizedMessage());
                }
            }
        }).start();

        image.setMinimumHeight(480);
    }


    public void clickOne(View view) {
        ObjectAnimator java = ObjectAnimator.ofFloat(image,"scaleY", 3f);
        java.setDuration(3000);
        java.start();
        ns.requestLayout();
    }




}
