package com.android.cong.customviewproj;

import android.app.Application;

/**
 * Created by xiaokecong on 14/07/2017.
 */

public class BaseApplication extends Application {
    private static BaseApplication instance = null;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
