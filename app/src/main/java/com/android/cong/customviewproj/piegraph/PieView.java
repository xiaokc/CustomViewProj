package com.android.cong.customviewproj.piegraph;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by xiaokecong on 29/06/2017.
 */

public class PieView extends View {
    // 颜色表 (注意: 此处定义颜色使用的是ARGB，带Alpha通道的)
    private int[] mColors = {
            0xFFCCFF00, 0xFF6495ED, 0xFFE32636,
            0xFF800000, 0xFF808000, 0xFFFF8C69,
            0xFF808080, 0xFFE6B800, 0xFF7CFC00};

    private float mStartAngle = 0; // 饼状图初始绘制角度

    private List<PieData> mData; // 饼状图数据

    private int mWidth, mHeight; // 宽高

    private Paint mPaint = new Paint(); // 画笔

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
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
        if (null == mData) {
            return;
        }

        float currentStartAngle = mStartAngle; // 当前起始角度
        canvas.translate(mWidth / 2, mHeight / 2); // 将画布坐标原点移动到中心位置
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8); // 饼状图半径
        RectF rect = new RectF(-r, -r, r, r);

        for (int i = 0; i < mData.size(); i++) {
            PieData pieData = mData.get(i);
            mPaint.setColor(pieData.getColor());
            canvas.drawArc(rect, currentStartAngle, pieData.getAngle(), true, mPaint);
            currentStartAngle += pieData.getAngle();
        }
    }

    // 设置起始角度
    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
    }

    // 设置数据
    public void setData(List<PieData> data) {
        this.mData = data;
        initData(mData);
        invalidate(); // 刷新，在更改了数据需要重绘界面时要调用invalidate()这个函数重新绘制
    }

    // 初始化数据
    private void initData(List<PieData> datas) {
        if (null == datas || datas.size() == 0) {
            return;
        }

        float sumValue = 0;
        for (int i = 0; i < datas.size(); i++) {
            PieData pieData = datas.get(i);

            sumValue += pieData.getValue(); // 计算数值和

            pieData.setColor(mColors[i % mColors.length]); // 设置颜色
        }

        float sumAngle = 0;
        for (int i = 0; i < datas.size(); i++) {
            PieData pieData = datas.get(i);

            float percentage = pieData.getValue() / sumValue; // 百分比
            float angle = percentage * 360; // 对应角度

            pieData.setPercentage(percentage); // 记录百分比
            pieData.setAngle(angle); // 记录角度

            sumAngle += angle; //

            Log.i("===>xkc", "angle:" + pieData.getAngle());

        }
    }

}
