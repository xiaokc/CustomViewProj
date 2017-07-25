package com.android.cong.customviewproj.pictureview.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by xiaokecong on 25/07/2017.
 */

public class ShowAndEditActivity extends Activity implements View.OnClickListener {
    private CustomImageView customImageView;

    private RelativeLayout layoutRevoke; // 撤销的view

    private RelativeLayout layoutToolbarShowImage;
    private LinearLayout layoutToolbarEditImage;

    private LinearLayout llShowEdit;
    private LinearLayout llShowShare;
    private LinearLayout llShowOcr;
    private LinearLayout llShowDelete;

    private Button btnSave;
    private ImageView ivEditClose;
    private ImageView ivEditShare;
    private ImageView btnRevoke;

    private ImageView ivColorWhite;
    private ImageView ivColorBlack;
    private ImageView ivColorRed;
    private ImageView ivColorOrange;
    private ImageView ivColorYellow;
    private ImageView ivColorGreen;
    private ImageView ivColorBlue;
    private ImageView ivColorPurple;

    private Map<Integer, Integer> viewMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale);

        initView();

        initEvent();
    }

    private void initView() {
        customImageView = (CustomImageView) findViewById(R.id.iv_image);
        customImageView.setClickEnable(true);

        layoutRevoke = (RelativeLayout) findViewById(R.id.layout_revoke);

        layoutToolbarShowImage = (RelativeLayout) findViewById(R.id.layout_toolbar_show_image);
        layoutToolbarEditImage = (LinearLayout) findViewById(R.id.layout_toolbar_edit_image);

        llShowEdit = (LinearLayout) findViewById(R.id.ll_show_edit);
        llShowShare = (LinearLayout) findViewById(R.id.ll_show_share);
        llShowOcr = (LinearLayout) findViewById(R.id.ll_show_ocr);
        llShowDelete = (LinearLayout) findViewById(R.id.ll_show_delete);

        btnRevoke = (ImageView) findViewById(R.id.btn_revoke);
        btnSave = (Button) findViewById(R.id.btn_edit_save);
        ivEditClose = (ImageView) findViewById(R.id.iv_edit_close);
        ivEditShare = (ImageView) findViewById(R.id.iv_edit_share);

        ivColorWhite = (ImageView) findViewById(R.id.iv_color_white);
        ivColorBlack = (ImageView) findViewById(R.id.iv_color_black);
        ivColorRed = (ImageView) findViewById(R.id.iv_color_red);
        ivColorOrange = (ImageView) findViewById(R.id.iv_color_orange);
        ivColorYellow = (ImageView) findViewById(R.id.iv_color_yellow);
        ivColorGreen = (ImageView) findViewById(R.id.iv_color_green);
        ivColorBlue = (ImageView) findViewById(R.id.iv_color_blue);
        ivColorPurple = (ImageView) findViewById(R.id.iv_color_purple);

        viewMap = new HashMap<>();
        viewMap.put(R.id.iv_color_white, R.id.iv_color_white_tag);
        viewMap.put(R.id.iv_color_black, R.id.iv_color_black_tag);
        viewMap.put(R.id.iv_color_red, R.id.iv_color_red_tag);
        viewMap.put(R.id.iv_color_orange, R.id.iv_color_orange_tag);
        viewMap.put(R.id.iv_color_yellow, R.id.iv_color_yellow_tag);
        viewMap.put(R.id.iv_color_green, R.id.iv_color_green_tag);
        viewMap.put(R.id.iv_color_blue, R.id.iv_color_blue_tag);
        viewMap.put(R.id.iv_color_purple, R.id.iv_color_purple_tag);

    }

    private void initEvent() {
        customImageView.setClickEnable(true);
        customImageView.setViewClickListener(new OnViewClickListener() {
            @Override
            public void onViewClick() {
                if (layoutToolbarShowImage.getVisibility() == View.VISIBLE) {
                    layoutToolbarShowImage.setVisibility(View.GONE);
                } else {
                    layoutToolbarShowImage.setVisibility(View.VISIBLE);
                }
            }
        });

        llShowEdit.setOnClickListener(this);
        llShowShare.setOnClickListener(this);
        llShowOcr.setOnClickListener(this);
        llShowDelete.setOnClickListener(this);

        btnRevoke.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        ivEditClose.setOnClickListener(this);
        ivEditShare.setOnClickListener(this);

        ivColorWhite.setOnClickListener(this);
        ivColorBlack.setOnClickListener(this);
        ivColorRed.setOnClickListener(this);
        ivColorOrange.setOnClickListener(this);
        ivColorYellow.setOnClickListener(this);
        ivColorGreen.setOnClickListener(this);
        ivColorBlue.setOnClickListener(this);
        ivColorPurple.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_show_edit:
                changeEditToolbar();
                break;
            case R.id.ll_show_share:
                Toast.makeText(this, "分享", Toast.LENGTH_LONG).show();
                handleShareEvent();
                break;
            case R.id.ll_show_ocr:
                Toast.makeText(this, "ocr", Toast.LENGTH_LONG).show();
                break;
            case R.id.ll_show_delete:
                Toast.makeText(this, "delete", Toast.LENGTH_LONG).show();
                break;

            case R.id.btn_revoke:
                customImageView.undo();
                break;
            case R.id.btn_edit_save:
                handleSaveEvent();
                break;
            case R.id.iv_edit_close:
                handleEditCloseEvent();
                break;
            case R.id.iv_edit_share:
                handleShareEvent();
                break;
            case R.id.iv_color_white:
                customImageView.setPaintColor(Color.parseColor("#ffffff"));
                updateColorToolbar(R.id.iv_color_white);
                break;
            case R.id.iv_color_black:
                customImageView.setPaintColor(Color.parseColor("#333333"));
                updateColorToolbar(R.id.iv_color_black);
                break;
            case R.id.iv_color_red:
                customImageView.setPaintColor(Color.parseColor("#ff3440"));
                updateColorToolbar(R.id.iv_color_red);
                break;
            case R.id.iv_color_orange:
                customImageView.setPaintColor(Color.parseColor("#ff5c26"));
                updateColorToolbar(R.id.iv_color_orange);
                break;
            case R.id.iv_color_yellow:
                customImageView.setPaintColor(Color.parseColor("#ffd24c"));
                updateColorToolbar(R.id.iv_color_yellow);
                break;
            case R.id.iv_color_green:
                customImageView.setPaintColor(Color.parseColor("#00cc88"));
                updateColorToolbar(R.id.iv_color_green);
                break;
            case R.id.iv_color_blue:
                customImageView.setPaintColor(Color.parseColor("#0ba5d9"));
                updateColorToolbar(R.id.iv_color_blue);
                break;
            case R.id.iv_color_purple:
                customImageView.setPaintColor(Color.parseColor("#d957d9"));
                updateColorToolbar(R.id.iv_color_purple);
                break;
        }

    }

    private void updateColorToolbar(int selectedViewId) {
        Iterator iterator = viewMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            int viewId = (Integer) entry.getKey();
            int viewIconId = (Integer) entry.getValue();

            if (selectedViewId == viewId) {
                ImageView selectedView = (ImageView) findViewById(selectedViewId);
                ImageView selectedIconView = (ImageView) findViewById(viewIconId);

                if (selectedViewId == R.id.iv_color_white) {
                    Drawable srcDrawable = selectedIconView.getDrawable();
                    Drawable dstDrawable = ImageUtil.tintDrawable(srcDrawable, R.color.c_8b8b8c);
                    selectedIconView.setImageDrawable(dstDrawable);
                }
                selectedIconView.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams params = selectedView.getLayoutParams();
                params.width = params.height = ScreenUtil.dipToPx(ShowAndEditActivity.this, 35);
                selectedView.setLayoutParams(params);

            } else {
                ImageView unSelectedView = (ImageView) findViewById(viewId);
                ImageView unSelectedViewIcon = (ImageView) findViewById(viewIconId);
                unSelectedViewIcon.setVisibility(View.INVISIBLE);

                ViewGroup.LayoutParams params = unSelectedView.getLayoutParams();
                params.width = params.height = ScreenUtil.dipToPx(ShowAndEditActivity.this, 30);
                unSelectedView.setLayoutParams(params);

            }
        }
    }

    private void handleEditCloseEvent() {
        // 提示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage("保存涂鸦？")
                .setNegativeButton("舍弃", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        customImageView.clear();
                        changeShowToolbar();

                    }
                })
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handleSaveEvent();
                    }
                })
                .show();

    }

    private void handleSaveEvent() {
        customImageView.save("/sdcard/" + System.currentTimeMillis() + ".png", new OnBitmapSaveListener() {
            @Override
            public void onSucc() {
                Toast.makeText(ShowAndEditActivity.this, "保存成功！", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ShowAndEditActivity.this, OcrHistoryActivity.class));
            }

            @Override
            public void onFail() {
                Toast.makeText(ShowAndEditActivity.this, "保存失败！", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleShareEvent() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackEvent();

    }

    private void handleBackEvent() {
        if (layoutToolbarEditImage.getVisibility() == View.VISIBLE) {
            // TODO: 25/07/2017  询问是否保存涂鸦内容的对话框
        } else {
            this.finish();
        }
    }

    // toolbar 切换至 toolbar_show_image
    private void changeShowToolbar() {
        layoutToolbarShowImage.setVisibility(View.VISIBLE);
        layoutToolbarEditImage.setVisibility(View.GONE);
        layoutRevoke.setVisibility(View.GONE);
        customImageView.setClickEnable(true);
        customImageView.setPaintEnable(false);
    }

    private void changeEditToolbar() {
        layoutToolbarShowImage.setVisibility(View.GONE);
        layoutToolbarEditImage.setVisibility(View.VISIBLE);
        layoutRevoke.setVisibility(View.VISIBLE);
        customImageView.setClickEnable(false);
        customImageView.setPaintEnable(true);
        customImageView.setPaintColor(Color.parseColor("#ffffff"));
        updateColorToolbar(R.id.iv_color_white);

    }
}
