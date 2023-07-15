package com.byd.dynaudio_app.fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_AUDIO_COLUMN;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_BLACK_PLASTIC;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.base.BaseViewPagerAdapter;
import com.byd.dynaudio_app.bean.ColumnBean;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BannerBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.ModuleBean;
import com.byd.dynaudio_app.bean.response.ModuleInfoBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.custom.HifiCard;
import com.byd.dynaudio_app.custom.NewsCard;
import com.byd.dynaudio_app.custom.xpop.MiniPlayerPopupView;
import com.byd.dynaudio_app.databinding.LayoutCardItem3dImmersionBinding;
import com.byd.dynaudio_app.databinding.LayoutCardItemAudioColumnBinding;
import com.byd.dynaudio_app.databinding.LayoutCardItemBlackPlasticBinding;
import com.byd.dynaudio_app.databinding.LayoutFragmentNewMainBinding;
import com.byd.dynaudio_app.databinding.LayoutTopBarItemBinding;
import com.byd.dynaudio_app.databinding.LayoutViewColumnCardBinding;
import com.byd.dynaudio_app.databinding.LayoutViewSoundAndGoldenBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.network.NetworkType;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.JumpUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TestUtils;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewMainFragment extends BaseFragment<LayoutFragmentNewMainBinding, BaseViewModel> {
    private BaseViewPagerAdapter<BannerBean, LayoutTopBarItemBinding> topBgAdapter;
    private LayoutViewTopBgBinding mTopBgBinding;
    private LayoutViewTopBarBinding mTopBarBinding;
    private HomeAdapter homeAdapter;
    private int defaultHeaderHeight;
    private int defaultHeaderWidth;
    private float alpha;
    private int mScrolledDistance;
    private int mCurrentPosition;
    private HifiCard hifiCard;
    private NewsCard stationBinding;

    private ArrayList<AlbumBean> blackNullBeanList = new ArrayList<>();
    private ArrayList<AlbumBean> audioNullBeanList = new ArrayList<>();
    private ArrayList<AlbumBean> immersionNullBeanList = new ArrayList<>();

    private ArrayList<AlbumBean> auStationNullBeanList = new ArrayList<>();

    private ArrayList<AlbumBean> hifiCardNullBeanList = new ArrayList<>();

    private long lastonclickTime = 0;//记录上次下滑的时间
    private long thresholdValue = 60000;//6万毫秒 5分钟阈值


    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.llSound, mDataBinding.llUser);
        if (!NetworkObserver.getInstance().isConnectNormal(mContext)) {//没网
            mDataBinding.checknet.setVisibility(VISIBLE);
        } else {
            mDataBinding.checknet.setVisibility(GONE);
        }
        syncacher();
        initRecycler();
        initTopBar();
        initSrl();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 如果返回主页 判断是否有数据 有就显示mini 播放器
        if (mContext instanceof MainActivity) {
            MainActivity activity = (MainActivity) mContext;
            MiniPlayerPopupView miniPlayer = activity.getMiniPlayer();
            miniPlayer.setVisibility(VISIBLE);
            if (miniPlayer.hasData()) {
                PlayerVisionManager.getInstance().showMiniPlayer();
            }
        }
    }

    /**
     * 下拉松手后钓鱼
     */
    private void topLoading() {
        if (!NetworkObserver.getInstance().isConnectNormal(mContext)) {
            CustomToast.makeText(mContext, mContext.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
            mDataBinding.srl.finishRefresh();
            return;
        }
        long time = SystemClock.uptimeMillis();//毫秒代码
        if ((!(time - lastonclickTime <= thresholdValue)) && !MusicPlayManager.getInstance().isPlaying()) {
            lastonclickTime = time;
            syncacher();
            initRecycler();
        }
    }

    private void syncacher() {
        hifiCardNullBeanList.clear();
        blackNullBeanList.clear();
        audioNullBeanList.clear();
        auStationNullBeanList.clear();
        immersionNullBeanList.clear();
        AlbumBean albumBean = new AlbumBean();//占位数据实例
        //从数据库获取缓存
        blackNullBeanList = (ArrayList<AlbumBean>) SPUtils.getDataList(mContext, getResources().getString(R.string.BLACK_PLASTIC), AlbumBean.class);
        audioNullBeanList = (ArrayList<AlbumBean>) SPUtils.getDataList(mContext, getResources().getString(R.string.AUDIO_COLUMN), AlbumBean.class);
        immersionNullBeanList = (ArrayList<AlbumBean>) SPUtils.getDataList(mContext, getResources().getString(R.string.IMMERSION_SOUND), AlbumBean.class);
        auStationNullBeanList = (ArrayList<AlbumBean>) SPUtils.getDataList(mContext, getResources().getString(R.string.AUDIOSTATION), AlbumBean.class);
        hifiCardNullBeanList = (ArrayList<AlbumBean>) SPUtils.getDataList(mContext, getResources().getString(R.string.HIFIBEAN), AlbumBean.class);

        if (auStationNullBeanList.isEmpty()) {
            auStationNullBeanList.add(albumBean);
        }
        if (blackNullBeanList.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                blackNullBeanList.add(albumBean);
            }
        }
        if (audioNullBeanList.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                audioNullBeanList.add(albumBean);
            }
        }
        if (immersionNullBeanList.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                immersionNullBeanList.add(albumBean);
            }
        }
    }

    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility", "Range"})

    private void initSrl() {
        mDataBinding.srl.setEnableRefresh(true);//是否启用下拉刷新功能
        mDataBinding.srl.setEnableLoadMore(true);//是否启用上拉加载功能
        mDataBinding.srl.setHeaderHeight(150);//Header标准高度（显示下拉高度>=标准高度 触发刷新）
        mDataBinding.srl.setHeaderMaxDragRate(2.0f);
        mDataBinding.srl.setFooterHeight(60);//Footer标准高度（显示上拉高度>=标准高度 触发加载）
        mDataBinding.srl.setFooterMaxDragRate(1.f);
        mDataBinding.srl.setEnableOverScrollBounce(true);//是否启用越界回弹
        mDataBinding.srl.setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）1.0.4
        mDataBinding.srl.setRefreshHeader(new RefreshHeader() {
            @NonNull
            @Override
            public View getView() {
                return new View(mContext);
            }

            @NonNull
            @Override
            public SpinnerStyle getSpinnerStyle() {
                return SpinnerStyle.Translate;
            }

            @Override
            public void setPrimaryColors(int... colors) {

            }

            @Override
            public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
            }

            @Override
            public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
                mDataBinding.progressView.startLoading();
            }

            @Override
            public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
            }

            @Override
            public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
            }

            @Override
            public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
                mDataBinding.progressView.stopLoading();
                return 200;
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
                switch (newState) {
                    case None:
                    case PullDownToRefresh:
                        mDataBinding.progressView.setVisibility(GONE);//隐藏动画
                        break;
                    case Refreshing:
                        mDataBinding.progressView.setVisibility(VISIBLE);//显示加载动画
                        break;
                    case ReleaseToRefresh:
                        mDataBinding.progressView.setVisibility(VISIBLE);//显示加载动画
                        break;
                    case PullDownCanceled:
                        topLoading();
                        break;
                }
            }
        });
        mDataBinding.srl.setRefreshFooter(new RefreshFooter() {
            @Override
            public boolean setNoMoreData(boolean noMoreData) {
                return false;
            }

            @NonNull
            @Override
            public View getView() {
                return new View(mContext);
            }

            @NonNull
            @Override
            public SpinnerStyle getSpinnerStyle() {
                return SpinnerStyle.Translate;
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
                return 200;
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
    }

    private void initTopBar() {
//        mDataBinding.frameTopBar.setAlpha(alpha);
        // top bar区域及显隐
        mTopBarBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bar, null, false);
        mDataBinding.frameTopBar.addView(mTopBarBinding.getRoot());

        mDataBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;

                setTopBarAlpha(totalDy);
            }
        });

        if (UserController.getInstance().isLoginStates()) {
            requestUserInfo();
        }
    }

    private float totalDy = 0;

    private void setTopBarAlpha(float totalDy) {
        // 带有播放按钮的详情页 0-55dp 透明度0 ; 60dp 满透明度
        int alpha0Pos = (DensityUtils.dp2Px(mContext, 25));
        int alpha1Pos = DensityUtils.dp2Px(mContext, 30);
        float alpha;
        if (totalDy < alpha0Pos) {
            alpha = 0.f;
        } else if (totalDy > alpha1Pos) {
            alpha = 1.f;
        } else {
            alpha = (totalDy - alpha0Pos) / (alpha1Pos - alpha0Pos);
        }

        if (alpha < 0) alpha = 0;
        if (alpha > 1.f) alpha = 1.f;
        // LogUtils.d("dy : " + dy + " alpha : " + alpha);
        mDataBinding.frameTopBar.setAlpha(alpha);
    }

    @Override
    protected void initListener() {
        mDataBinding.llSound.setOnClickListener(v -> toFragment(new SoundSettingsFragment()));
        mDataBinding.llUser.setOnClickListener(v -> toFragment(new MyFragment()));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (mContext instanceof MainActivity) {
                MainActivity activity = (MainActivity) mContext;
                MiniPlayerPopupView miniPlayer = activity.getMiniPlayer();
                if (miniPlayer.hasData()) {
                    miniPlayer.setVisibility(VISIBLE);
                    PlayerVisionManager.getInstance().showMiniPlayer();
                }
            }
        }
        if (UserController.getInstance().isLoginStates()) {
            requestUserInfo();
        } else {
            mDataBinding.imgUser.setImageResource(R.drawable.ic_user_def);
        }
    }

    @Override
    protected void initObserver() {
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler() {
        // mDataBinding.recycler.setBackgroundColor(Color.parseColor("#FF000000"));
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        homeAdapter = new HomeAdapter(Objects.requireNonNull(TestUtils.getMainTestList()));
        View view = new View(mContext);
        mDataBinding.getRoot().postDelayed(() -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                // bugId:197   最下面的模块最终挺溜的位置在迷你播放器上面，间距10dp(原来为73 + 10 - 20)
                layoutParams.height = DensityUtils.dp2Px(mContext, 73 + 10 - 30/*mini bar的高度+间距10 所有的上移了20*/);
                view.setLayoutParams(layoutParams);
            }
        }, 100);
        homeAdapter.addFooterView(view);
        mDataBinding.recycler.setAdapter(homeAdapter);
        // 列表上移20dp
