package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.adapter.AudioListAdapter;
import com.byd.dynaudio_app.adapter.IItemOnClickListener;
import com.byd.dynaudio_app.adapter.MusicListAdapter;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.custom.SingerInfoView;
import com.byd.dynaudio_app.custom.TabLayout;
import com.byd.dynaudio_app.custom.smart_refresh_layout.HHeader;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutFragmentAlbumDetailsBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarTitleMidBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgAlbumBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.network.NetworkType;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlbumDetailsFragment extends BaseFragment<LayoutFragmentAlbumDetailsBinding, BaseViewModel> {

    public enum Type {
        blackGlue, blog, immersionMusic
    }

    private Type mType;
    private String mAlbumId = "1";

    public void setType(Type type) {
        this.mType = type;
    }

    public void setAlbumId(String albumId) {
        this.mAlbumId = albumId;
    }

    private String mTabText;//tab标题 节目/曲目
    private boolean mIsPositiveSequence = true;// 当前排序方式(默认正序)
    private final ArrayList<MusicListBean> mMusicList = new ArrayList<>();
    private MusicListAdapter mMusicAdapter;
    private AudioListAdapter mAudioProgramAdapter;
    private LayoutViewTopBgAlbumBinding mTopBgBinding;  // 顶部背景
    private LayoutViewTopBarTitleMidBinding mTopBarBinding;  // 顶部前台

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.imgBack);
        //loadNullData();
        initSwipe();
        mDataBinding.topBg.addView(getTopBg());
        mDataBinding.topBar.addView(getTopBar());
        if (!NetworkObserver.getInstance().isConnectNormal(mContext)) {//没网
            mDataBinding.checknet.setVisibility(View.VISIBLE);
        } else {
            mDataBinding.checknet.setVisibility(View.GONE);
        }
        checkTheType();
        initRecyclerView();
    }

    private void loadNullData() {
        mMusicList.clear();
        MusicListBean musicListBean = new MusicListBean();
        for (int i = 0; i < 5; i++) {
            mMusicList.add(musicListBean);
        }
    }


    private void checkTheType() {
        switch (mType) {
            case blackGlue:
                mTabText = "曲目列表 ";
                initMusicView();
                requestBlackGlue();
                break;
            case immersionMusic:
                mTabText = "曲目列表 ";
                initMusicView();
                requestImmersionMusic();
                break;
            case blog:
                mTabText = "节目列表 ";
                initAudioView();
                requestBlog();
                break;
        }
    }


    @Override
    public void onNetworkChanged(boolean isConnected, NetworkType type) {
        super.onNetworkChanged(isConnected, type);
        if (isConnected) {
            mDataBinding.checknet.setVisibility(View.GONE);
            checkTheType();
            initRecyclerView();
        }
    }

    private void initMusicView() {
        mDataBinding.tabLayout.setFirstText(mTabText);
        mDataBinding.tabLayout.setSecondText("专辑介绍");
        mDataBinding.tabLayout.selectFirstTab();
        mDataBinding.txtSequence.setVisibility(View.GONE);// 音乐类不需要排序方式
        mDataBinding.icSequence.setVisibility(View.GONE);
    }

    private void initAudioView() {
        mDataBinding.tabLayout.setFirstText(mTabText);
        mDataBinding.tabLayout.setSecondText("栏目介绍");
        mTopBgBinding.tvTagFinish.setVisibility(View.VISIBLE);
        mDataBinding.tabLayout.selectFirstTab();
    }

    private void requestBlackGlue() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().blackGlueMusic(mAlbumId, userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<AlbumBean> bean) {
                if (bean.getData() != null) setViewData(bean.getData());
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void requestBlog() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().blog(mAlbumId, 1, userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<AlbumBean> bean) {
                if (bean.getData() != null) setViewData(bean.getData());
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void requestImmersionMusic() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().immersionMusic(mAlbumId, userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<AlbumBean> bean) {
                if (bean.getData() != null) setViewData(bean.getData());
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void setViewData(AlbumBean data) {
        if (isHasData(data)) {
            mTopBarBinding.llPlayAll.setVisibility(View.VISIBLE);
            mTopBarBinding.tvDynaudio.setVisibility(View.VISIBLE);
            mTopBgBinding.llPlayAll.setVisibility(View.VISIBLE);
            mTopBgBinding.tvTitle.setBackgroundColor(getResources().getColor(R.color.transport));
            mTopBgBinding.tvAuthor.setBackgroundColor(getResources().getColor(R.color.transport));
            mTopBgBinding.tvContent.setBackgroundColor(getResources().getColor(R.color.transport));
            mTopBgBinding.tvTitle.setTextColor(getResources().getColor(R.color.white_alpha_80));
            mTopBgBinding.tvAuthor.setTextColor(getResources().getColor(R.color.white_alpha_45));
            mTopBgBinding.tvContent.setTextColor(getResources().getColor(R.color.white_alpha_45));
            mTopBgBinding.tvContent.setText(data.getAlbumSimpleDesc());
            mTopBarBinding.tvDynaudio.setText(data.getAlbumName());
            mTopBgBinding.tvTitle.setText(data.getAlbumName());
            // 有聲才顯示左上角的label
            mTopBgBinding.tvLabel.setLabelImgPath(data.getRecommendLabel());
            mTopBgBinding.tvLabel.setVisibility(TextUtils.equals(data.getTypeName(), "ys") ? View.VISIBLE : View.GONE);
            if (data.getSingerList() != null && data.getSingerList().get(0) != null) {
                mTopBgBinding.tvAuthor.setText(data.getSingerList().get(0).getAutherName());
            }
            // LogUtils.d("name : " + data.getAlbumName());
            ImageLoader.load(getContext(), data.getAlbumImgUrl(), mTopBgBinding.imgAvatar);
        }
        mMusicList.clear();
        List<MusicListBean> list = data.getLibraryStoreList();
        if (!UserController.getInstance().isLoginStates()) overlayData(list);
        mMusicList.addAll(list);

        // 如果歌曲数目为0 不显示列表 显示暂无内容
        mDataBinding.tvEmptyData.setVisibility(mMusicList != null && mMusicList.size() > 0 ? View.INVISIBLE : View.VISIBLE);

        mDataBinding.tabLayout.setFirstText(mTabText + mMusicList.size());
        if (mType == Type.blog) {
            mAudioProgramAdapter.notifyDataSetChanged();
        } else {
            mMusicAdapter.notifyDataSetChanged();
        }
        setAlbumDetail(data);
    }

    /**
     * 查询返回的数据中是否有未登录用户的收藏歌曲 覆盖原有的collectFlag值
     */
    private void overlayData(List<MusicListBean> list) {
        if (mType == Type.blog) {
            for (MusicListBean bean : list) {
                boolean collect = DBController.queryAudioCollect(mContext, bean);
                bean.setCollectFlag(collect);
            }
        } else {
            for (MusicListBean bean : list) {
                boolean collect = DBController.queryMusicCollect(mContext, bean);
                bean.setCollectFlag(collect);
            }
        }
    }

    private void setAlbumDetail(AlbumBean bean) {
        mDataBinding.txtIntroduction.setText(bean.getAlbumDesc());// 这里不用一句话介绍
//        mDataBinding.txtDetailedIntroduction.setText(bean.getAlbumDesc());

//        if (mType == Type.blog) {
//            mDataBinding.txtSingerTitle.setText("节目主播");
//        } else {
//            mDataBinding.txtSingerTitle.setText("艺术家");
//        }
//        if (bean.getSingerList() != null && bean.getSingerList().get(0) != null) {
//            SingerBean singerBean = bean.getSingerList().get(0);
//            SingerInfoView singerView = new SingerInfoView(getContext());
//            singerView.setImgAvatar(singerBean.getHeadUrl());
//            singerView.setTxtSinger(singerBean.getAutherName());
//            singerView.setTxtSingerInfo(singerBean.getAutherDesc());
//            mDataBinding.llSinger.addView(singerView);
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRecyclerView() {
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext));
        if (mType == Type.blog) {
            mAudioProgramAdapter = new AudioListAdapter(mContext, mMusicList);
            mAudioProgramAdapter.setShowCollectBtn(true);
            mDataBinding.recycler.setAdapter(mAudioProgramAdapter);
        } else {
            mMusicAdapter = new MusicListAdapter(mContext, mMusicList);
            mMusicAdapter.setShowCollectBtn(true);
            mDataBinding.recycler.setAdapter(mMusicAdapter);
        }
        mDataBinding.recycler.setOnTouchListener((view1, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
//                    mDataBinding.srl.finishLoadMore();

                    mDataBinding.getRoot().postDelayed(() -> {
                        LogUtils.d("total dy : " + totalDy);
                        if (totalDy < DensityUtils.dp2Px(mContext, 60)) {
                            mDataBinding.llScroll.scrollBy(0, (int) -totalDy);
                            totalDy = 0;
                        }
                    }, 400);
                    break;
            }
            return false;
        });

        initRecyclerClickListener();
    }

    private void initRecyclerClickListener() {
        if (mAudioProgramAdapter != null) {
            mAudioProgramAdapter.setClickListener(new IItemOnClickListener() {
                @Override
                public void onClick(int position) {
                    MusicListBean bean = mMusicList.get(position);
                    MusicPlayManager.getInstance().playMusic(mMusicList, bean, position, mAlbumId);
                }

                @Override
                public void onSelect(int position, boolean isSelect) {

                }

                @Override
                public void onDeleteClick(int position) {

                }
            });
        }
        if (mMusicAdapter != null) {
            mMusicAdapter.setClickListener(new IItemOnClickListener() {
                @Override
                public void onClick(int position) {
                    MusicListBean bean = mMusicList.get(position);
                    MusicPlayManager.getInstance().playMusic(mMusicList, bean, position, mAlbumId);
                }

                @Override
                public void onSelect(int position, boolean isSelect) {

                }

                @Override
                public void onDeleteClick(int position) {

                }
            });
        }
    }

    private float totalDy = 0;


    @Override
    protected void initListener() {
        // 监听滑动进度 更新显隐
        mDataBinding.llScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            // 计算top_bg的可见性百分比
            int scrollY = mDataBinding.llScroll.getScrollY();
            totalDy = scrollY;

            // 带有播放按钮的详情页 0-55dp 透明度0 ; 60dp 满透明度
            int alpha0Pos = (DensityUtils.dp2Px(mContext, 55));
            int alpha1Pos = DensityUtils.dp2Px(mContext, 60);
            float alpha;
            if (scrollY < alpha0Pos) {
                alpha = 0.f;
            } else if (scrollY > alpha1Pos) {
                alpha = 1.f;
            } else {
                alpha = (totalDy - alpha0Pos) / (alpha1Pos - alpha0Pos);
            }

            if (alpha < 0) alpha = 0;
            if (alpha > 1.f) alpha = 1.f;
            LogUtils.d("dy : " + totalDy + " alpha : " + alpha);
            mDataBinding.topBar.setAlpha(alpha);
        });
        mDataBinding.imgBack.setOnClickListener(v -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));
        mDataBinding.tabLayout.addOnTabSelectedListener(new TabLayout.IOnTabSelectedListener() {
            @Override
            public void onFirstSelected() {
                mDataBinding.llRecycler.setVisibility(View.VISIBLE);
                mDataBinding.llIntroduce.setVisibility(View.GONE);
                if (mType == Type.blog) {
                    mDataBinding.txtSequence.setVisibility(View.VISIBLE);
                    mDataBinding.icSequence.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSecondSelected() {
                mDataBinding.llRecycler.setVisibility(View.GONE);
                mDataBinding.llIntroduce.setVisibility(View.VISIBLE);
                mDataBinding.txtSequence.setVisibility(View.GONE);
                mDataBinding.icSequence.setVisibility(View.GONE);
            }
        });
        mDataBinding.icSequence.setOnClickListener(view -> changeListOrder());
        mDataBinding.txtSequence.setOnClickListener(view -> changeListOrder());

        //播放按钮
        mTopBarBinding.llPlayAll.setOnClickListener(v -> {
            MusicPlayManager.getInstance().addToPlaylistAndPlay(mMusicList);
        });

        mTopBgBinding.llPlayAll.setOnClickListener(v -> {
            MusicPlayManager.getInstance().playMusic(mMusicList, null, 0, mAlbumId);
        });
    }

    @Override
    protected void initObserver() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe(this, musicPlayerBean -> {
            boolean playing = MusicPlayManager.getInstance().isPlaying() && mAlbumId != null && mAlbumId.equals(MusicPlayManager.getInstance().getCurrentAlbumId());
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
        return R.layout.layout_fragment_album_details;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void changeListOrder() {
        mIsPositiveSequence = !mIsPositiveSequence;
        if (mIsPositiveSequence) {
            mDataBinding.txtSequence.setText("正序");
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_positive_sequence, null);
            mDataBinding.icSequence.setBackground(drawable);
        } else {
            mDataBinding.txtSequence.setText("倒序");
            Drawable drawable = getResources().getDrawable(R.mipmap.ic_reverse_order, null);
            mDataBinding.icSequence.setBackground(drawable);
        }
        if (!mMusicList.isEmpty()) {
            Collections.reverse(mMusicList);
            mAudioProgramAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 返回一个顶部收起时的view
     */
    protected View getTopBar() {
        mTopBarBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bar_title_mid, null, false);
        TouchUtils.bindClickItem(mTopBarBinding.llPlayAll);
        return mTopBarBinding.getRoot();
    }

    /**
     * 返回一个顶部展开时的view
     */
    protected View getTopBg() {
        mTopBgBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bg_album, null, false);
        TouchUtils.bindClickItem(mTopBgBinding.llPlayAll);
        return mTopBgBinding.getRoot();
    }

    private void initSwipe() {
        mDataBinding.swipe.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() { // 上拉加载更多
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                mDataBinding.swipe.finishLoadMore(10);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mDataBinding.swipe.setEnableRefresh(true);
                initRecyclerView();
                mDataBinding.swipe.finishRefresh(10);
            }
        });
        mDataBinding.swipe.setEnableLoadMore(false);//是否启用上拉加载功能
        mDataBinding.swipe.setEnableRefresh(true);
        mDataBinding.swipe.setRefreshHeader(new HHeader(mContext));
    }
}