package com.android.cong.customviewproj.pictureview.custom.history;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.android.cong.customviewproj.pictureview.custom.ShowAndEditActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
                Log.i("===>xkc","点击了history item, path:"+itemPath);
                Intent intent = new Intent(mContext, ShowAndEditActivity.class);
                intent.putExtra("path",itemPath);
                intent.putExtra("isShow",true);
                mContext.startActivity(intent);
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

}
