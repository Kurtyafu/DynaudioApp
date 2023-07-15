package com.byd.dynaudio_app.fragment;

import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_3D_IMMERSION_SOUND;
import static com.byd.dynaudio_app.bean.ColumnBean.ColumnBeanType.ITEM_TYPE_AUDIO_COLUMN;
import static com.byd.dynaudio_app.fragment.AggregationFragment.AggregationAdapter.HEADER_VIEW;
import static com.byd.dynaudio_app.fragment.AggregationFragment.AggregationAdapter.ONE_VIEW;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.ModuleBean;
import com.byd.dynaudio_app.bean.response.ModuleInfoBean;
import com.byd.dynaudio_app.databinding.LayoutFragmentNewAggregationBinding;
import com.byd.dynaudio_app.databinding.LayoutItemAggregation3dImmersionBinding;
import com.byd.dynaudio_app.databinding.LayoutItemAggregationAudioColumnBinding;
import com.byd.dynaudio_app.databinding.LayoutItemAggregationBlackPlasticBinding;
import com.byd.dynaudio_app.databinding.LayoutRecyclerBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarTitleMidBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgWithPlayBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.network.INetworkListener;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.network.NetworkType;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.JumpUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
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
 * 专辑聚合页 LayoutViewTopBarTitleMidBinding, LayoutViewTopBgWithPlayBinding
 */
public class AggregationFragment extends BaseFragment<LayoutFragmentNewAggregationBinding, BaseViewModel> implements INetworkListener {
    private int aggregationType = ITEM_TYPE_3D_IMMERSION_SOUND;
    private BaseRecyclerViewAdapter<AlbumBean, LayoutItemAggregationBlackPlasticBinding> blackGlueAdapter;
    private BaseRecyclerViewAdapter<AlbumBean, LayoutItemAggregationAudioColumnBinding> audioColumnAdapter;
    private BaseRecyclerViewAdapter<AlbumBean, LayoutItemAggregation3dImmersionBinding> immersion3dAdapter;

    private LayoutViewTopBgWithPlayBinding mTopBgBinding;
    private LayoutViewTopBarTitleMidBinding mTopBarBinding;
    private RecyclerView recyclerView;

