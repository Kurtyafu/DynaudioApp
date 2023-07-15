package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutFragmentNewGoldenSongsBinding;
import com.byd.dynaudio_app.databinding.LayoutItemGoldenDetailBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarTitleMidBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgWithPlayBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 甄选金曲详情页
 */
public class GoldenSongDetailFragment extends BaseFragment<LayoutFragmentNewGoldenSongsBinding,
        BaseViewModel> {
    private LayoutViewTopBgWithPlayBinding mTopBgBinding;
    private LayoutViewTopBarTitleMidBinding mTopBarBinding;
    private String albumId;
    private GoldenSongsAdapter goldenSongsAdapter;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.imgBack);
        initRecycler();
        initSrl();
        initTopBar();
        initTopLoading();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler() {
        // mDataBinding.recycler.setBackgroundColor(Color.parseColor("#FF000000"));
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        goldenSongsAdapter = new GoldenSongsAdapter(R.layout.layout_item_golden_detail);

        mTopBgBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.layout_view_top_bg_with_play, null, false);
        TouchUtils.bindClickItem(mTopBgBinding.llPlayAll);
        goldenSongsAdapter.addHeaderView(mTopBgBinding.getRoot());

        View view = new View(mContext);
        mDataBinding.getRoot().postDelayed(() -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = DensityUtils.dp2Px(mContext, 73 + 10 - 20/*mini bar的高度+间距10 所有的上移了20*/);
                if (!SPUtils.isPad()) { // 车机底部高度不知道为什么对 需要加高
                    layoutParams.height += DensityUtils.dp2Px(mContext, 70);
                }
                view.setLayoutParams(layoutParams);
            }
        }, 100);
//        goldenSongsAdapter.addFooterView(view);
        mDataBinding.recycler.setAdapter(goldenSongsAdapter);
        mDataBinding.recycler.setPadding(mDataBinding.recycler.getPaddingLeft(), mDataBinding.recycler.getPaddingTop(), mDataBinding.recycler.getPaddingTop(),
                DensityUtils.dp2Px(mContext, 73 + 10 - 30));

        requestData();
        // 列表上移20dp
