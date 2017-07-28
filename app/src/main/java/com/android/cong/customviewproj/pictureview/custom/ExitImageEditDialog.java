package com.android.cong.customviewproj.pictureview.custom;

import com.android.cong.customviewproj.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by xiaokecong on 28/07/2017.
 */

public class ExitImageEditDialog extends Dialog implements View.OnClickListener {
    private TextView tvTitle;
    private Button btnCancel;
    private Button btnOk;

    private Context mContext;
    private String mTitle;
    private String mNegativeBtnName;
    private String mPositiveBtnName;
    private OnDialogCloseListener mDialogCloseListener;

    public ExitImageEditDialog(@NonNull Context context) {
        this(context, 0, "", null);
    }

    public ExitImageEditDialog(@NonNull Context context, @StyleRes int themeResId) {
        this(context, themeResId, "", null);
    }

    public ExitImageEditDialog(@NonNull Context context, @StyleRes int themeResId,
                               String title, OnDialogCloseListener dialogCloseListener) {
        super(context, themeResId);
        init(context, title, dialogCloseListener);
    }

    private void init(Context context, String title, OnDialogCloseListener dialogCloseListener) {
        this.mContext = context;
        this.mTitle = title;
        this.mDialogCloseListener = dialogCloseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_exit_edit);
        setCanceledOnTouchOutside(true);

        initView();

    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnOk = (Button) findViewById(R.id.btn_ok);

        if (!TextUtils.isEmpty(mTitle)) {
            tvTitle.setText(mTitle);
        }
        if (!TextUtils.isEmpty(mNegativeBtnName)) {
            btnCancel.setText(mNegativeBtnName);
        }

        if (!TextUtils.isEmpty(mPositiveBtnName)) {
            btnOk.setText(mPositiveBtnName);
        }

        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
    }

    public ExitImageEditDialog setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public ExitImageEditDialog setNegativeButtonName(String name) {
        this.mNegativeBtnName = name;
        return this;
    }

    public ExitImageEditDialog setPositiveButtonName(String name) {
        this.mPositiveBtnName = name;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (mDialogCloseListener != null) {
                    mDialogCloseListener.onClose(this, false);
                }
                break;
            case R.id.btn_ok:
                if (mDialogCloseListener != null) {
                    mDialogCloseListener.onClose(this, true);
                }
                break;
        }
    }

    public interface OnDialogCloseListener {
        void onClose(Dialog dialog, boolean confirm);
    }
}
