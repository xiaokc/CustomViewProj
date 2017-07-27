package com.android.cong.customviewproj.dragview;

import com.android.cong.customviewproj.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * 双向拖动条
 * Created by xiaokecong on 06/07/2017.
 */

public class ATDragView extends View {
    private int viewWidth;
    private int viewHeight;
    private int seekBgColor;
    private int seekPbColor;
    private int seekBallSolidColor;
    private int seekBallStrokeColor;
    private int seekTextColor;
    private int seekTextSize;

    private Paint seekBgPaint;
    private Paint seekPbPaint;
    private Paint seekBallPaint;
    private Paint seekBallEndPaint;
    private Paint seekBallStrokePaint;
    private Paint seekTextPaint;

    private int currentMovingType;

    public ATDragView(Context context) {
        this(context, null);
    }

    public ATDragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ATDragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ATDragView,
                defStyleAttr, R.style.def_dragview);

        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.ATDragView_seek_bg_color:
                    seekBgColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ATDragView_seek_pb_color:
                    seekPbColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ATDragView_seek_ball_solid_color:
                    seekBallSolidColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ATDragView_seek_ball_stroke_color:
                    seekBallStrokeColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ATDragView_seek_text_color:
                    seekTextColor = typedArray.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ATDragView_seek_text_size:
                    seekTextSize = typedArray.getDimensionPixelSize(attr, 0);
                    break;
            }
        }
        typedArray.recycle();
        init();

    }

    private void init() {
        currentMovingType = BallType.LEFT;

        seekTextPaint = createPaint(seekTextColor, seekTextSize, Paint.Style.FILL, 0);
        seekBgPaint = createPaint(seekBgColor, 0, Paint.Style.FILL, 0);
        seekPbPaint = createPaint(seekPbColor, 0, Paint.Style.FILL, 0);
        seekBallPaint = createPaint(seekBallSolidColor, 0, Paint.Style.FILL, 0);
        seekBallEndPaint = createPaint(seekPbColor, 0, Paint.Style.FILL, 0);
        seekBallStrokePaint = createPaint(seekBallStrokeColor, 0, Paint.Style.FILL, 0);
        seekBallStrokePaint.setShadowLayer(5,2,2,seekBallStrokeColor);

    }

    private Paint createPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private static class BallType {
        private static final int LEFT = 0x100;
        private static final int RIGHT = 0x101;
    }
}
