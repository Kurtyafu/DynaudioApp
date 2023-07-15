package com.byd.dynaudio_app.custom.smart_refresh_layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;

public class VTopView extends LinearLayout implements NestedScrollingParent {
    private NestedScrollingParentHelper mNestedScrollingParentHelper;

    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;
    private FrameLayout mHeaderView;
    private FrameLayout mFooterView;
    private RecyclerView mRecyclerView;

    public VTopView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LogUtils.d();
        // 貌似是初始化的
        // 获取子view

//        mHeaderView = findViewById(R.id.refresh_view);
        mHeaderView.setOnTouchListener((v, event) -> mRecyclerView.onTouchEvent(event));
        mRecyclerView = findViewById(R.id.recycler);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        setNestedScrollingEnabled(false);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {
        if (isNestedScrollingEnabled()) {
            return dispatchNestedPreFling(velocityX, velocityY);
        }
        return false;
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        if (isNestedScrollingEnabled()) {
            return dispatchNestedFling(velocityX, velocityY, consumed);
        }
        return false;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean result = isEnabled() /*&& !mRefreshing*/
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;

        return result;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        if (isNestedScrollingEnabled()) {
            startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
            mNestedScrollInProgress = true;
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);

        final int[] parentConsumed = mParentScrollConsumed;
        if (isNestedScrollingEnabled()) {
            if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
                consumed[0] += parentConsumed[0];
                consumed[1] += parentConsumed[1];
                return;
            }
        }
        int spinnerDy = (int) calculateDistanceY(target, dy);
        LogUtils.d("spinner Dy:" + spinnerDy);
       // mRecyclerView.setTranslationY(mRecyclerView.getTranslationY() - dy);

        if (!mRecyclerView.canScrollVertically(-1)) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mHeaderView.getLayoutParams();
            lp.height += (-spinnerDy);
            if (lp.height < 0) {
                lp.height = 0;
            }
            mHeaderView.setLayoutParams(lp);
            for (int i =0;i<mHeaderView.getChildCount();i++){
                mHeaderView.getChildAt(i).setScaleY(lp.height*1.f/ DensityUtils.dp2Px(getContext(),300));
                mHeaderView.getChildAt(i).setScaleX(lp.height*1.f/ DensityUtils.dp2Px(getContext(),300));
            }

            mRecyclerView.setTranslationY(lp.height);

        }
    }

    private double calculateDistanceY(View target, int dy) {
        int viewHeight = target.getMeasuredHeight();
        double ratio = (viewHeight - Math.abs(target.getY())) / 1.0d / viewHeight * 0.4f;
        if (ratio <= 0.01d) {
            //Filter tiny scrolling action
            ratio = 0.01d;
        }
        return ratio * dy;
    }
}