    private ArrayList<AlbumBean> blackNullBeanList = new ArrayList<>();
    private ArrayList<AlbumBean> audioNullBeanList = new ArrayList<>();
    private ArrayList<AlbumBean> immersionNullBeanList = new ArrayList<>();


    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.imgBack);
        if (!NetworkObserver.getInstance().isConnectNormal(mContext)) {//没网
            mDataBinding.checknet.setVisibility(View.VISIBLE);
        } else {
            mDataBinding.checknet.setVisibility(View.GONE);
        }
        loadNullData();
        initRecycler();
        initSrl();
        initTopBar();
        initTopLoading();
    }

    @Override
    public void onResume() {
        super.onResume();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mDataBinding.recycler.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(0, 0);
        totalDy = 0;
        setTopBarAlpha(totalDy);
    }

    private AggregationAdapter aggregationAdapter;

    private void loadNullData() {
        AlbumBean albumBean = new AlbumBean();
        blackNullBeanList.clear();
        audioNullBeanList.clear();
        immersionNullBeanList.clear();
        for (int i = 0; i < 9; i++) {
            blackNullBeanList.add(albumBean);
        }
        for (int i = 0; i < 8; i++) {
            audioNullBeanList.add(albumBean);
        }
        for (int i = 0; i < 15; i++) {
            immersionNullBeanList.add(albumBean);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecycler() {
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        // 获取当前页面应该显示的type: 黑胶专区、有声专栏、3d沉浸声
        Integer value = LiveDataBus.get().with(LiveDataBusConstants.aggregation_type, Integer.class).getValue();
        if (value != null) aggregationType = value;
        aggregationAdapter = new AggregationAdapter(new ArrayList<>() {{
            add(new Aggregation(aggregationType, true));
            add(new Aggregation(aggregationType, false));
        }});
        View view = new View(mContext);
        mDataBinding.getRoot().postDelayed(() -> {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = DensityUtils.dp2Px(mContext, 73 + 10 - 20 - 10 - 50/*mini bar的高度+间距10 所有的上移了20 最后的10不知道是哪里的偏差*/);
//                if (!SPUtils.isPad()) { // 车机底部高度不知道为什么对 需要加高
//                    layoutParams.height += DensityUtils.dp2Px(mContext, 70);
//                }
                view.setLayoutParams(layoutParams);
                view.setBackgroundColor(Color.parseColor("#FF272829"));
            }
        }, 100);
//        aggregationAdapter.addFooterView(view);
        mDataBinding.recycler.setAdapter(aggregationAdapter);

        mDataBinding.recycler.setOnTouchListener((view1, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
//                    mDataBinding.srl.finishLoadMore();

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
//        mDataBinding.srl.setOnRefreshListener(refreshLayout -> mDataBinding.srl.finishRefresh());
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
        mTopBarBinding.llPlayAll.setVisibility(View.GONE);
        mDataBinding.frameTopBar.addView(mTopBarBinding.getRoot());

        mDataBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;
                setTopBarAlpha(totalDy);
            }
        });
    }

    private void drawTopBgView(String detailImgUrl, String detailTitle, String detailDesc) {
        mTopBgBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
        mTopBgBinding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
        Glide.with(mContext)
                .load(detailImgUrl)
                .placeholder(R.color.topDefaultColor)
                .error(R.color.topDefaultColor)
                .into(mTopBgBinding.imgBg);
        mTopBgBinding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        mTopBgBinding.tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mTopBgBinding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
        mTopBgBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
        mTopBgBinding.tvTitle.setText(detailTitle);
        mTopBgBinding.tvSubtitle.setText(detailDesc);
    }

    private float totalDy = 0;

    private void setTopBarAlpha(float dy) {
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


    /**
     * 黑胶专区的adapter
     */
    private void initBlackPlastic() {
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3, RecyclerView.VERTICAL, false));
        blackGlueAdapter = new BaseRecyclerViewAdapter<>(mContext, new ArrayList<>()) {
            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_aggregation_black_plastic;
            }

            @Override
            protected void bindItem(LayoutItemAggregationBlackPlasticBinding dataBinding, AlbumBean itemBean, int position) {
                // judgeIfScale(dataBinding, position);
                if (isHasData(itemBean)) {
                    dataBinding.placeimage.setVisibility(View.GONE);
                    dataBinding.imgShow.setVisibility(View.VISIBLE);
                    dataBinding.imgRecord.setVisibility(View.VISIBLE);
                    // dataBinding.gas.setVisibility(View.VISIBLE);
                    dataBinding.imgPlay.setVisibility(View.VISIBLE);
                    dataBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                    dataBinding.tvTitle.setText(itemBean.getAlbumName());
                    dataBinding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));
                    Glide.with(mContext)
                            .setDefaultRequestOptions(ImageLoader.getOptions())
                            .load(itemBean.getAlbumImgUrl())
                            .into(dataBinding.imgShow);
                    dataBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_45));
                } else {
                    dataBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.transport));
                }
