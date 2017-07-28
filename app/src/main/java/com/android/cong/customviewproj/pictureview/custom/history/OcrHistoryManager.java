package com.android.cong.customviewproj.pictureview.custom.history;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.custom.DeleteHistoryItemDialog;
import com.android.cong.customviewproj.pictureview.custom.ShowAndEditActivity;
import com.android.cong.customviewproj.pictureview.custom.ToolbarTabView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryManager {
    private final static int PAGE_SIZE = 5; // 分页加载数据，每页20条
    private final static int LOAD_MORE = 0x01;
    private final static int LOAD_REFRESH = 0x02;
    private final static String TAG = "OcrHistoryManager";

    private Context mContext;
    private RecyclerView mRecyclerView;
    private OcrHistoryAdapter mAdapter;

    private List<OcrHistoryItem> mDataList;

    private boolean mIsLoadingMore;
    private boolean mIsLoading;
    private boolean mIsNoMoreData;

    private int mPage = 0;
    private OcrDbHelper mOcrDb;

    private OcrHistoryManagerCallback mManagerCallback;
    private OcrHistoryEmptyCallback mHistoryEmptyCallback;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_REFRESH:
                    if (mIsLoading) {
                        mIsLoading = false;
                        if (mManagerCallback != null) {
                            mManagerCallback.onLoadSucc(mDataList.size());
                        }

                        mAdapter.setData(mDataList);
                        mAdapter.notifyDataSetChanged();

                    }
                    break;
                case LOAD_MORE:
                    if (mIsLoadingMore) {
                        mIsLoadingMore = false;
                        mAdapter.setData(mDataList);
                        mAdapter.notifyDataSetChanged();
                    }
                    break;

            }
        }
    };

    public OcrHistoryManager(Context context, RecyclerView recyclerView) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        init();
    }

    private void init() {
        mAdapter = new OcrHistoryAdapter(mContext);
        mDataList = new CopyOnWriteArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);

        mOcrDb = OcrDbHelper.getInstance();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && mDataList.size() > 0) {
                    int lastVisibleItem =
                            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    int totalItemCount = mRecyclerView.getAdapter().getItemCount();
                    if (!mIsLoadingMore && !mIsLoading && !mIsNoMoreData && lastVisibleItem >= totalItemCount - 2) {
                        loadMore();
                    }
                }
            }
        });

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        });

        mAdapter.setOnHistoryItemClickListener(new OnHistoryItemClickListener() {
            @Override
            public void onItemCardClick(String itemPath) {
                Log.i(TAG, "点击了history item, path:" + itemPath);
                startActivity(itemPath, true);
            }
        });

        mAdapter.setOnHistoryItemTabViewClickListener(new OnHistoryTabViewClickListener() {
            @Override
            public void onHistoryItemTabViewClick(View view, String itemPath) {
                if (view instanceof ToolbarTabView) {
                    switch (view.getId()) {
                        case R.id.tab_edit:
                            startActivity(itemPath, true);
                            break;
                        case R.id.tab_share:
                            Toast.makeText(mContext, "分享:" + itemPath, Toast.LENGTH_LONG).show();
                            break;
                        case R.id.tab_ocr:
                            Toast.makeText(mContext, "Ocr:" + itemPath, Toast.LENGTH_LONG).show();
                            break;
                        case R.id.tab_delete:
                            Log.i(TAG, "点击了删除,path:" + itemPath);
                            handleDeleteEvent(itemPath);
                            break;
                    }
                }
            }
        });
    }

    /***对外方法***/
    public void loadData() {
        if (mIsLoading) {
            return;
        }

        mIsLoading = true;
        mIsLoadingMore = false;

        new Thread() {
            @Override
            public void run() {
                synchronized (OcrHistoryManager.class) {
                    refresh();

                    Message msg = mHandler.obtainMessage();
                    msg.what = LOAD_REFRESH;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void refresh() {
        mPage = 1;
        mDataList.clear();
        mIsNoMoreData = false;
        getPageData();

    }

    private void loadMore() {
        if (mIsLoadingMore) {
            return;
        }
        mIsLoadingMore = true;
        if (mAdapter != null) {
            mAdapter.setIsHasMore(true);
        }

        new Thread() {
            @Override
            public void run() {
                synchronized (OcrHistoryManager.class) {
                    mPage++;
                    getPageData();
                    Message msg = mHandler.obtainMessage();
                    msg.what = LOAD_MORE;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    // 获取一页记录
    private void getPageData() {
        mOcrDb.open();
        List<OcrHistoryItem> onePageItems = mOcrDb.getOnePageItems(mPage, PAGE_SIZE);

        if (null == onePageItems || onePageItems.size() == 0) { // 没有更多数据了
            if (mPage == 1) { // 数据库没有一条记录
                mHistoryEmptyCallback.onHistoryEmpty();
                return;
            }
            mIsNoMoreData = true;
            if (mAdapter != null) {
                mAdapter.setIsHasMore(false);
            }
            mOcrDb.close();
        } else {
            mDataList.addAll(onePageItems);
        }
    }

    // 获取全部记录
    private void getAllData() {
        mOcrDb.open();
        List<OcrHistoryItem> allItems = mOcrDb.getAllItems();

        if (null == allItems || allItems.size() == 0) { // 没有更多数据了
            mIsNoMoreData = true;
            if (mAdapter != null) {
                mAdapter.setIsHasMore(false);
            }
            mOcrDb.close();
        } else {
            mDataList.clear();
            mDataList.addAll(allItems);
        }
    }

    public interface OcrHistoryManagerCallback {
        void onLoadSucc(int count);
    }

    public void setOcrHistoryManagerCallback(OcrHistoryManagerCallback managerCallback) {
        this.mManagerCallback = managerCallback;
    }

    public interface OcrHistoryEmptyCallback{
        void onHistoryEmpty();
    }

    public void setOcrHistoryEmptyCallback(OcrHistoryEmptyCallback historyEmptyCallback) {
        this.mHistoryEmptyCallback = historyEmptyCallback;
    }

    /**
     * 界面跳转
     *
     * @param itemPath
     * @param isShow
     */
    private void startActivity(String itemPath, boolean isShow) {
        Intent intent = new Intent(mContext, ShowAndEditActivity.class);
        intent.putExtra("imagePath", itemPath);
        intent.putExtra("isShow", isShow);
        mContext.startActivity(intent);
    }

//    /**
//     * 处理item的删除事件
//     *
//     * @param itemPath
//     */
//    private void handleDeleteEvent(final String itemPath) {
//        new DeleteHistoryItemDialog(mContext, R.style.Theme_AppCompat_Dialog, "删除历史记录？",
//                new DeleteHistoryItemDialog.OnCloseListener() {
//                    @Override
//                    public void onClose(Dialog dialog, boolean confirm, boolean deleteRealFile) {
//                        if (!confirm) {
//                            dialog.dismiss();
//                            return;
//                        }
//
//                        // 删除历史记录，从数据库中删除
//                        boolean deleteSuccFromDb = mOcrDb.deleteItemWithPath(itemPath);
//
//                        // 同时删除图片，从本地文件中删除
//                        boolean deleteSuccFromLocal = false;
//
//                        if (deleteRealFile) {
//                            File file = new File(itemPath);
//                            if (file.exists()) {
//                                deleteSuccFromLocal = file.delete();
//                            } else {
//                                Toast.makeText(mContext, "图片路径不存在", Toast.LENGTH_LONG).show();
//                                dialog.dismiss();
//                                return;
//                            }
//
//                            if (!deleteSuccFromDb && !deleteSuccFromLocal) {
//                                Toast.makeText(mContext, "删除失败", Toast.LENGTH_LONG).show();
//                                dialog.dismiss();
//                            } else {
//                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
//                                dialog.dismiss();
//                            }
//                        } else {
//                            if (deleteSuccFromDb) {
//                                Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
//                                dialog.dismiss();
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        loadData();
//                                    }
//                                });
//                            }
//                        }
//
//                    }
//                })
//                .setNegativeButton("取消")
//                .setPositiveButton("确定")
//                .setRadioText("同时删除图片")
//                .show();
//
//    }


    private void handleDeleteEvent(String itemPath) {
        boolean deleteSucc = mOcrDb.deleteItemWithPath(itemPath);
        if (deleteSucc) {
            Toast.makeText(mContext, "删除成功", Toast.LENGTH_LONG).show();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadData();
                }
            });
        }
    }

}
