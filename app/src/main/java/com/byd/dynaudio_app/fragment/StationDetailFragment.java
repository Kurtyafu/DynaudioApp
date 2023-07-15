package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
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
import com.byd.dynaudio_app.databinding.LayoutFragmentNewStationBinding;
import com.byd.dynaudio_app.databinding.LayoutItemStationDetailBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarTitleMidBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgWithPlayBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
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
 * 电台详情页
 */
public class StationDetailFragment extends BaseFragment<LayoutFragmentNewStationBinding,
        BaseViewModel> {
    private LayoutViewTopBgWithPlayBinding mTopBgBinding;
    private LayoutViewTopBarTitleMidBinding mTopBarBinding;
    private String albumId;
    private List<MusicListBean> playList;
    private StationAdapter stationAdapter;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.imgBack);

        initRecycler();
        initSrl();
        initTopBar();
        initTopLoading();

        // requestData();
        AlbumBean value = LiveDataBus.get().with(LiveDataBusConstants.station_data, AlbumBean.class).getValue();
        if (value != null) setData(value);
    }

    @Override
    protected void initListener() {
        mDataBinding.imgBack.setOnClickListener(v -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));
        //播放按钮
        mTopBarBinding.llPlayAll.setOnClickListener(v -> MusicPlayManager.getInstance().addToPlaylistAndPlay(albumId, playList));
        mTopBgBinding.llPlayAll.setOnClickListener(v -> MusicPlayManager.getInstance().playMusic(playList, null, 0, albumId));
    }

    @Override
    protected void initObserver() {
        LiveDataBus.get().with(LiveDataBusConstants.station_data, AlbumBean.class).observe(this, new Observer<AlbumBean>() {
            @Override
            public void onChanged(AlbumBean albumBean) {
                setData(albumBean);
            }
        });

        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe(this, musicPlayerBean -> {
            boolean playing = MusicPlayManager.getInstance().isPlaying() && albumId != null && albumId.equals(MusicPlayManager.getInstance().getCurrentAlbumId());
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
        return R.layout.layout_fragment_new_station;
    }

    private void setData(AlbumBean albumBean) {
        albumId = String.valueOf(albumBean.getId());

        List<MusicListBean> libraryStoreList = albumBean.getLibraryStoreList();
        if (libraryStoreList == null) libraryStoreList = new ArrayList<>();

        if (playList == null) playList = new ArrayList<>();
        playList.clear();
        playList.addAll(libraryStoreList);

//        for (int i = 0; i < libraryStoreList.size(); i++) {
//            LogUtils.d("before : " + libraryStoreList.get(i).getName() + ",(" + libraryStoreList.get(i).getLibraryType()+")");
//        }

        // 台宣：播放列表需要有 但界面不显示
//        libraryStoreList.removeIf(bean -> "2".equals(bean.getLibraryType()));
        // todo 测试提了bug
        playList.removeIf(bean -> "2".equals(bean.getLibraryType()));

//        for (int i = 0; i < libraryStoreList.size(); i++) {
//            LogUtils.d("before2 : " + libraryStoreList.get(i).getName() + "," + libraryStoreList.get(i).getLibraryType());
//        }

        stationAdapter.setNewData(playList);

        // 设置标题和图片
        mTopBgBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
        mTopBgBinding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
        mTopBgBinding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
        mTopBgBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
        mTopBgBinding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        mTopBgBinding.tvTitle.setText(albumBean.getAlbumName());
        mTopBarBinding.tvDynaudio.setVisibility(View.VISIBLE);
        mTopBarBinding.tvDynaudio.setText(albumBean.getAlbumName());
        mTopBarBinding.llPlayAll.setVisibility(View.VISIBLE);

        mTopBgBinding.tvSubtitle.setText(albumBean.getAlbumDesc());
        Glide.with(mContext)
                .load(albumBean.getAlbumDescImgUrl())
                .centerInside()
                .placeholder(R.color.topDefaultColor)
                .error(R.color.topDefaultColor)
                .into(mTopBgBinding.imgBg);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler() {
        // mDataBinding.recycler.setBackgroundColor(Color.parseColor("#FF000000"));
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        stationAdapter = new StationAdapter(R.layout.layout_item_station_detail);

        mTopBgBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                R.layout.layout_view_top_bg_with_play, null, false);
        TouchUtils.bindClickItem(mTopBgBinding.llPlayAll);
        stationAdapter.addHeaderView(mTopBgBinding.getRoot());

//        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_footer_two, null, false);
        /*mDataBinding.getRoot().postDelayed(() -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = DensityUtils.dp2Px(mContext, 43*//*mini bar的高度+间距10 所有的上移了20*//*);
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.parseColor("#FF272829"));
            }
        }, 100);
        view.setPadding(DensityUtils.dp2Px(mContext, 20), 0, DensityUtils.dp2Px(mContext, 20), 0);*/
//        stationAdapter.addFooterView(view);
        mDataBinding.recycler.setAdapter(stationAdapter);
        mDataBinding.recycler.setPadding(mDataBinding.recycler.getPaddingLeft(), mDataBinding.recycler.getPaddingTop(), mDataBinding.recycler.getPaddingTop(),
                DensityUtils.dp2Px(mContext, 73 + 10 - 30));

        // requestData();
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
                    }, 200);
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

    public class StationAdapter extends BaseQuickAdapter<MusicListBean, BaseDataBindingHolder> {

        public StationAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder holder, MusicListBean itemBean) {
            LayoutItemStationDetailBinding dataBinding = (LayoutItemStationDetailBinding) holder.getDataBinding();

            // 偏移30dp
            dataBinding.getRoot().setTranslationY(DensityUtils.dp2Px(mContext, -30));
            for (int i = 0; i < dataBinding.clItemStation.getChildCount(); i++) {
                dataBinding.clItemStation.getChildAt(i).setTranslationY(DensityUtils.dp2Px(mContext, 10));
            }

            dataBinding.tvNum.setText(String.valueOf(stationAdapter.getItemPosition(itemBean) + 1));
            dataBinding.tvLabel.setLabelImgPath(itemBean.getQualityUrl());

            dataBinding.tvTitle.setText(itemBean.getName());
            if (itemBean.getQualityUrl() == null || itemBean.getQualityUrl().isEmpty()) {
                dataBinding.tvTitle.setMaxWidth(dataBinding.tvTitle.getMaxWidth() - dataBinding.tvLabel.getWidth());
            }
            dataBinding.tvSinger.setText(SPUtils.formatAuther(itemBean.getSingerList()));
            dataBinding.tvDuration.setText(SPUtils.formatTime(itemBean.getDuration()));
            dataBinding.imgCollect.setImageResource(itemBean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);

            // item点击就播放当前音乐
            dataBinding.getRoot().setOnClickListener(v -> {
                boolean isHighLight = dataBinding.getRoot().getTag() != null && (boolean) dataBinding.getRoot().getTag();
                if (!isHighLight) {
                    MusicPlayManager.getInstance().playMusic(playList, itemBean, stationAdapter.getItemPosition(itemBean), albumId);
                } else {
                    PlayerVisionManager.getInstance().showFullPlayer();
                }
            });

            // 收藏按钮
            dataBinding.imgCollect.setOnClickListener(v -> MusicPlayManager.getInstance().setCollect(itemBean, !itemBean.isCollectFlag()));


            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                boolean isHighLight = bean != null && bean.getSpecialId() == (itemBean.getSpecialId()
//                            && bean.getRuleId() != null && bean.getRuleId().equals(bean.getRuleId())
                );
                // 序号
                dataBinding.tvNum.setVisibility(isHighLight ? View.INVISIBLE : View.VISIBLE);
                dataBinding.imgNum.setVisibility(isHighLight ? View.VISIBLE : View.INVISIBLE);
                // 文字
                dataBinding.tvTitle.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#CCFFFFFF"));
                // 存储状态
                dataBinding.getRoot().setTag(isHighLight);
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                // 收藏
                if (bean != null && bean.getSpecialId() == itemBean.getSpecialId()) {
                    boolean isCollected = bean.isCollectFlag();
                    dataBinding.imgCollect.setImageResource(isCollected ? R.drawable.img_collected : R.drawable.img_collect);
                    itemBean.setCollectFlag(isCollected);
                }
            });

        }
    }

}
