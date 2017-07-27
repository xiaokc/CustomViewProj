package com.android.cong.customviewproj.pictureview.custom.history;

import java.util.ArrayList;
import java.util.List;

import com.android.cong.customviewproj.pictureview.custom.ToolbarTabView;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryAdapter extends RecyclerView.Adapter<OcrHistoryBaseViewHolder> implements View.OnTouchListener {
    private final static int TYPE_FOOTER = 0x100;
    private final static int TYPE_NORMAL = 0x101;

    private List<OcrHistoryItem> mDataList;
    private Context mContext;
    private boolean mHasFooter;
    private boolean mIsHasMore;

    private OnHistoryItemClickListener mItemClickListener;
    private OnHistoryTabViewClickListener mItemTabViewClickListener;

    private final int DEFAULT_TEXT_COLOR = Color.parseColor("#99000000");
    private final int TOUCH_DOWN_COLOR = Color.parseColor("#2274e6");

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
        } else if (holder instanceof OcrHistoryViewHolder) {
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

            // 为4个tabview 设置触摸事件监听
            ((OcrHistoryViewHolder) holder).tabEdit.setOnTouchListener(this);
            ((OcrHistoryViewHolder) holder).tabShare.setOnTouchListener(this);
            ((OcrHistoryViewHolder) holder).tabOcr.setOnTouchListener(this);
            ((OcrHistoryViewHolder) holder).tabDelete.setOnTouchListener(this);

            // 为4个tabview 设置点击事件监听
            ((OcrHistoryViewHolder) holder).tabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemTabViewClickListener.onHistoryItemTabViewClick(
                            ((OcrHistoryViewHolder) holder).tabEdit, item.getPath());
                }
            });

            ((OcrHistoryViewHolder) holder).tabShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemTabViewClickListener.onHistoryItemTabViewClick(
                            ((OcrHistoryViewHolder) holder).tabShare, item.getPath());
                }
            });
            ((OcrHistoryViewHolder) holder).tabOcr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemTabViewClickListener.onHistoryItemTabViewClick(
                            ((OcrHistoryViewHolder) holder).tabOcr, item.getPath());
                }
            });
            ((OcrHistoryViewHolder) holder).tabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemTabViewClickListener.onHistoryItemTabViewClick(
                            ((OcrHistoryViewHolder) holder).tabDelete, item.getPath());
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

    public void setOnHistoryItemTabViewClickListener(OnHistoryTabViewClickListener listener) {
        mItemTabViewClickListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((ToolbarTabView) v).setTouchDownColor(TOUCH_DOWN_COLOR);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ((ToolbarTabView) v).setDefaultColor(DEFAULT_TEXT_COLOR);
                break;
        }
        return false;
    }
}
