package com.android.cong.customviewproj.screenocr;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created by xiaokecong on 14/07/2017.
 */

public class ScreenUtil {

    /**
     * convert a dip value to px value
     */
    public static int dipToPx(Context context, float dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }
}
