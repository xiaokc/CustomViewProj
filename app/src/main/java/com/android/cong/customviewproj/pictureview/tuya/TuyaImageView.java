package com.android.cong.customviewproj.pictureview.tuya;

import java.util.ArrayList;
import java.util.List;

import com.android.cong.customviewproj.BaseApplication;
import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by xiaokecong on 19/07/2017.
 */

public class TuyaImageView extends android.support.v7.widget.AppCompatImageView {
    float clickX;
    float clickY;

    Paint paint;
    Line currentLine = new Line();
    List<Line> lines = new ArrayList<>();

    public TuyaImageView(Context context) {
        super(context);
        init(context);
    }

    public TuyaImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(ScreenUtil.dipToPx(context, 3));
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画出之前所有的线
        for (int i = 0; i < lines.size(); i ++) {
            drawLine(canvas, lines.get(i));
        }

        // 画出当前的线
        drawLine(canvas, currentLine);
    }

    /**
     * 将两个点连成线
     * @param canvas
     * @param line
     */
    private void drawLine(Canvas canvas, Line line) {
        for (int i = 0; i < line.points.size() - 1; i ++) {
            ViewPoint point = line.points.get(i);
            float x = point.x;
            float y = point.y;

            ViewPoint nextPoint = line.points.get(i + 1);
            float nextX = nextPoint.x;
            float nextY = nextPoint.y;

            canvas.drawLine(x,y,nextX,nextY,paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        clickX = event.getX();
        clickY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                ViewPoint point = new ViewPoint();
                point.x = clickX;
                point.y = clickY;
                currentLine.points.add(point);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                lines.add(currentLine);
                currentLine = new Line();

                invalidate();
                break;
        }
        return true;
    }

    // 撤销上一笔
    public void dropLastLine() {
        if (lines != null && lines.size() > 0) {
            lines.remove(lines.size() - 1);
            invalidate();
        }

    }

    // 清屏
    public void clearAllLines() {
        if (lines != null && lines.size() > 0) {
            lines.clear();
            invalidate();
        }
    }

    /**
     * 保存涂鸦
     * @param filePath
     */
    public void save(String filePath) {

        Drawable drawable = getDrawable();
        Bitmap srcBitmap = ImageUtil.drawableToBitmap(drawable);
        if (srcBitmap != null) {
            int width = srcBitmap.getWidth();
            int height = srcBitmap.getHeight();

            Bitmap resBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(resBitmap);
            canvas.drawBitmap(srcBitmap, 0, 0, null);

            // 画出之前所有的线
            for (int i = 0; i < lines.size(); i ++) {
                drawLine(canvas,lines.get(i));
            }

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

            Bitmap waterMark = BitmapFactory.decodeResource(BaseApplication.getInstance().getResources(), R.drawable.es_logo);
            Bitmap newBitmap = ImageUtil.
                    createWaterMaskRightBottom(BaseApplication.getInstance(),resBitmap, waterMark, 10, 10);

            ImageUtil.saveBitmapToFile(newBitmap, filePath);
        }

    }

    class ViewPoint{
        float x;
        float y;
    }

    class Line{
        List<ViewPoint> points = new ArrayList<>();
    }
}
