package com.android.cong.customviewproj.pictureview.custom.history;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.custom.FileUtil;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by xiaokecong on 25/07/2017.
 */

public class OcrHistoryActivity extends Activity {
    private RecyclerView rvHistory;
    private ImageView ivHeadbarBack;
    private ImageView ivHeadbarMore;

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
        ivHeadbarBack = (ImageView) findViewById(R.id.iv_headbar_back);
        ivHeadbarMore = (ImageView) findViewById(R.id.iv_headbar_more);

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
}
