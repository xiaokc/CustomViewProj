package com.android.cong.customviewproj.screenocr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.os.AsyncTaskCompat;
import android.text.TextUtils;

/**
 * Created by xiaokecong on 18/07/2017.
 */

public class ScreenShotHelper {
    private WeakReference<Context> mRefContext;
    private ImageReader mImageReader;

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private OnShotListener mOnShotFinishListener;
    private static final String SCREENCAP_NAME = "screenshot";
    private String mLocalUrl;

    public ScreenShotHelper(Context context, Intent data) {
        this.mRefContext = new WeakReference<>(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjection = createMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, data);
            mImageReader = ImageReader.newInstance(getWidth(), getHeight(), PixelFormat.RGBA_8888, 1);
        }

    }

    public void startScreenShot(OnShotListener listener) {
        mOnShotFinishListener = listener;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createVirtualDisplay();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Image image = mImageReader.acquireLatestImage();
                    AsyncTaskCompat.executeParallel(new SaveTask(), image);
                }
            }, 300);
        }

    }

    private void createVirtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(
                SCREENCAP_NAME, getWidth(), getHeight(), getDpi(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mImageReader.getSurface(), null, null);
    }

    private MediaProjectionManager createMediaProjectionManager() {
        return (MediaProjectionManager) getContext().getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
    }

    private Context getContext() {
        return mRefContext.get();
    }

    private int getWidth() {
        return ScreenUtil.getScreenWidth(getContext());
    }

    private int getHeight() {
        return ScreenUtil.getScreenHeight(getContext());
    }

    private int getDpi() {
        return ScreenUtil.getDensity(getContext());
    }

    class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... params) {
            if (null == params || params.length < 1 || null == params[0]) {
                return null;
            }
            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();

            // 每个像素的间距
            int pixelStride = planes[0].getPixelStride();

            // 总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();

            File fileImage = null;
            if (bitmap != null) {
                try {

                    if (TextUtils.isEmpty(mLocalUrl)) {
                        mLocalUrl = getContext().getExternalFilesDir("screenshot").getAbsoluteFile()
                                + "/" + SystemClock.currentThreadTimeMillis() + ".png";
                    }
                    fileImage = new File(mLocalUrl);

                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(fileImage);
                    if (fos != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fileImage = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }

                if (fileImage != null) {
                    return bitmap;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }

            if (mVirtualDisplay != null) {
                mVirtualDisplay.release();
            }

            if (mOnShotFinishListener != null) {
                mOnShotFinishListener.onFinish();
            }
        }
    }

    interface OnShotListener {
        void onFinish();
    }
}
