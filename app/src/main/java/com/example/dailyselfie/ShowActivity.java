package com.example.dailyselfie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent intent = getIntent();
        byte[] bytes = intent.getExtras().getByteArray("img_show");

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

        ((ImageView) findViewById(R.id.iv_show)).setImageBitmap(bitmap);
    }
}