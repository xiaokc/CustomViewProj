package com.android.cong.customviewproj.pictureview;

import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

/**
 * 图片工具类
 * Created by xiaokecong on 18/07/2017.
 */

public class ImageUtil {

    /**
     * 绘制水印到左上角
     *
     * @param context     上下文
     * @param src         原始图片
     * @param waterMark   水印图片
     * @param paddingLeft 左间距
     * @param paddingTop  上间距
     *
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(Context context,
                                                Bitmap src, Bitmap waterMark,
                                                int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, waterMark,
                ScreenUtil.dipToPx(context, paddingLeft), ScreenUtil.dipToPx(context, paddingTop));

    }

    /**
     * 绘制水印到左下角
     *
     * @param context
     * @param src
     * @param waterMark
     * @param paddingLeft
     * @param paddingBottom
     *
     * @return
     */
    public static Bitmap createWaterMaskLeftBottom(Context context,
                                                   Bitmap src, Bitmap waterMark,
                                                   int paddingLeft, int paddingBottom) {
        return createWaterMaskBitmap(src, waterMark, ScreenUtil.dipToPx(context, paddingLeft),
                src.getHeight() - waterMark.getHeight() - ScreenUtil.dipToPx(context, paddingBottom));
    }

    /**
     * 绘制水印到右上角
     *
     * @param context
     * @param src
     * @param waterMark
     * @param paddingRight
     * @param paddingTop
     *
     * @return
     */
    public static Bitmap createWaterMaskRightTop(Context context,
                                                 Bitmap src, Bitmap waterMark,
                                                 int paddingRight, int paddingTop) {
        return createWaterMaskBitmap(src, waterMark,
                src.getWidth() - waterMark.getWidth() - ScreenUtil.dipToPx(context, paddingRight),
                ScreenUtil.dipToPx(context, paddingTop));
    }

    /**
     * 绘制水印到右下角
     *
     * @param context
     * @param src
     * @param waterMark
     * @param paddingRight
     * @param paddingBottom
     *
     * @return
     */
    public static Bitmap createWaterMaskRightBottom(Context context,
                                                    Bitmap src, Bitmap waterMark,
                                                    int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, waterMark,
                src.getWidth() - waterMark.getWidth() - ScreenUtil.dipToPx(context, paddingRight),
                src.getHeight() - waterMark.getHeight() - ScreenUtil.dipToPx(context, paddingBottom));
    }

    /**
     * 绘制水印到中间
     *
     * @param context
     * @param src
     * @param waterMark
     *
     * @return
     */
    public static Bitmap createWaterMaskCenter(Context context, Bitmap src, Bitmap waterMark) {
        return createWaterMaskBitmap(src, waterMark,
                (src.getWidth() - waterMark.getWidth()) / 2,
                (src.getHeight() - waterMark.getHeight()) / 2);
    }

    /**
     * 绘制图片水印
     *
     * @param src
     * @param waterMark
     * @param paddingLeft
     * @param paddingTop
     *
     * @return
     */
    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap waterMark, int paddingLeft, int paddingTop) {
        if (null == src) {
            return null;
        }

        int width = src.getWidth();
        int height = src.getHeight();

        // 创建一个和原始图片宽高相同的bitmap
        Bitmap resBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // 创建画布，并将该图片传入
        Canvas canvas = new Canvas(resBitmap);

        // 在画布0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);

        // 将水印图片绘制在画布上
        canvas.drawBitmap(waterMark, paddingLeft, paddingTop, null);

        // 保存所有绘制内容
        canvas.save(Canvas.ALL_SAVE_FLAG);

        canvas.restore();
        return resBitmap;

    }

    /**
     * 绘制文字到左上角
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingTop
     *
     * @return
     */
    public static Bitmap drawTextToBitmapLeftTop(Context context, Bitmap bitmap,
                                                 String text, int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.dipToPx(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint,
                ScreenUtil.dipToPx(context, paddingLeft),
                ScreenUtil.dipToPx(context, paddingTop) + bounds.height());

    }

    /**
     * 绘制文字到左下角
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     *
     * @return
     */
    public static Bitmap drawTextToBitmapLeftBottom(Context context, Bitmap bitmap, String text,
                                                    int size, int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.dipToPx(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint,
                ScreenUtil.dipToPx(context, paddingLeft),
                bitmap.getHeight() - ScreenUtil.dipToPx(context, paddingBottom));
    }

    /**
     * 绘制文字到右上角
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingTop
     *
     * @return
     */
    public static Bitmap drawTextToBitmapRightTop(Context context, Bitmap bitmap, String text,
                                                  int size, int color, int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.dipToPx(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint,
                bitmap.getWidth() - bounds.width() - ScreenUtil.dipToPx(context, paddingRight),
                ScreenUtil.dipToPx(context, paddingTop) + bounds.height());
    }

    /**
     * 绘制文字到右下角
     *
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingBottom
     *
     * @return
     */
    public static Bitmap drawTextToBitmapRightBottom(Context context, Bitmap bitmap, String text,
                                                     int size, int color, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.dipToPx(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint,
                bitmap.getWidth() - bounds.width() - ScreenUtil.dipToPx(context, paddingRight),
                bitmap.getHeight() - ScreenUtil.dipToPx(context, paddingBottom));
    }

    /**
     * 绘制文字到中间
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @return
     */
    public static Bitmap drawTextToBitmapCenter(Context context, Bitmap bitmap, String text,
                                                int size, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(ScreenUtil.dipToPx(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(bitmap, text, paint,
                (bitmap.getWidth() - bounds.width()) / 2,
                (bitmap.getHeight() - bounds.height()) / 2);
    }

    /**
     * 绘制文字水印
     *
     * @param bitmap
     * @param text
     * @param paint
     *
     * @return
     */
    private static Bitmap drawTextToBitmap(Bitmap bitmap, String text, Paint paint, int paddingLeft, int paddingTop) {
        if (null == bitmap) {
            return null;
        }

        Bitmap.Config config = bitmap.getConfig();

        if (!TextUtils.isEmpty(text)) {
            paint.setDither(true); // 获得更清晰的图像采样
            paint.setFilterBitmap(true);

            if (null == config) {
                config = Bitmap.Config.ARGB_8888;
            }

            bitmap = bitmap.copy(config, true);
            Canvas canvas = new Canvas(bitmap);

            canvas.drawText(text, paddingLeft, paddingTop, paint);
            return bitmap;
        }
        return bitmap;
    }

}
