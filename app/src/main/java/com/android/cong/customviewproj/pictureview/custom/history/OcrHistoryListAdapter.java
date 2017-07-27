package com.android.cong.customviewproj.pictureview.custom.history;

import java.text.DecimalFormat;
import java.util.List;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.pictureview.custom.FileUtil;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryListAdapter extends RecyclerView.Adapter<OcrHistoryListAdapter.HistoryItemViewHolder> {

    private Context mContext;
    private List<OcrHistoryItem> mDataList;

    public OcrHistoryListAdapter(Context context, List<OcrHistoryItem> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_ocrhistory, null, false);
        HistoryItemViewHolder holder = new HistoryItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(HistoryItemViewHolder holder, int position) {
        OcrHistoryItem item = mDataList.get(position);
        String path = item.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        String name = path.substring(path.lastIndexOf("/") + 1);
        holder.ivImage.setImageBitmap(
                ImageUtil.getBitmapThumb(bitmap,ScreenUtil.dipToPx(mContext,200), ScreenUtil.dipToPx(mContext,200)));
        holder.tvSizeAndWh.setText(ImageUtil.getSizeStr(bitmap));
        holder.tvName.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 查看
                Toast.makeText(mContext,"查看",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setDataList(List<OcrHistoryItem> dataList) {
        this.mDataList = dataList;
        notifyDataSetChanged();
    }

    class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvSizeAndWh;
        private TextView tvName;

        private LinearLayout llShowEdit;
        private LinearLayout llShowShare;
        private LinearLayout llShowOcr;
        private LinearLayout llShowDelete;

        public HistoryItemViewHolder(View itemView) {
            super(itemView);
            ivImage = (ImageView) itemView.findViewById(R.id.iv_item_image);
            tvSizeAndWh = (TextView) itemView.findViewById(R.id.tv_item_size_wh);
            tvName = (TextView) itemView.findViewById(R.id.tv_item_name);

            llShowEdit = (LinearLayout) itemView.findViewById(R.id.ll_show_edit);
            llShowShare = (LinearLayout) itemView.findViewById(R.id.ll_show_share);
            llShowOcr = (LinearLayout) itemView.findViewById(R.id.ll_show_ocr);
            llShowDelete = (LinearLayout) itemView.findViewById(R.id.ll_show_delete);
        }
    }
}
