package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.appbar.CollapsingToolbarLayout;

public class CallbackCollapsingToolbarLayout extends CollapsingToolbarLayout {
    public CallbackCollapsingToolbarLayout(@NonNull Context context) {
        super(context);
    }

    public CallbackCollapsingToolbarLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CallbackCollapsingToolbarLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setScrimsShown(boolean shown, boolean animate) {
        super.setScrimsShown(shown, animate);

        if (scrimListener != null) scrimListener.onScrimsChanged(shown);
    }

    public void setScrimListener(OnScrimListener scrimListener) {
        this.scrimListener = scrimListener;
    }

    private OnScrimListener scrimListener;

    public interface OnScrimListener {
        /**
         * 上方缩起来时 为true
         * 上方展开时 为false
         */
        void onScrimsChanged(boolean shown);
    }
}
