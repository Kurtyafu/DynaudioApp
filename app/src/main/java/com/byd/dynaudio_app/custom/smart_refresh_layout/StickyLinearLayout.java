package com.byd.dynaudio_app.custom.smart_refresh_layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;

import com.byd.dynaudio_app.R;

public class StickyLinearLayout extends LinearLayout implements View.OnScrollChangeListener {

    private boolean mIsChildrenDrawingOrderEnabled;

    public StickyLinearLayout(Context context) {
        this(context, null);
    }

    public StickyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StickyLinearLayout_Layout, defStyleAttr, 0);
        mIsChildrenDrawingOrderEnabled = a.getBoolean(R.styleable.StickyLinearLayout_Layout_isSticky, false);
        a.recycle();
        setChildrenDrawingOrderEnabled(mIsChildrenDrawingOrderEnabled);
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        boolean isFixed = false;
        boolean isNext = true;
        int previousOffset = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);
            if (offsetHelper != null) {
                int offset = scrollY - offsetHelper.getLayoutTop();
                if (isFixed) {
                    if (isNext) {
                        isNext = false;
                        int currentOffset = offset - previousOffset;
                        offsetHelper.setTopAndBottomOffset(currentOffset);
                    } else {
                    }
                } else {
                    offsetHelper.setTopAndBottomOffset(MathUtils.clamp(offset, 0, Integer.MAX_VALUE));
                    View nextView = nextStickyView(i);
                    if (nextView != null) {
                        previousOffset = MathUtils.clamp(offset + nextView.getMeasuredHeight(), 0, Integer.MAX_VALUE);
                        isFixed = previousOffset > 0;
                    }
                }
            }
        }
    }

    @Nullable
    private View nextStickyView(int index) {
        for (int i = index - 1; i >= 0; i--) {
            View child = getChildAt(i);
            ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);
            if (offsetHelper != null) {
                return child;
            }
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int minHeight = 0;
        for (int i = getChildCount() - 1; i >= 0; i--) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.isSticky) {
                minHeight = child.getMeasuredHeight();
                break;
            }
        }
        setMinimumHeight(minHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ViewOffsetHelper offsetHelper = getViewOffsetHelper(child);
            if (offsetHelper != null) {
                offsetHelper.onViewLayout();
                offsetHelper.applyOffsets();
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return childCount - i - 1;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        public boolean isSticky;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.StickyLinearLayout_Layout);
            isSticky = a.getBoolean(R.styleable.StickyLinearLayout_Layout_isSticky, false);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, float weight) {
            super(width, height, weight);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LinearLayout.LayoutParams source) {
            super(source);
        }
    }

    private ViewOffsetHelper getViewOffsetHelper(View child) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.isSticky) {
            ViewOffsetHelper offsetHelper = (ViewOffsetHelper) child.getTag(com.google.android.material.R.id.view_offset_helper);
            if (offsetHelper == null) {
                offsetHelper = new ViewOffsetHelper(child);
                child.setTag(com.google.android.material.R.id.view_offset_helper, offsetHelper);
            }
            return offsetHelper;
        } else {
            return null;
        }
    }

}