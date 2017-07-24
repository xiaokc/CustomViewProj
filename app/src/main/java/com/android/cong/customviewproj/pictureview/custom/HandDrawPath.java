package com.android.cong.customviewproj.pictureview.custom;

import android.graphics.Path;

/**
 * Created by xiaokecong on 24/07/2017.
 */

public class HandDrawPath {
    private Path path;
    private int paintColor;

    public HandDrawPath() {
    }

    public HandDrawPath(Path path, int paintColor) {
        this.path = path;
        this.paintColor = paintColor;
    }

    public Path getPath() {

        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }
}
