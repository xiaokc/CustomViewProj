package com.android.cong.customviewproj.pictureview.scaleupdown;

import com.android.cong.customviewproj.BaseApplication;
import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by xiaokecong on 20/07/2017.
 */

public class TuyaScaleView extends View implements View.OnTouchListener {
    private Bitmap mBitmap;
    private Drawable mDrawable;

    private int drawableWidth; // 图片宽高
    private int drawableHeight;

    private boolean mFirst; // 第一次加载图片时调整图片缩放比例，使图片宽或高充满屏幕，scaleType一定设置为matrix

    private float mInitScale; // 图片的初始化比例

    private float mMaxScale; // 最大比例

    private float mMidScale; // 双击图片放大的比例

    private int mLastPointerCount; // 上一次触摸点的数量

    private boolean isCanDrag; // 是否可以拖动

    private float mLastX; // 上一次滑动的x和y
    private float mLastY;

    private int mTouchSlop; // 可滑动的临界值

    private boolean isCheckLeftAndRight; // 是否用检查左右边界
    private boolean isCheckTopAndBottom; // 是否用检查上下边界

    private boolean isTuya = true; // 是否在涂鸦
    private Path mPath;
    private Bitmap mBufferBitmap;
    private Canvas mBufferCanvas;
    private Paint mPaint;

    private float pivotX;
    private float pivotY;

    private boolean isScale;

    private int mWidth; // 控件宽高
    private int mHeight;

    public TuyaScaleView(Context context) {
        this(context, null);

    }

    public TuyaScaleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TuyaScaleView);
        mDrawable = typedArray.getDrawable(R.styleable.TuyaScaleView_src);
        measureDrawable();
        mBitmap = ImageUtil.drawableToBitmap(mDrawable);
        typedArray.recycle();

        setOnTouchListener(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop(); // 得到可滑动的临界值

        initPaint();

    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);
        mPaint.setColor(0XFF000000);
    }

    private void measureDrawable() {
        drawableWidth = mDrawable.getIntrinsicWidth();
        drawableHeight = mDrawable.getIntrinsicHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = getSize(ScreenUtil.dipToPx(BaseApplication.getInstance(), 200), widthMeasureSpec);
        mHeight = getSize(ScreenUtil.dipToPx(BaseApplication.getInstance(), 200), heightMeasureSpec);

        setMeasuredDimension(mWidth, mHeight);
    }

    private int getSize(int defaultSize, int measureSpec) {
        int retSize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                retSize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                retSize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                retSize = size;
                break;
            }
        }
        return retSize;
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

        // 只有当第一次加载图片时才会进行初始化
        if (!mFirst) {
            mFirst = true;

            float scale = 1.0f; // 根据图片和控件的宽高，确定缩放值

            if (drawableWidth > mWidth && drawableHeight < mHeight) {
                // 图片宽度大于控件宽度，图片高度小于控件宽度，图片应缩小，为保证图片宽高比不变，高度也要乘以该scale
                scale = mWidth * 1.0f / drawableWidth;
            }

            if (drawableHeight > mHeight && drawableWidth < mWidth) {
                scale = mHeight * 1.0f / drawableHeight;
            }

            if ((drawableWidth < mWidth && drawableHeight < mHeight)
                    || (drawableWidth > mWidth && drawableHeight > mHeight)) {
                scale = Math.min(mWidth * 1.0f / drawableWidth, mHeight * 1.0f / drawableHeight);
            }

            // 平移，将图片移动到屏幕居中位置
            int dx = mWidth / 2 - drawableWidth / 2;
            int dy = mHeight / 2 - drawableHeight / 2;

            canvas.scale(scale, scale, pivotX, pivotY);
            canvas.translate(dx, dy);
            mDrawable.draw(canvas);

            // 初始化缩放边界值
            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;
        }

        doDraw(canvas);

    }

    private void doDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, null);
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
        }

    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        float x = 0.0f;
        float y = 0.0f;

        int pointerCount = event.getPointerCount();
        //将所有触控点的坐标累加起来
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        //取平均值，得到的就是多点触控后产生的那个点的坐标
        x /= pointerCount;
        y /= pointerCount;
        //如果触控点的数量变了，则置为不可滑动
        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (null == mPath) {
                    mPath = new Path();
                }
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    if (mBitmap != null) {
                        isCheckLeftAndRight = true;
                        isCheckTopAndBottom = true;

                    }
                }

                if (!isScale) {
                    mPath.quadTo(mLastX, mLastY, (x + mLastX) / 2, (y + mLastY) / 2);
                    if (null == mBufferBitmap) {
                        initBuffer();
                    }

                    mBufferCanvas.drawPath(mPath, mPaint);
                    invalidate();
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
                mLastPointerCount = 0;
                mPath.reset();
                break;
        }

        return true;
    }

    /**
     * 判断是否是移动的操作
     */
    private boolean isMoveAction(float dx, float dy) {
        //勾股定理，判断斜边是否大于可滑动的一个临界值
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop && !isTuya;
    }

    private void initBuffer() {

        mBufferBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

}
