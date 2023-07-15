package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.custom.smart_refresh_layout.HFooter;
import com.byd.dynaudio_app.databinding.LayoutFragmentTopShrinkBinding;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("RestrictedApi")

public abstract class TopShrinkFragment<T extends ViewDataBinding, V extends ViewDataBinding> extends BaseFragment<LayoutFragmentTopShrinkBinding, BaseViewModel> {

    private static final float DEFAULT_DY = 40;
    protected T mTopBarBinding;  // 顶部前台
    protected V mTopBgBinding;  // 顶部背景
    protected float alpha;
    protected int dy;
    private boolean isHeaderDragging;
    private ProgressDrawable progressDrawable;
    private boolean startShrink;

    private int distance;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LogUtils.d("222222 :   " + getDistance() + " :" + factor
                    + " last distance : " + distance);
            if (startShrink) {
                getDistance();
                if (distance != getDistance()) {
                    distance = getDistance();
                } else {
                    distance -= 43;
                }
                LogUtils.d("222222 :   " + getDistance() + " :" + factor);
                factor = (getDistance() + mDataBinding.topBg.getHeight())
                        * 1.f / (mDataBinding.topBg.getHeight() * 1.f);
                if (factor >= 1) {
                    if (factor == 1) {
                        startShrink = false;
                        return;
                    }


                    setTopBgScale(factor);
                }
                mDataBinding.getRoot().postDelayed(runnable, 15);
            }
        }
    };

    private void setTopBgScale(Float factor) {
        LogUtils.d("set scale : " + factor);
        if (factor == null) return;
        if (factor > 1.1f) factor = 1.1f;
        if (mDataBinding.topBg != null) {
//            mDataBinding.topBg.setScaleX(factor);
//            mDataBinding.topBg.setScaleY(factor);
        }
    }

    private float factor;
    private boolean isFooterDragging;
    private boolean isHeaderRelease;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                onMoving(getDistance());
            }
        }, 10, 10);

        initSwipe();

        mDataBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtils.d("dyyyy : " + dy);

                isHeaderRelease = false;
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mDataBinding.swipe.getLayoutParams();
                if (!isFooterDragging) {
                    // layoutParams.bottomMargin = DensityUtils.dp2Px(mContext, 0);
                    mDataBinding.swipe.setLayoutParams(layoutParams);
                }

//                mDataBinding.vvv.setVisibility(View.GONE);
            }
        });


//        mDataBinding.recycler.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return mDataBinding.swipe.onTouchEvent(event);
//            }
//        });


        mDataBinding.topBg.addView(getTopBg());
        mDataBinding.topBar.addView(getTopBar());
        dy = DensityUtils.dp2Px(mContext, 0);

        mDataBinding.swipe.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            alpha = Math.max(0, scrollY * 1.f - dy) / (200 * 1.f);

            // 如果recycler 不能上拉了 就直接显示完全
            if (!mDataBinding.swipe.canScrollVertically(1)) {
                alpha = 1.f;
            }

            LogUtils.d("2222 alpha : " + alpha);
            if (isMainPage()) { // 如果是首页 就一直显示top bar相关的内容 只显隐背景
                mDataBinding.topBar.setAlpha(1);
                View imgBg = mDataBinding.topBar.findViewById(R.id.img_bg);
                if (imgBg != null) imgBg.setAlpha(alpha);
            } else {
                mDataBinding.topBar.setAlpha(alpha);
            }

            // factor = (scrollY + mDataBinding.topBg.getHeight()) * 1.f / (mDataBinding.topBg.getHeight() * 1.f);
            LogUtils.d("dy : " + scrollY);
        });


        // 播放按钮可见性
        if (mTopBgBinding != null) {
            View playView = mTopBgBinding.getRoot().findViewById(R.id.img_play);
            if (playView != null) playView.setVisibility(getPlayIconVisibility());
        }

        // 设置recycle统一的属性
        mDataBinding.recycler.setLayoutManager(new GridLayoutManager(mContext, 1, RecyclerView.VERTICAL, false));

        // 列表上移20dp
        if (moveTop()) {
            mDataBinding.recycler.setTranslationY(DensityUtils.dp2Px(mContext, -30));
            // mDataBinding.tvLoading.setTranslationY(DensityUtils.dp2Px(mContext, -20));
        }
        mDataBinding.swipe.setEnableLoadMore(true);

