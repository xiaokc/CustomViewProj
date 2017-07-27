package com.android.cong.customviewproj.pictureview.custom;

import com.android.cong.customviewproj.BaseApplication;
import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by xiaokecong on 27/07/2017.
 */

public class ColorCubView extends RelativeLayout {
    private RelativeLayout mRootLayout;
    private ImageView mImageViewBg;
    private ImageView mImageViewTag;

    private Drawable mCubBackground;
    private Drawable mCubTagSrc;
    private boolean mCubTagVisible;

    public ColorCubView(Context context) {
        super(context);
        initView(context);
    }

    public ColorCubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        initView(context);
    }

    public ColorCubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        initView(context);
    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorCubView);
        mCubBackground = mTypedArray.getDrawable(R.styleable.ColorCubView_cubBackground);
        mCubTagSrc = mTypedArray.getDrawable(R.styleable.ColorCubView_cubTagSrc);
        mCubTagVisible = mTypedArray.getBoolean(R.styleable.ColorCubView_cubTagVisible, false);
        mTypedArray.recycle();

    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.cub_color_view, this, true);
        mRootLayout = (RelativeLayout) findViewById(R.id.cub_root);
        mImageViewBg = (ImageView) findViewById(R.id.iv_cub_bg);
        mImageViewTag = (ImageView) findViewById(R.id.iv_cub_tag);

        mImageViewBg.setBackground(mCubBackground);
        mImageViewTag.setImageDrawable(mCubTagSrc);
        mImageViewTag.setVisibility(mCubTagVisible ? VISIBLE : GONE);

        setClickable(true);
        mRootLayout.setClickable(true);
        mImageViewBg.setClickable(true);
    }

    public void setCubTagVisible(boolean cubTagVisible) {
        mImageViewTag.setVisibility(cubTagVisible ? VISIBLE : GONE);
    }

    public void setCubSelected() {
        mImageViewTag.setVisibility(VISIBLE);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = params.height = ScreenUtil.dipToPx(BaseApplication.getInstance(), 35);
        setLayoutParams(params);
    }

    public void setCubUnSelected() {
        mImageViewTag.setVisibility(GONE);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = params.height = ScreenUtil.dipToPx(BaseApplication.getInstance(), 30);
        setLayoutParams(params);
    }
}
