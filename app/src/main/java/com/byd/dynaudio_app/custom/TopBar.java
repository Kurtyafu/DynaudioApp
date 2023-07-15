package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarBinding;

/**
 * 顶部收起后依然显示的部分
 */
public class TopBar extends BaseView<LayoutViewTopBarBinding> {
    public TopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopBar(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init(AttributeSet attrs) {
        setAlpha(0);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_top_bar;
    }
}
