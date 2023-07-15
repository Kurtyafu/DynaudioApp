package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.EdgeEffect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.utils.DensityUtils;

/**
 * 去除反向拉伸阴影 && 添加边缘模糊
 */
public class VerticalSingleRecyclerView extends RecyclerView {

    public VerticalSingleRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setEdgeEffectFactory(new VerticalSingleRecyclerView.MyEdgeEffectFactory());
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(DensityUtils.dp2Px(context, 100));
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    public static class MyEdgeEffectFactory extends RecyclerView.EdgeEffectFactory {
        @NonNull
        @Override
        protected EdgeEffect createEdgeEffect(@NonNull RecyclerView recyclerView, int direction) {
            return new EdgeEffect(recyclerView.getContext()) {
                @Override
                public boolean draw(Canvas canvas) {
                    return true;
                }
            };
        }
    }

}
