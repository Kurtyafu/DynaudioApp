package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.AlbumBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.ModuleBean;
import com.byd.dynaudio_app.bean.response.ModuleInfoBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.databinding.LayoutItemGoldenBinding;
import com.byd.dynaudio_app.databinding.LayoutViewHifiCardBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.List;

public class HifiCard extends BaseView<LayoutViewHifiCardBinding> {

    private BaseRecyclerViewAdapter<MusicListBean, LayoutItemGoldenBinding> adapter;
    private String id;// 专辑id
    private List<MusicListBean> data;

    public HifiCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init(AttributeSet attrs) {
        TouchUtils.bindClickItem(mDataBinding.imgPlay);

        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return super.canScrollVertically();
            }
        });

        adapter = new BaseRecyclerViewAdapter<>(mContext, null) {

            @Override
            protected int getLayoutId() {
                return R.layout.layout_item_golden;
            }

            @Override
            protected void bindItem(LayoutItemGoldenBinding dataBinding, MusicListBean itemBean, int position) {
                LogUtils.d("bind item : " + position);
                dataBinding.tvTitle.setText((itemBean.getName()));
                dataBinding.tvSinger.setText(SPUtils.formatAuther(itemBean.getSingerList()));
                dataBinding.tvLabel.setLabelImgPath(itemBean.getQualityUrl());

                dataBinding.imgAni.setVisibility(MusicPlayManager.getInstance().getCurrentItem() != null && MusicPlayManager.getInstance().getCurrentItem().getSpecialId() == itemBean.getSpecialId() ? View.VISIBLE : View.GONE);


                dataBinding.getRoot().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("singer width : " + dataBinding.tvSinger.getWidth()
                                + " max width : " + dataBinding.tvSinger.getMaxWidth());
                        if (dataBinding.tvSinger.getWidth() >= dataBinding.tvSinger.getMaxWidth()
                                && !TextUtils.isEmpty(itemBean.getQualityUrl())) {
                            // 歌手过长 并且有标签的情况下 不显示歌手这个了
                            dataBinding.tvSinger.setVisibility(GONE);
                            dataBinding.tvTitle.setMaxWidth(DensityUtils.dp2Px(mContext, 272));
                        }
                    }
                }, 100);

                LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                    boolean isHighLight = bean != null && bean.getSpecialId() == (itemBean.getSpecialId())
                            /*   && TextUtils.equals(bean.getTypeName(), itemBean.getTypeName())*/;
                    if (isHighLight /*&& !isDragPlayList && System.currentTimeMillis() - lastDragPlayListTime > 2_000*/) {
                        smoothScrollTo(position);
                    }
                    // 序号
                    dataBinding.imgAni.setVisibility(isHighLight ? View.VISIBLE : View.GONE);
                    // 文字
                    dataBinding.tvTitle.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#CCFFFFFF"));
                    dataBinding.tvSinger.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#73FFFFFF"));
                });
            }
        };
        mDataBinding.recycler.setAdapter(adapter);
//                            hifiCardBinding.recycler.setOnTouchListener((v, event) -> true);


        // 点击播放按钮 添加到播放列表
        mDataBinding.imgPlay.setOnClickListener(v -> {
            MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();

            if (id.equals(MusicPlayManager.getInstance().getCurrentAlbumId()) && (value != null && data != null && TextUtils.equals(value.getTypeName(), data.get(0).getTypeName()))) { // 操作是当前的专辑
                if (MusicPlayManager.getInstance().isPlaying()) {
                    MusicPlayManager.getInstance().pauseMusic();
                } else {
                    MusicPlayManager.getInstance().playMusic();
                }
            } else { // 不是当前的专辑 直接切换专辑
                MusicPlayManager.getInstance().addToPlaylistAndPlay(id, data);
                MusicPlayManager.getInstance().playMusic();
            }
        });

        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class).observe((LifecycleOwner) mContext, musicPlayerBean -> {
            MusicListBean value = LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).getValue();
            ImageLoader.load(mContext, MusicPlayManager.getInstance().isPlaying()
                    && TextUtils.equals(MusicPlayManager.getInstance().getCurrentAlbumId(), id)
                    && (value != null && data != null
                    && TextUtils.equals(value.getTypeName(), data.get(0).getTypeName())) ? R.drawable.img_pause_with_circle : R.drawable.img_play, mDataBinding.imgPlay);
        });

        mDataBinding.recycler.setOnTouchListener((v, event) -> {
            return HifiCard.super.onTouchEvent(event);
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_hifi_card;
    }

    public void setTopImgPath(@NonNull String path) {
        if (TextUtils.isEmpty(path)) {
            mDataBinding.imgPlay.setVisibility(GONE);
        } else {
            mDataBinding.imgPlay.setVisibility(VISIBLE);
        }
        Glide.with(mContext).load(path).centerInside().into(mDataBinding.imgTitleBg);
    }

    public void setLeftData(@NonNull List<MusicListBean> data, String id) {
        adapter.setData(data);
        this.id = id;
        this.data = data;
        mDataBinding.recycler.scrollBy(0, 1);
        mDataBinding.recycler.scrollBy(0, -1);

        MusicListBean currentItem = MusicPlayManager.getInstance().getCurrentItem();
        if (currentItem != null && TextUtils.equals(currentItem.getTypeName(), "zx")) {
            // LogUtils.d("is in zx...");
            // 保证当前在播放甄选的情况下 对应的item能显示出来

            mDataBinding.recycler.scrollToPosition(
                    MusicPlayManager.getInstance().getPlayer().getCurrentMediaItemIndex());
        }
    }

    /**
     * 0520 暂时只做高亮 滚动效果先不做 需求没确定 后续需要放出上面和下面注释的部分即可
     */
    public void smoothScrollTo(int position) {
        CustomSmoothScroller smoothScroller = new CustomSmoothScroller(mContext);
        smoothScroller.setTargetPosition(position); // 设置要滚动到的目标位置
        mDataBinding.recycler.getLayoutManager().startSmoothScroll(smoothScroller);
    }
}
