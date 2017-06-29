package com.android.cong.customviewproj.piegraph;

import java.util.ArrayList;
import java.util.List;

import com.android.cong.customviewproj.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by xiaokecong on 29/06/2017.
 */

public class PieViewActivity extends Activity {
    private PieView pieView;
    private List<PieData> datas;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieview);

        pieView = (PieView) findViewById(R.id.pieview);
        initData();

        pieView.setData(datas);
    }

    private void initData() {
        datas = new ArrayList<>();
        PieData data1 = new PieData("学习",60);
        PieData data2 = new PieData("休息",20);
        PieData data3 = new PieData("购物",5);
        PieData data4 = new PieData("娱乐",15);

        datas.add(data1);
        datas.add(data2);
        datas.add(data3);
        datas.add(data4);
    }
}
