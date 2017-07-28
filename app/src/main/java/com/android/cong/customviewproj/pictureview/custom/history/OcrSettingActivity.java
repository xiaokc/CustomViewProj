package com.android.cong.customviewproj.pictureview.custom.history;

import com.android.cong.customviewproj.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * TODO:
 * 1. 每次进入该页面，先检查sharedpreference中switch button的start状态
 * 2. 每次退出该设置页面时，更新sharedpreference中的switch button的start状态
 *
 * Created by xiaokecong on 28/07/2017.
 */

public class OcrSettingActivity extends AppCompatActivity {
    private Toolbar toolbarSetting;
    private TextView tvOcrEnable;
    private SwitchCompat switchOcrStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oct_setting);

        initView();

        initEvent();
    }

    private void initView() {
        toolbarSetting = (Toolbar) findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbarSetting);
        toolbarSetting.setNavigationIcon(R.drawable.left_arrow_white);

        tvOcrEnable = (TextView) findViewById(R.id.tv_ocr_enable);
        switchOcrStart = (SwitchCompat) findViewById(R.id.switch_ocr_start);
    }

    private void initEvent() {
        toolbarSetting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OcrSettingActivity.this, OcrHistoryActivity.class));
                finish();
            }
        });
        switchOcrStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tvOcrEnable.setText(isChecked ? "Enabled" : "Disabled");
            }
        });
    }
}
