package com.android.cong.customviewproj.canvas;

import com.android.cong.customviewproj.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xiaokecong on 07/07/2017.
 */

public class CheckView extends View {
    private static final int ANIM_NULL = 0; // 动画状态-没有
    private static final int ANIM_CHECK = 1; // 动画状态-check
    private static final int ANIM_UNCHECK = 2; // 动画状态-uncheck

    private Context mContext;
    private int mWidth, mHeight;
    private Handler mHandler;

    private Paint mPaint;
    private Bitmap okBitmap;

    private int animCurrentPage = 1; // 动画当前页码
    private int animMaxPage = 13; // 最大页数
    private int animDuration = 500; // 持续时长
    private int animState = ANIM_NULL; // 动画状态

    private boolean isCheck = false; // 是否是check状态

    public CheckView(Context context) {
        this(context, null);
    }

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mPaint = new Paint();
        mPaint.setColor(0xffFF5317);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        okBitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.checkmark);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (animCurrentPage < animMaxPage && animCurrentPage >= 0) {
                    invalidate();
                    if (animState == ANIM_NULL) {
                        return;
                    } else if (animState == ANIM_CHECK) {
                        animCurrentPage ++;
                    } else if (animState == ANIM_UNCHECK) {
                        animCurrentPage --;
                    }
                    this.sendEmptyMessageDelayed(0, animDuration / animMaxPage);
                } else {
                    if (isCheck) {
                        animCurrentPage = animMaxPage - 1;
                    } else {
                        animCurrentPage = -1;
                    }

                    invalidate();
                    animState = ANIM_NULL;
                }
            }
        };


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 移动坐标系到画布中央
        canvas.translate(mWidth / 2, mHeight / 2);

        // 绘制北京圆形
        canvas.drawCircle(0,0,200,mPaint);

        // 图像边长
        int sideLength = okBitmap.getHeight();

        // 图像选区 和 实际绘制位置
        Rect src = new Rect(sideLength* animCurrentPage, 0, sideLength * (animCurrentPage + 1), sideLength);
        Rect dst = new Rect(-200,-200,200,200);

        canvas.drawBitmap(okBitmap, src, dst, null);
    }

    public void check() {
        if (animState != ANIM_NULL || isCheck) {
            return;
        }

        animState = ANIM_CHECK;
        animCurrentPage = 0;
        mHandler.sendEmptyMessageDelayed(0, animDuration / animMaxPage);
        isCheck = true;
    }

    public void unCheck() {
        if (animState != ANIM_NULL || !isCheck) {
            return;
        }
        animState = ANIM_UNCHECK;
        animCurrentPage = animMaxPage - 1;
        mHandler.sendEmptyMessageDelayed(0, animDuration / animMaxPage);
        isCheck = false;
    }

    public void setAnimDuration(int animDuration) {
        this.animDuration = animDuration;
    }

    public void setBackgroundColor(int color) {
        mPaint.setColor(color);
    }
}
