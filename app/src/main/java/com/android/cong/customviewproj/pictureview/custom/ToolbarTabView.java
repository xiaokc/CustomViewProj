package com.android.cong.customviewproj.pictureview.custom;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class ToolbarTabView extends LinearLayout {
    private LinearLayout mRootLayout;
    private ImageView mImageView;
    private TextView mTextView;
    private String mText;
    private int mTextDefaultColor;
    private int mTouchDownColor;
    private Drawable mDefaultDrawable;

    public ToolbarTabView(Context context) {
        super(context);
        initView(context);
    }

    public ToolbarTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initTypedArray(context, attrs);
        initView(context);
    }

    public ToolbarTabView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypedArray(context, attrs);
        initView(context);

    }

    private void initTypedArray(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ToolbarTabView);
        mText = mTypedArray.getString(R.styleable.ToolbarTabView_text);
        mTextDefaultColor = mTypedArray
                .getColor(R.styleable.ToolbarTabView_textDefaultColor, Color.parseColor("#99000000"));
        mTouchDownColor = mTypedArray
                .getColor(R.styleable.ToolbarTabView_textTouchDownColor, Color.parseColor("#2274e6"));
        mDefaultDrawable = mTypedArray.getDrawable(R.styleable.ToolbarTabView_imageSrc);

        mTypedArray.recycle();

    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.tab_view, this, true);
        mRootLayout = (LinearLayout) findViewById(R.id.root_tab);
        mImageView = (ImageView) findViewById(R.id.iv_tab);
        mTextView = (TextView) findViewById(R.id.tv_tab);

        mTextView.setText(mText);
        mTextView.setTextColor(mTextDefaultColor);
        mImageView.setImageDrawable(mDefaultDrawable);
    }

    public void setTextColor(int color) {
        mTextView.setTextColor(color);
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setTouchDownColor(int color) {
        mTextView.setTextColor(color);
        Drawable touchDownDrawable = ImageUtil.tintDrawable(mDefaultDrawable, color);
        mImageView.setImageDrawable(touchDownDrawable);
    }

    public void setDefaultColor(int color) {
        mTextView.setTextColor(color);
        mImageView.setImageDrawable(mDefaultDrawable);
    }

}
