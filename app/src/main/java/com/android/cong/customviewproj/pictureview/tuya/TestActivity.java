package com.android.cong.customviewproj.pictureview.tuya;

import com.android.cong.customviewproj.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

/**
 * Created by xiaokecong on 19/07/2017.
 */

public class TestActivity extends Activity {
//    TuyaImageView tuyaImageView;
    Button btnDrop;
    Button btnClear;
    Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tuya);

//        tuyaImageView = (TuyaImageView) findViewById(R.id.iv_tuya);
        btnDrop = (Button) findViewById(R.id.btn_drop);
        btnClear = (Button) findViewById(R.id.btn_clear);
        btnSave = (Button) findViewById(R.id.btn_save);

//        btnDrop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tuyaImageView.dropLastLine();
//            }
//        });
//
//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tuyaImageView.clearAllLines();
//            }
//        });
//
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tuyaImageView.save("/sdcard/tuya.png");
//            }
//        });
    }
}
