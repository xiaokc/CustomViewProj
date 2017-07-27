package com.android.cong.customviewproj.pictureview.custom.history;

import com.android.cong.customviewproj.R;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryFooterViewHolder extends OcrHistoryBaseViewHolder {
    protected TextView mTxt;
    protected ProgressBar mPrg;
    public OcrHistoryFooterViewHolder(Context context) {
        super(context, R.layout.log_footer);
    }

    @Override
    protected void initView(View itemView) {
        mTxt = (TextView)itemView.findViewById(R.id.log_footer_txt);
        mPrg = (ProgressBar)itemView.findViewById(R.id.log_footer_prg);
    }

    @Override
    protected void bindData(Object info) {
        boolean tIsHasMore = (Boolean)info;
        mTxt.setVisibility(View.VISIBLE);
        if(tIsHasMore){
            mPrg.setVisibility(View.VISIBLE);
            mTxt.setText("Loading...");
        }else{
            mPrg.setVisibility(View.GONE);
            mTxt.setText("No more data");
        }
    }
}
