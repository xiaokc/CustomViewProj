package com.android.cong.customviewproj.pictureview.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.pictureview.custom.history.OcrDbHelper;
import com.android.cong.customviewproj.pictureview.custom.history.OcrHistoryActivity;
import com.android.cong.customviewproj.pictureview.custom.history.OcrHistoryItem;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
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

public class ShowAndEditActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    private CustomImageView customImageView;

    private RelativeLayout layoutRevoke; // 撤销的view

    private LinearLayout layoutToolbarShowImage;
    private LinearLayout layoutToolbarEditImage;

    private ToolbarTabView tabEdit;
    private ToolbarTabView tabShare;
    private ToolbarTabView tabOcr;
    private ToolbarTabView tabDelete;

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

    private Map<Integer, Integer> colorViewMap; // 编辑toolbar上的色块，key: 色块resId,value: 对应色块对勾的resId

    private OcrDbHelper mOcrDb;

    private final int DEFAULT_TEXT_COLOR = Color.parseColor("#99000000");
    private final int TOUCH_DOWN_COLOR = Color.parseColor("#2274e6");

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

        layoutToolbarShowImage = (LinearLayout) findViewById(R.id.layout_toolbar_show_image);
        layoutToolbarEditImage = (LinearLayout) findViewById(R.id.layout_toolbar_edit_image);

        tabEdit = (ToolbarTabView) findViewById(R.id.tab_edit);
        tabShare = (ToolbarTabView) findViewById(R.id.tab_share);
        tabOcr = (ToolbarTabView) findViewById(R.id.tab_ocr);
        tabDelete = (ToolbarTabView) findViewById(R.id.tab_delete);

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

        colorViewMap = new HashMap<>();
        colorViewMap.put(R.id.iv_color_white, R.id.iv_color_white_tag);
        colorViewMap.put(R.id.iv_color_black, R.id.iv_color_black_tag);
        colorViewMap.put(R.id.iv_color_red, R.id.iv_color_red_tag);
        colorViewMap.put(R.id.iv_color_orange, R.id.iv_color_orange_tag);
        colorViewMap.put(R.id.iv_color_yellow, R.id.iv_color_yellow_tag);
        colorViewMap.put(R.id.iv_color_green, R.id.iv_color_green_tag);
        colorViewMap.put(R.id.iv_color_blue, R.id.iv_color_blue_tag);
        colorViewMap.put(R.id.iv_color_purple, R.id.iv_color_purple_tag);

    }

    private void initEvent() {
        mOcrDb = OcrDbHelper.getInstance();

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

        tabEdit.setOnClickListener(this);
        tabShare.setOnClickListener(this);
        tabOcr.setOnClickListener(this);
        tabDelete.setOnClickListener(this);

        tabEdit.setOnTouchListener(this);
        tabShare.setOnTouchListener(this);
        tabOcr.setOnTouchListener(this);
        tabDelete.setOnTouchListener(this);

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
            // 图片查看页面底部的四个tab
            case R.id.tab_edit:
                changeEditToolbar();
                break;
            case R.id.tab_share:
                Toast.makeText(this, "分享", Toast.LENGTH_LONG).show();
                handleShareEvent();
                break;
            case R.id.tab_ocr:
                Toast.makeText(this, "ocr", Toast.LENGTH_LONG).show();
                break;
            case R.id.tab_delete:
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // 手指按下的时候
                ((ToolbarTabView) v).setTouchdDownColor(TOUCH_DOWN_COLOR);
                break;
            case MotionEvent.ACTION_UP:
                ((ToolbarTabView) v).setDefaultColor(DEFAULT_TEXT_COLOR);
                break;
        }
        return false;
    }

    /**
     * 更新颜色块选中后的toolbar状态
     * 选中的色块，宽高为35dp，色块上的对勾图片可见
     * 非选中色块，宽高为30dp，色块上的对勾图片不可见
     *
     * @param selectedViewId
     */
    private void updateColorToolbar(int selectedViewId) {
        Iterator iterator = colorViewMap.entrySet().iterator();
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
        long time = System.currentTimeMillis();
        String path = "/sdcard/" + time + ".png";
        customImageView.save(path, new OnBitmapSaveListener() {
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

        insertItemToDb(path, time);
    }

    /**
     * 保存的图片插入数据库
     *
     * @param path
     * @param time
     */
    private void insertItemToDb(String path, long time) {
        mOcrDb.open();
        OcrHistoryItem item = new OcrHistoryItem();
        item.setPath(path);
        item.setLastModifiedTime(time);
        mOcrDb.insertOneItem(item);
        mOcrDb.close();

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

    // toolbar 切换至 toolbar_edit_image
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
