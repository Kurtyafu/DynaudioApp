package com.byd.dynaudio_app.adapter;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.databinding.LayoutItemMusicBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.SPUtils;

import java.util.List;

public class MusicListAdapter extends BaseRecyclerViewAdapter<MusicListBean, LayoutItemMusicBinding> {

    private boolean mShowCollectBtn;
    private IItemOnClickListener mClickListener;

    public void setShowCollectBtn(boolean showCollectBtn) {
        this.mShowCollectBtn = showCollectBtn;
    }

    public void setClickListener(IItemOnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public MusicListAdapter(Context mContext, List mData) {
        super(mContext, mData);
        setHasStableIds(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_music;
    }

    @Override
    protected void bindItem(LayoutItemMusicBinding dataBinding, MusicListBean bean, int position) {
        // 基础信息
        int singNumber = position + 1;
//        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) dataBinding.txtSinger.getLayoutParams();
//        int marginStart = (int) mContext.getResources().getDimension(R.dimen.single_marginStart);
//        if (singNumber > mContext.getResources().getInteger(R.integer.nine)) {
//            marginStart = (int) mContext.getResources().getDimension(R.dimen.two_marginStart);
//        } else if (singNumber > mContext.getResources().getInteger(R.integer.ninety_nine)) {
//            marginStart = (int) mContext.getResources().getDimension(R.dimen.three_marginStart);
//        }
//        lp.setMarginStart(marginStart);
        // dataBinding.txtSinger.setLayoutParams(lp);
        dataBinding.txtNum.setText(String.valueOf(singNumber));
        dataBinding.txtName.setText(bean.getName());// 标题
        // 列表上下滑动或者左划删除会导致标题缩短
//        if (bean.getQualityUrl() == null || bean.getQualityUrl().isEmpty()) {
//            dataBinding.txtName.setMaxWidth(dataBinding.txtName.getMaxWidth() - dataBinding.labView.getWidth());
//        }
//        if (SPUtils.isPad()){
//            dataBinding.txtName.setMaxWidth(dataBinding.txtName.getMaxWidth() - dataBinding.labView.getWidth());
//        }
        List<SingerBean> singerList = bean.getSingerList();
        if (singerList != null && singerList.size() > 0) {
            bean.setSinger(SPUtils.formatAuther(singerList));
        }
        dataBinding.txtSinger.setText(bean.getSinger());// 歌手
        dataBinding.txtSize.setText(SPUtils.formatSize(bean.getSize()));// 体积，单位为M
        dataBinding.txtDuration.setText(SPUtils.formatTime2(bean.getDuration()));// 总时长
        dataBinding.labView.setLabelImgPath(bean.getQualityUrl());// 音质
        // 是否需要显示收藏按钮（除了我的收藏页面不需要，其他的都需要显示）
        if (mShowCollectBtn) {
            dataBinding.txtDelete.setText("删除");// 播放记录页面的删除文案为删除，我的收藏页面为取消收藏
            dataBinding.viewCollect.setSelected(bean.isCollectFlag());
            dataBinding.viewCollect.setOnClickListener(view -> MusicPlayManager.getInstance().setCollect(bean, !bean.isCollectFlag()));
            if (bean.isShowCollect()) {
                dataBinding.viewCollect.setVisibility(View.VISIBLE);
            } else {
                dataBinding.viewCollect.setVisibility(View.GONE);
            }
        }
        // 编辑删除模式
        if (bean.isEditMode()) {
            dataBinding.txtNum.setVisibility(View.INVISIBLE);
            dataBinding.viewRadioBtn.setVisibility(View.VISIBLE);
            dataBinding.viewRadioBtn.setSelected(bean.isSelected());
            if (mShowCollectBtn) {
                dataBinding.viewCollect.setVisibility(View.GONE);
            }
        } else {
            dataBinding.txtNum.setVisibility(View.VISIBLE);
            dataBinding.viewRadioBtn.setVisibility(View.GONE);
            if (mShowCollectBtn) {
                dataBinding.viewCollect.setVisibility(View.VISIBLE);
            }
        }
        // item滑动删除
        dataBinding.txtDelete.setOnClickListener(view -> {
            mClickListener.onDeleteClick(position);
        });
        // item点击事件
        dataBinding.getRoot().setOnClickListener(view -> {
            if (bean.isEditMode()) {
                // 编辑模式下item的复选
                boolean selected = dataBinding.viewRadioBtn.isSelected();
                dataBinding.viewRadioBtn.setSelected(!selected);
                bean.setSelected(!selected);
                mClickListener.onSelect(position, !selected);
            } else {
                // 当前点击的item不是当前播放的歌曲 是一个新作品 则需要全部刷新数据
                if (dataBinding.viewPlaying.getVisibility() == View.INVISIBLE) {
                    LiveDataBus.get().with("refresh_video_progress", Integer.class).postValue(0);
                }
                if (!dataBinding.txtName.isSelected()) {
                    mClickListener.onClick(position);
                } else {
                    PlayerVisionManager.getInstance().showFullPlayer();
                }
            }
        });
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, itemBean -> {
            // 这里执行频率为100ms 需要判断当前是否为编辑模式
            if (bean.isEditMode()) {
                dataBinding.txtNum.setVisibility(View.INVISIBLE);
                dataBinding.viewRadioBtn.setSelected(bean.isSelected());
                dataBinding.viewPlaying.setVisibility(View.GONE);
                dataBinding.txtName.setSelected(false);
            } else {
                boolean isHighLight = itemBean != null && itemBean.getSpecialId() == (bean.getSpecialId());
                // 序号
                dataBinding.txtNum.setVisibility(isHighLight ? View.INVISIBLE : View.VISIBLE);
                dataBinding.viewPlaying.setVisibility(isHighLight ? View.VISIBLE : View.INVISIBLE);
                // 文字
                dataBinding.txtName.setSelected(isHighLight);
            }
        });

        LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, itemBean -> {
            // 收藏
            if (itemBean != null && itemBean.getSpecialId() == bean.getSpecialId()) {
                boolean isCollected = itemBean.isCollectFlag();
                dataBinding.viewCollect.setSelected(isCollected);
            }
        });
    }
}

