package com.byd.dynaudio_app.custom.smart_refresh_layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.ViewCompat;

import java.util.Arrays;

public class NestedChildView extends View implements NestedScrollingChild {

    public static final String TAG = "NestedChildView";

    private final NestedScrollingChildHelper childHelper = new NestedScrollingChildHelper(this);
    private float downY;

    private int[] consumed = new int[2];
    private int[] offsetInWindow = new int[2];


    public NestedChildView(Context context) {
        super(context);
        init();
    }


    public NestedChildView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        setNestedScrollingEnabled(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = MotionEventCompat.getActionMasked(event);

        // 取第一个接触屏幕的手指Id
        final int pointerId = MotionEventCompat.getPointerId(event, 0);
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:

                // 取得当前的Y，并赋值给lastY变量
                downY = getPointerY(event, pointerId);
                // 找不到手指，放弃掉这个触摸事件流
                if (downY == -1) {
                    return false;
                }

                // 通知父View，开始滑动
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                break;
            case MotionEvent.ACTION_MOVE:

                // 获得当前手指的Y
                final float pointerY = getPointerY(event, pointerId);

                // 找不到手指，放弃掉这个触摸事件流
                if (pointerY == -1) {
                    return false;
                }

                // 计算出滑动的偏移量
                float deltaY = pointerY - downY;

                Log.d(TAG, String.format("downY = %f", deltaY));

                Log.d(TAG, String.format("before dispatchNestedPreScroll, deltaY = %f", deltaY));

                // 通知父View, 子View想滑动 deltaY 个偏移量，父View要不要先滑一下，然后把父View滑了多少，告诉子View一下
                // 下面这个方法的前两个参数为在x，y方向上想要滑动的偏移量
                // 第三个参数为一个长度为2的整型数组，父View将消费掉的距离放置在这个数组里面
                // 第四个参数为一个长度为2的整型数组，父View在屏幕里面的偏移量放置在这个数组里面
                // 返回值为 true，代表父View有消费任何的滑动.
                if (dispatchNestedPreScroll(0, (int) deltaY, consumed, offsetInWindow)) {

                    // 偏移量需要减掉被父View消费掉的
                    deltaY -= consumed[1];
                    Log.d(TAG, String.format("after dispatchNestedPreScroll , deltaY = %f", deltaY));

                }

                // 上面的 (int)deltaY 会造成精度丢失，这里把精度给舍弃掉
                if (Math.floor(Math.abs(deltaY)) == 0) {
                    deltaY = 0;
                }

                // 这里移动子View，下面的min,max是为了控制边界，避免子View越界
                setY(Math.min(Math.max(getY() + deltaY, 0), ((View) getParent()).getHeight() - getHeight()));


                break;
        }
        return true;
    }

    /**
     * 这个方法通过pointerId获取pointerIndex,然后获取Y
     */
    private float getPointerY(MotionEvent event, int pointerId) {
        final int pointerIndex = MotionEventCompat.findPointerIndex(event, pointerId);
        if (pointerIndex < 0) {
            return -1;
        }
        return MotionEventCompat.getY(event, pointerIndex);
    }

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        Log.d(TAG, String.format("setNestedScrollingEnabled , enabled = %b", enabled));
        childHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        Log.d(TAG, "isNestedScrollingEnabled");
        return childHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        Log.d(TAG, String.format("startNestedScroll , axes = %d", axes));
        return childHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        Log.d(TAG, "stopNestedScroll");
        childHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        Log.d(TAG, "hasNestedScrollingParent");
        return childHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        final boolean b = childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
        Log.d(TAG, String.format("dispatchNestedScroll , dxConsumed = %d, dyConsumed = %d, dxUnconsumed = %d, dyUnconsumed = %d, offsetInWindow = %s", dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, Arrays.toString(offsetInWindow)));
        return b;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        final boolean b = childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        Log.d(TAG, String.format("dispatchNestedPreScroll , dx = %d, dy = %d, consumed = %s, offsetInWindow = %s", dx, dy, Arrays.toString(consumed), Arrays.toString(offsetInWindow)));
        return b;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        Log.d(TAG, String.format("dispatchNestedFling , velocityX = %f, velocityY = %f, consumed = %b", velocityX, velocityY, consumed));
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        Log.d(TAG, String.format("dispatchNestedPreFling , velocityX = %f, velocityY = %f", velocityX, velocityY));
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}