package com.android.cong.customviewproj.pictureview.test;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.custom.ShowAndEditActivity;
import com.android.cong.customviewproj.pictureview.custom.history.OcrHistoryActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by xiaokecong on 31/07/2017.
 */

public class TestMainActivity extends Activity{
    private Intent intent;
    private String imagePath = "/storage/emulated/0/Pictures/Screenshots/S70322-162832.jpg";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_main);

        findViewById(R.id.btn_set_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(TestMainActivity.this, ShowAndEditActivity.class);
                intent.putExtra("intentFrom","screenshot");
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("isShow", true);
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.screenshot3);
//                intent.putExtra("bitmap",bitmap);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(TestMainActivity.this, OcrHistoryActivity.class);
                startActivity(intent);
            }
        });
    }
}
