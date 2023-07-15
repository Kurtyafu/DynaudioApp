package com.byd.dynaudio_app.manager;

import static com.byd.dynaudio_app.bean.MusicPlayerBean.PlaybackMode.List_Loop;
import static com.byd.dynaudio_app.bean.MusicPlayerBean.PlaybackMode.Random_Cycle;
import static com.google.android.exoplayer2.Player.REPEAT_MODE_OFF;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.controller.CollectController;
import com.byd.dynaudio_app.controller.PlayRecordController;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.custom.xpop.FullPlayerPopupView;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.http.ProxyCache;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.SharedPreferencesUtil;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicPlayManager {

    //音乐
    public static String MUSIC = "1";
    //有声
    public static String VOICE = "3";
    //资讯
    public static String INFORMATION = "4";

    private static MusicPlayManager instance;
    private ExoPlayer exoPlayer;
    private String musicUri;
    private String musicUri2;
    private boolean isPlaying;
    private MusicPlayerBean musicPlayerBean;
    private Handler progressHandler;
    private Runnable progressRunnable = () -> {
        updateProgress();
        startProgressTimer();
    };
    private String albumId;
    private String typeName;
    private DefaultDataSourceFactory sourceFactory;
    private boolean autoPlay = true;

    private Handler handler = new Handler();
    private float volume;
    private boolean isInTaixuan; // 是否处于台宣 台宣过程中屏蔽播放按钮点击
    private int lastIndex = -1; // 用于判断台宣是否上一曲的情况

    private MediaPlayer mediaPlayer;
    private float beforeYouShengSpeed = -1.f;// 切换成非有声的时候 之前的倍速

    private MusicPlayManager() {
        // 构造方法私有化，防止在外部创建对象
    }

    public static MusicPlayManager getInstance() {
        if (instance == null) {
            instance = new MusicPlayManager();
        }
        return instance;
    }

    private Context mContext;
    private MusicListBean currentPlay;  // 当前播放内容

    public void init(@NonNull Context context) {
        mContext = context;

        musicPlayerBean = MusicPlayerBean.getDefault();

        progressHandler = new Handler();

        // 初始化player
        initPlayer();

        musicUri = "https://vod.ruotongmusic.com/sv/3388bdb0-17d31793e6c/3388bdb0-17d31793e6c.wav";
        musicUri2 = "http://downsc.chinaz.net/files/download/sound1/201206/1638.mp3";

        // 上次关闭前的专辑歌单
        List<MusicListBean> musicList = DBController.queryLastAlbum(mContext);
        if (musicList.size() > 0) {
            // 覆盖收藏字段(未登录检索本地数据)
            if (!UserController.getInstance().isLoginStates()) {
                for (MusicListBean bean : musicList) {
                    boolean collect;
                    if (bean.getLibraryType().equals("1")) {//音乐
                        collect = DBController.queryMusicCollect(mContext, bean);
                    } else {//有声
                        collect = DBController.queryAudioCollect(mContext, bean);
                    }
                    bean.setCollectFlag(collect);
                }
            }

            autoPlay = false;
            addToPlaylist(musicList);
            pauseMusic();
            handler.postDelayed(() -> PlayerVisionManager.getInstance().showMiniPlayer(), 2000);
        } else {
            handler.postDelayed(() -> PlayerVisionManager.getInstance().hideMiniPlayer(), 2000);
        }
        // 读取倍速 可能需要设置
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(mContext);
        float speed = sharedPreferencesUtil.getFloat(LiveDataBusConstants.Player.PLAY_SPEED, -1);
        // LogUtils.d("2222before speed : " + speed);
        if (speed > 0) {
            MusicPlayManager.getInstance().setSpeed(speed);
        }
    }

    private void initPlayer() {
        exoPlayer = new ExoPlayer.Builder(mContext).build();
        exoPlayer.addListener(new Player.Listener() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);

                LogUtils.d("play when ready : " + playWhenReady
                        + " play back state : " + playbackState);
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    switch (getPlaybackMode()) {
                        case List_Loop:
                            playNext();
                            break;
                        case Random_Cycle:
                            playRandom();
                            break;
                        case Single_Cycle:
                            exoPlayer.seekToDefaultPosition(exoPlayer.getMediaItemCount() - 1);
                            break;
                    }

                    playMusic();
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                Player.Listener.super.onIsPlayingChanged(isPlaying);
                MusicPlayManager.this.isPlaying = isPlaying;
                // 这里控制界面应该显示的播放状态
                musicPlayerBean.setPlayStatus(isPlaying ? MusicPlayerBean.PlayStatus.Playing : MusicPlayerBean.PlayStatus.Paused);
                // notifyCurrentItem
                notifyCurrentItem();
                if (isPlaying) {
                    startProgressTimer();
                } else {
                    progressHandler.removeCallbacks(progressRunnable);
                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                LogUtils.d("play back state : " + playbackState);
                switch (playbackState) {
                    case Player.STATE_IDLE: // 播放器停止和播放失败
                        musicPlayerBean.setPlayStatus(MusicPlayerBean.PlayStatus.Paused);
                        CustomToast.makeText(mContext, "暂时无法收听,将自动切到下一个播放", Toast.LENGTH_SHORT).show();
             /*           if (PlayerVisionManager.getInstance().getFullPlayer().isShow()) {
                            PlayerVisionManager.getInstance().getFullPlayer().toNext();
                        } else {*/
                        playNext();
//                        }
                        break;
                    case Player.STATE_BUFFERING:  // 加载、缓冲
                        musicPlayerBean.setPlayStatus(MusicPlayerBean.PlayStatus.Loading);
                        break;
                    case Player.STATE_READY:  // 可以播放
                        // 更新进度
                        updateProgress();
                        break;
                    case Player.STATE_ENDED:  // 完成了所有媒体的播放
//                        musicPlayerBean.setPlayStatus(MusicPlayerBean.PlayStatus.Paused);
                        break;
                }
            }

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                LogUtils.d("reason : " + reason + " media item : " + mediaItem);

                if (mediaItem != null) {
                    MusicListBean music = (MusicListBean) mediaItem.playbackProperties.tag;
                    // 播放记录
                    playRecord(music);
                    // 记录上一首有声节目的剩余时间
                    if (currentPlay != null) {
                        boolean isAudio = currentPlay.getLibraryType().equals("3") || currentPlay.getLibraryType().equals("4");
                        if (isAudio && currentPlay.getSpecialId() != music.getSpecialId()) {
                            Log.e("剩余时间", "已切歌，记录上一首的剩余时间");
                            Log.e("剩余时间", "播放到了 >>> " + currentPlay.getCurrentTime());
                            Log.e("剩余时间", "总时长为 >>> " + currentPlay.getDuration());
                            long rts = currentPlay.getDuration() - currentPlay.getCurrentTime();
                            Log.e("剩余时间", "剩余时间为 >>> " + rts);
                            Log.e("剩余时间", "剩余时间 格式化 >>> " + SPUtils.formatTime3(rts));
                            DBController.insertCurrentTime(mContext, currentPlay);
                        }
                    }
                }

                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                // 根据reason可以知道是为什么切换了
                MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
                if (currentMediaItem != null) {
                    currentPlay = (MusicListBean) currentMediaItem.playbackProperties.tag;
//                    LogUtils.d("current play : " + currentPlay.getName()
//                            + " 前方台宣 : " + currentPlay.getBeforeAudioUrl()
//                            + " 后方台宣 : " + currentPlay.getAfterAudioUrl());
                    // LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY_ID).postValue(currentPlay.getSpecialId());
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    int currentMediaItemIndex = exoPlayer.getCurrentMediaItemIndex();
                    if (currentMediaItemIndex == lastIndex - 1) { // 上一曲
                        // 如果有后方台宣 就先播放后方台宣 并且禁止播放器行为
                        if (currentPlay.getAfterAudioUrl() != null) {
                            startPlayTaixuan(currentPlay.getAfterAudioUrl());
                            // 台宣播放的时候 将exoplayer暂停 使用media player来播放台宣 此时屏蔽歌曲操作（上一曲，下一曲，播放暂停等） 但此时可以切换播放列表 仍不受影响
                        } else {
                            if (mediaPlayer != null) {
                                try {
                                    mediaPlayer.stop();
                                } catch (IllegalStateException e) {
                                    // e.printStackTrace();
                                }
                            }
                            setIsInTaixuan(false);
                        }
                    } else {
                        // 如果有前方台宣 就先播放前方台宣 并且禁止播放器行为
                        if (currentPlay.getBeforeAudioUrl() != null) {
                            startPlayTaixuan(currentPlay.getBeforeAudioUrl());
                            // 台宣播放的时候 将exoplayer暂停 使用media player来播放台宣 此时屏蔽歌曲操作（上一曲，下一曲，播放暂停等） 但此时可以切换播放列表 仍不受影响
                        } else {
                            if (mediaPlayer != null) {
                                try {
                                    mediaPlayer.stop();
                                } catch (IllegalStateException e) {
                                    // e.printStackTrace();
                                }
                            }
                            setIsInTaixuan(false);
                        }
                    }

                    if (lastIndex != exoPlayer.getCurrentMediaItemIndex()) {
                        notifyIndexChange();
                    }
                    lastIndex = exoPlayer.getCurrentMediaItemIndex();
                    notifyCurrent();
                }
            }

            @Override
            public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
                Player.Listener.super.onPositionDiscontinuity(oldPosition, newPosition, reason);
                LogUtils.d("old : " + oldPosition.windowIndex + " new : " + newPosition.windowIndex
                        + " reason : " + reason);

                int oldPos = oldPosition.windowIndex;
                int newPos = newPosition.windowIndex;

                if (oldPos > newPos) {
                    // 上一曲

                    // 获取当前这一首歌是否有台宣

                } else if (reason == 0
                        || (reason == 1 && (newPos > oldPos))
                        || reason == 2) {
                    // 下一曲

                    // 和之前的逻辑一样
                }

                updateProgress();
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                // 在状态转换为idle前调用 可以进行retry
                LogUtils.e(error.toString() + " message : " + error.getMessage() + " code :" + error.getErrorCodeName());
                playNext();
            }

            @Override
            public void onPlaylistMetadataChanged(MediaMetadata mediaMetadata) {
                Player.Listener.super.onPlaylistMetadataChanged(mediaMetadata);
                LogUtils.d();
            }
        });

        sourceFactory = new DefaultDataSourceFactory(mContext, mContext.getPackageName());
        // 这个应该是设置播放模式
        // exoPlayer.setShuffleOrder(new ShuffleOrder.DefaultShuffleOrder(1));
    }

    /**
     * 通知item发生变化
     * 仅切换歌曲的时候才会触发
     */
    private void notifyIndexChange() {
        LiveDataBus.get().with(LiveDataBusConstants.PLAY_INDEX, Integer.class).postValue(exoPlayer.getCurrentMediaItemIndex());
    }

    private void startPlayTaixuan(String beforeAudioUrl) {
        setIsInTaixuan(true);
        Uri audioUrl = Uri.parse(beforeAudioUrl);
        exoPlayer.setPlayWhenReady(false);
        handler.postDelayed(() -> exoPlayer.pause(), 100);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());

        try {
            mediaPlayer.setDataSource(mContext, audioUrl);
            mediaPlayer.setOnPreparedListener(mp -> mp.start());
            mediaPlayer.setOnCompletionListener(mp -> {
                setIsInTaixuan(false);

                playMusic();
                mediaPlayer = null;
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 缓冲并播放当前列表
     */
    public void playMusic() {
        tryRequestFocus();

        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare();
        PlayerVisionManager.getInstance().showMiniPlayer();
    }

    public void pauseMusic() {
        exoPlayer.pause();
        PlayerVisionManager.getInstance().showMiniPlayer();
    }

    /**
     * //黑胶、有声、资讯每个播放顺序类型都不一样  当播放类型是有声和资讯时设置为列表循环
     *
     * @param currentMusic
     */
    public void syncPlayMode(MusicListBean currentMusic) {
        if ((currentMusic.getLibraryType().equals(MusicPlayManager.VOICE)
                || currentMusic.getLibraryType().equals(MusicPlayManager.INFORMATION))
                && getPlaybackMode() != List_Loop) {// 当前不是列表循环再去切换成列表循环
            MusicPlayManager.getInstance().setPlaybackMode(List_Loop);
        }
    }

    /**
     * 如果当前是最后一首 就下一首 如果是最后一首 就播放第一首
     */
    public void playNext() {
        if (MusicPlayManager.getInstance().isInRandomCycle()) {
            MusicPlayManager.getInstance().playRandom();
            return;
        }
        tryRequestFocus();

        long currentPosition = exoPlayer.getCurrentMediaItemIndex();
        LogUtils.d("current : " + currentPosition + " total : " + exoPlayer.getMediaItemCount());

        if (PlayerVisionManager.getInstance().getFullPlayer() != null
                && PlayerVisionManager.getInstance().getFullPlayer().isHasFirstIn()
                && PlayerVisionManager.getInstance().getFullPlayer().isShow()) {
            PlayerVisionManager.getInstance().getFullPlayer().toNext();
        } else {
            if (currentPosition == exoPlayer.getMediaItemCount() - 1) {
                exoPlayer.seekToDefaultPosition(0);
            } else {
                exoPlayer.seekToNext();
            }
        }
    }

    public void playPrevious() {
        tryRequestFocus();

        if (PlayerVisionManager.getInstance().getFullPlayer() != null
                && PlayerVisionManager.getInstance().getFullPlayer().isHasFirstIn()
                && PlayerVisionManager.getInstance().getFullPlayer().isShow()) {
            PlayerVisionManager.getInstance().getFullPlayer().toBefore();
        } else {
            if (exoPlayer.getCurrentMediaItemIndex() == 0) {
                exoPlayer.seekToDefaultPosition(exoPlayer.getMediaItemCount() - 1);
            } else {
                exoPlayer.seekToPrevious();
            }
        }
    }

    /**
     * 调节进度 比如前进30s 后退15s
     *
     * @param delta 秒数
     */
    public void playPrevious(int delta) {
        long currentPosition = exoPlayer.getCurrentPosition(); // 获取当前播放进度
        long newPosition = currentPosition + delta * 1000L; // 计算新的播放进度，delta 单位为秒，需要转换为毫秒
        if (newPosition < 0) {
            newPosition = 0;
        }
        exoPlayer.seekTo(newPosition); // 跳转到新的播放进度
    }

    /**
     * 如果当前专辑id是一样的 就定位到对应的歌曲 并播放
     * 如果不一样 就替换当前播放列表 并播放
     */
    private void playMusic(@NonNull List<MusicListBean> list, MusicListBean itemBean, int position) {
        tryRequestFocus();

        int mediaItemCount = exoPlayer.getMediaItemCount();

        if (mediaItemCount == list.size()) {
            for (int i = 0; i < mediaItemCount; i++) {
                MediaItem mediaItemAt = exoPlayer.getMediaItemAt(i);
                if (mediaItemAt.playbackProperties == null
                        || mediaItemAt.playbackProperties.tag == null) break;
                MusicListBean bean = (MusicListBean) mediaItemAt.playbackProperties.tag;
                if (itemBean != null && bean.getSpecialId() == itemBean.getSpecialId()
                        && TextUtils.equals(itemBean.getTypeName(), bean.getTypeName())) {
                    // 说明有这首歌
                    playMusic(i);
                    return;
                }
            }
        }

        // 替换播放列表
        addToPlaylistAndPlay(list, position);
    }

    public void playMusic(List<MusicListBean> list, MusicListBean itemBean, int position, String albumId) {
        // 0509需求： 切换歌单，播放器状态不变化
        LiveDataBus.get().with(LiveDataBusConstants.MINI_PLAY_STATUS_CAN_CHANGE, Boolean.class).postValue(false);

        playMusic(list, itemBean, position);
        this.albumId = albumId;
    }

    /**
     * 跳转到指定的index进行播放
     */
    private void playMusic(int index) {
        exoPlayer.seekTo(index, 0);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare();
    }

    /**
     * 播放内容
     */
    private void notifyCurrent() {
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY).postValue(currentPlay);
    }

    /**
     * 跳转到指定进度
     */
    public void setProgress(int progress) {
        // LogUtils.d("to progress : " + progress);
        // 获取当前播放进度
        long duration = exoPlayer.getDuration();

        // 计算指定进度的位置
        long seekPosition = (long) (duration * (progress / 10001.0f));

        // 将播放进度跳转到指定的位置
        exoPlayer.seekTo(seekPosition);
    }

    public void setTime(long time) {
        exoPlayer.seekTo(time);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isInRandomCycle() {
        return getPlaybackMode().equals(Random_Cycle);
    }

    /**
     * 添加到播放列表 并清空现在的播放列表
     */
    private void addToPlaylist(List<MusicListBean> list) {
        MusicListBean currentItem = getCurrentItem();
        boolean beforeIsYouSheng = currentItem != null && "ys".equals(currentItem.getTypeName());

        List<MediaSource> mediaSources = new ArrayList<>();
        boolean isYouSheng = true;
        if (list == null) list = new ArrayList<>();
        for (MusicListBean bean : list) {
            MediaItem item = generateMediaItem(bean);
            if (item != null && item.localConfiguration != null) {
                ProgressiveMediaSource.Factory factory = new ProgressiveMediaSource.Factory(sourceFactory);
                if (factory != null) {
                    MediaSource mediaSource = factory.createMediaSource(item);
                    mediaSources.add(mediaSource);
                }
            }
            if (!"ys".equals(bean.getTypeName())) {
                isYouSheng = false;
            }
        }
        ConcatenatingMediaSource concatenatedSource = new ConcatenatingMediaSource();
        concatenatedSource.addMediaSources(mediaSources);

        exoPlayer.setMediaSource(concatenatedSource);

        // 重新设置播放器的状态
        exoPlayer.prepare();
        exoPlayer.seekTo(0, 0);
        exoPlayer.setPlayWhenReady(autoPlay);
        // 切换播放列表 就把倍速恢复成1倍速


        if (!isYouSheng) { // 新播放的是音乐类
            if (beforeIsYouSheng) { // 新的是音乐 之前的有声
                beforeYouShengSpeed = musicPlayerBean.getSpeed();
            } else { // 新的是音乐 之前的音乐

            }
            setSpeed(1.f);
        } else { // 有声类
            if (beforeYouShengSpeed > 0) {
                setSpeed(beforeYouShengSpeed);
            } else {
                // 读取倍速 可能需要设置
                SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(mContext);
                float speed = sharedPreferencesUtil.getFloat(LiveDataBusConstants.Player.PLAY_SPEED, -1);
                // LogUtils.d("2222before speed : " + speed);
                if (speed > 0) {
                    MusicPlayManager.getInstance().setSpeed(speed);
                }
            }

        }

//
//        exoPlayer.clearMediaItems();
//        exoPlayer.addMediaItems(0, items);

        notifyPlayList();
    }

    /**
     * 添加到播放列表  并且重头开始
     */
    public void addToPlaylistAndPlay(List<MusicListBean> list) {
        addToPlaylistAndPlay(list, 0);
    }

    /**
     * 添加到播放列表
     */
    private void addToPlaylistAndPlay(List<MusicListBean> list, int index) {
        addToPlaylist(list);
        exoPlayer.seekToDefaultPosition(index);
        playMusic();
        DBController.insertLastAlbum(mContext, list);
    }

    private MediaItem generateMediaItem(@NonNull MusicListBean bean) {
        /**
         * 这里有几种可能
         * 1.有音频 无视频  --> 正常加载
         * 2.无音频 有视频  --> 把视频加载到player中 往视频上渲染
         * 3.有音频 有视频  --> 把音频加载到player中 正常播放 视频也正常播放
         */
        String url = bean.getAudioUrl() != null ? bean.getAudioUrl() : bean.getVideoUrl() != null ? bean.getVideoUrl() : "dashabi";

        // 使用代理，实现边听边缓存
        HttpProxyCacheServer proxy = ProxyCache.getProxy();
        // 歌曲&视频
        String proxyUrl = proxy.getProxyUrl(url);

        MediaItem mediaItem = new MediaItem.Builder().setUri(proxyUrl).setTag(bean).build();
        LogUtils.d("bean : " + bean.getName() + " media id : " + mediaItem.mediaId + " url :"
                + bean.getAudioUrl() + " type : " + bean.getLibraryType() + "  word url : " + bean.getWordUrl()
                + " type name : " + bean.getTypeName());
        return mediaItem;
    }

    private void notifyCurrentItem() {
        // 获取当前的播放对象
        MediaItem currentMediaItem = exoPlayer.getCurrentMediaItem();
        if (currentMediaItem != null) {
            LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAY).postValue(currentMediaItem.playbackProperties.tag);
        }
    }

    public void setCollect(boolean b) {
        setCollect(currentPlay, b);
    }

    public void setCollect(@NonNull MusicListBean bean, boolean isCollect) {
        bean.setCollectFlag(isCollect);
        CollectController.getInstance().collect(isCollect, bean);
        LiveDataBus.get().with(LiveDataBusConstants.Player.ITEM_PLAY).postValue(bean);
        if (isCollect)
            CustomToast.makeText(mContext, R.drawable.img_collected, "收藏成功", Toast.LENGTH_SHORT).show();
    }

    public boolean isCollect() {
        return currentPlay != null && currentPlay.isCollectFlag();
    }

    public void playRecord(@NonNull MusicListBean bean) {
        PlayRecordController.getInstance().record(bean);
    }

    public void notifyPlayList() {
        List<MusicListBean> list = new ArrayList<>();
        int mediaItemCount = exoPlayer.getMediaItemCount();
        for (int i = 0; i < mediaItemCount; i++) {
            MediaItem mediaItemAt = exoPlayer.getMediaItemAt(i);
            list.add((MusicListBean) Objects.requireNonNull(mediaItemAt.playbackProperties).tag);
        }
        musicPlayerBean.setPlayList(list);
    }

    private void updateProgress() {
        long duration = exoPlayer.getDuration();
        long currentPosition = exoPlayer.getCurrentPosition();
        int progress = (int) (currentPosition * 10001.f / duration);

        String currentTimeString = SPUtils.formatTime(currentPosition);
        String durationString = SPUtils.formatTime(duration);

        if (currentPlay != null) {
            currentPlay.setBeginTime(currentTimeString);
            currentPlay.setEndTime(durationString);
            if (currentPosition != 0) {
                currentPlay.setCurrentTime(currentPosition);
            }
            notifyCurrent();
        }

        musicPlayerBean.setProgress(progress);
    }


    private void startProgressTimer() {
        progressHandler.postDelayed(progressRunnable, 100);
    }

    public MusicPlayerBean.PlaybackMode getPlaybackMode() {
        return musicPlayerBean.getPlaybackMode();
    }

    public void setPlaybackMode(MusicPlayerBean.PlaybackMode toMode) {
        musicPlayerBean.setPlaybackMode(toMode);

        switch (toMode) {
            case Single_Cycle:
                exoPlayer.setShuffleModeEnabled(false);
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
                break;
            case Random_Cycle:
                exoPlayer.setShuffleModeEnabled(true);
                exoPlayer.setRepeatMode(REPEAT_MODE_OFF);
                break;
            case List_Loop:
            default:
                exoPlayer.setShuffleModeEnabled(false);
                exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
                break;
        }
    }

    public void setSpeed(float speed) {
        musicPlayerBean.setSpeed(speed);
        exoPlayer.setPlaybackSpeed(speed);
    }

    public float getSpeed() {
        if (musicPlayerBean != null) {
            return musicPlayerBean.getSpeed();
        }

        return -1;
    }

    /**
     * 添加到播放列表 并且 播放第0个
     */
    public void addToPlaylistAndPlay(String id, List<MusicListBean> libraryStoreList) {
//        LogUtils.d("album id : " + id);
        // 0509需求： 切换歌单，播放器状态不变化
        LiveDataBus.get().with(LiveDataBusConstants.MINI_PLAY_STATUS_CAN_CHANGE, Boolean.class).postValue(false);
        albumId = id;
        addToPlaylistAndPlay(libraryStoreList, 0);
    }

    public String getCurrentAlbumId() {
        return albumId;
    }

    public MusicPlayerBean getMusicPlayerBean() {
        return musicPlayerBean;
    }

    public Player getPlayer() {
        return exoPlayer;
    }

    public MusicListBean getCurrentItem() {
        return currentPlay;
    }

    public boolean isInTaixuan() {
        return isInTaixuan;
    }

    public void setIsInTaixuan(boolean is) {
        LogUtils.d("is : " + is);
        isInTaixuan = is;
        LiveDataBus.get().with("isInTaixuan", Boolean.class).postValue(is);
    }

    /**
     * 随机切换到当前列表的非当前歌曲
     */
    public void playRandom() {
        int mediaItemCount = exoPlayer.getMediaItemCount();
        if (mediaItemCount == 0) {
            return;
        } else if (mediaItemCount == 1) {
            setProgress(0);
        } else {
            int currentMediaItemIndex = exoPlayer.getCurrentMediaItemIndex();
            int toIndex = currentMediaItemIndex;
            // mediaItemCount 随机出一个非currentMediaItemIndex的数 这些都是整数
            while (toIndex == currentMediaItemIndex) {
                toIndex = (int) (Math.random() * mediaItemCount);
            }

            FullPlayerPopupView fullPlayer = PlayerVisionManager.getInstance().getFullPlayer();
            if (fullPlayer != null && fullPlayer.isShow()) {
                fullPlayer.toIndex(toIndex, false);
            } else {
                exoPlayer.seekToDefaultPosition(toIndex);
            }
        }
    }

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = focusChange -> {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                // 获得音频焦点，可以恢复播放或者调整音量
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                // 失去音频焦点，并且无法再次请求，需要停止播放并释放资源
                pauseMusic();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // 暂时失去音频焦点，会在短时间内返回，可以重新请求
                // 可以暂停播放，等待重新获取焦点后恢复播放
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 暂时失去音频焦点，但是允许继续播放，需要降低音量
                break;
        }
    };

    public void tryRequestFocus() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.requestAudioFocus(audioFocusChangeListener
                    , AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }
}
