package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class MainScroller extends NestedScrollView {
    private int maxScrollHeight;

    public MainScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        maxScrollHeight = this.getChildAt(0).getMeasuredHeight() - this.getMeasuredHeight();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(target, dx, dy, consumed, type);
        //如果最大可滚动距离大于 0 则表示 scrollview 可以滚动，则去先消费用户滑动
        if (this.maxScrollHeight > 0) {
            //判断用户是否是向上滑动，并且没有超出 scrollview 可滑动的最大距离
            boolean headerScrollUp = dy > 0 && getScrollY() < this.maxScrollHeight;

            //判断用户是否是向下滑动，并且没有超出可滑动距离
            boolean headerScrollDown = dy < 0 && getScrollY() > 0;

            //如果 scrollview 可以滑动，则去消费滑动
            if (headerScrollUp || headerScrollDown) {
                scrollBy(0, dy);
                consumed[1] = dy;
            }
            //通知子列表，父级消费的距离
            this.dispatchNestedPreScroll(dx, dy, consumed, null, type);
        }
    }
}