//        mDataBinding.topBg.setOnTouchListener((v, event) -> mDataBinding.recycler.onTouchEvent(event));
        mDataBinding.recycler.setOnTouchListener((view, motionEvent) -> {
            float rawY = motionEvent.getRawY();
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = rawY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtils.d("onTouch: raw y : " + rawY + "last y :" + lastY);
                    float dy = rawY - lastY;
                    lastY = rawY;

//                    if (dy < 0 && mDataBinding.recycler.canScrollVertically(1)) {
//                        return true;
//                    }


                    // return handleMove(mDataBinding.topBg, mDataBinding.recycler, dy);


                    // setChildHeight(child, (int) (recyclerView.getTop() + recyclerView.getTranslationY()));
                    // break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    mDataBinding.swipe.finishRefresh();
                    break;
            }
            return false;
        });

        mDataBinding.swipe.setEnableRefresh(true);
        mDataBinding.swipe.setEnableOverScrollBounce(false);
        mDataBinding.swipe.setHeaderTriggerRate(0.5f);
        // mDataBinding.swipe.setDragRate(0.1f);
        mDataBinding.swipe.setHeaderMaxDragRate(1.5f);

        // 如果是首页 top bg的 高度是300dp 非首页只有236dp
        ViewGroup.LayoutParams layoutParams = mDataBinding.topBg.getLayoutParams();
        if (layoutParams != null && !(this instanceof MainFragment)) {
            layoutParams.height = DensityUtils.dp2Px(mContext, SPUtils.isPad() ? 220 : 422);
            mDataBinding.topBg.setLayoutParams(layoutParams);

        }

    }

    private float lastY;
    private float topScaleHeight;
    private float factor2;

    private boolean handleMove(View child, RecyclerView recyclerView, float dy) {
        dy *= 0.1f;
        LogUtils.d("onTouch: dy : " + dy);

        topScaleHeight = recyclerView.getTop() + recyclerView.getTranslationY();
        factor2 = (topScaleHeight) / child.getHeight();
        LogUtils.d("onTouch: factor :" + factor2);

        if (factor2 >= 1.5f) {
            factor2 = 1.5f;

        }
        if (factor2 <= 1.f) factor2 = 1.f;

        child.setPivotY(0);
        child.setScaleX(factor2);
        child.setScaleY(factor2);

        if (dy > 0) {
            recyclerView.setTranslationY(recyclerView.getTranslationY() + dy);
        } else { // 上拉
            if (factor2 > 1.f) {
                recyclerView.setTranslationY(recyclerView.getTranslationY() + dy);
                return true;
            } else {
                // recycler 处理
            }


            // 是否可用下拉recycle
//            LogUtils.d("can scroll " + mDataBinding.recycler.canScrollVertically(-1));
//            if (!mDataBinding.recycler.canScrollVertically(-1)) {
//                recyclerView.setTranslationY(recyclerView.getTranslationY() + dy);
//                return true;
//            } else {
//                return true;
//            }
        }


        return false;
    }

    @Override
    protected void initListener() {
        mDataBinding.vvBack.setOnClickListener(v -> {
            LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
        });
    }

    private void initSwipe() {

        // 通过反射获取        mDataBinding.swipe 的 mSpinner 属性
        mDataBinding.swipe.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {


            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
                // startShrink = false;
                LogUtils.d(" is dragging : " + isDragging + " percent : " + percent + " offset : " + offset + " header height : " + headerHeight + " max drag height : " + maxDragHeight);
                if (!isDragging && !isHeaderDragging) {
                    isHeaderDragging = true;
                }
                TopShrinkFragment.this.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
                isHeaderRelease = true;

            }

            @Override
            public void onHeaderReleased(RefreshHeader header, int headerHeight, int maxDragHeight) {
                super.onHeaderReleased(header, headerHeight, maxDragHeight);
                LogUtils.d("factor release..." + mDataBinding.swipe.getScrollY());
                mDataBinding.swipe.finishRefresh();
                // mDataBinding.topBg.setScaleX(1);
                // mDataBinding.topBg.setScaleY(1);
                // todo 开始每 15ms 一次 scale factor减0.01 直到为1
                startShrink = true;
                // mDataBinding.getRoot().postDelayed(runnable, 15);

            }

            @Override
            public void onHeaderFinish(RefreshHeader header, boolean success) {
                super.onHeaderFinish(header, success);
                LogUtils.d("is dragging : finish");
                mDataBinding.getRoot().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        setTopBgScale(1.f);
                    }
                }, 1000);
            }

            @Override
            public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
                super.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight);
                LogUtils.d("2222 is drag : " + isDragging + "max : " + maxDragHeight
                        + " is header dragging : " + isHeaderDragging);
                TopShrinkFragment.this.onFooterMoving(footer, isDragging, percent, offset, footerHeight, maxDragHeight);
            }

            @Override
            public void onFooterReleased(RefreshFooter footer, int footerHeight, int maxDragHeight) {
                super.onFooterReleased(footer, footerHeight, maxDragHeight);
                LogUtils.d("222222222222");

            }

            @Override
            public void onFooterFinish(RefreshFooter footer, boolean success) {
                super.onFooterFinish(footer, success);
                LogUtils.d("222222222222");
//                mDataBinding.vvv.setVisibility(View.VISIBLE);

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mDataBinding.swipe.getLayoutParams();
//                if (layoutParams != null
//                    /*&& !(!isDragging && percent == 0 && offset == 0)*/) {
//                    layoutParams.bottomMargin = DensityUtils.dp2Px(mContext, 63);
//                    mDataBinding.swipe.setLayoutParams(layoutParams);
//                }
            }
        });

        mDataBinding.getRoot().postDelayed(() -> onHeaderMoving(null, false, 0.01f, 1, 1, 1), 100);

        mDataBinding.swipe.setEnableLoadMore(true);//是否启用上拉加载功能

        mDataBinding.swipe.setRefreshHeader(new RefreshHeader() {
            @NonNull
            @Override
            public View getView() {
                return new View(mContext);
            }

            @NonNull
            @Override
            public SpinnerStyle getSpinnerStyle() {
                return SpinnerStyle.Scale;
            }

            @Override
            public void setPrimaryColors(int... colors) {

            }

            @Override
            public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

            }

            @Override
            public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

            }

            @Override
            public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

            }

            @Override
            public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
                return 0;
            }

            @Override
            public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

            }

            @Override
            public boolean isSupportHorizontalDrag() {
                return false;
            }

            @Override
            public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {

            }
        });
        mDataBinding.swipe.setRefreshFooter(new HFooter(mContext));

        progressDrawable = new ProgressDrawable();
        mDataBinding.loading.setImageDrawable(progressDrawable);
        progressDrawable.start();

    }

    private int getDistance() {
        try {
            // 获取 SwipeRefreshLayout 的 Class 对象
            Class<?> swipeRefreshLayoutClass = mDataBinding.swipe.getClass();

            // 获取 mSpinner 字段的 Field 对象
            Field spinnerField = swipeRefreshLayoutClass.getDeclaredField("mSpinner");

            // 设置字段可访问性为 true，以便从对象中读取该字段的值
            spinnerField.setAccessible(true);

            // 读取 mDataBinding.swipe 的 mSpinner 字段的值
            int mSpinner = spinnerField.getInt(mDataBinding.swipe);

            return mSpinner;

            // 现在您可以使用 mSpinner 变量进行操作了
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
        if (isDragging) {
            // 等比缩放top bar
//            mDataBinding.topBar.setPivotY(mDataBinding.topBar.getHeight());
//            mDataBinding.topBar.setPivotX(mDataBinding.topBar.getWidth() / 2);
//            float scaleFactor = (mDataBinding.topBar.getWidth() + offset) * 1.f / (mDataBinding.topBar.getWidth() * 1.f);
//            LogUtils.d("scale factor : " + scaleFactor
//            + " width : " + mDataBinding.topBar.getWidth()
//            + " offset : " + offset);

            factor = (offset + mDataBinding.topBg.getHeight()) * 1.f / (mDataBinding.topBg.getHeight() * 1.f);
//            LogUtils.d("factor : " + factor
//                    + " offset222 : " + offset);
//            mDataBinding.topBg.setPivotX(mDataBinding.topBg.getWidth() / 2);
//            mDataBinding.topBg.setPivotY(mDataBinding.topBg.getHeight());
            LogUtils.d(" set scale : offset : " + offset);
            setTopBgScale(factor);

            mDataBinding.getRoot().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("current scale : " + mDataBinding.topBg.getScaleX());

                }
            }, 4000);