//        mDataBinding.recycler.setTranslationY(DensityUtils.dp2Px(mContext, -30));
//        mDataBinding.tvLoading.setVisibility(View.GONE);

        mDataBinding.getRoot().postDelayed(() -> {
            defaultHeaderHeight = mTopBgBinding.getRoot().getHeight();
            defaultHeaderWidth = mTopBgBinding.getRoot().getWidth();
        }, 300);
        // 顶部缩放
        mDataBinding.recycler.setOnTouchListener(new View.OnTouchListener() {

            private float rawY = -1;
            float maxFactor = 1.f; // 最大缩放系数
            float fate = 1.f;  // 灵敏系数

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 判断是否处于下拉 并且recycler已经不能下拉 并且获取此时的下拉的偏移量
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (rawY == -1) {
                            rawY = event.getRawY();
                            return false;
                        }

                        float dy = event.getRawY() - rawY; // 大于0下拉 小于0上拉 表示移动过程成相对于初始位置的位移
                        rawY = event.getRawY();
//                        LogUtils.d("dy   :" + dy);

                        dy *= fate;

                        // 目标的纵向缩放
                        float toFactorY = (mTopBgBinding.getRoot().getHeight() + dy) / defaultHeaderHeight;
                        float currentFactorY = mTopBgBinding.getRoot().getHeight() * 1.f / defaultHeaderHeight;

                        if (toFactorY >= maxFactor) toFactorY = maxFactor;
                        if (toFactorY <= 1.f) toFactorY = 1.f;

                        if ((dy < 0 && currentFactorY > 1.f) || (dy > 0 && currentFactorY < maxFactor) && !mDataBinding.recycler.canScrollVertically(-1)) {  // 上拉且没到最小缩放

                            // 屏蔽srl的头部
                            mDataBinding.srl.finishRefresh(false);

                            ViewGroup.LayoutParams layoutParams = mTopBgBinding.getRoot().getLayoutParams();
                            if (layoutParams != null) {
                                layoutParams.height = (int) (toFactorY * defaultHeaderHeight);
                                layoutParams.width = (int) (toFactorY * defaultHeaderWidth);
                                mTopBgBinding.getRoot().setLayoutParams(layoutParams);
                                mTopBgBinding.getRoot().setScaleY(toFactorY);
                                mTopBgBinding.getRoot().setScaleX(toFactorY);
                            }
                            return true;
                        } else {
                            mDataBinding.srl.setEnableRefresh(true);
                            mDataBinding.srl.setEnableLoadMore(true);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mDataBinding.srl.finishRefresh(1);
                        mDataBinding.srl.finishLoadMore(100);
                        rawY = -1;

                        mDataBinding.getRoot().postDelayed(() -> {
                            if (mDataBinding.recycler.canScrollVertically(-1)) {
                                if (totalDy < DensityUtils.dp2Px(mContext, 30)) {
                                    mDataBinding.recycler.scrollBy(0, (int) -totalDy);
                                    totalDy = 0;
                                }
                            }
                        }, 200);
                        break;
                }
                return false;
            }
        });
    }

    private void requestUserInfo() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().getUserInfo(userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<UserInfoBean> bean) {
                UserInfoBean data = bean.getData();
                if (data != null) {
                    UserController.getInstance().setUserInfo(data);
                    updateUserView(data);
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void updateUserView(UserInfoBean bean) {
//        mDataBinding.tvUserName.setText(bean.getUserName());
        mDataBinding.imgUser.setImageResource(R.mipmap.ic_logo);
        // ImageLoader.load(mContext, bean.getHeadImgUrl(), mTopBarBinding.imgUser);
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_new_main;
    }

    @Override
    public void onNetworkChanged(boolean isConnected, NetworkType type) {
        if (isConnected) {
            String token = (String) SPUtils.getValue(mContext, SPUtils.SP_KEY_TOKEN, "");
            // 无网状态下的token需要替换正式游客token
            if (!TextUtils.isEmpty(token) && token.equals("lLLJqH1aaYGQCP8BS4rk")) {
                requestToken();
            }

            mDataBinding.checknet.setVisibility(View.GONE);
            syncacher();
            initRecycler();
        }
    }

    private void requestToken() {
        // 游客登录
        String equipmentId = UserController.getInstance().getEquipmentId();// 设备号
        ApiClient.getInstance().visitorLogin(equipmentId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<UserInfoBean> bean) {
                if (bean.isSuccess()) {
                    UserInfoBean data = bean.getData();
                    UserController.getInstance().setToken(data.getToken());
                    SPUtils.putValue(mContext, SPUtils.SP_KEY_TOKEN, data.getToken());
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    public class HomeAdapter extends BaseMultiItemQuickAdapter<ColumnBean, BaseDataBindingHolder> {


        public static final int HEADER_VIEW = 0x10000997;
        public static final int FOOTER_VIEW = 0x10000997;


        public static final int ONE_VIEW = 0x10000998;

        public static final int TWO_VIEW = 0x10000999;


        public HomeAdapter(@Nullable List<ColumnBean> data) {
            super(data);
            addItemType(HEADER_VIEW, R.layout.layout_view_top_bg);
            addItemType(ONE_VIEW, R.layout.layout_view_column_card);
            addItemType(TWO_VIEW, R.layout.layout_view_sound_and_golden);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder baseViewHolder, ColumnBean columnBean) {
            LogUtils.d("type :" + columnBean.getItemType());

            switch (columnBean.getItemType()) {
                case HEADER_VIEW:
                    bindHeader(baseViewHolder, columnBean);
                    break;
                case ONE_VIEW:
                    bindColumn(baseViewHolder, columnBean);
                    // 除了header 上移20dp
                    baseViewHolder.getDataBinding().getRoot().setTranslationY(DensityUtils.dp2Px(mContext, -30));
                    break;
                case TWO_VIEW:
                    bindTwoCard(baseViewHolder, columnBean);
                    // 除了header 上移20dp
                    baseViewHolder.getDataBinding().getRoot().setTranslationY(DensityUtils.dp2Px(mContext, -30));
                    break;
            }
        }

        @Override
        public void setNewData(@Nullable List<ColumnBean> data) {
            if (data.size() > 9) { // 首页每个item最多显示9个
                data = data.subList(0, 9);
            }
            super.setNewData(data);
        }

        private void bindHeader(BaseDataBindingHolder baseViewHolder, ColumnBean columnBean) {
            topBgAdapter = new BaseViewPagerAdapter<>(new ArrayList<>(), R.layout.layout_top_bar_item, mContext) {
                @Override
                protected void bindItem(LayoutTopBarItemBinding binding, ViewGroup container, int position, BannerBean bean) {
                    // LogUtils.d("title " + bean.getImgUrl());
                    binding.tvTopTitle.setText(bean.getTitle());
                    binding.tvTopContent.setText(bean.getAssistantTitle());

                    Glide.with(mContext)
                            .load(bean.getImgUrl())
                            .placeholder(R.color.topDefaultColor)
                            .error(R.color.topDefaultColor)
                            .centerInside()
                            .into(binding.imgBg);
//                    ImageLoader.load(mContext, bean.getImgUrl(), binding.imgBg);

                    binding.getRoot().setOnClickListener(v -> {
                        switch (position) {
                            case 0:
                                // 跳转到销售演示首页
                                toFragment(new SalesPresentationFragment());
                                break;
                            default:
                                // 跳转到声音设置
                                toFragment(new SoundSettingsFragment());
                                break;
                        }
                    });
                }
            };

            mTopBgBinding = (LayoutViewTopBgBinding) baseViewHolder.getDataBinding();

            mTopBgBinding.vpTopBar.setAdapter(topBgAdapter);
            mTopBgBinding.vpTopBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    setPoint(position);
                    mCurrentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            requestBannerData();
        }

        private void requestBannerData() {
            ApiClient.getInstance().getTopBanner().subscribe(new BaseObserver<BaseBean<List<BannerBean>>>() {
                @Override
                protected void onSuccess(BaseBean<List<BannerBean>> responseData) {
                    if (responseData == null) {
                        LogUtils.e("responseData null...");
                        return;
                    }
                    List<BannerBean> data = responseData.getData();
                    if (data == null || data.size() < 2) {
                        return;
                    }
                    if (topBgAdapter != null) {
                        topBgAdapter.setData(data);
                    }
                    setTopBannerData(data);
                    mTopBgBinding.vpTopBar.setCurrentItem(mCurrentPosition, true);
                    setPoint(mCurrentPosition);
                }

                @Override
                protected void onFail(Throwable e) {
                    LogUtils.e(e.toString());
                }
            });
        }

        Map<Integer, ImageView> map = new HashMap<>();

        public void setTopBannerData(List<BannerBean> data) {
            // LogUtils.d("banner size : " + data.size());
            map.clear();
            mTopBgBinding.llPoint.removeAllViews();
            if (data != null && data.size() > 0) {

                for (int i = 0; i < data.size(); i++) {

                    ShapeableImageView imageView = new ShapeableImageView(mContext);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtils.dp2Px(mContext, 6), DensityUtils.dp2Px(mContext, 6));
                    layoutParams.leftMargin = DensityUtils.dp2Px(mContext, 15);
                    imageView.setLayoutParams(layoutParams);

                    // 创建新的ShapeAppearanceModel对象，并将所有角设置为圆角
                    ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel.Builder().setAllCornerSizes(DensityUtils.dp2Px(mContext, 3)).build();

                    // 将新的ShapeAppearanceModel对象设置为ShapeableImageView的形状外观
                    imageView.setShapeAppearanceModel(shapeAppearanceModel);

                    // 设置背景颜色为红色
                    imageView.setBackgroundColor(i == 0 ? Color.parseColor("#FFCF022D") : Color.parseColor("#73FFFFFF"));

                    int finalI = i;
                    imageView.setOnClickListener(v -> {
                        mTopBgBinding.vpTopBar.setCurrentItem(finalI, true);
                        setPoint(finalI);
                    });

                    map.put(i, imageView);
                    mTopBgBinding.llPoint.addView(imageView);
                }
            }
        }

        public void setPoint(int position) {
            for (int pos : map.keySet()) {
                map.get(pos).setBackgroundColor(pos == position ? Color.parseColor("#FFCF022D") : Color.parseColor("#73FFFFFF"));
            }
        }

        private void bindTwoCard(BaseDataBindingHolder holder, ColumnBean columnBean) {
            LayoutViewSoundAndGoldenBinding dataBinding = (LayoutViewSoundAndGoldenBinding) holder.getDataBinding();
            // 甄选金曲
            dataBinding.goldenSongs.setOnClickListener(v -> {
                if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网不跳转
                    CustomToast.makeText(mContext, mContext.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    return;
                }
                LiveDataBus.get().with(LiveDataBusConstants.to_fragment).postValue(new GoldenSongDetailFragment());
            });

            hifiCard = dataBinding.goldenSongs;
            requestGoldenSongData(hifiCard);

            // 有声电台
            stationBinding = dataBinding.audioStation;
            requestAudioStationData(stationBinding);
        }

        private void bindColumn(BaseDataBindingHolder holder, ColumnBean columnBean) {
            LayoutViewColumnCardBinding dataBinding = holder.getBinding();
            dataBinding.tvTitle.setText(columnBean.getTitle());
            // LogUtils.d("card title : " + columnBean.getTitle());
            // tab 点击事件
            dataBinding.vvTop.setOnClickListener(v -> {
                if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网不跳转
                    CustomToast.makeText(mContext, mContext.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                    return;
                }
                LiveDataBus.get().with(LiveDataBusConstants.aggregation_type, Integer.class).postValue(columnBean.getType());
                // 跳转到对应的界面  ---> 专辑聚合页都是用的aggregation fragment，使用不同的type值区分
                LiveDataBus.get().with(LiveDataBusConstants.to_fragment, BaseFragment.class).postValue(new AggregationFragment());
            });

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            dataBinding.rvColumn.setLayoutManager(linearLayoutManager);
            BaseQuickAdapter columnAdapter;
            switch (columnBean.getType()) {
                case ITEM_TYPE_BLACK_PLASTIC: // 黑胶
                    columnAdapter = new BaseQuickAdapter<AlbumBean, BaseDataBindingHolder>(R.layout.layout_card_item_black_plastic) {
                        @Override
                        protected void convert(@NonNull BaseDataBindingHolder holder, AlbumBean itemBean) {
                            LayoutCardItemBlackPlasticBinding binding = (LayoutCardItemBlackPlasticBinding) holder.getDataBinding();
                            TouchUtils.bindClickItem(binding.imgPlay);
                            if (isHasData(itemBean)) {
                                binding.gas.setVisibility(VISIBLE);
                                binding.imgPlay.setVisibility(VISIBLE);
                                binding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                                binding.tvTitle.setText(itemBean.getAlbumName());
                                binding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));
                                Glide.with(mContext).setDefaultRequestOptions(ImageLoader.getOptions()).load(itemBean.getAlbumImgUrl()).into(binding.imgTopBg);
                                binding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
                                binding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_45));
                            } else {
                                binding.tvTitle.setTextColor(getResources().getColor(R.color.transport));
                                binding.tvSubtitle.setTextColor(getResources().getColor(R.color.transport));
                            }
                            binding.getRoot().setOnClickListener(v -> {
                                if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网不跳转
                                    CustomToast.makeText(mContext, mContext.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                // 跳转到专辑详情页
                                JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.blackGlue, itemBean.getId());
                            });

                            binding.imgPlay.setOnClickListener(v -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                if (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) { // 操作是当前的专辑
                                    if (MusicPlayManager.getInstance().isPlaying()) {
                                        MusicPlayManager.getInstance().pauseMusic();
                                    } else {
                                        MusicPlayManager.getInstance().playMusic();
                                    }
                                } else { // 不是当前的专辑 直接切换专辑
                                    MusicPlayManager.getInstance().addToPlaylistAndPlay(String.valueOf(itemBean.getId()), itemBean.getLibraryStoreList());
                                    MusicPlayManager.getInstance().playMusic();
                                }
                            });

                            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, bean -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                binding.imgPlay.setImageResource(MusicPlayManager.getInstance().isPlaying() && (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) ? R.mipmap.ic_pause_small : R.mipmap.ic_play_small);
                            });

                        }

                        @Override
                        public int getItemCount() {
                            return getData().size();
                        }
                    };
                    columnAdapter.setList(blackNullBeanList);
                    requestBlackGlueData(columnAdapter);
                    break;
                case ITEM_TYPE_AUDIO_COLUMN:  // 有声
                    columnAdapter = new BaseQuickAdapter<AlbumBean, BaseDataBindingHolder>(R.layout.layout_card_item_audio_column) {
                        @Override
                        protected void convert(@NonNull BaseDataBindingHolder holder, AlbumBean itemBean) {
                            LayoutCardItemAudioColumnBinding binding = (LayoutCardItemAudioColumnBinding) holder.getDataBinding();
                            TouchUtils.bindClickItem(binding.imgPlay);
                            if (isHasData(itemBean)) {
                                binding.gas.setVisibility(VISIBLE);
                                binding.imgPlay.setVisibility(VISIBLE);
                                binding.tvLabel.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvContent1.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvContent2.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                                binding.tvTitle.setText(itemBean.getAlbumName());
                                binding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));
                                Glide.with(mContext).setDefaultRequestOptions(ImageLoader.getOptions()).load(itemBean.getAlbumImgUrl()).into(binding.imgTopBg);
                                binding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
                                binding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_45));
                                binding.tvContent1.setTextColor(getResources().getColor(R.color.white_alpha_45));
                                binding.tvContent2.setTextColor(getResources().getColor(R.color.white_alpha_45));
                            } else {
                                binding.tvTitle.setTextColor(getResources().getColor(R.color.transport));
                                binding.tvSubtitle.setTextColor(getResources().getColor(R.color.transport));
                                binding.tvContent1.setTextColor(getResources().getColor(R.color.transport));
                                binding.tvContent2.setTextColor(getResources().getColor(R.color.transport));
                            }

                            binding.getRoot().setOnClickListener(v -> {
                                if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网不跳转
                                    CustomToast.makeText(mContext, mContext.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.blog, itemBean.getId());
                            });

                            // 03.28 所有的都按照播客类来处理
                            // 获取最新一期的碎片 然后获取标题
                            List<MusicListBean> libraryStoreList = itemBean.getLibraryStoreList();
                            // 根据播客类或是有声类分别设置 --> 全部按照
                            // String type1 = itemBean.getType();
                            if (libraryStoreList != null && libraryStoreList.size() > 0) {
                                MusicListBean musicListBean = libraryStoreList.get(libraryStoreList.size() - 1);
                                String content = musicListBean.getBatch() != null ? "#" + musicListBean.getBatch() + " " : "" + musicListBean.getName();
                                SpannableString spannableString = new SpannableString(content);
                                StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);
                                int startIndex = 0;
                                int endIndex = content.indexOf(" ");
                                spannableString.setSpan(boldStyleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                binding.tvContent2.setText(spannableString);
                            }
                            if (libraryStoreList != null && libraryStoreList.size() > 1) {
                                MusicListBean musicListBean = libraryStoreList.get(libraryStoreList.size() - 2);
                                String content = musicListBean.getBatch() != null ? "#" + musicListBean.getBatch() + " " : "" + musicListBean.getName();
                                SpannableString spannableString = new SpannableString(content);
                                StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);
                                int startIndex = 0;
                                int endIndex = content.indexOf(" ");
                                spannableString.setSpan(boldStyleSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                binding.tvContent1.setText(spannableString);
                            }

                            binding.imgPlay.setOnClickListener(v -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                if (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) { // 操作是当前的专辑
                                    if (MusicPlayManager.getInstance().isPlaying()) {
                                        MusicPlayManager.getInstance().pauseMusic();
                                    } else {
                                        MusicPlayManager.getInstance().playMusic();
                                    }
                                } else { // 不是当前的专辑 直接切换专辑
                                    MusicPlayManager.getInstance().addToPlaylistAndPlay(String.valueOf(itemBean.getId()), itemBean.getLibraryStoreList());
                                    MusicPlayManager.getInstance().playMusic();
                                }
                            });

                            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, bean -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                binding.imgPlay.setImageResource(MusicPlayManager.getInstance().isPlaying() && (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) ? R.mipmap.ic_pause_small : R.mipmap.ic_play_small);
                            });
                        }

                        @Override
                        public int getItemCount() {
                            return getData().size();
                        }
                    };
                    columnAdapter.setList(audioNullBeanList);
                    requestAudioColumnData(columnAdapter);
                    break;
                default:  // 沉浸音
                    columnAdapter = new BaseQuickAdapter<AlbumBean, BaseDataBindingHolder>(R.layout.layout_card_item_3d_immersion) {
                        @Override
                        protected void convert(@NonNull BaseDataBindingHolder holder, AlbumBean itemBean) {
                            LayoutCardItem3dImmersionBinding binding = (LayoutCardItem3dImmersionBinding) holder.getDataBinding();
                            TouchUtils.bindClickItem(binding.imgPlay);
                            if (isHasData(itemBean)) {
                                binding.gas.setVisibility(VISIBLE);
                                binding.imgPlay.setVisibility(VISIBLE);
                                binding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                                binding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                                Glide.with(mContext).setDefaultRequestOptions(ImageLoader.getOptions()).load(itemBean.getAlbumImgUrl()).into(binding.imgTopBg);
                                binding.tvTitle.setText(itemBean.getAlbumName());
                                binding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));
                                binding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                                binding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
                                binding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_45));
                            } else {
                                binding.tvTitle.setTextColor(getResources().getColor(R.color.transport));
                                binding.tvSubtitle.setTextColor(getResources().getColor(R.color.transport));
                            }

                            binding.getRoot().setOnClickListener(v -> {
                                if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网不跳转
                                    CustomToast.makeText(mContext, mContext.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.immersionMusic, itemBean.getId());
                            });
                            binding.imgPlay.setOnClickListener(v -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                if (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) { // 操作是当前的专辑
                                    if (MusicPlayManager.getInstance().isPlaying()) {
                                        MusicPlayManager.getInstance().pauseMusic();
                                    } else {
                                        MusicPlayManager.getInstance().playMusic();
                                    }
                                } else { // 不是当前的专辑 直接切换专辑
                                    MusicPlayManager.getInstance().addToPlaylistAndPlay(String.valueOf(itemBean.getId()), itemBean.getLibraryStoreList());
                                    MusicPlayManager.getInstance().playMusic();
                                }
                            });

                            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, bean -> {
                                MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                                binding.imgPlay.setImageResource(MusicPlayManager.getInstance().isPlaying() && (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) ? R.mipmap.ic_pause_small : R.mipmap.ic_play_small);
                            });
                        }

                        @Override
                        public int getItemCount() {
                            return getData().size();
                        }
                    };
                    dataBinding.rvColumn.addItemDecoration(new RecyclerView.ItemDecoration() {
                        @Override
                        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                            super.getItemOffsets(outRect, view, parent, state);

                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) dataBinding.getRoot().getLayoutParams();
                            int margin = layoutParams.leftMargin;
                            int position = parent.getChildAdapterPosition(view);
                            int itemCount = parent.getAdapter().getItemCount();
                            if (position == itemCount - 1) {
                                outRect.right = margin;
                            }
                        }
                    });
                    columnAdapter.setList(immersionNullBeanList);
                    request3dImmersionData(columnAdapter);
