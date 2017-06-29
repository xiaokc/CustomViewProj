package com.android.cong.customviewproj.piegraph;

import android.support.annotation.NonNull;

/**
 * Created by xiaokecong on 29/06/2017.
 */

public class PieData {
    // 关心的数据
    private String name;
    private float value;
    private float percentage;

    // 非关心数据
    private int color = 0;
    private float angle = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public PieData(@NonNull String name, @NonNull float value) {
        this.name = name;
        this.value = value;

    }
}
