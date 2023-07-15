package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class DViewPager extends ViewPager {
    public DViewPager(@NonNull Context context) {
        super(context);
    }

    public DViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean intercepted = super.onInterceptTouchEvent(ev);
//        if (ev.getAction() == MotionEvent.ACTION_DOWN && !intercepted) {
//            float x = ev.getX();
//            if (x < getWidth() / 4f || x > getWidth() * 3f / 4f) {
//                // Disable swipe gesture at the edges
//                return false;
//            }
//        }
//        return intercepted;
//    }
}
