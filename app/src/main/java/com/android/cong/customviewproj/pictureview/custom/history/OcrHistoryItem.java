package com.android.cong.customviewproj.pictureview.custom.history;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryItem {
    private String path;
    private long lastModifiedTime;

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
