package com.byd.dynaudio_app.custom.xpop;


import static com.byd.dynaudio_app.LiveDataBusConstants.Player.CURRENT_PLAYER;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.bean.response.SingerBean;
import com.byd.dynaudio_app.databinding.LayoutViewMiniPlayerBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.List;

public class MiniPlayerPopupView extends BaseView<LayoutViewMiniPlayerBinding> {

    private ObjectAnimator animator;
    private boolean hasData = false;
    private MusicListBean currentMusic;

    public MiniPlayerPopupView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {
        initView();
        initObserver();
        initListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_mini_player;
    }

    private void initView() {
        TouchUtils.bindClickItem(mDataBinding.imgCollect, mDataBinding.imgPlay, mDataBinding.imgNext, mDataBinding.imgMenu);
    }

    private void initObserver() {
        if (mContext instanceof MainActivity) {
            MainActivity activity = (MainActivity) mContext;

            // 播放器状态
            LiveDataBus.get().with(CURRENT_PLAYER, MusicPlayerBean.class).observe(activity, player -> {
                // 1.播放状态
//                LogUtils.d("can play btn change : " + canPlayBtnChange);
                if (canPlayBtnChange) {
                    switch (player.getPlayStatus()) {
                        case Playing:
                            mDataBinding.imgPlay.setImageResource(R.drawable.img_pause);
                            break;
                        case Loading:
                            // todo show loading
                            break;
                        default:
                            mDataBinding.imgPlay.setImageResource(R.drawable.icon_play_mini);
                            break;
                    }
                }
                // 2.进度
                // LogUtils.d("progress : " + player.getProgress());
                setProgress(player.getProgress());
            });
            // 界面内容更新
            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY, MusicListBean.class).observe(activity, musicListBean -> {
                if (musicListBean == null) return;
                currentMusic = musicListBean;
                // 0410 如果过来的是台宣 就看看下一个是不是有 且非台宣 就显示下一个
                if ("2".equals(musicListBean.getLibraryType())) {
                    return;
                }

                // 1. 图片更新
                Glide.with(mContext).load(musicListBean.getImageUrl()).error(R.drawable.miniplay_loading).into(mDataBinding.imgIcon);
                // 2. 标题、副标题、标签
                if (!TextUtils.equals(mDataBinding.tvTitle.getText().toString(), musicListBean.getName())) {
                    mDataBinding.tvTitle.setText(musicListBean.getName());
                }
                List<SingerBean> singerList = musicListBean.getSingerList();
                if (singerList != null && singerList.size() > 0) {
                    musicListBean.setSinger(SPUtils.formatAuther(singerList));
                }
                mDataBinding.tvContent.setText(musicListBean.getSinger());
//                LogUtils.d("mini name : " + musicListBean.getName()
//                        + " singer : " + SPUtils.formatAuther(singerList));
                mDataBinding.tvLabel.setLabelImgPath(!TextUtils.equals("ys", musicListBean.getTypeName()) ? musicListBean.getQualityUrl() : null);
                // 3. 是否收藏
                mDataBinding.imgCollect.setImageResource(musicListBean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);
                hasData = true;
            });

            LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY, MusicListBean.class).observe((LifecycleOwner) mContext, bean -> {
                // 收藏
                if (bean != null && currentMusic != null && currentMusic.getSpecialId() == bean.getSpecialId()) {
                    mDataBinding.imgCollect.setImageResource(bean.isCollectFlag() ? R.drawable.img_collected : R.drawable.img_collect);
                }
            });

