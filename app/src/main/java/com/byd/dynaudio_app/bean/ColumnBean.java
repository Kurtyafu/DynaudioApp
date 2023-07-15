package com.byd.dynaudio_app.bean;

import static com.byd.dynaudio_app.fragment.NewMainFragment.HomeAdapter.HEADER_VIEW;
import static com.byd.dynaudio_app.fragment.NewMainFragment.HomeAdapter.ONE_VIEW;
import static com.byd.dynaudio_app.fragment.NewMainFragment.HomeAdapter.TWO_VIEW;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

public class ColumnBean implements MultiItemEntity {
    // 会根据这个的不同加载不同的布局
    private int type;

    @Override
    public int getItemType() {
        switch (this.type) {
            case ColumnBeanType.ITEM_TYPE_BANNER:
                return HEADER_VIEW;
            case ColumnBeanType.ITEM_TYPE_SELECTION_OF_GOLDEN_SONGS:
            case ColumnBeanType.ITEM_TYPE_AUDIO_STATION:
                return TWO_VIEW;
            default:
                return ONE_VIEW;
        }
    }

    public class ColumnBeanType {
        public static final int ITEM_TYPE_BLACK_PLASTIC = 0;  // 黑胶专区
        public static final int ITEM_TYPE_AUDIO_COLUMN = 1;  // 有声专栏
        public static final int ITEM_TYPE_3D_IMMERSION_SOUND = 2;  // 3d沉浸声
        public static final int ITEM_TYPE_SELECTION_OF_GOLDEN_SONGS = 4; // 甄选金曲
        public static final int ITEM_TYPE_AUDIO_STATION = 5; // 有声电台

        public static final int ITEM_TYPE_BANNER = 6; // banner
        public static final int ITEM_TYPE_FOOTER = 7; // footer

    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
