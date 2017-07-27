package com.android.cong.customviewproj.pictureview.scaleupdown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

/**
 * Created by xiaokecong on 19/07/2017.
 */

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView implements ScaleGestureDetector
        .OnScaleGestureListener, View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener {

    private ScaleGestureDetector mScaleGestureDetector; // 缩放手势监测

    private GestureDetector mGestureDetector; // 手势监听

    private Matrix mScaleMatrix; // 缩放平移的矩阵

    private boolean mFirst; // 第一次加载图片时调整图片缩放比例，使图片宽或高充满屏幕，scaleType一定设置为matrix

    private float mInitScale; // 图片的初始化比例

    private float mMaxScale; // 最大比例

    private float mMidScale; // 双击图片放大的比例

    private boolean isAutoScale;// 是否在自动放大或缩小

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

    private float scaleFactor;
    private float pivotX;
    private float pivotY;

    private boolean isScale;


    public ScaleImageView(Context context) {
        this(context, null, 0);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.MATRIX); // 将图片scaleType设置为Matrix

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        mScaleMatrix = new Matrix();
        setOnTouchListener(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop(); // 得到可滑动的临界值

        // 初始化手势监听器，监听双击事件
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale) {
                    return true;
                }

                float x = e.getX();
                float y = e.getY();

                if (getScale() < mMidScale) {
                    // 如果当前图片的缩放值小于指定的双击缩放值，则进行自动放大
                    post(new AutoScaleRunnable(mMidScale, x, y));
                } else {
                    // 当前图片缩放值大于指定的双击缩放值，则进行自动缩小
                    post(new AutoScaleRunnable(mInitScale, x, y));
                }
                return true;
            }
        });

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);
        mPaint.setColor(0XFF000000);
    }

    /**
     * 当View添加到window时调用，早于onGlobalLayout，因此可以在此注册监听器
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    /**
     * 当View从window上移除时调用，因此可以在这里移除监听器
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * 实现OnGlobalLayoutListener的方法
     * 当View布局树发生变化时调用此方法，可以在此方法中获得控件的宽和高
     */
    @Override
    public void onGlobalLayout() {
        // 只有当第一次加载图片时才会进行初始化
        if (!mFirst) {
            mFirst = true;

            // 得到控件的宽和高
            int width = getWidth();
            int height = getHeight();

            // 得到当前ImageView中加载的图片
            Drawable d = getDrawable();
            if (null == d) { // 如果没有加载图片，直接返回
                return;
            }

            // 得到当前图片的宽和高，与控件的宽和高进行判断，将图片完整的显示在屏幕中
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();

            float scale = 1.0f; // 根据图片和控件的宽高，确定缩放值

            if (dw > width && dh < height) {
                // 图片宽度大于控件宽度，图片高度小于控件宽度，图片应缩小，为保证图片宽高比不变，高度也要乘以该scale
                scale = width * 1.0f / dw;
            }

            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }

            //如果图片的宽度小于控件宽度，高度小于控件高度时，我们应该将图片放大
            //比如图片宽度是控件宽度的1/2 ，图片高度是控件高度的1/4
            //如果我们将图片放大4倍，则图片的高度是和控件高度一样了，但是图片宽度就超出控件宽度了
            //因此我们应该选择一个最小值，那就是将图片放大2倍，此时图片宽度等于控件宽度
            //同理，如果图片宽度大于控件宽度，图片高度大于控件高度，我们应该将图片缩小
            //缩小的倍数也应该为那个最小值
            if ((dw < width && dh < height) || (dw > width && dh > height)) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }

            // 平移，将图片移动到屏幕居中位置
            int dx = width / 2 - dw / 2;
            int dy = height / 2 - dh / 2;

            mScaleMatrix.postTranslate(dx, dy); // 对图片平移
            mScaleMatrix.postScale(scale, scale, width / 2, height / 2); //对图片进行缩放，后两个参数是缩放中心点

            setImageMatrix(mScaleMatrix); // 将矩阵作用于图片上，图片真正得到平移和缩放
            invalidate();

            // 初始化缩放边界值
            mInitScale = scale;
            mMaxScale = mInitScale * 4;
            mMidScale = mInitScale * 2;

        }
    }

    /**
     * 获得图片当前的缩放比例
     *
     * @return
     */
    private float getScale() {
        // Matrix 是 3*3的矩阵，共9个值
        float[] values = new float[9];

        mScaleMatrix.getValues(values);

        // 图片宽度和高度的缩放比例一致，取一个就可以
        return values[Matrix.MSCALE_X];
    }

    /**
     * 获得缩放后图片的上下左右坐标以及宽高
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix; // 当前图片矩阵

        RectF rectF = new RectF();

        Drawable d = getDrawable();

        if (d != null) {
            // 使这个矩阵的宽高和当前图片一致
            rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            //将矩阵映射到矩形上面，之后我们可以通过获取到矩阵的上下左右坐标以及宽高
            //来得到缩放后图片的上下左右坐标和宽高
            matrix.mapRect(rectF);
        }

        return rectF;
    }

    /**
     * 缩放时检查边界并且使图片居中
     */
    private void checkBorderAndCenterWhenScale() {
        if (getDrawable() == null) {
            return;
        }
        //初始化水平和竖直方向的偏移量
        float deltaX = 0.0f;
        float deltaY = 0.0f;
        //得到控件的宽和高
        int width = getWidth();
        int height = getHeight();
        //拿到当前图片对应的矩阵
        RectF rectF = getMatrixRectF();
        //如果当前图片的宽度大于控件宽度，当前图片处于放大状态
        if (rectF.width() >= width) {
            //如果图片左边坐标是大于0的，说明图片左边离控件左边有一定距离，
            //左边会出现一个小白边
            if (rectF.left > 0) {
                //我们将图片向左边移动
                deltaX = -rectF.left;
            }
            //如果图片右边坐标小于控件宽度，说明图片右边离控件右边有一定距离，
            //右边会出现一个小白边
            if (rectF.right < width) {
                //我们将图片向右边移动
                deltaX = width - rectF.right;
            }
        }

        //上面是调整宽度，这是调整高度
        if (rectF.height() >= height) {
            //如果上面出现小白边，则向上移动
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            }
            //如果下面出现小白边，则向下移动
            if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        }
        //如果图片的宽度小于控件的宽度，我们要对图片做一个水平的居中
        if (rectF.width() < width) {
            deltaX = width / 2f - rectF.right + rectF.width() / 2f;
        }

        //如果图片的高度小于控件的高度，我们要对图片做一个竖直方向的居中
        if (rectF.height() < height) {
            deltaY = height / 2f - rectF.bottom + rectF.height() / 2f;
        }
        //将平移的偏移量作用到矩阵上
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 平移时检查上下左右边界
     */
    private void checkBorderWhenTranslate() {
        //获得缩放后图片的相应矩形
        RectF rectF = getMatrixRectF();
        //初始化水平和竖直方向的偏移量
        float deltaX = 0.0f;
        float deltaY = 0.0f;
        //得到控件的宽度
        int width = getWidth();
        //得到控件的高度
        int height = getHeight();
        //如果是需要检查左和右边界
        if (isCheckLeftAndRight) {
            //如果左边出现的白边
            if (rectF.left > 0) {
                //向左偏移
                deltaX = -rectF.left;
            }
            //如果右边出现的白边
            if (rectF.right < width) {
                //向右偏移
                deltaX = width - rectF.right;
            }
        }
        //如果是需要检查上和下边界
        if (isCheckTopAndBottom) {
            //如果上面出现白边
            if (rectF.top > 0) {
                //向上偏移
                deltaY = -rectF.top;
            }
            //如果下面出现白边
            if (rectF.bottom < height) {
                //向下偏移
                deltaY = height - rectF.bottom;
            }
        }

        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    /**
     * 实现OnScaleGestureListener的方法
     *
     * @param detector
     *
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        isScale = true;
        //当我们两个手指进行分开操作时，说明我们想要放大，这个scaleFactor是一个稍微大于1的数值
        //当我们两个手指进行闭合操作时，说明我们想要缩小，这个scaleFactor是一个稍微小于1的数值
        scaleFactor = detector.getScaleFactor();
        pivotX = detector.getFocusX();
        pivotY = detector.getFocusY();
        float scale = getScale();
        if (null == getDrawable()) {
            return true;
        }

        if ((scaleFactor > 1.0f && scale * scaleFactor < mMaxScale)
                || (scaleFactor < 1.0f && scale * scaleFactor > mInitScale)) {
            if (scale * scaleFactor > mMaxScale + 0.01f) {
                scaleFactor = mMaxScale / scale;
            }

            if (scale * scaleFactor < mInitScale + 0.01f) {
                scaleFactor = mInitScale / scale;
            }

            mScaleMatrix.postScale(scaleFactor, scaleFactor, pivotX, pivotY);
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);
            invalidate();
        }
        isScale = false;
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save(Canvas.ALL_SAVE_FLAG);
//        canvas.scale(scaleFactor, scaleFactor, pivotX, pivotY);
        canvas.concat(mScaleMatrix);
        if (mBufferBitmap != null) {
            canvas.drawBitmap(mBufferBitmap, 0, 0, null);
            //            updateBitmap(mBufferBitmap);

        }
        canvas.restore();

    }

    private void updateBitmap() {
        if (mBufferBitmap != null) {
            updateBitmap(mBufferBitmap);
        }
    }

    private void updateBitmap(Bitmap bufferBitmap) {
//        RectF rectF = getMatrixRectF();
//        Bitmap newBmp = Bitmap.createBitmap((int)rectF.width(), (int) rectF.height(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(newBmp);
//        canvas.drawBitmap(ImageUtil.drawableToBitmap(getDrawable()),0,0,null);
//        canvas.drawBitmap(bufferBitmap,0,0,null);
//        canvas.save(Canvas.ALL_SAVE_FLAG);
//        canvas.restore();
//        setImageBitmap(newBmp);

//        setDrawingCacheEnabled(true);
//        Bitmap cacheBmp = getDrawingCache();
//        if (cacheBmp != null) {
//            Bitmap newBmp = Bitmap.createBitmap(cacheBmp);
//            destroyDrawingCache();
//            setDrawingCacheEnabled(false);
//            setImageBitmap(newBmp);
//        }
    }

    /**
     * 实现OnTouchListener的方法
     *
     * @param v
     * @param event
     *
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 双击操作时，不允许移动图片
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

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
        RectF rectF = getMatrixRectF();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isCanDrag = false;
                //当图片处于放大状态时，禁止ViewPager拦截事件，将事件传递给图片，进行拖动
                if (rectF.width() > getWidth() + 0.01f || rectF.height() > getHeight() + 0.01f) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                if (null == mPath) {
                    mPath = new Path();
                }
                mPath.moveTo(x, y);
//                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.01f || rectF.height() > getHeight() + 0.01f) {
                    if (getParent() instanceof ViewPager) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = true;
                        isCheckTopAndBottom = true;

                        if (rectF.width() < getWidth()) {
                            // 如果图片宽度小于控件宽度
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }

                        if (rectF.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                    }

                    mScaleMatrix.postTranslate(dx, dy);
                    checkBorderWhenTranslate();
                    setImageMatrix(mScaleMatrix);
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

    private void initBuffer() {
        RectF rectF = getMatrixRectF();
        mBufferBitmap = Bitmap.createBitmap((int) rectF.width(), (int) rectF.height(), Bitmap.Config.ARGB_8888);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    /**
     * 判断是否是移动的操作
     */
    private boolean isMoveAction(float dx, float dy) {
        //勾股定理，判断斜边是否大于可滑动的一个临界值
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop && !isTuya;
    }

    /**
     * 自动放大缩小，实现自动缩放的原理是View.postDelay()方法，每隔16ms调用一次run方法，给人视觉上形成自动的效果
     */
    private class AutoScaleRunnable implements Runnable {
        //放大或者缩小的目标比例
        private float mTargetScale;
        //可能是BIGGER,也可能是SMALLER
        private float tempScale;
        //放大缩小的中心点
        private float x;
        private float y;
        //比1稍微大一点，用于放大
        private final float BIGGER = 1.07f;
        //比1稍微小一点，用于缩小
        private final float SMALLER = 0.93f;

        //构造方法，将目标比例，缩放中心点传入，并且判断是要放大还是缩小
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            //如果当前缩放比例小于目标比例，说明要自动放大
            if (getScale() < mTargetScale) {
                //设置为Bigger
                tempScale = BIGGER;
            }
            //如果当前缩放比例大于目标比例，说明要自动缩小
            if (getScale() > mTargetScale) {
                //设置为Smaller
                tempScale = SMALLER;
            }
        }

        @Override
        public void run() {
            //这里缩放的比例非常小，只是稍微比1大一点或者比1小一点的倍数
            //但是当每16ms都放大或者缩小一点点的时候，动画效果就出来了
            mScaleMatrix.postScale(tempScale, tempScale, x, y);
            invalidate();
            //每次将矩阵作用到图片之前，都检查一下边界
            checkBorderAndCenterWhenScale();
            //将矩阵作用到图片上
            setImageMatrix(mScaleMatrix);
            //得到当前图片的缩放值
            float currentScale = getScale();
            //如果当前想要放大，并且当前缩放值小于目标缩放值
            //或者  当前想要缩小，并且当前缩放值大于目标缩放值
            if ((tempScale > 1.0f) && currentScale < mTargetScale
                    || (tempScale < 1.0f) && currentScale > mTargetScale) {
                //每隔16ms就调用一次run方法
                postDelayed(this, 16);
            } else {
                //current*scale=current*(mTargetScale/currentScale)=mTargetScale
                //保证图片最终的缩放值和目标缩放值一致
                float scale = mTargetScale / currentScale;
                mScaleMatrix.postScale(scale, scale, x, y);
                invalidate();
                checkBorderAndCenterWhenScale();
                setImageMatrix(mScaleMatrix);
                //自动缩放结束，置为false
                isAutoScale = false;
            }
        }
    }

}
