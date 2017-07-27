package com.android.cong.customviewproj.dragview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiaokecong on 06/07/2017.
 */

public class VideoDragView extends View {
    public VideoDragView(Context context) {
        this(context,null);
    }

    public VideoDragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoDragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