//            mDataBinding.topBar.setScaleX(scaleFactor);
//            mDataBinding.topBar.setScaleY(0.5f);
        } else {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mDataBinding.swipe.getLayoutParams();
            if (layoutParams != null
                /*&& !(!isDragging && percent == 0 && offset == 0)*/) {

                layoutParams.bottomMargin = DensityUtils.dp2Px(mContext, 0);
//                mDataBinding.getRoot().postDelayed(() -> mDataBinding.swipe.setLayoutParams(layoutParams), 100);
            }
        }

//        if (!isDragging && offset != 0 && needHeaderBack()) {
//            mDataBinding.swipe.finishRefresh();
//
//
//
//            mDataBinding.getRoot().postDelayed(() ->
//                    mDataBinding.swipe.scrollBy(0, dy - mDataBinding.swipe.getScrollY()), 100);
//        }
//        mDataBinding.swipe.finishRefresh();
//
        mDataBinding.loading.setVisibility(isDragging ? View.VISIBLE : View.INVISIBLE);
        mDataBinding.swipe.postDelayed(() -> {
            mDataBinding.loading.setVisibility(View.INVISIBLE);
            mDataBinding.swipe.finishRefresh();
        }, 1000);
    }

    protected boolean needHeaderBack() {
        return true;
    }


    protected void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
        if (!isDragging && percent > 0) {
            mDataBinding.swipe.finishLoadMore();
        }
    }

    protected boolean isMainPage() {
        return false;
    }

    /**
     * 是否将recycleview部分网上移动
     */
    protected boolean moveTop() {
        return true;
    }

    /**
     * 重写该方法 返回false可以屏蔽拉到底部显示加载
     *
     * @return
     */
    protected boolean showLoadingMode() {
        return true;
    }

    /**
     * 如果要隐藏播放按钮 重写返回Invisible
     */
    protected int getPlayIconVisibility() {
        return View.VISIBLE;
    }

    /**
     * 返回一个顶部收起时的view
     * 如果不重写该方法 使用默认的
     */
    protected View getTopBar() {
        mTopBarBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bar_title_mid, null, false);
        return mTopBarBinding.getRoot();
    }

    /**
     * 返回一个顶部展开时的view
     * 如果不重写该方法 使用默认的
     */
    protected View getTopBg() {
        mTopBgBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bg_with_play, null, false);
        return mTopBgBinding.getRoot();
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_top_shrink;
    }

    @Override
    public void onResume() {
        super.onResume();

        judgeIfMiniBar();
    }

    /**
     * 常驻显示mini bar
     * 如果没数据 就拿播放记录的数据
     * 如果播放记录也没数据 不显示
     */
    private void judgeIfMiniBar() {
        MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
        if (value != null) {
            // 这就说明现在就有数据 直接显示
            PlayerVisionManager.getInstance().showMiniPlayer();
        } else {
            // 在MusicPlayManager的init方法会自动更新本地数据到播放列表 所以上述过程会自动有数据
            // 所以到这里就直接不显示即可
            PlayerVisionManager.getInstance().hideMiniPlayer();
        }
    }
}
