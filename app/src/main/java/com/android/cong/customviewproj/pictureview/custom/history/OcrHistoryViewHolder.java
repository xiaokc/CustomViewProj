package com.android.cong.customviewproj.pictureview.custom.history;

import java.io.File;

import com.android.cong.customviewproj.R;
import com.android.cong.customviewproj.pictureview.ImageUtil;
import com.android.cong.customviewproj.pictureview.custom.ToolbarTabView;
import com.android.cong.customviewproj.screenocr.ScreenUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by xiaokecong on 26/07/2017.
 */

public class OcrHistoryViewHolder extends OcrHistoryBaseViewHolder {
    ImageView ivImage;
    TextView tvSizeAndWh;
    TextView tvName;

    LinearLayout llShowEdit;
    LinearLayout llShowShare;
    LinearLayout llShowOcr;
    LinearLayout llShowDelete;

    ToolbarTabView tabEdit;
    ToolbarTabView tabShare;
    ToolbarTabView tabOcr;
    ToolbarTabView tabDelete;

    public OcrHistoryViewHolder(Context context) {
        super(context, R.layout.item_recyclerview_ocrhistory);
    }

    @Override
    protected void initView(View itemView) {
        ivImage = (ImageView) itemView.findViewById(R.id.iv_item_image);
        tvSizeAndWh = (TextView) itemView.findViewById(R.id.tv_item_size_wh);
        tvName = (TextView) itemView.findViewById(R.id.tv_item_name);

        //        llShowEdit = (LinearLayout) itemView.findViewById(R.id.ll_show_edit);
        //        llShowShare = (LinearLayout) itemView.findViewById(R.id.ll_show_share);
        //        llShowOcr = (LinearLayout) itemView.findViewById(R.id.ll_show_ocr);
        //        llShowDelete = (LinearLayout) itemView.findViewById(R.id.ll_show_delete);

        tabEdit = (ToolbarTabView) itemView.findViewById(R.id.tab_edit);
        tabShare = (ToolbarTabView) itemView.findViewById(R.id.tab_share);
        tabOcr = (ToolbarTabView) itemView.findViewById(R.id.tab_ocr);
        tabDelete = (ToolbarTabView) itemView.findViewById(R.id.tab_delete);

    }

    @Override
    protected void bindData(Object info) {
        OcrHistoryItem item = (OcrHistoryItem) info;
        String path = item.getPath();
        File file = new File(path);
        if (!file.exists()) { // 图片路径不存在
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        String name = path.substring(path.lastIndexOf("/") + 1);
        ivImage.setImageBitmap(
                ImageUtil.getBitmapThumb(bitmap, ScreenUtil.dipToPx(mContext, 200), ScreenUtil.dipToPx(mContext, 200)));
        tvSizeAndWh.setText(ImageUtil.getSizeStr(bitmap));
        tvName.setText(name);

    }
}