//                    SPUtils.setViewMarginStart(dataBinding.rvColumn, DensityUtils.dp2Px(mContext, 10));
//                    SPUtils.setViewMarginEnd(dataBinding.rvColumn, DensityUtils.dp2Px(mContext, 10));
                    break;
            }
            dataBinding.rvColumn.setAdapter(columnAdapter);
        }

        private void requestGoldenSongData(HifiCard hifiCard) {
            ApiClient.getInstance().getGoldenSongs().subscribe(new BaseObserver<>() {
                @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
                @Override
                protected void onSuccess(BaseBean<ModuleBean> responseData) {
                    ModuleBean data = responseData.getData();
                    if (data == null) {
                        LogUtils.d("data is null...");
                        return;
                    }
                    ModuleInfoBean moduleInfo = data.getModuleInfo();

                    hifiCard.setTopImgPath(moduleInfo.getHomeImgUrl());
                    SPUtils.putValue(mContext, getResources().getString(R.string.HIFIURL), moduleInfo.getHomeImgUrl());
                    List<AlbumBean> albumBeanList = data.getModuleData();
                    if (albumBeanList.isEmpty()) {
                        return;
                    }
                    SPUtils.setDataList(mContext, getResources().getString(R.string.HIFIBEAN), albumBeanList);
                    List<MusicListBean> musicBeanList = albumBeanList.get(0).getLibraryStoreList();
                    String id = String.valueOf(albumBeanList.get(0).getId());
                    LiveDataBus.get().with(LiveDataBusConstants.golden_detail_id).postValue(id);
                    hifiCard.setLeftData(musicBeanList, id);
                }

                @Override
                protected void onFail(Throwable e) {
                    LogUtils.e("data222" + e.toString());
                    if (!hifiCardNullBeanList.isEmpty()) {
                        List<MusicListBean> musicBeanList = hifiCardNullBeanList.get(0).getLibraryStoreList();
                        String id = String.valueOf(hifiCardNullBeanList.get(0).getId());
                        LiveDataBus.get().with(LiveDataBusConstants.golden_detail_id).postValue(id);
                        hifiCard.setLeftData(musicBeanList, id);
                    }
                    hifiCard.setTopImgPath((String) SPUtils.getValue(mContext, getResources().getString(R.string.HIFIURL), ""));
                }
            });

            // ImageLoader.load(mContext, "", hifiCardBinding.imgTitleBg);
        }

        private void requestAudioStationData(NewsCard newsCard) {
            ApiClient.getInstance().getAudioStation().subscribe(new BaseObserver<>() {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onSuccess(BaseBean<ModuleBean> responseData) {
                    if (responseData == null || responseData.getData() == null) {
                        LogUtils.e("responseData null...");
                        return;
                    }
                    ModuleBean data = responseData.getData();
                    List<AlbumBean> albumBeanList = data.getModuleData();
                    if (albumBeanList != null && albumBeanList.size() > 0) {
                        int size = albumBeanList.size();
                        ArrayList<AlbumBean> list = new ArrayList<>();

                        AlbumBean moduleData1 = size >= 1 ? albumBeanList.get(0) : null;
                        AlbumBean moduleData2 = size >= 2 ? albumBeanList.get(1) : null;
                        AlbumBean moduleData3 = size >= 3 ? albumBeanList.get(2) : null;
                        if (moduleData1 != null) list.add(moduleData1);
                        if (moduleData2 != null) list.add(moduleData2);
                        if (moduleData3 != null) list.add(moduleData3);

                        newsCard.setData(albumBeanList);
                        SPUtils.setDataList(mContext, getResources().getString(R.string.AUDIOSTATION), albumBeanList);

                        //LitePalManager.updateBytype(getResources().getString(R.string.AUDIOSTATION), albumBeanList);
                    }

                }

                @Override
                protected void onFail(Throwable e) {
                    newsCard.setData(auStationNullBeanList);
                    LogUtils.e(e.toString());
                }
            });
        }

        private void requestBlackGlueData(BaseQuickAdapter adapter) {
            ApiClient.getInstance().getBlackGlue().subscribe(new BaseObserver<>() {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onSuccess(BaseBean<ModuleBean> responseData) {
                    if (responseData == null || responseData.getData() == null) {
                        LogUtils.e("responseData null...");
                        return;
                    }
                    /*if (!data.isShowFlag()) {
                        LogUtils.d("hide module : " + position);
                        holder.itemView.setVisibility(View.GONE);
                        return;
                    }*/
                    adapter.setList(responseData.getData().getModuleData());
                    SPUtils.setDataList(mContext, getResources().getString(R.string.BLACK_PLASTIC), responseData.getData().getModuleData());
                    //LitePalManager.updateBytype(getResources().getString(R.string.BLACK_PLASTIC), data.getModuleData());
                }

                @Override
                protected void onFail(Throwable e) {
                    LogUtils.e(e.toString());
                }
            });
        }

        private void requestAudioColumnData(BaseQuickAdapter adapter) {
            ApiClient.getInstance().getAudioColumn().subscribe(new BaseObserver<>() {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onSuccess(BaseBean<ModuleBean> responseData) {
                    LogUtils.d("data222 " + responseData.toString());
                    if (responseData == null) {
                        LogUtils.e("responseData null...");
                        return;
                    }
                    ModuleBean data = responseData.getData();
                    if (data == null) {
                        LogUtils.d("data is null...");
                        return;
                    }
                    adapter.setList(data.getModuleData());
                    SPUtils.setDataList(mContext, getResources().getString(R.string.AUDIO_COLUMN), data.getModuleData());
                }

                @Override
                protected void onFail(Throwable e) {
                    LogUtils.e(e.toString());
                }
            });
        }

        private void request3dImmersionData(BaseQuickAdapter adapter) {
            ApiClient.getInstance().getImmersion().subscribe(new BaseObserver<>() {
                @SuppressLint("SetTextI18n")
                @Override
                protected void onSuccess(BaseBean<ModuleBean> responseData) {
                    LogUtils.d("data222 " + responseData.toString());
                    if (responseData == null) {
                        LogUtils.e("responseData null...");
                        return;
                    }
                    ModuleBean data = responseData.getData();
                    if (data == null) {
                        LogUtils.d("data is null...");
                        return;
                    }
                    adapter.setList(data.getModuleData());
                    SPUtils.setDataList(mContext, getResources().getString(R.string.IMMERSION_SOUND), data.getModuleData());
                }

                @Override
                protected void onFail(Throwable e) {
                    LogUtils.e(e.toString());
                }
            });
        }
    }
}