//                ImageLoader.load(mContext, itemBean.getAlbumImgUrl(), dataBinding.imgShow);
                dataBinding.getRoot().setOnClickListener(view -> {
                    JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.blackGlue, itemBean.getId());
                });

                dataBinding.imgPlay.setOnClickListener(v -> {
                    MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                    if (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) { // 操作是当前的专辑
                        if (MusicPlayManager.getInstance().isPlaying()) {
                            MusicPlayManager.getInstance().pauseMusic();
                        } else {
                            MusicPlayManager.getInstance().playMusic();
                        }
                    } else { // 不是当前的专辑 直接切换专辑
                        MusicPlayManager.getInstance().addToPlaylistAndPlay(String.valueOf(itemBean.getId()),
                                itemBean.getLibraryStoreList());
                    }
                });

                LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, bean -> {
                    MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();

//                    if (value != null){
//                        LogUtils.d("current id : " + itemBean.getId() + " type : " + itemBean.getTypeName()
//                                + " post id : " + MusicPlayManager.getInstance().getCurrentAlbumId() + " type : " + value.getTypeName());
//                    }

                    ImageLoader.load(mContext, MusicPlayManager.getInstance().isPlaying()
                            && String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))
                            ? R.mipmap.ic_pause_small : R.mipmap.ic_play_small, dataBinding.imgPlay);
                });

            }
        };

        recyclerView.setAdapter(blackGlueAdapter);
        blackGlueAdapter.setData(blackNullBeanList);

        // 请求数据
        ApiClient.getInstance().getBlackGlueAggregate().subscribe(new BaseObserver<BaseBean<ModuleBean>>() {
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                // LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                List<AlbumBean> albumBeanList = data.getModuleData();
                blackGlueAdapter.setData(albumBeanList);

                // 设置顶部
                ModuleInfoBean moduleInfo = data.getModuleInfo();
                String detailImgUrl = null, detailTitle = null, detailDesc = null;
                if (moduleInfo != null) {
                    detailImgUrl = moduleInfo.getDetailImgUrl();
                    detailTitle = moduleInfo.getDetailTitle();
                    detailDesc = moduleInfo.getDetailDesc();
                }
                drawTopBgView(detailImgUrl, detailTitle, detailDesc);
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });
    }

    private void initAudioColumn() {
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2, RecyclerView.VERTICAL, false));
        audioColumnAdapter = new BaseRecyclerViewAdapter<>(mContext, new ArrayList<>()) {
            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_aggregation_audio_column;
            }

            @Override
            protected void bindItem(LayoutItemAggregationAudioColumnBinding dataBinding, AlbumBean itemBean, int position) {
                if (isHasData(itemBean)) {
                    dataBinding.gas.setVisibility(View.VISIBLE);
                    dataBinding.imgPlay.setVisibility(View.VISIBLE);
                    dataBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvContent1.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvContent2.setBackgroundColor(getResources().getColor(R.color.transport));
                    Glide.with(mContext)
                            .setDefaultRequestOptions(ImageLoader.getOptions())
                            .load(itemBean.getAlbumImgUrl())
                            .into(dataBinding.imgShow);
                    dataBinding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                    dataBinding.tvTitle.setText(itemBean.getAlbumName());
                    dataBinding.tvSubtitle.setText(itemBean.getAlbumSimpleDesc());
                    dataBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_45));
                } else {
                    dataBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.transport));
                }

                List<MusicListBean> libraryStoreList = itemBean.getLibraryStoreList();
                if (libraryStoreList != null) {
                    int size = libraryStoreList.size();

                    if (size >= 1) {
                        MusicListBean musicListBean = libraryStoreList.get(size - 1);
                        String content1 = musicListBean.getName();
                        String batch = musicListBean.getBatch();
                        /*if (batch != null) {*/
                        content1 = /*batch + " " +*/ content1;
                        // 0518 需求：序号及标签加粗
                        // 创建一个 SpannableString 对象
                        SpannableString spannableString = new SpannableString(content1);
                        // 创建一个 StyleSpan 对象，设置为粗体
                        StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);

                        // 找到需要加粗的文字的起始位置和结束位置
                        int startIndex = 0;
                        int endIndex = content1.indexOf(" ");
                        // 将 StyleSpan 应用到指定的文本范围
                        spannableString.setSpan(boldStyleSpan, startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dataBinding.tvContent1.setText(spannableString);
                        /*}*/
                    }

                    if (size >= 2) {
                        MusicListBean musicListBean = libraryStoreList.get(size - 2);
                        String content2 = musicListBean.getName();
                        String batch = musicListBean.getBatch();
//                        if (batch != null) {
                        content2 = /*batch + " " +*/ content2;
                        // 0518 需求：序号及标签加粗
                        // 创建一个 SpannableString 对象
                        SpannableString spannableString = new SpannableString(content2);
                        // 创建一个 StyleSpan 对象，设置为粗体
                        StyleSpan boldStyleSpan = new StyleSpan(Typeface.BOLD);

                        // 找到需要加粗的文字的起始位置和结束位置
                        int startIndex = 0;
                        int endIndex = content2.indexOf(" ");
                        // 将 StyleSpan 应用到指定的文本范围
                        spannableString.setSpan(boldStyleSpan, startIndex, endIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        dataBinding.tvContent2.setText(spannableString);
//                        }
                    }
                }

//                ImageLoader.load(mContext, itemBean.getAlbumImgUrl(), dataBinding.imgShow);

                dataBinding.getRoot().setOnClickListener(view -> {
                    JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.blog, itemBean.getId());
                });

                dataBinding.imgPlay.setOnClickListener(v -> {
                    MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                    if (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) { // 操作是当前的专辑
                        if (MusicPlayManager.getInstance().isPlaying()) {
                            MusicPlayManager.getInstance().pauseMusic();
                        } else {
                            MusicPlayManager.getInstance().playMusic();
                        }
                    } else { // 不是当前的专辑 直接切换专辑
                        MusicPlayManager.getInstance()
                                .addToPlaylistAndPlay(String.valueOf(itemBean.getId()),
                                        itemBean.getLibraryStoreList());
                        MusicPlayManager.getInstance().playMusic();
                    }
                });

                LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, bean -> {
                    MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                    ImageLoader.load(mContext, MusicPlayManager.getInstance().isPlaying()
                            && String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))
                            ? R.mipmap.ic_pause_small : R.mipmap.ic_play_small, dataBinding.imgPlay);
                });
            }
        };
        recyclerView.setAdapter(audioColumnAdapter);
        audioColumnAdapter.setData(audioNullBeanList);
