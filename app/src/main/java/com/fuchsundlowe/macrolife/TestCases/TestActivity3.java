package com.fuchsundlowe.macrolife.TestCases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import com.fuchsundlowe.macrolife.R;
import java.io.IOException;
import java.net.URL;

public class TestActivity3 extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);
        image = findViewById(R.id.imageView);
        loadImage();
    }


    private void loadImage() {
        final String location = "https://orig00.deviantart.net/8690/f/2011/068/b/5/black_and_white_swan_by_sku1c-d3b9pz1.jpg";
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Pee", "Sam");
        return super.onTouchEvent(event);
    }

}