//        mDataBinding.recycler.setTranslationY(DensityUtils.dp2Px(mContext, -30));
//        mDataBinding.tvLoading.setVisibility(View.GONE);
        mDataBinding.recycler.setOnTouchListener((view1, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mDataBinding.srl.finishLoadMore();
                    mDataBinding.getRoot().postDelayed(() -> {
                        if (totalDy < DensityUtils.dp2Px(mContext, 60)) {
                            mDataBinding.recycler.scrollBy(0, (int) -totalDy);
                            totalDy = 0;
                        }
                    }, 400);
                    break;
            }
            return false;
        });
    }

    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility", "Range"})

    private void initSrl() {
        mDataBinding.srl.setEnableRefresh(true);
        mDataBinding.srl.setEnableLoadMore(false);
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
        mDataBinding.srl.setOnRefreshListener(refreshLayout -> mDataBinding.srl.finishRefresh());
        mDataBinding.srl.setHeaderHeight(300);
        mDataBinding.srl.setHeaderMaxDragRate(1.f);
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
//        mDataBinding.srl.setOnLoadMoreListener(refreshLayout -> mDataBinding.srl.finishLoadMore());
        mDataBinding.srl.setFooterHeight(100);
        mDataBinding.srl.setFooterMaxDragRate(1.f);
    }

    private void initTopBar() {
        // top bar区域及显隐
        mTopBarBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bar_title_mid,
                        null, false);
        mDataBinding.frameTopBar.addView(mTopBarBinding.getRoot());

        mDataBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;
                setTopBarAlpha(totalDy);
            }
        });

        TouchUtils.bindClickItem(mTopBarBinding.llPlayAll);
    }

    private float totalDy = 0;

    private void setTopBarAlpha(float dy) {
        // 带有播放按钮的详情页 0-55dp 透明度0 ; 60dp 满透明度
        int alpha0Pos = (DensityUtils.dp2Px(mContext, 55));
        int alpha1Pos = DensityUtils.dp2Px(mContext, 60);
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

    private ProgressDrawable progressDrawable;

    private void initTopLoading() {
        progressDrawable = new ProgressDrawable();
        mDataBinding.loading.setImageDrawable(progressDrawable);
        progressDrawable.start();
        mDataBinding.srl.setOnMultiPurposeListener(new SimpleMultiPurposeListener() {
            @Override
            public void onHeaderMoving(RefreshHeader header, boolean isDragging, float percent, int offset, int headerHeight, int maxDragHeight) {
                super.onHeaderMoving(header, isDragging, percent, offset, headerHeight, maxDragHeight);
                mDataBinding.loading.setVisibility(isDragging ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private void requestData() {
        String value = LiveDataBus.get().with(LiveDataBusConstants.golden_detail_id, String.class).getValue();
        String id = value != null ? value : "";
        albumId = id;
        // todo 这个接口应该传userId,专辑是固定的,和登录状态无关
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().goldenSongsMusic(userId).subscribe(new BaseObserver<>() {
            @SuppressLint("CheckResult")
            @Override
            protected void onSuccess(BaseBean<AlbumBean> albumBeanBaseBean) {
                AlbumBean data = albumBeanBaseBean.getData();
                if (data != null) {
                    List<MusicListBean> libraryStoreList = data.getLibraryStoreList();
                    if (libraryStoreList == null) {
                        libraryStoreList = new ArrayList<>();
                    }
                    // 未登录遍历本地收藏列表重新赋值
                    if (!UserController.getInstance().isLoginStates()) {
                        for (MusicListBean bean : libraryStoreList) {
                            boolean collect = DBController.queryMusicCollect(mContext, bean);
                            bean.setCollectFlag(collect);
                        }
                    }

                    goldenSongsAdapter.setNewData(libraryStoreList);

                    boolean showTopBar = libraryStoreList.size() >= 6;
                    mTopBarBinding.getRoot().setVisibility(showTopBar ? View.VISIBLE : View.INVISIBLE);

                    ViewGroup.LayoutParams layoutParams = mTopBgBinding.imgBg.getLayoutParams();

                    mTopBgBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    mTopBgBinding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    mTopBgBinding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
                    mTopBgBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
                    mTopBgBinding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                    Glide.with(mContext)
                            .load(data.getAlbumDescImgUrl())
                            .placeholder(R.color.topDefaultColor)
                            .error(R.color.topDefaultColor)
                            .into(mTopBgBinding.imgBg);

                    mTopBgBinding.tvTitle.setText(data.getAlbumName());
                    mTopBgBinding.tvSubtitle.setText(data.getAlbumDesc());
                    mTopBarBinding.tvDynaudio.setVisibility(View.VISIBLE);
                    mTopBarBinding.tvDynaudio.setText(data.getAlbumName());
                    mTopBarBinding.llPlayAll.setVisibility(View.VISIBLE);

                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
        goldenSongsAdapter.setNewData(new ArrayList<>());
    }

    @Override
    protected void initListener() {
        mDataBinding.imgBack.setOnClickListener(v -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));
        //播放按钮
        mTopBarBinding.llPlayAll.setOnClickListener(v -> MusicPlayManager.getInstance().addToPlaylistAndPlay(goldenSongsAdapter.getData()));
        mTopBgBinding.llPlayAll.setOnClickListener(v -> MusicPlayManager.getInstance().playMusic(goldenSongsAdapter.getData(), null, 0, albumId));
    }

    @Override
    protected void initObserver() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                .observe(this, musicPlayerBean -> {
                    boolean playing = MusicPlayManager.getInstance().isPlaying()
                            && albumId != null
                            && albumId.equals(MusicPlayManager.getInstance().getCurrentAlbumId());
                    mTopBarBinding.llPlayAll.setTag(playing);
                    mTopBgBinding.llPlayAll.setTag(playing);
                });
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_new_golden_songs;
    }

    public class GoldenSongsAdapter extends BaseQuickAdapter<MusicListBean, BaseDataBindingHolder> {

        public GoldenSongsAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder holder, MusicListBean itemBean) {
            LayoutItemGoldenDetailBinding dataBinding = (LayoutItemGoldenDetailBinding) holder.getDataBinding();

            // 偏移30dp
            dataBinding.getRoot().setTranslationY(DensityUtils.dp2Px(mContext, -30));
            for (int i = 0; i < dataBinding.clItemGolden.getChildCount(); i++) {
                dataBinding.clItemGolden.getChildAt(i).setTranslationY(DensityUtils.dp2Px(mContext, -7));
            }

            dataBinding.vvTriangle.reverse();
            dataBinding.vvTriangle.setColor(Color.parseColor("#0FFFFFFF"));
            dataBinding.tvNum.setText(String.valueOf(goldenSongsAdapter.getItemPosition(itemBean) + 1));

            dataBinding.tvTitle.setText(itemBean.getName());
            if (itemBean.getQualityUrl() == null || itemBean.getQualityUrl().isEmpty()) {
                dataBinding.tvTitle.setMaxWidth(dataBinding.tvTitle.getMaxWidth() - dataBinding.tvLabel.getWidth());
            }
            dataBinding.tvLabel.setLabelImgPath(itemBean.getQualityUrl());
            dataBinding.tvSinger.setText(SPUtils.formatAuther(itemBean.getSingerList()));
            dataBinding.tvSize.setText(SPUtils.formatSize(itemBean.getSize()));
            dataBinding.tvDuration.setText(SPUtils.formatTime2(itemBean.getDuration()));
            dataBinding.tvContent.setText(itemBean.getRecommendCause());
            dataBinding.imgCollect.setImageResource(itemBean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);

            // item点击就播放当前音乐
            dataBinding.getRoot().setOnClickListener(v -> {
                boolean isHighLight = dataBinding.getRoot().getTag() != null && (boolean) dataBinding.getRoot().getTag();
                if (!isHighLight) {
                    MusicPlayManager.getInstance().playMusic(goldenSongsAdapter.getData(), itemBean,
                            goldenSongsAdapter.getItemPosition(itemBean), albumId);
                } else {
                    PlayerVisionManager.getInstance().showFullPlayer();
                }
            });

            // 收藏按钮
            dataBinding.imgCollect.setOnClickListener(v ->
                    MusicPlayManager.getInstance().setCollect(
                            itemBean, !itemBean.isCollectFlag()));


            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe(
                    (LifecycleOwner) mContext, bean -> {
                        boolean isHighLight = bean != null
                                && bean.getSpecialId() == (itemBean.getSpecialId());
                        // 序号
                        dataBinding.tvNum.setVisibility(isHighLight ? View.INVISIBLE : View.VISIBLE);
                        dataBinding.imgNum.setVisibility(isHighLight ? View.VISIBLE : View.INVISIBLE);
                        // 文字
                        dataBinding.tvTitle.setTextColor(isHighLight
                                ? Color.parseColor("#FFFF3D46")
                                : Color.parseColor("#CCFFFFFF"));
                        // 存储状态
                        dataBinding.getRoot().setTag(isHighLight);
                    }
            );

            LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe(
                    (LifecycleOwner) mContext, bean -> {
                        // 收藏
                        if (bean != null && bean.getSpecialId() == itemBean.getSpecialId()) {
                            boolean isCollected = bean.isCollectFlag();
                            dataBinding.imgCollect.setImageResource(isCollected
                                    ? R.drawable.img_collected : R.drawable.img_collect);
                            itemBean.setCollectFlag(isCollected);
                        }
                    }
            );
        }
    }
}
