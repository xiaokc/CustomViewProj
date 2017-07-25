package com.android.cong.customviewproj.pictureview.custom;

import java.util.ArrayList;
import java.util.List;

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
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Created by xiaokecong on 21/07/2017.
 */

public class CustomImageView extends View {
    private Bitmap mBitmap; // 原始图片
    private Drawable mDrawable;
    private int mOriginalWidth; // 原始图片宽高
    private int mOriginalHeight;
    private float mOriginalPivotX; // 图片中心
    private float mOriginalPivotY;

    private int mDefaultWidth = 300;
    private int mDefaultHeight = 400;

    private Bitmap mBufferBitmap; // 双缓冲Bitmap
    private Canvas mBufferCanvas;
    private Paint mPaint;
    private int mPaintSize = 20; // 画笔粗细
    private int mPaintColor;

    private float mScale; // 图片在相对于居中时的缩放倍数 （ 图片真实的缩放倍数为 mPrivateScale*mScale ）

    private float mPrivateScale; // 图片适应屏幕（mScale=1）时的缩放倍数
    private int mPrivateHeight, mPrivateWidth;// 图片在缩放mPrivateScale倍数的情况下，适应屏幕（mScale=1）时的大小（肉眼看到的在屏幕上的大小）
    private float mCenterTranX, mCenterTranY;// 图片在缩放mPrivateScale倍数的情况下，居中（mScale=1）时的偏移（肉眼看到的在屏幕上的偏移）

    private float mTransX = 0, mTransY = 0;
    // 图片在相对于居中时且在缩放mScale倍数的情况下的偏移量 （ 图片真实偏移量为　(mCenterTranX + mTransX)/mPrivateScale*mScale ）

/*
      假设不考虑任何缩放，图片就是肉眼看到的那么大，此时图片的大小width =  mPrivateWidth * mScale ,
      偏移量x = mCenterTranX + mTransX，而view的大小为width = getWidth()。height和偏移量y以此类推。
*/

    private boolean mIsPainting = false; // 是否正在绘制
    private float mTouchDownX, mTouchDownY, mLastTouchX, mLastTouchY, mTouchX, mTouchY;

    private Path mCurrPath; // 当前手写的路径
    private HandDrawPath mCurrHandDrawPath; // 当前手绘路径（封装了画笔颜色）

    private final float ONEOFFSET = 1f;
    private int mTouchMode; // 触摸模式，用于判断单点或多点触摸

    private boolean mIsScale; // 是否正在缩放
    private int mTouchSlop; // 滑动阈值
    private boolean mIsDoubleTap; // 是否正在双击

    private float mOldDist, mNewDist, mScaleCenterX,
            mScaleCenterY, mTouchCenterX, mTouchCenterY; // 双指缩放拖动相关的坐标

    private final float MAX_SCALE = 4.0f; // 最大缩放倍数
    private final float MIN_SCALE = 1.0f; // 最小缩放倍数

    private GestureDetector mGestureDector; // 手势检测
    private float mLastScale; // 存放上次缩放比例，便于再次双击时还原

    private List<HandDrawPath> mBufferPathList; // 存储涂鸦的绘制路径
    private Context mContext;

    private OnViewClickListener mClickListener; // 点击事件监听

    public CustomImageView(Context context) {
        this(context, null);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        // 关闭硬件加速，因为bitmap的Canvas不支持硬件加速
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView);
        //todo: 这里直接读取属性设置的图片，有可能会oom
        mDrawable = typedArray.getDrawable(R.styleable.CustomImageView_image_src);
        mBitmap = ImageUtil.drawableToBitmap(mDrawable);
        typedArray.recycle();