//        mDataBinding.recycler.addItemDecoration(new ItemSpacingDecoration(DensityUtils.dp2Px(mContext, 20)));

        ApiClient.getInstance().getAudioColumnAggregate().subscribe(new BaseObserver<BaseBean<ModuleBean>>() {
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                // LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                List<AlbumBean> albumBeanList = data.getModuleData();
                audioColumnAdapter.setData(albumBeanList);

                // 设置顶部
                ModuleInfoBean moduleInfo = data.getModuleInfo();
                String detailImgUrl = null, detailTitle = null, detailDesc = null;
                if (moduleInfo != null) {
                    detailImgUrl = moduleInfo.getDetailImgUrl();
                    detailTitle = moduleInfo.getDetailTitle();
                    detailDesc = moduleInfo.getDetailDesc();
                }

                drawTopBgView(detailImgUrl, detailTitle, detailDesc);
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });
    }

    private void init3dImmersionSound() {
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, RecyclerView.VERTICAL, false));
        immersion3dAdapter = new BaseRecyclerViewAdapter<>(mContext, new ArrayList<>()) {
            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_aggregation_3d_immersion;
            }

            @Override
            protected void bindItem(LayoutItemAggregation3dImmersionBinding dataBinding, AlbumBean itemBean, int position) {
                if (isHasData(itemBean)) {
                    dataBinding.gas.setVisibility(View.VISIBLE);
                    dataBinding.imgPlay.setVisibility(View.VISIBLE);
                    dataBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvSubtitle.setBackgroundColor(getResources().getColor(R.color.transport));
                    dataBinding.tvLabel.setLabelImgPath(itemBean.getRecommendLabel());
                    dataBinding.tvTitle.setText(itemBean.getAlbumName());
                    dataBinding.tvSubtitle.setText(SPUtils.formatAuther(itemBean.getSingerList()));
                    dataBinding.getRoot().postDelayed(() -> Glide.with(dataBinding.getRoot())
//                            .setDefaultRequestOptions(ImageLoader.getOptions())
                            .load(itemBean.getAlbumImgUrl())
//                        .centerCrop()
                            .into(dataBinding.imgShow),20);
                    dataBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.white_alpha_45));
                } else {
                    dataBinding.tvSubtitle.setTextColor(getResources().getColor(R.color.transport));
                }

                // 如果是车机 需要设置左右margin
                mDataBinding.getRoot().postDelayed(() ->
                        SPUtils.setViewHeight(dataBinding.imgShow, dataBinding.imgShow.getWidth()), 10);

                dataBinding.getRoot().setOnClickListener(v -> JumpUtils.jumpToDetail(AlbumDetailsFragment.Type.immersionMusic, itemBean.getId()));


                dataBinding.imgPlay.setOnClickListener(v -> {
                    MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                    if (String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))) { // 操作是当前的专辑
                        if (MusicPlayManager.getInstance().isPlaying()) {
                            MusicPlayManager.getInstance().pauseMusic();
                        } else {
                            MusicPlayManager.getInstance().playMusic();
                        }
                    } else { // 不是当前的专辑 直接切换专辑
                        MusicPlayManager.getInstance()
                                .addToPlaylistAndPlay(String.valueOf(itemBean.getId()),
                                        itemBean.getLibraryStoreList());
                        MusicPlayManager.getInstance().playMusic();
                    }
                });

                LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, bean -> {
                    MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
                    ImageLoader.load(mContext, MusicPlayManager.getInstance().isPlaying()
                            && String.valueOf(itemBean.getId()).equals(MusicPlayManager.getInstance().getCurrentAlbumId())
                            && (value != null && TextUtils.equals(value.getTypeName(), itemBean.getTypeName()))
                            ? R.mipmap.ic_pause_small : R.mipmap.ic_play_small, dataBinding.imgPlay);
                });

                /**
                 * 会变形😂
                 */
