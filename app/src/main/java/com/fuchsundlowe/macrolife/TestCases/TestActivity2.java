package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.fuchsundlowe.macrolife.R;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class TestActivity2 extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        image = findViewById(R.id.imageViewTest);
        loadImage();
    }

    private void loadImage() {
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

    }
}
