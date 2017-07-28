package com.android.cong.customviewproj.pictureview.custom;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.commondialog.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by xiaokecong on 27/07/2017.
 */

public class DeleteHistoryItemDialog extends Dialog implements View.OnClickListener {
    private TextView tvTitle;
    private TextView tvRadio;
    private CheckBox btnDeleteRealFile;
    private Button btnCancel;
    private Button btnDelete;

    private String mNegativeName;
    private String mPositiveName;
    private String mRadioText;

    private Context mContext;
    private String mTitle;
    private OnCloseListener mListener;

    public DeleteHistoryItemDialog(@NonNull Context context) {
        this(context, 0, "");
    }

    public DeleteHistoryItemDialog(Context context, int themeResId, String title) {
        this(context, themeResId, title, null);
    }

    public DeleteHistoryItemDialog(Context context, int themeResId, String title, OnCloseListener listener) {
        super(context, themeResId);
        init(context, title, listener);

    }

    protected DeleteHistoryItemDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        this(context, 0, "");
    }

    private void init(Context context, String content, OnCloseListener listener) {
        this.mContext = context;
        this.mTitle = content;
        this.mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_history_item);
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRadio = (TextView) findViewById(R.id.tv_radio);
        btnDeleteRealFile = (CheckBox) findViewById(R.id.btn_delete_real_file);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnDelete = (Button) findViewById(R.id.btn_delete);

        tvTitle.setText(mTitle);
        tvRadio.setText(mRadioText);

        btnDeleteRealFile.setEnabled(true);

        btnDelete.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        if (!TextUtils.isEmpty(mPositiveName)) {
            btnDelete.setText(mPositiveName);
        }

        if (!TextUtils.isEmpty(mNegativeName)) {
            btnCancel.setText(mNegativeName);
        }
    }


    public DeleteHistoryItemDialog setPositiveButton(String name){
        this.mPositiveName = name;
        return this;
    }

    public DeleteHistoryItemDialog setNegativeButton(String name){
        this.mNegativeName = name;
        return this;
    }

    public DeleteHistoryItemDialog setRadioText(String text) {
        this.mRadioText = text;
        return this;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                if (mListener != null) {
                    mListener.onClose(this, false, false);
                }
                break;
            case R.id.btn_delete:
               if (mListener != null) {
                   mListener.onClose(this, true, btnDeleteRealFile.isChecked());
               }
                break;
        }

    }
    public interface OnCloseListener {
        void onClose(Dialog dialog, boolean confirm, boolean deleteRealFile);
    }


}
