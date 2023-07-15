package com.byd.dynaudio_app.manager;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.custom.xpop.FullPlayerPopupView;
import com.byd.dynaudio_app.custom.xpop.MiniPlayerPopupView;
import com.byd.dynaudio_app.custom.xpop.PlayListPopupView;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.enums.PopupAnimation;

public class PlayerVisionManager {
    // 单例实例
    private static volatile PlayerVisionManager instance;

    // 全局变量
    private Activity mContext;
    private MiniPlayerPopupView miniPlayerPopupView;
    private FullPlayerPopupView fullPlayerPopupView;
    private PlayListPopupView playListPopupView;


    // 私有构造函数，防止外部创建实例
    private PlayerVisionManager() {
    }

    // 获取单例实例
    public static PlayerVisionManager getInstance() {
        if (instance == null) {
            synchronized (PlayerVisionManager.class) {
                if (instance == null) {
                    instance = new PlayerVisionManager();
                }
            }
        }
        return instance;
    }

    // 初始化方法
    public PlayerVisionManager init(AppCompatActivity activity) {
        mContext = activity;

       /* LiveDataBus.get().with(LiveDataBusConstants.Player.IS_MINI_PLAYER_SHOW, Boolean.class)
                .observe(activity, aBoolean -> {
                    LogUtils.d("show mini : " + aBoolean);
                    if (aBoolean == null) return;
                    boolean isShowMiniPlayer = aBoolean;
                    if (isShowMiniPlayer) {
                        if (miniPlayerPopupView != null) miniPlayerPopupView.show();
                        else {
                            initMiniPlayer(mContext);
                            showMiniPlayer();
                        }
                    } else {
                        if (miniPlayerPopupView != null) miniPlayerPopupView.smartDismiss();
                        miniPlayerPopupView = null;
                    }
                });*/

        return this;
    }

    /**
     * 初始化迷你播放器
     */
   /* public PlayerVisionManager initMiniPlayer(@NonNull Activity activity) {
        if (miniPlayerPopupView == null) {
            miniPlayerPopupView = (MiniPlayerPopupView) new XPopup
                    .Builder(activity)
                    .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(false)
                    .enableDrag(false)
                    .hasShadowBg(false)
                    .isTouchThrough(true)
                    .offsetY(!SPUtils.isPad() ? -DensityUtils.dp2Px(mContext, 100) : 0) // 如果是车机 往上移动mini bar
                    .asCustom(new MiniPlayerPopupView(activity));
        }
        return this;
    }*/

    /**
     * 初始化全屏播放器
     */
    public PlayerVisionManager initFullPlayer(@NonNull Activity activity) {
        if (fullPlayerPopupView == null) {
            fullPlayerPopupView = (FullPlayerPopupView) new XPopup.Builder(activity)
                    .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
                    .dismissOnTouchOutside(false)
                    .dismissOnBackPressed(true)
                    .hasShadowBg(false)
                    .hasStatusBar(false)
                    .animationDuration(600)
                    .asCustom(new FullPlayerPopupView(activity));

            fullPlayerPopupView.offsetTopAndBottom(10000);

            fullPlayerPopupView.show();
            fullPlayerPopupView.setAlpha(0);
            fullPlayerPopupView.postDelayed(() -> {
                fullPlayerPopupView.dismiss();
                fullPlayerPopupView.offsetTopAndBottom(0);
                fullPlayerPopupView.postDelayed(() -> fullPlayerPopupView.setAlpha(1), 500);
            }, 100);
        }
        return this;
    }

    /**
     * 初始化播放列表
     */
    public PlayerVisionManager initPlayList(@NonNull Activity activity) {
        if (playListPopupView == null) {
            playListPopupView = (PlayListPopupView) new XPopup.Builder(activity)
                    .popupAnimation(PopupAnimation.TranslateAlphaFromBottom)
                    .dismissOnTouchOutside(true)
                    .dismissOnBackPressed(true)
                    .hasStatusBar(false)
                    .hasShadowBg(true)
                    .animationDuration(800)
                    .asCustom(new PlayListPopupView(activity));
        }
        return this;
    }

    /**
     * 显示 mini 播放器
     */
    public PlayerVisionManager showMiniPlayer() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.IS_MINI_PLAYER_SHOW).postValue(true);
        return this;
    }

    /**
     * 隐藏 mini 播放器
     */
    public void hideMiniPlayer() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.IS_MINI_PLAYER_SHOW).postValue(false);
    }

    /**
     * 设置 mini 播放器数据
     */
    public <T> PlayerVisionManager setMiniPlayerData(@NonNull String key, Class<T> type, T value) {
        LiveDataBus.get().with(key, type).postValue(value);
        return this;
    }

    /**
     * 显示全屏播放器
     */
    public void showFullPlayer() {
        if (!NetworkObserver.getInstance().isConnectNormal(mContext) && !MusicPlayManager.getInstance().isPlaying()) {
            return;
        }
        if (fullPlayerPopupView != null) fullPlayerPopupView.show();
    }

    /**
     * 隐藏全屏播放器
     */
    public void hideFullPlayer() {
        if (fullPlayerPopupView != null) fullPlayerPopupView.dismiss();
    }

    public FullPlayerPopupView getFullPlayer() {
        return fullPlayerPopupView;
    }

    public MiniPlayerPopupView getMiniPlayer() {
        return miniPlayerPopupView;
    }

    public PlayListPopupView getPlayList() {
        return playListPopupView;
    }

    public void showPlayList() {
        if (playListPopupView != null) {
            playListPopupView.show();
        }
    }

    public void dismissPlayList() {
        if (playListPopupView != null) {
            playListPopupView.dismiss();
        }
    }
}