        mGestureDector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mIsDoubleTap = true;
                float x = e.getX();
                float y = e.getY();
                mScaleCenterX = toX(x); // 缩放中心点在图片坐标系上的坐标
                mScaleCenterY = toY(y);
                if (mScale < MAX_SCALE) {
                    mLastScale = mScale; // 记录上次缩放比例
                    mScale = MAX_SCALE;
                } else if (mScale == MAX_SCALE) {
                    mScale = mLastScale; // 还原
                }
                mTransX = toTransX(x, mScaleCenterX); // 计算偏移量
                mTransY = toTransY(y, mScaleCenterY);
                updatePosition();
                invalidate();
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mIsDoubleTap = false; // 双击手指都抬起时，将双击动作标记为false，以接收单指action_down动作
                        break;

                }
                return true;
            }
        });

        init();
    }

    private void init() {
        mOriginalWidth = mBitmap.getWidth();
        mOriginalHeight = mBitmap.getHeight();
        mOriginalPivotX = mOriginalWidth / 2f;
        mOriginalPivotY = mOriginalHeight / 2f;

        mScale = 1.0f;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mPaintSize);
        mPaint.setColor(0XFF000000);

        mTouchSlop = ViewConfiguration.get(BaseApplication.getInstance()).getScaledTouchSlop();
        mBufferPathList = new ArrayList<>();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBg(); // 设置背景
    }

    private void setBg() {
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();

        if (nw > nh) {
            mPrivateScale = 1 / nw;
            mPrivateWidth = getWidth();
            mPrivateHeight = (int) (h * mPrivateScale);
        } else {
            mPrivateScale = 1 / nh;
            mPrivateHeight = getHeight();
            mPrivateWidth = (int) (w * mPrivateScale);
        }

        // 图片居中的偏移量
        mCenterTranX = (getWidth() - mPrivateWidth) / 2;
        mCenterTranY = (getHeight() - mPrivateHeight) / 2;

        // 根据图片调整比例设置控件宽高
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = mPrivateWidth;
        params.height = mPrivateHeight;
        setLayoutParams(params);

        initCanvas();

        mPaintSize /= mPrivateScale;

        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = getSize(ScreenUtil.dipToPx(BaseApplication.getInstance(), mDefaultWidth), widthMeasureSpec);
        int height = getSize(ScreenUtil.dipToPx(BaseApplication.getInstance(), mDefaultHeight), heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    private int getSize(int defaultSize, int measureSpec) {
        int retSize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {
                retSize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                retSize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {
                retSize = size;
                break;
            }
        }
        return retSize;
    }

    private void initCanvas() {
        if (mBufferBitmap != null) {
            mBufferBitmap.recycle();
        }

        mBufferBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mBufferCanvas = new Canvas(mBufferBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap.isRecycled() || mBufferBitmap.isRecycled()) {
            return;
        }

        canvas.save();
        doDraw(canvas);
        canvas.restore();
    }

    private void doDraw(Canvas canvas) {
        float leftOffset = (mCenterTranX + mTransX) / (mPrivateScale * mScale);
        float topOffset = (mCenterTranY + mTransY) / (mPrivateScale * mScale);

        // 根据缩放比例和偏移量对画布进行调整
        canvas.scale(mPrivateScale * mScale, mPrivateScale * mScale);
        canvas.translate(leftOffset, topOffset);

        canvas.save();

        // 将涂鸦区域限制在图片内部
        canvas.clipRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());

        // 绘制涂鸦的内容
        canvas.drawBitmap(mBufferBitmap, 0, 0, null);

        if (mIsPainting) {
            mPaint.setStrokeWidth(mPaintSize);
            draw(canvas, mPaint, mCurrPath);
        }

        canvas.restore();
    }

    /**
     * 将path画在画布上
     *
     * @param canvas
     * @param paint
     * @param path
     */
    private void draw(Canvas canvas, Paint paint, Path path) {
        canvas.drawPath(path, paint);
    }

    /**
     * 将path list中的每个path依次绘制到画布上
     *
     * @param canvas
     * @param pathList
     */
    private void draw(Canvas canvas, List<HandDrawPath> pathList) {
        for (HandDrawPath handDrawPath : pathList) {
            mPaint.setColor(handDrawPath.getPaintColor());
            draw(canvas, mPaint, handDrawPath.getPath());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDector.onTouchEvent(event)) {
            return true; // 双击操作时，不允许移动图片
        }

        checkZoomAndDragEvent(event);
        if (!mIsScale && mPaintColor != 0) { // 多指缩放手指抬起，且设置了画笔颜色，才能接受单指涂鸦
            checkHandWriteEvent(event);
        }

        return true;
    }

    /**
     * 检测多指缩放和拖拽
     *
     * @param event
     */
    private void checkZoomAndDragEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) { // 多指按下
                    mOldDist = spacing(event);
                    mTouchCenterX = (event.getX(0) + event.getX(1)) / 2;
                    mTouchCenterY = (event.getY(0) + event.getY(1)) / 2;
                    mScaleCenterX = toX(mTouchCenterX);
                    mScaleCenterY = toY(mTouchCenterY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() >= 2) { // 多指缩放或拖拽
                    mIsScale = true;
                    mNewDist = spacing(event);
                    if (Math.abs(mNewDist - mOldDist) >= mTouchSlop) { // 认为是滑动操作
                        float tmpScale = mNewDist / mOldDist;
                        mScale *= tmpScale;
                        if (mScale > MAX_SCALE) { // 控制缩放比例在min~max之间
                            mScale = MAX_SCALE;
                        }

                        if (mScale < MIN_SCALE) {
                            mScale = MIN_SCALE;
                        }
                    }

                    float mMoveCenterX = (event.getX(0) + event.getX(1)) / 2;
                    float mMoveCenterY = (event.getY(0) + event.getY(1)) / 2;

                    mTransX = toTransX(mMoveCenterX, mScaleCenterX); // 使缩放中心点偏移到两指中间的点上
                    mTransY = toTransY(mMoveCenterY, mScaleCenterY);
                    updatePosition();
                    invalidate();
                    mOldDist = mNewDist;

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsScale = false;
                break;
        }
    }

    /**
     * 检测单指涂鸦
     *
     * @param event
     */
    private void checkHandWriteEvent(MotionEvent event) {
        if (mIsDoubleTap) {
            return;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = mTouchX = mLastTouchX = event.getX();
                mTouchDownY = mTouchY = mLastTouchY = event.getY();

                mCurrPath = new Path();
                mCurrPath.moveTo(toX(mTouchDownX), toY(mTouchDownY));
                mIsPainting = true;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastTouchX = mTouchX;
                mLastTouchY = mTouchY;
                mTouchX = event.getX();
                mTouchY = event.getY();

                mCurrPath.quadTo(
                        toX(mLastTouchX),
                        toY(mLastTouchY),
                        toX((mTouchX + mLastTouchX) / 2),
                        toY((mTouchY + mLastTouchY) / 2));

                invalidate(); // 调用onDraw()方法绘制path
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mTouchDownX == mTouchX && mTouchDownY == mTouchY
                        && mTouchDownX == mLastTouchX && mTouchDownY == mLastTouchY) { // 点击事件
                    if (mClickListener != null) {
                        mClickListener.onViewClick();
                    }

                } else {
                    // 手指抬起时更新双缓冲画布，以避免撤销时缓冲区画布重绘（视觉上会闪一下）
                    draw(mBufferCanvas, mPaint, mCurrPath);
                    mCurrHandDrawPath = new HandDrawPath(mCurrPath, mPaint.getColor());
                    mBufferPathList.add(mCurrHandDrawPath);
                    mIsPainting = false;
                }
                break;
        }
    }

    /**
     * 计算两指的中心点坐标
     *
     * @param event
     *
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = Math.abs(event.getX(0) - event.getX(1));
        float y = Math.abs(event.getY(0) - event.getY(1));
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 计算缩放后图片的在x和y方向上的相对偏移量
     */
    private void updatePosition() {
        // 先调整宽度上的
        if (mPrivateWidth * mScale < getWidth()) { // 缩放后图片宽度小于控件宽度，缩小状态
            if (mTransX + mCenterTranX < 0) {
                mTransX = -mCenterTranX;
            } else if (mTransX + mCenterTranX + mPrivateWidth * mScale > getWidth()) {
                mTransX = getWidth() - mCenterTranX - mPrivateWidth * mScale;
            }
        } else { // 放大状态
            if (mTransX + mCenterTranX > 0) {
                mTransX = -mCenterTranX;
            } else if (mTransX + mCenterTranX + mPrivateWidth * mScale < getWidth()) {
                mTransX = getWidth() - mCenterTranX - mPrivateWidth * mScale;
            }
        }

        // 调整高度上的
        if (mPrivateHeight * mScale < getHeight()) {
            if (mTransY + mCenterTranY < 0) {
                mTransY = -mCenterTranY;
            } else if (mTransY + mCenterTranY + mPrivateHeight * mScale > getHeight()) {
                mTransY = getHeight() - mCenterTranY - mPrivateHeight * mScale;
            }
        } else {
            if (mTransY + mCenterTranY > 0) {
                mTransY = -mCenterTranY;
            } else if (mTransY + mCenterTranY + mPrivateHeight * mScale < getHeight()) {
                mTransY = getHeight() - mCenterTranY - mPrivateHeight * mScale;
            }
        }
    }

    /**
     * 设置画笔颜色
     *
     * @param color
     */
    public void setPaintColor(int color) {
        mPaintColor = color;
        mPaint.setColor(mPaintColor);
    }

    /**
     * 撤销
     */
    public void undo() {
        if (mBufferPathList != null && mBufferPathList.size() > 0) { // 如果有可撤销的path
            mBufferPathList.remove(mBufferPathList.size() - 1);
            initCanvas();
            draw(mBufferCanvas, mBufferPathList);
            invalidate();
        }
    }

    /**
     * 清屏
     */
    public void clear() {
        if (mBufferPathList != null) {
            mBufferPathList.clear();
            initCanvas();
            invalidate();
        }

    }

    /**
     * 得到当前编辑过的bitmap
     *
     * @return
     */
    private Bitmap getEditedBitmap() {
        Bitmap editedBitmap = Bitmap.createBitmap(mOriginalWidth, mOriginalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(editedBitmap);
        canvas.drawBitmap(mBitmap, 0, 0, null);

        for (HandDrawPath handDrawPath : mBufferPathList) {
            mPaint.setColor(handDrawPath.getPaintColor());
            canvas.drawPath(handDrawPath.getPath(), mPaint);
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return editedBitmap;

    }

    /**
     * bitmap右下角添加水印
     *
     * @param waterMark     水印图片
     * @param paddingRight  右边距
     * @param paddingBottom 下边距
     *
     * @return
     */
    public Bitmap addWaterMarkRightBottom(Bitmap waterMark, int paddingRight, int paddingBottom) {
        Bitmap srcBitmap = getEditedBitmap();
        return ImageUtil.createWaterMaskRightBottom(mContext, srcBitmap, waterMark, paddingRight, paddingBottom);
    }

    /**
     * 保存涂鸦后的图片，不带水印
     *
     * @param filePath
     * @param listener
     */
    public void save(String filePath, OnBitmapSaveListener listener) {
        Bitmap retBitmap = getEditedBitmap();
        boolean isSucc = ImageUtil.saveBitmapToFile(retBitmap, filePath);
        if (isSucc) {
            listener.onSucc();
        } else {
            listener.onFail();
        }
    }

    /**
     * 保存涂鸦后的图片，带水印
     *
     * @param filePath
     * @param waterMark
     * @param paddingRight
     * @param paddingBottom
     * @param listener
     */
    public void save(String filePath, Bitmap waterMark, int paddingRight, int paddingBottom,
                     OnBitmapSaveListener listener) {
        Bitmap retBitmap = addWaterMarkRightBottom(waterMark, paddingRight, paddingBottom);
        boolean isSucc = ImageUtil.saveBitmapToFile(retBitmap, filePath);
        if (isSucc) {
            listener.onSucc();
        } else {
            listener.onFail();
        }
    }

    /**
     * 屏幕触摸点坐标转换为图片中的坐标
     *
     * @param touchX
     *
     * @return
     */
    private float toX(float touchX) {
        return (touchX - mCenterTranX - mTransX) / (mPrivateScale * mScale);
    }

    private float toY(float touchY) {
        return (touchY - mCenterTranY - mTransY) / (mPrivateScale * mScale);
    }

    /**
     * 计算缩放后在x轴方向上的坐标偏移量
     *
     * @param touchX
     * @param scaleCenterX
     *
     * @return
     */
    private float toTransX(float touchX, float scaleCenterX) {
        return touchX - mCenterTranX - scaleCenterX * (mPrivateScale * mScale);
    }

    private float toTransY(float touchY, float scaleCenterY) {
        return touchY - mCenterTranY - scaleCenterY * (mPrivateScale * mScale);
    }

    public void setViewClickListener(OnViewClickListener listener) {
        this.mClickListener = listener;
    }

    /**
     * 设置要编辑的图片
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {

    }

}
