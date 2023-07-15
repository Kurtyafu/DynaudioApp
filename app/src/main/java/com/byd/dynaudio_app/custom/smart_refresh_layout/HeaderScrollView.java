package com.byd.dynaudio_app.custom.smart_refresh_layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewCompat.NestedScrollType;
import androidx.core.view.ViewCompat.ScrollAxis;
import androidx.core.view.ViewCompat.ScrollIndicators;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import static java.lang.Math.min;

/**
 * NestedScrollView + RecyclerView 会导致RecyclerView无法回收循环复用，
 * 目前网上没有一个非常完美的解决方案，Google也不推荐NestedScrollView嵌套RecyclerView，
 * 如果用RecyclerView+Header的方式能解决问题但是用法不方便。
 * 此类用法虽有局限性但用法方便。
 * 视情况酌情使用
 */
public class HeaderScrollView extends NestedScrollView {
    private static final String TAG = HeaderScrollView.class.getSimpleName();

    private boolean needInvalidate = false;
    private boolean isStickyLayout = false;

    private ViewGroup headView;
    private ViewGroup contentView;

    public HeaderScrollView(Context context) {
        super(context);
        init();
    }

    public HeaderScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOverScrollMode(OVER_SCROLL_NEVER);
        setMotionEventSplittingEnabled(false);
    }

    /**
     * 获取头部区域的高度
     */
    private int getHeadViewHeight() {
        return headView != null && headView.getVisibility() == View.VISIBLE ? headView.getMeasuredHeight() : 0;
    }

    /**
     * 嵌套滚动布局的高度
     */
    private int getContentViewHeight() {
        int contentHeight = getMeasuredHeight();
        if (contentView != null && contentView.getVisibility() == View.VISIBLE) {
            contentHeight += getHeadViewHeight() - getHeadViewMinHeight();
        }
        return contentHeight;
    }

    private int getHeadViewMinHeight() {
        return headView != null && headView.getVisibility() == View.VISIBLE ? headView.getMinimumHeight() : 0;
    }

    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        int scrollMax = getHeadViewHeight() - headView.getMinimumHeight();
        super.onOverScrolled(scrollX, Math.min(scrollMax, scrollY), clampedX, clampedY);
    }

    @Override
    public void scrollTo(int x, int y) {
        int scrollMax = getHeadViewHeight() - headView.getMinimumHeight();
        super.scrollTo(x, Math.min(scrollMax, y));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ViewGroup childView = (ViewGroup) getChildAt(0);
        if (childView.getChildCount() == 2) {
            headView = (ViewGroup) childView.getChildAt(0);
            contentView = (ViewGroup) childView.getChildAt(1);
            isStickyLayout = headView instanceof View.OnScrollChangeListener;
            if (isStickyLayout) {
                headView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        ((View.OnScrollChangeListener) headView)
                                .onScrollChange(HeaderScrollView.this, getScrollX(), getScrollY(), getScrollX(), getScrollY());
                    }
                });
            }
        } else {
            throw new IllegalStateException(TAG + " is designed for nested scrolling and can only have two direct child");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        headView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        measureChildren();
    }

    private void measureChildren() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(getContentViewHeight(), MeasureSpec.EXACTLY));
        }
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed, int type) {
        boolean isParentScroll = dispatchNestedPreScroll(dx, dy, consumed, null, type);
        if (!isParentScroll) {
            boolean needKeepScroll = dy > 0 && getScrollY() < (getHeadViewHeight() - headView.getMinimumHeight());
            if (needKeepScroll) {
                needInvalidate = true;
                scrollBy(0, dy);
                consumed[1] = dy;
            }
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
        needInvalidate = true;
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
    }

    @Override
    public boolean awakenScrollBars() {
        if (needInvalidate) {
            invalidate();
            return true;
        } else {
            return super.awakenScrollBars();
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (isStickyLayout) {
            ((View.OnScrollChangeListener) headView).onScrollChange(this, l, t, oldl, oldt);
        }
    }

    @Override
    public void fling(int velocityY) {

        RecyclerView recyclerView = findRecyclerView(contentView);
        if (recyclerView != null) {
            if (recyclerView.canScrollVertically(1)) {
                recyclerView.fling(0, velocityY);
                return;
            }
        }
        super.fling(velocityY);
    }

    private RecyclerView findRecyclerView(ViewGroup contentView) {
        if (contentView instanceof RecyclerView && contentView.getClass() == RecyclerView.class) {
            return (RecyclerView) contentView;
        }
        for (int i = 0; i < contentView.getChildCount(); i++) {
            View view = contentView.getChildAt(i);
            if (view instanceof ViewGroup) {
                RecyclerView target = findRecyclerView((ViewGroup) view);
                if (target != null) {
                    return target;
                }
            }
        }
        return null;
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            RecyclerView recyclerView = findRecyclerView(contentView);
            if (recyclerView != null) {
                if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                    recyclerView.stopScroll();
                }
                onStopNestedScroll(recyclerView, ViewCompat.TYPE_NON_TOUCH);
            }
        }
        return super.startNestedScroll(axes, type);
    }

}
