package com.android.cong.customviewproj.pictureview.custom.history;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.custom.FileUtil;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by xiaokecong on 25/07/2017.
 */

public class OcrHistoryActivity extends AppCompatActivity {
    private RecyclerView rvHistory;
    private Toolbar toolbarHistory;
    private TextView tvEmpty;

    private List<OcrHistoryItem> mDataList;
    private String mHistoryPath = "/sdcard/"; // 历史记录路径：/sdcard/ocr

    private OcrHistoryManager mOcrHistoryManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_history);

        initView();

        initData();
    }

    private void initView() {
        rvHistory = (RecyclerView) findViewById(R.id.rv_history);
        toolbarHistory = (Toolbar) findViewById(R.id.toolbar_history);
        tvEmpty = (TextView) findViewById(R.id.tv_empty_history);

        setSupportActionBar(toolbarHistory);
        toolbarHistory.setNavigationIcon(R.drawable.left_arrow_white);
        toolbarHistory.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OcrHistoryActivity.this,"back",Toast.LENGTH_LONG).show();
            }
        });

        rvHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    private void initData() {
        mOcrHistoryManager = new OcrHistoryManager(this, rvHistory);
        mOcrHistoryManager.setOcrHistoryManagerCallback(new OcrHistoryManager.OcrHistoryManagerCallback() {
            @Override
            public void onLoadSucc(int count) {
                Log.i("===>xkc","load succ, count:"+count);
            }
        });

        // 无截图时
        mOcrHistoryManager.setOcrHistoryEmptyCallback(new OcrHistoryManager.OcrHistoryEmptyCallback() {
            @Override
            public void onHistoryEmpty() {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });


    }

    private List<OcrHistoryItem> getLocalDataList() {
        List<OcrHistoryItem> dataList = new ArrayList<>();
        File dir = new File(mHistoryPath);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (FileUtil.isImage(file)) {
                    OcrHistoryItem item = new OcrHistoryItem();
                    item.setPath(file.getAbsolutePath());
                    dataList.add(item);
                }
            }
        }
        return dataList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOcrHistoryManager.loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_overflow:
                popUpOverflowWindow();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // 点击右上角的溢出菜单，弹出popupwindow
    private void popUpOverflowWindow() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int yOffset = frame.top + toolbarHistory.getHeight() + 5;
        int xOffset = ScreenUtil.dipToPx(this, 5);

        View popView = LayoutInflater.from(this).inflate(R.layout.popup_history_overflow,null);
        final PopupWindow popupWindow = new PopupWindow(popView,
                ScreenUtil.dipToPx(this, 100), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable()); // 这样设置的目的是避免点击弹窗外部，弹窗不消失的问题
        popupWindow.setOutsideTouchable(true);

        popupWindow.showAtLocation(popView.getRootView(), Gravity.RIGHT | Gravity.TOP, xOffset, yOffset);

        TextView tvSetting = (TextView) popView.findViewById(R.id.tv_setting);
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 进入设置界面
                popupWindow.dismiss();
                Intent intent = new Intent(OcrHistoryActivity.this, OcrSettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


    }
}
