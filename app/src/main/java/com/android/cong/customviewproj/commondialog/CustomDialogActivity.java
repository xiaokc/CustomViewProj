package com.android.cong.customviewproj.commondialog;

import com.android.cong.customviewproj.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

/**
 * Created by xiaokecong on 06/07/2017.
 */

public class CustomDialogActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomDialog(CustomDialogActivity.this,
                        R.style.Theme_AppCompat_Dialog, "视频过大，需要剪切后编辑", new CustomDialog.OnCloseListener() {
                    @Override
                    public void onClose(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            Toast.makeText(CustomDialogActivity.this,"点击确定", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CustomDialogActivity.this,"点击取消", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }).setTitle("提示信息").show();
            }
        });
    }

}
