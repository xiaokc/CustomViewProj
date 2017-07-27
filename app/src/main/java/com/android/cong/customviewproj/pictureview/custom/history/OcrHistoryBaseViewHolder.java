package com.android.cong.customviewproj.pictureview.custom.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public abstract class OcrHistoryBaseViewHolder extends RecyclerView.ViewHolder {
    protected Context mContext;
    public OcrHistoryBaseViewHolder(Context context, int layoutId) {
        super(LayoutInflater.from(context).inflate(layoutId, null));
        this.mContext = context;

        initView(itemView);
    }

    protected abstract void initView(View itemView);
    protected abstract void bindData(Object info);
}