//                if (SPUtils.isPad()) {
//                    dataBinding.getRoot().setScaleX(.9f);
//                }
            }
        };
        recyclerView.setAdapter(immersion3dAdapter);
        immersion3dAdapter.setData(immersionNullBeanList);
//        mDataBinding.recycler.addItemDecoration(new ItemSpacingDecoration(DensityUtils.dp2Px(mContext, 20)));

        ApiClient.getInstance().getImmersionAggregate().subscribe(new BaseObserver<BaseBean<ModuleBean>>() {
            @Override
            protected void onSuccess(BaseBean<ModuleBean> responseData) {
                // LogUtils.d("data222 " + responseData.toString());
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                ModuleBean data = responseData.getData();
                if (data == null) {
                    LogUtils.d("data is null...");
                    return;
                }

                List<AlbumBean> albumBeanList = data.getModuleData();
                immersion3dAdapter.setData(albumBeanList);

                // 设置顶部

                ModuleInfoBean moduleInfo = data.getModuleInfo();
                String detailImgUrl = null, detailTitle = null, detailDesc = null;
                if (moduleInfo != null) {
                    detailImgUrl = moduleInfo.getDetailImgUrl();
                    detailTitle = moduleInfo.getDetailTitle();
                    detailDesc = moduleInfo.getDetailDesc();
                }
                drawTopBgView(detailImgUrl, detailTitle, detailDesc);
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
            }
        });
    }

    @Override
    protected void initListener() {
        mDataBinding.imgBack.setOnClickListener(v -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));
    }

    @Override
    protected void initObserver() {

    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_new_aggregation;
    }

    @Override
    protected void setLevel() {
        level = 1;
    }

    private void judgeIfScale(ViewDataBinding dataBinding, int position) {
        if (SPUtils.isPad()) {
            // 获取recycleview的宽度
            int width = recyclerView.getWidth();
            // 理应 宽度为(recycle的宽度-4*间距)/3
            int itemWidth = (width - 4 * DensityUtils.dp2Px(mContext, 20)) / 3;
            float scaleFactor = (itemWidth * 1.f) / (DensityUtils.dp2Px(mContext, 248) * 1.f);
//            LogUtils.d("scale factor : " + scaleFactor
//                    + " width 1 : " + itemWidth
//                    + " width 2 : " + dataBinding.getRoot().getWidth());

            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            int row = position / (layoutManager != null ? layoutManager.getSpanCount() : 3);
            // 原本ui里的高度
            int normalHeight = 0;
            switch (aggregationType) {
                case ITEM_TYPE_AUDIO_COLUMN:
                    normalHeight = DensityUtils.dp2Px(mContext, 152);
                    break;
                case ITEM_TYPE_3D_IMMERSION_SOUND:
                    normalHeight = DensityUtils.dp2Px(mContext, 216);
                    break;
                default:
                    normalHeight = DensityUtils.dp2Px(mContext, 333);
                    break;
            }


            dataBinding.getRoot().setTranslationY(-row * (normalHeight * (1 - scaleFactor)));
            dataBinding.getRoot().setScaleX(scaleFactor);
            dataBinding.getRoot().setScaleY(scaleFactor);

            dataBinding.getRoot().setPivotY(0);
        } else {
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) dataBinding.getRoot().getLayoutParams();
            if (layoutParams != null) {
                layoutParams.leftMargin = DensityUtils.dp2Px(mContext, 20);
                layoutParams.topMargin = DensityUtils.dp2Px(mContext, 20);
                dataBinding.getRoot().setLayoutParams(layoutParams);
            }
        }
    }

    @Override
    public void onNetworkChanged(boolean isConnected, NetworkType type) {
        if (isConnected) {
            mDataBinding.checknet.setVisibility(View.GONE);
            loadNullData();
            initRecycler();
        }
    }

    public class AggregationAdapter extends BaseMultiItemQuickAdapter<Aggregation, BaseDataBindingHolder> {
        public static final int HEADER_VIEW = 0x10001997;
        public static final int ONE_VIEW = 0x10001998;

        public AggregationAdapter(@Nullable List<Aggregation> data) {
            super(data);

            addItemType(HEADER_VIEW, R.layout.layout_view_top_bg_with_play);
            addItemType(ONE_VIEW, R.layout.layout_recycler);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder holder, Aggregation aggregation) {
            if (aggregation.isHeader) {
                // 设置header
                bindHeader((LayoutViewTopBgWithPlayBinding) holder.getDataBinding(), aggregation);
            } else {
                recyclerView = (RecyclerView) holder.findView(R.id.inner_recycler);
                recyclerView.setMinimumHeight(DensityUtils.dp2Px(mContext, SPUtils.isPad() ? 1030 : 700));
                recyclerView.setPadding(recyclerView.getPaddingLeft(), recyclerView.getPaddingTop(), recyclerView.getPaddingTop(),
                        DensityUtils.dp2Px(mContext, 73 + 10 - 30));

                // 底部
                switch (aggregationType) {
                    case ITEM_TYPE_AUDIO_COLUMN:
                        initAudioColumn();
                        mTopBarBinding.tvDynaudio.setText(R.string.audio_column);
                        break;
                    case ITEM_TYPE_3D_IMMERSION_SOUND:
                        init3dImmersionSound();
                        mTopBarBinding.tvDynaudio.setText(R.string.immersion_special);
                        break;
                    default: // ITEM_TYPE_BLACK_PLASTIC
                        initBlackPlastic();
                        mTopBarBinding.tvDynaudio.setText(R.string.black_glue);
                        break;
                }

                // 除了header 上移30dp
                int offset = DensityUtils.dp2Px(mContext, -30);
//                LogUtils.d("offset : " + offset);
                holder.getDataBinding().getRoot().setTranslationY(offset);
            }
        }

        private void bindHeader(LayoutViewTopBgWithPlayBinding dataBinding, Aggregation aggregation) {
            // header 内容待底部数据获取以后再设置
            mTopBgBinding = dataBinding;
            mTopBgBinding.llPlayAll.setVisibility(View.INVISIBLE);
        }
    }

    class Aggregation implements MultiItemEntity {
        int aggregationType;

        boolean isHeader;

        public Aggregation(int aggregationType, boolean isHeader) {
            this.aggregationType = aggregationType;
            this.isHeader = isHeader;
        }

        @Override
        public int getItemType() {
            return isHeader ? HEADER_VIEW : ONE_VIEW;
        }
    }
}
