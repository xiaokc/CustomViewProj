package com.android.cong.customviewproj.pictureview.custom;

import java.io.File;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class FileUtil {

    /**
     * 判断文件是否是图片
     *
     * @param file
     *
     * @return
     */
    public static boolean isImage(File file) {
        String path = file.getPath();
        return path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith("jpeg");
    }


}
