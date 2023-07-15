package com.byd.dynaudio_app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseRecyclerViewAdapter;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutItemAudioProgramBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.SPUtils;

import java.util.List;

public class AudioListAdapter extends BaseRecyclerViewAdapter<MusicListBean, LayoutItemAudioProgramBinding> {

    private boolean mShowCollectBtn;
    IItemOnClickListener mClickListener;

    private long lastClickTime = -1;

    public void setShowCollectBtn(boolean showCollectBtn) {
        this.mShowCollectBtn = showCollectBtn;
    }

    public void setClickListener(IItemOnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public AudioListAdapter(Context mContext, List mData) {
        super(mContext, mData);
        setHasStableIds(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_item_audio_program;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void bindItem(LayoutItemAudioProgramBinding dataBinding, MusicListBean bean, int position) {
        // 基础信息
        int singNumber = position + 1;
        dataBinding.txtNum.setText(String.valueOf(singNumber));// 序号
        dataBinding.txtName.setText(bean.getName());// 标题
        dataBinding.txtUpdateTime.setText(SPUtils.getUpdateStr(bean.getUpdateTime()));// 最近更新时间
        // 上次播放进度
        long currentTime = DBController.queryCurrentTime(mContext, bean.getSpecialId());
        long timeRemaining = bean.getDuration() - currentTime;

        String all = SPUtils.formatTime3(bean.getDuration());//总时长
        String remaining = SPUtils.formatTime3(timeRemaining);//剩余时长

        if (currentTime == 0) { // 未播放
            dataBinding.txtDuration.setText("总长" + all);
            dataBinding.txtDuration.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.ic_time_not_start), null, null, null);
        } else if (timeRemaining < 20000) { // 已播完
            dataBinding.txtDuration.setText("完播" + all);
            dataBinding.txtDuration.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.ic_time_finish), null, null, null);
        } else {
            dataBinding.txtDuration.setText("剩余" + remaining);
            dataBinding.txtDuration.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.ic_time_remaining), null, null, null);
        }

//        dataBinding.labView.setLabelImgPath(bean.getQualityUrl());// 音质
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
            if (System.currentTimeMillis() - lastClickTime < 300) {
                return;
            }
            lastClickTime = System.currentTimeMillis();

            if (bean.isEditMode()) {
                // 编辑模式下item的复选
                boolean selected = dataBinding.viewRadioBtn.isSelected();
                dataBinding.viewRadioBtn.setSelected(!selected);
                bean.setSelected(!selected);
                mClickListener.onSelect(position, !selected);
            } else {
                // 已播放的歌曲防止重新播放
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

