package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearSmoothScroller;

public class CustomSmoothScroller extends LinearSmoothScroller {
    private static final float MILLISECONDS_PER_INCH = 500f; // 每英寸的滚动时间

    public CustomSmoothScroller(Context context) {
        super(context);
    }

    @Override
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        // 返回滚动每个像素所需要的时间（以毫秒为单位）
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }
}
