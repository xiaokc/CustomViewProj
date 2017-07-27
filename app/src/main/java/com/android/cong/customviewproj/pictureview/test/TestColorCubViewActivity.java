package com.android.cong.customviewproj.pictureview.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.pictureview.custom.ColorCubView;
import com.android.cong.customviewproj.pictureview.custom.ShowAndEditActivity;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by xiaokecong on 27/07/2017.
 */

public class TestColorCubViewActivity extends Activity implements View.OnClickListener {
    private ColorCubView whiteCubView;
    private ColorCubView blackCubView;
    private ColorCubView redCubView;

    private List<ColorCubView> mColorCubViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_color_cub_view);

        whiteCubView = (ColorCubView) findViewById(R.id.cub_white);
        blackCubView = (ColorCubView) findViewById(R.id.cub_black);
        redCubView = (ColorCubView) findViewById(R.id.cub_red);

        whiteCubView.setOnClickListener(this);
        blackCubView.setOnClickListener(this);
        redCubView.setOnClickListener(this);

        mColorCubViews = new ArrayList<>();
        mColorCubViews.add(whiteCubView);
        mColorCubViews.add(blackCubView);
        mColorCubViews.add(redCubView);

    }

    @Override
    public void onClick(View v) {
        updateClickCubParams(v);
    }

    public void updateClickCubParams(View view) {
       if (view instanceof ColorCubView) {
           for (ColorCubView cubView : mColorCubViews) {
               if (cubView.getId() == view.getId()) {
                   cubView.setCubSelected();
               } else {
                   cubView.setCubUnSelected();
               }
           }
       }

    }
}
