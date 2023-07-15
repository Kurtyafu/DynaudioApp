package com.byd.dynaudio_app.bean;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放器对象 封装了播放器相关的状态:
 * 播放状态(暂停，加载，播放）
 * 播放速度
 * 播放进度 0-100
 * 播放模式: 列表循环、单曲循环、随机循环
 * 播放列表
 * 当前播放的是播放列表的第几个
 */
public class MusicPlayerBean {
    private PlayStatus playStatus;
    private float speed;
    private int progress;// 改成0-10000 进度是万分之
    private PlaybackMode playbackMode;
    private List<MusicListBean> playList;
    private int index; // * 当前播放的是播放列表的第几个

    /**
     * 获取默认的播放器状态
     */
    public static MusicPlayerBean getDefault() {
        MusicPlayerBean musicPlayerBean = new MusicPlayerBean();
        musicPlayerBean.setPlayStatus(PlayStatus.Paused);
        musicPlayerBean.setSpeed(1.f);
        musicPlayerBean.setProgress(0);
        musicPlayerBean.setPlaybackMode(PlaybackMode.List_Loop);
        musicPlayerBean.setPlayList(new ArrayList<>());
        musicPlayerBean.setIndex(0);
        return musicPlayerBean;
    }

    public PlayStatus getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(PlayStatus playStatus) {
        this.playStatus = playStatus;

        notifyData();
    }

    private void notifyData() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER).postValue(this);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
        notifyData();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        notifyData();
    }

    public PlaybackMode getPlaybackMode() {
        return playbackMode;
    }

    public void setPlaybackMode(PlaybackMode playbackMode) {
        this.playbackMode = playbackMode;
        notifyData();
    }

    public List<MusicListBean> getPlayList() {
        return playList;
    }

    public void setPlayList(List<MusicListBean> playList) {
        this.playList = playList;
        notifyData();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public enum PlaybackMode {
        List_Loop, // 列表循环
        Single_Cycle, // 单曲循环
        Random_Cycle, // 随机循环
    }

    /**
     * 播放状态
     */
    public enum PlayStatus {
        Paused, Loading, Playing,
    }
}