            LiveDataBus.get().with(LiveDataBusConstants.MINI_PLAY_STATUS_CAN_CHANGE, Boolean.class)
                    .observe((LifecycleOwner) mContext, aBoolean -> {
                        if (aBoolean != null && !aBoolean) {
                            startPlayBtnChange();
                        }
                    });
        }

        LiveDataBus.get().with(LiveDataBusConstants.Player.IS_MINI_PLAYER_SHOW, Boolean.class)
                .observe((LifecycleOwner) mContext, aBoolean -> {
                    if (aBoolean != null) {
                        if (mContext instanceof MainActivity) {
                            MainActivity activity = (MainActivity) mContext;
                            if (!activity.canShowMiniPlayer()) {
                                aBoolean = false;
                            }
                        }
                        showOrDismiss(aBoolean);
                    }
                });
    }

    private void showOrDismiss(boolean show) {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        float[] floats = new float[]{getTranslationY(), show ? 0 : getHeight()};
        animator = ObjectAnimator.ofFloat(this, "translationY", floats);
        animator.setDuration(500);
        animator.start();
    }

    /**
     * 设置进度
     */
    private void setProgress(Integer integer) {
        int progress;
        progress = integer != null ? integer : 0;

        progress = Math.max(progress, 1);
        int width = mDataBinding.getRoot().getWidth();

        if (width == 0) {
            int finalProgress = progress;
            mDataBinding.getRoot().postDelayed(() -> setProgress(finalProgress), 100);
        }

        ViewGroup.LayoutParams layoutParams = mDataBinding.imgProgress.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = (int) (progress / 10001.f * width);
            mDataBinding.imgProgress.setLayoutParams(layoutParams);
        }
    }

    private void initListener() {
        // 收藏按钮点击
        mDataBinding.imgCollect.setOnClickListener(v -> MusicPlayManager.getInstance().setCollect(!MusicPlayManager.getInstance().isCollect()));
        // 播放按钮点击
        mDataBinding.imgPlay.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;

            // 当前是点击下一曲/上一曲的情况下 播放按钮点击后立即切换状态
            if (!canPlayBtnChange) {
                mDataBinding.getRoot().removeCallbacks(playBtnChangeRunnable);
                mDataBinding.getRoot().post(playBtnChangeRunnable);
            }

            if (MusicPlayManager.getInstance().isPlaying()) {
                MusicPlayManager.getInstance().pauseMusic();
            } else {
                MusicPlayManager.getInstance().playMusic();
            }
        });
        // 下一首
        mDataBinding.imgNext.setOnClickListener(v -> {
            // 当前在台宣 点击无效
            if (MusicPlayManager.getInstance().isInTaixuan()) return;

            // 做一个屏蔽播放按钮变化的时间屏蔽 如果是暂停的状态 不需要切换
            if (MusicPlayManager.getInstance().isPlaying()) {
                startPlayBtnChange();
            }// 屏蔽播放按钮变化1s
            MusicPlayManager.getInstance().syncPlayMode(currentMusic);
            MusicPlayManager.getInstance().playNext();
        });
        // 点击展开全屏播放器
        mDataBinding.getRoot().setOnClickListener(v -> {
            MusicPlayManager.getInstance().syncPlayMode(currentMusic);
            LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_FULLPLAY).setValue(true);
            PlayerVisionManager.getInstance().showFullPlayer();
        });
        // 点击显示全屏播放器-列表界面
        mDataBinding.imgMenu.setOnClickListener(v -> {
            if (!PlayerVisionManager.getInstance().getPlayList().isShow()) {
                PlayerVisionManager.getInstance().showPlayList();
            }
//            LiveDataBus.get().with(LiveDataBusConstants.Player.SHOW_LIST).postValue(true);
//            MusicPlayManager.getInstance().syncPlayMode(currentMusic);
//            PlayerVisionManager.getInstance().showFullPlayer();
        });

    }

    protected int getPopupWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    protected int getPopupHeight() {
        return DensityUtils.dp2Px(mContext, 73);
    } // 车机方便点击

    public boolean hasData() {
        return hasData;
    }

    private boolean canPlayBtnChange = true;

    /**
     * 开始计时 时间到后才运行按钮状态发生
     */
    public void startPlayBtnChange() {
        LogUtils.d();
        mDataBinding.getRoot().removeCallbacks(playBtnChangeRunnable);
        canPlayBtnChange = false;
        mDataBinding.getRoot().postDelayed(playBtnChangeRunnable, 1000);
    }

    private Runnable playBtnChangeRunnable = () -> canPlayBtnChange = true;
}
