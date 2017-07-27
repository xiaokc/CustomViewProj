package com.android.cong.customviewproj.pictureview.scaleupdown;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.pictureview.custom.CustomImageView;
import com.android.cong.customviewproj.pictureview.custom.OnBitmapSaveListener;
import com.android.cong.customviewproj.pictureview.custom.OnViewClickListener;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Trace;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by xiaokecong on 20/07/2017.
 */

public class ScaleActivity extends Activity implements View.OnClickListener {
    private CustomImageView customImageView;
    private Button btnSave;
    private ImageView btnRevoke;

    private ImageView ivColorWhite;
    private ImageView ivColorBlack;
    private ImageView ivColorRed;
    private ImageView ivColorOrange;
    private ImageView ivColorYellow;
    private ImageView ivColorGreen;
    private ImageView ivColorBlue;
    private ImageView ivColorPurple;

    private Map<Integer, Integer> viewMap;
    private boolean isFirst = true;

    private boolean isClick = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);

        initView();

        initEvent();
    }

    private void initView() {
        customImageView = (CustomImageView) findViewById(R.id.iv_image);
        btnRevoke = (ImageView) findViewById(R.id.btn_revoke);
        btnSave = (Button) findViewById(R.id.btn_edit_save);

        ivColorWhite = (ImageView) findViewById(R.id.iv_color_white);
        ivColorBlack = (ImageView) findViewById(R.id.iv_color_black);
        ivColorRed = (ImageView) findViewById(R.id.iv_color_red);
        ivColorOrange = (ImageView) findViewById(R.id.iv_color_orange);
        ivColorYellow = (ImageView) findViewById(R.id.iv_color_yellow);
        ivColorGreen = (ImageView) findViewById(R.id.iv_color_green);
        ivColorBlue = (ImageView) findViewById(R.id.iv_color_blue);
        ivColorPurple = (ImageView) findViewById(R.id.iv_color_purple);

        viewMap = new HashMap<>();
        viewMap.put(R.id.iv_color_white, R.id.iv_color_white_tag);
        viewMap.put(R.id.iv_color_black, R.id.iv_color_black_tag);
        viewMap.put(R.id.iv_color_red, R.id.iv_color_red_tag);
        viewMap.put(R.id.iv_color_orange, R.id.iv_color_orange_tag);
        viewMap.put(R.id.iv_color_yellow, R.id.iv_color_yellow_tag);
        viewMap.put(R.id.iv_color_green, R.id.iv_color_green_tag);
        viewMap.put(R.id.iv_color_blue, R.id.iv_color_blue_tag);
        viewMap.put(R.id.iv_color_purple, R.id.iv_color_purple_tag);

    }

    private void initEvent() {

//        customImageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        isClick = false;
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        if (isClick) {
//                            isClick = true;
//                            Toast.makeText(ScaleActivity.this,"点击事件",Toast.LENGTH_LONG).show();
//                        }
//                        return true;
//                }
//                return false;
//            }
//        });

        btnRevoke.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        ivColorWhite.setOnClickListener(this);
        ivColorBlack.setOnClickListener(this);
        ivColorRed.setOnClickListener(this);
        ivColorOrange.setOnClickListener(this);
        ivColorYellow.setOnClickListener(this);
        ivColorGreen.setOnClickListener(this);
        ivColorBlue.setOnClickListener(this);
        ivColorPurple.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_revoke:
                customImageView.undo();
                break;
            case R.id.btn_edit_save:
                customImageView.save("/sdcard/" + System.currentTimeMillis() + ".png", new OnBitmapSaveListener() {
                    @Override
                    public void onSucc() {
                        Toast.makeText(ScaleActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFail() {
                        Toast.makeText(ScaleActivity.this, "保存失败！", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.iv_color_white:
                customImageView.setPaintColor(Color.parseColor("#ffffff"));
                updateColorToolbar(R.id.iv_color_white);
                break;
            case R.id.iv_color_black:
                customImageView.setPaintColor(Color.parseColor("#333333"));
                updateColorToolbar(R.id.iv_color_black);
                break;
            case R.id.iv_color_red:
                customImageView.setPaintColor(Color.parseColor("#ff3440"));
                updateColorToolbar(R.id.iv_color_red);
                break;
            case R.id.iv_color_orange:
                customImageView.setPaintColor(Color.parseColor("#ff5c26"));
                updateColorToolbar(R.id.iv_color_orange);
                break;
            case R.id.iv_color_yellow:
                customImageView.setPaintColor(Color.parseColor("#ffd24c"));
                updateColorToolbar(R.id.iv_color_yellow);
                break;
            case R.id.iv_color_green:
                customImageView.setPaintColor(Color.parseColor("#00cc88"));
                updateColorToolbar(R.id.iv_color_green);
                break;
            case R.id.iv_color_blue:
                customImageView.setPaintColor(Color.parseColor("#0ba5d9"));
                updateColorToolbar(R.id.iv_color_blue);
                break;
            case R.id.iv_color_purple:
                customImageView.setPaintColor(Color.parseColor("#d957d9"));
                updateColorToolbar(R.id.iv_color_purple);
                break;
        }
    }

    private void updateColorToolbar(int selectedViewId) {
        Iterator iterator = viewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            int viewId = (Integer) entry.getKey();
            int viewIconId = (Integer) entry.getValue();

            if (selectedViewId == viewId) {
                ImageView selectedView = (ImageView) findViewById(selectedViewId);
                ImageView selectedIconView = (ImageView) findViewById(viewIconId);

                if (selectedViewId == R.id.iv_color_white) {
                    Drawable srcDrawable = selectedIconView.getDrawable();
                    Drawable dstDrawable = ImageUtil.tintDrawable(srcDrawable, R.color.c_8b8b8c);
                    selectedIconView.setImageDrawable(dstDrawable);
                }
                selectedIconView.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams params = selectedView.getLayoutParams();
                params.width = params.height = ScreenUtil.dipToPx(ScaleActivity.this, 35);
                selectedView.setLayoutParams(params);

            } else {
                ImageView unSelectedView = (ImageView) findViewById(viewId);
                ImageView unSelectedViewIcon = (ImageView) findViewById(viewIconId);
                unSelectedViewIcon.setVisibility(View.INVISIBLE);

                ViewGroup.LayoutParams params = unSelectedView.getLayoutParams();
                params.width = params.height = ScreenUtil.dipToPx(ScaleActivity.this, 30);
                unSelectedView.setLayoutParams(params);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
            customImageView.setPaintColor(Color.parseColor("#ffffff"));
            updateColorToolbar(R.id.iv_color_white);
        }
    }
}
