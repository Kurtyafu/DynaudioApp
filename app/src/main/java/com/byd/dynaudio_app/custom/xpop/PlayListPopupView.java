package com.byd.dynaudio_app.custom.xpop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ViewUtils;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.databinding.LayoutFullPlayListBinding;
import com.byd.dynaudio_app.databinding.LayoutItemPlayListInPlayfragmentBinding;
import com.byd.dynaudio_app.databinding.LayoutViewPlaylistBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.BottomPopupView;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放列表的弹窗view
 */
public class PlayListPopupView extends BottomPopupView {
    private Context mContext;
    private LayoutViewPlaylistBinding mDataBinding;

    private PlayListAdapter playListAdapter;
    private boolean isDragPlayList = false;
    private long lastDragPlayListTime;

    public PlayListPopupView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void addInnerContent() {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_playlist, bottomPopupContainer, false);
        bottomPopupContainer.addView(mDataBinding.getRoot());

        TouchUtils.bindClickItem(mDataBinding.imgCancel);

        initView();
        initObserver();
        initListener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        // 播放列表初始化
        mDataBinding.recyclerPlayList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        playListAdapter = new PlayListAdapter(R.layout.layout_item_play_list_in_playfragment);
        mDataBinding.recyclerPlayList.setAdapter(playListAdapter);
        mDataBinding.recyclerPlayList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                outRect.top = DensityUtils.dp2Px(mContext, 20);
                if (position == parent.getAdapter().getItemCount() - 1) {
                    outRect.bottom = DensityUtils.dp2Px(mContext, 20);
                }
            }
        });
        mDataBinding.recyclerPlayList.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    isDragPlayList = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isDragPlayList = false;
                    lastDragPlayListTime = System.currentTimeMillis();
                    break;
            }
            return false;
        });
    }

    private void initObserver() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                .observe((LifecycleOwner) mContext, player -> {
                    if (player != null) {
                        List<MusicListBean> playList = player.getPlayList();
                        if (playList != null && TouchUtils.isDiffData(playListAdapter.getData(), playList)) {
                            playListAdapter.setList(playList);
                        }
                    }
                });
    }

    private void initListener() {
        mDataBinding.imgCancel.setOnClickListener(v -> dismiss());
    }

    @Override
    public BasePopupView show() {
        BasePopupView view = super.show();

        return view;
    }

    @Override
    protected int getPopupHeight() {
        // 返回屏幕高度的2/3
        // 获取屏幕高度
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;

        // 计算弹出窗口高度（屏幕高度的 1/3）
        int popupHeight = (int) (screenHeight / 3.f * 2.f);
        return DensityUtils.dp2Px(mContext, 624);
    }

    class PlayListAdapter extends BaseQuickAdapter<MusicListBean, BaseDataBindingHolder> {

        public PlayListAdapter(int layoutResId) {
            super(layoutResId);
        }

        @Override
        protected void convert(@NonNull BaseDataBindingHolder baseDataBindingHolder, MusicListBean itemBean) {
            LayoutItemPlayListInPlayfragmentBinding dataBinding = (LayoutItemPlayListInPlayfragmentBinding) baseDataBindingHolder.getDataBinding();
            int position = getItemPosition(itemBean);
            LogUtils.d("bind item : " + position);
            // 给item设置一个tag 解决收藏多个的问题 bugId : 487
            dataBinding.getRoot().setTag(position);

            dataBinding.tvNum.setText(String.valueOf(position + 1));
            dataBinding.tvTitle.setText(itemBean.getName());
            dataBinding.tvTitle.setMaxWidth(getWidth() - DensityUtils.dp2Px(mContext, 69 + 134));
            // LogUtils.d("tv max width : " + dataBinding.tvTitle.getMaxWidth());
            List<SingerBean> singerList = itemBean.getSingerList();
            if (singerList != null && singerList.size() > 0) {
                itemBean.setSinger(SPUtils.formatAuther(singerList));
            }
            dataBinding.tvSubtitle.setText(itemBean.getSinger());// 歌手
            dataBinding.tvLabel.setLabelImgPath(itemBean.getQualityUrl());

            // item点击就播放当前音乐
            dataBinding.getRoot().setOnClickListener(v -> {
//                    startPointAnimation(false, true);
                FullPlayerPopupView fullPlayer = PlayerVisionManager.getInstance().getFullPlayer();
                if (fullPlayer != null && fullPlayer.isShow()) {
                    fullPlayer.getXBanner().setBannerCurrentItem(position, false);
                } else {
                    MusicPlayManager.getInstance().playMusic(getData(), itemBean, position, MusicPlayManager.getInstance().getCurrentAlbumId());
                }
//                    mDataBinding.vpRecord.setBannerCurrentItem(position);
//                    startPlayBtnChange();// 屏蔽播放按钮变化1s
            });

            // 收藏按钮
            dataBinding.imgCollect.setImageResource(itemBean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);
            dataBinding.imgCollect.setOnClickListener(v -> MusicPlayManager.getInstance().setCollect(itemBean, !itemBean.isCollectFlag()));

            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                boolean isHighLight = bean != null && bean.getSpecialId() == (itemBean.getSpecialId());

                if (isHighLight && !isDragPlayList && System.currentTimeMillis() - lastDragPlayListTime > Long.MAX_VALUE) {
                    mDataBinding.recyclerPlayList.smoothScrollToPosition(position);
                }
                // 序号
                dataBinding.tvNum.setVisibility(isHighLight ? View.INVISIBLE : View.VISIBLE);
                dataBinding.imgNum.setVisibility(isHighLight ? View.VISIBLE : View.INVISIBLE);
                // 文字
                dataBinding.tvTitle.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#CCFFFFFF"));
                dataBinding.tvTitle.setTypeface(null, isHighLight ? Typeface.BOLD : Typeface.NORMAL);
                // dataBinding.tvSubtitle.setTextColor(isHighLight ? Color.parseColor("#FFFF3D46") : Color.parseColor("#73FFFFFF"));
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                // 收藏
                if (bean != null && bean.getSpecialId() == itemBean.getSpecialId()) {
                    boolean isCollected = bean.isCollectFlag();
                    boolean isSameItem = true;
                    if (dataBinding.getRoot().getTag() != null) {
                        isSameItem = position == (int) dataBinding.getRoot().getTag();
                    }
                    dataBinding.imgCollect.setImageResource(isCollected && isSameItem ? R.drawable.img_collected : R.drawable.img_collect);
                    itemBean.setCollectFlag(isCollected);
                }
            });
        }
    }
}
