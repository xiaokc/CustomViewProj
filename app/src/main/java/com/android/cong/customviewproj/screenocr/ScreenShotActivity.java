package com.android.cong.customviewproj.screenocr;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by xiaokecong on 18/07/2017.
 */

public class ScreenShotActivity extends Activity {
    public static final int REQUEST_MEDIA_PROJECTION = 0x100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);           // 透明状态栏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);    // 透明导航栏

        requestScreenShot();

    }

    private void requestScreenShot() {
        if (Build.VERSION.SDK_INT >= 21) {
            startActivityForResult(((MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE))
                    .createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        } else {
            toast("版本过低,无法截屏");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:
                if (resultCode == RESULT_OK && data != null) {
                    ScreenShotHelper helper = new ScreenShotHelper(this, data);
                    helper.startScreenShot(new ScreenShotHelper.OnShotListener(){
                        @Override
                        public void onFinish() {
                            toast("finish shot");
                            finish();
                        }
                    });
                }
                break;
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
