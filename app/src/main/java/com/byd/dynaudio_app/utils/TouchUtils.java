package com.byd.dynaudio_app.utils;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.bean.MusicListBean;

import java.util.List;

public class TouchUtils {
    public static final int HIDE_VALUE = 100;
    public static final int SHOW_VALUE = 100;

    @SuppressLint("ClickableViewAccessibility")
    public static void bind(@NonNull RecyclerView recycler) {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastDy = 0;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    LogUtils.d("last Dy : " + lastDy);

                    if (lastDy > SHOW_VALUE) {
                        showMiniPlayer();
                        lastDy = 0;
                    }

                    if (lastDy < -HIDE_VALUE) {
                        hideMiniPlayer();
                        lastDy = 0;
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastDy += dy;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void bind(@NonNull NestedScrollView nestedScrollView) {
        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            private int lastDy = 0;

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int dy = scrollY - oldScrollY;
                lastDy += dy;

                if (lastDy > SHOW_VALUE) {
                    showMiniPlayer();
                    lastDy = 0;
                } else if (lastDy < -HIDE_VALUE) {
                    hideMiniPlayer();
                    lastDy = 0;
                }
            }
        });
    }

    /**
     * 两次点击按钮之间的点击间隔不能少于500毫秒，防止非人类操作
     */
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    /**
     * 按压给50透明度
     * 所有可以点击的item均应该绑定
     */
    @SuppressLint("ClickableViewAccessibility")
    public static void bindClickItem(@NonNull ImageView view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private float alpha;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alpha = view.getAlpha();
                        view.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setAlpha(alpha);
                        alpha = 0.f;
                        // 防止非人类操作
                        long currentClickTime = System.currentTimeMillis();
                        if ((currentClickTime - lastClickTime) < MIN_CLICK_DELAY_TIME) {
                            lastClickTime = currentClickTime;
                            return true;
                        }
                        lastClickTime = currentClickTime;
                        break;
                }

                return false;
            }
        });
    }

    /**
     * 重载
     */
    public static void bindClickItem(@NonNull View... views) {
        for (View view : views) {
            view.setOnTouchListener(new View.OnTouchListener() {
                private float alpha;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            alpha = view.getAlpha();
                            view.setAlpha(0.5f);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            view.setAlpha(alpha);
                            alpha = 0.f;
                            // 防止非人类操作
                            long currentClickTime = System.currentTimeMillis();
                            if ((currentClickTime - lastClickTime) < MIN_CLICK_DELAY_TIME) {
                                lastClickTime = currentClickTime;
                                return true;
                            }
                            lastClickTime = currentClickTime;
                            break;
                    }
                    return false;
                }
            });
        }
    }

    private static void showMiniPlayer() {
        // PlayerVisionManager.getInstance().showMiniPlayer();
        LogUtils.d();
    }

    private static void hideMiniPlayer() {
        LogUtils.d();
        // PlayerVisionManager.getInstance().hideMiniPlayer();
    }

    /**
     * 获取两点间距
     */
    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * 渐隐
     */
    public static void hide(@NonNull View view, long duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0.8f, 0.f);

        alpha.setDuration(duration);
        alpha.start();

        view.postDelayed(() -> view.setVisibility(View.INVISIBLE), duration);
    }

    /**
     * 渐显
     */
    public static void show(@NonNull View view, long duration) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0.f, 0.8f, 1.f);
        alpha.setDuration(duration);
        alpha.start();

        view.postDelayed(() -> view.setVisibility(View.VISIBLE), duration);
    }

    public static void hide(@NonNull View view) {
        hide(view, 330);
    }

    public static void show(@NonNull View view) {
        show(view, 330);
    }

    /**
     * 判断两个列表数据是否不一致
     */
    public static boolean isDiffData(List<MusicListBean> currentData, @NonNull List<MusicListBean> newData) {
        if (currentData == null) {
            // 当前数据为空，视为数据不一致
            return true;
        }

        // 检查列表大小是否相同
        if (currentData.size() != newData.size()) {
            return true;
        }

        // 逐个比较列表中的元素
        for (int i = 0; i < currentData.size(); i++) {
            MusicListBean currentMusic = currentData.get(i);
            MusicListBean newMusic = newData.get(i);

            // 检查每个元素的属性是否一致
            if (!currentMusic.equals(newMusic)) {
                return true;
            }
        }

        // 列表数据完全一致
        return false;
    }

    public static void setMargin(@NonNull View view, int l, int t, int r, int b) {
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            if (l != -1) layoutParams.leftMargin = 0;
            if (t != -1) layoutParams.topMargin = 0;
            if (r != -1) layoutParams.rightMargin = 0;
            if (b != -1) layoutParams.bottomMargin = 0;

            view.setLayoutParams(layoutParams);
        }
    }
}
