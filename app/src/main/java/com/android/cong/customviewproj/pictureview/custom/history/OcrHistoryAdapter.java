package com.android.cong.customviewproj.pictureview.custom.history;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryAdapter extends RecyclerView.Adapter<OcrHistoryBaseViewHolder> {
    private final static int TYPE_FOOTER = 0x100;
    private final static int TYPE_NORMAL = 0x101;

    private List<OcrHistoryItem> mDataList;
    private Context mContext;
    private boolean mHasFooter;
    private boolean mIsHasMore;

    private OnHistoryItemClickListener mItemClickListener;

    public OcrHistoryAdapter(Context context) {
        mContext = context;
        mDataList = new ArrayList<>();
        mHasFooter = true;
        mIsHasMore = false;
    }
    @Override
    public OcrHistoryBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OcrHistoryBaseViewHolder holder;
        switch (viewType) {
            case TYPE_FOOTER:
                holder = new OcrHistoryFooterViewHolder(mContext);
                break;
            default:
                holder = new OcrHistoryViewHolder(mContext);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final OcrHistoryBaseViewHolder holder, int position) {
        if (null == holder) {
            return;
        }

        if (holder instanceof OcrHistoryFooterViewHolder) {
            holder.bindData(mIsHasMore);
        } else if (holder instanceof OcrHistoryViewHolder){
            final OcrHistoryItem item = mDataList.get(position);
            holder.bindData(item);
            ((OcrHistoryViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemCardClick(item.getPath());
                    }
                }
            });

            ((OcrHistoryViewHolder) holder).llShowDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("===>xkc","ll show delete click");
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHasFooter && position != 0 && position == (getItemCount() - 1)) {
            return TYPE_FOOTER;
        }

        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (mHasFooter && mDataList.size() != 0) {
            return mDataList.size() + 1;
        }
        return mDataList.size();
    }

    public void setData(List<OcrHistoryItem> dataList) {
        mDataList.clear();
        mDataList.addAll(dataList);
    }

    public void setIsHasMore(boolean isHasMore) {
        mIsHasMore = isHasMore;
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        mItemClickListener = listener;
    }
}
