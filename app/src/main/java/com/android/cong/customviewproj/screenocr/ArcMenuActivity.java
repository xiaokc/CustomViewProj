package com.android.cong.customviewproj.screenocr;

import com.android.cong.customviewproj.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by xiaokecong on 18/07/2017.
 */

public class ArcMenuActivity extends Activity {
    private ArcMenu arcMenu;
    private static final int[] ITEM_DRAWABLES = { R.drawable.composer_camera, R.drawable.composer_music,
            R.drawable.composer_place, R.drawable.composer_sleep, R.drawable.composer_thought, R.drawable.composer_with };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arc_menu);

        arcMenu = (ArcMenu) findViewById(R.id.arc_menu);

        initArcMenu(arcMenu,ITEM_DRAWABLES);

    }

    private void initArcMenu(ArcMenu arcMenu, int[] itemDrawables) {
        final int itemCount = itemDrawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(this);
            item.setImageResource(itemDrawables[i]);

            final int position = i;
            arcMenu.addItem(item, new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(ArcMenuActivity.this, "position:" + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
