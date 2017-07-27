package com.android.cong.customviewproj.pictureview.test;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.custom.ToolbarTabView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xiaokecong on 27/07/2017.
 */

public class TestTabViewActivity extends Activity implements View.OnClickListener, View.OnTouchListener{
    private static final String TAG = "===>xkc";
    private ToolbarTabView tabEdit;
    private ToolbarTabView tabShare;
    private ToolbarTabView tabOcr;
    private ToolbarTabView tabDelete;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_tabview);

        tabEdit = (ToolbarTabView) findViewById(R.id.tab_edit);
        tabShare = (ToolbarTabView) findViewById(R.id.tab_share);
        tabOcr = (ToolbarTabView) findViewById(R.id.tab_ocr);
        tabDelete = (ToolbarTabView) findViewById(R.id.tab_delete);

        tabEdit.setOnClickListener(this);
        tabEdit.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                updateTouchDownColor(v);
                ((ToolbarTabView)v).setTouchDownColor(Color.parseColor("#2274e6"));
                break;
            case MotionEvent.ACTION_UP:
//                resetDefaultColor();
                ((ToolbarTabView)v).setDefaultColor(Color.parseColor("#99000000"));
                break;
        }
        return false;
    }

    private void updateTouchDownColor(View view) {
        switch (view.getId()) {
            case R.id.tab_edit:
                tabEdit.setTouchDownColor(Color.parseColor("#2274e6"));
                break;
        }
    }

    private void resetDefaultColor() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_edit:
                Log.i(TAG,"edit click");

                break;
        }
    }
}
