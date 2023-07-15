package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.databinding.LayoutViewMiniPlayerBinding;

/**
 * 迷你播放器的自定义view
 */
public class MiniPlayerView extends BaseView<LayoutViewMiniPlayerBinding> {
    public MiniPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {

    }

    public LayoutViewMiniPlayerBinding getBinding() {
        return mDataBinding;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_mini_player;
    }
}
