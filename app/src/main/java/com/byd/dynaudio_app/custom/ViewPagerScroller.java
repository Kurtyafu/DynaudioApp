package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;
 
public class ViewPagerScroller extends Scroller {
 
    private int mScrollDuration = 1500; // 默认的翻页动画持续时间为500毫秒
 
    public ViewPagerScroller(Context context) {
        super(context);
    }
 
    public ViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }
 
    public ViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }
 
    public void setScrollDuration(int duration) {
        mScrollDuration = duration;
    }
 
    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
}
