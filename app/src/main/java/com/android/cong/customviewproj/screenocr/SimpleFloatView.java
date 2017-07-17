package com.android.cong.customviewproj.screenocr;

import java.lang.reflect.Field;

import com.android.cong.customviewproj.BaseApplication;
import com.android.cong.customviewproj.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by xiaokecong on 14/07/2017.
 */

public class SimpleFloatView {
    static boolean isShow = false;

    public static void show() {
        FloatView floatView = new FloatView(BaseApplication.getInstance());
        floatView.show();
    }

    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        int statusBarHeight = 0;

        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = BaseApplication.getInstance().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return statusBarHeight;
    }

    private static class FloatView extends View {

        static Paint paint = new Paint();
        int alpha = 255;
        WindowManager.LayoutParams wmParams;
        WindowManager windowManager;
        float mTouchScreenX;
        float mTouchScreenY;
        float mTouchX;
        float mTouchY;
        float mStartX;
        float mStartY;

        OnClickListener mClickListener;

        static {
            paint.setAntiAlias(true);
        }

        public FloatView(Context context) {
            super(context);
            init(context);
        }

        private void init(Context context) {
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            wmParams = new WindowManager.LayoutParams();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            drawSmallView(canvas);
        }

        private void drawSmallView(Canvas canvas) {
            paint.setAlpha(alpha);

            int width = getWidth();
            int height = getHeight();

            float r = ScreenUtil.dipToPx(getContext(), 15);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xaa104E8B);
            canvas.drawCircle(width - r, height / 2, r, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xccffffff);
            paint.setStrokeWidth(ScreenUtil.dipToPx(getContext(), 1));
            canvas.drawCircle(width - r, height / 2, r, paint);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.float_view_circle_inside);

            Rect srcRect = new Rect();
            Rect dstRect = new Rect();

            srcRect.left = srcRect.top = 0;
            srcRect.right = bitmap.getWidth();
            srcRect.bottom = bitmap.getHeight();

            dstRect.left = (int) (width - 2 * r);
            dstRect.right = width;
            dstRect.top = (int) (height / 2 - r);
            dstRect.bottom = (int) (dstRect.top + 2 * r);

            int padding = ScreenUtil.dipToPx(getContext(), 5);
            dstRect.left += padding;
            dstRect.top += padding;
            dstRect.right -= padding;
            dstRect.bottom -= padding;

            canvas.drawBitmap(bitmap, srcRect, dstRect, paint);

        }

        long currentTime;
        long lastTime;
        float lastX;
        float lastY;

        boolean isLongPressed = false;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            mTouchScreenX = event.getRawX();
            mTouchScreenY = event.getRawY() - getStatusBarHeight();
            Log.i("===>xkc","x:"+event.getX()+",y:"+event.getY());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 手指按下
                    mTouchX = event.getX();
                    mTouchY = event.getY();

                    mStartX = mTouchScreenX;
                    mStartY = mTouchScreenY;
                    break;
                case MotionEvent.ACTION_MOVE: // 手指移动
                    updateViewPosition(); // 更新view坐标
                    currentTime = System.currentTimeMillis();
                    if (Math.abs(event.getX() - lastX) > 2 || Math.abs(event.getY() - lastY) > 2) {
                        lastTime = currentTime;
                    }

                    if (currentTime - lastTime >= 1000 && !isLongPressed && lastTime != 0) { // 长按
                        Log.i("===xkc", "long click");
                        isLongPressed = true;
                    }
                    break;
                case MotionEvent.ACTION_UP: // 手指抬起
                    updateViewPosition(); // 更新view坐标
                    isLongPressed = false;
                    mTouchX = mTouchY = 0;
                    if (mTouchScreenX - mStartX < 5 && mTouchScreenY - mStartY < 5) {
                        if (mClickListener != null) {
                            mClickListener.onClick(this);
                        }
                    }

                    absorbentView(); // 吸附到左侧或者右侧
                    break;
            }
            lastX = event.getX();
            lastY = event.getY();
            return true;

        }

        private void updateViewPosition() {
            // 更新浮动窗口的位置参数
            wmParams.x = (int) (mTouchScreenX - mTouchX);
            wmParams.y = (int) (mTouchScreenY - mTouchY);
            windowManager.updateViewLayout(this, wmParams); // 刷新显示位置
        }

        private void absorbentView() {
            if (windowManager != null && wmParams != null) {
                final int screenWidth = ScreenUtil.getScreenWidth(BaseApplication.getInstance());
                int floatViewX = wmParams.x;
                final Handler handler = new Handler(Looper.getMainLooper());
                if (floatViewX > screenWidth / 2) { // 吸附到右侧
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean isStop = refreshAbsorbentView(true, screenWidth);
                            if (!isStop) {
                                handler.postDelayed(this, 8);
                            }
                        }
                    });
                } else { // 吸附到左侧
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            boolean isStop = refreshAbsorbentView(false, screenWidth);
                            if (!isStop) {
                                handler.postDelayed(this, 8);
                            }
                        }
                    });
                }
            }
        }

        /**
         * 更新吸附后的位置，完成吸附效果
         *
         * @param isToRight
         * @param screenWidth
         *
         * @return
         */
        private boolean refreshAbsorbentView(boolean isToRight, int screenWidth) {
            int floatViewX = wmParams.x;
            int offset = 15;
            boolean isStop = false;
            if (isToRight) {
                if (floatViewX < screenWidth && floatViewX > screenWidth / 2) {
                    wmParams.x = floatViewX + offset;
                    windowManager.updateViewLayout(this, wmParams);
                    isStop = false;
                } else if (floatViewX >= screenWidth) {
                    isStop = true;
                }
            } else {
                if (floatViewX > 0 && floatViewX <= screenWidth / 2) {
                    wmParams.x = floatViewX - offset;
                    windowManager.updateViewLayout(this, wmParams);
                    isStop = false;
                } else if (floatViewX <= 0) {
                    isStop = true;
                }
            }
            return isStop;
        }

        @Override
        public void setOnClickListener(@Nullable OnClickListener l) {
            super.setOnClickListener(l);
            this.mClickListener = l;
        }

        public void show() {
            isShow = true;

            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE; // 设置window type
            wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

            // 设置flag
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角，便与调整坐标

            // location and size
            wmParams.x = 0;
            wmParams.y = ScreenUtil.dipToPx(getContext(), 100);
            wmParams.width = ScreenUtil.dipToPx(getContext(), 40);
            wmParams.height = ScreenUtil.dipToPx(getContext(), 40);

            // 显示FloatView
            windowManager.addView(this, wmParams);

        }
    }
}
