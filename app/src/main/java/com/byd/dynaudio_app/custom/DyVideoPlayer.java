package com.byd.dynaudio_app.custom;

import static com.byd.dynaudio_app.utils.TouchUtils.hide;
import static com.byd.dynaudio_app.utils.TouchUtils.show;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import org.jzvd.jzvideo.JZVideoA;
import org.jzvd.jzvideo.JZVideoAKt;

import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.JZDataSource;
import cn.jzvd.JZMediaInterface;
import cn.jzvd.JZMediaSystem;
import cn.jzvd.JZUtils;
import cn.jzvd.Jzvd;

public class DyVideoPlayer extends Jzvd {

    protected static Timer dismiss_controller_timer;
    public ProgressBar loadingProgressBar;
    public ImageView posterImageView;
    protected DismissControlViewTimerTask mDismissControlViewTimerTask;

    protected ImageView smallPlayBtn;
    public final long DURATION = 330;

    // 是否仅播放视频 隐藏界面其他所有组件
    private boolean hideAll = false;

    // 是否隐藏中间的播放按钮
    private boolean hidePlay = false;
    private int streamVolume; // 静音时之前的音量 存储

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    private boolean mute = false;// 是否静音播放

    public DyVideoPlayer(Context context) {
        super(context);
    }

    public DyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        progressBar = findViewById(R.id.bottom_seek_progress);

//        backButton = findViewById(R.id.back);
        smallPlayBtn = findViewById(R.id.img_play_in_progress);
        posterImageView = findViewById(R.id.poster);
        loadingProgressBar = findViewById(R.id.loading);

        posterImageView.setOnClickListener(this);
        smallPlayBtn.setOnClickListener(this);
        // backButton.setOnClickListener(this);
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        changeUiToNormal();
        LogUtils.d();
        notifyStatus();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        changeUiToPreparing();
        LogUtils.d();
        notifyStatus();
        if (mute) {
            mute();
        } else {
            unmute();
        }
    }

    @Override
    public void onStatePreparingPlaying() {
        super.onStatePreparingPlaying();
        changeUIToPreparingPlaying();
        LogUtils.d();
        notifyStatus();
    }

    @Override
    public void onStatePreparingChangeUrl() {
        super.onStatePreparingChangeUrl();
        changeUIToPreparingChangeUrl();
        LogUtils.d();
        notifyStatus();
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        changeUiToPlayingClear();
        LogUtils.d();
        notifyStatus();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        changeUiToPauseShow();
        LogUtils.d();
        cancelDismissControlViewTimer();
        notifyStatus();
    }

    @Override
    public void onStateError() {
        super.onStateError();
        changeUiToError();
        LogUtils.d();
        notifyStatus();
    }

    private boolean autoCircle;

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        changeUiToComplete();
        cancelDismissControlViewTimer();
        //bottomProgressBar.setProgress(100);
        LogUtils.d();
        notifyStatus();
        if (autoCircle) {
            pause();
            seekTo0();
            startVideo();
        }
    }

    public void setAutoCircle(boolean autoCircle) {
        this.autoCircle = autoCircle;
    }

    @Override
    public void startVideo() {
        if (state != STATE_PLAYING) {
            super.startVideo();
        }
    }

    /**
     * 双击
     */
    protected GestureDetector gestureDetector = new GestureDetector(getContext().getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 没有双击暂停的功能
//            if (state == STATE_PLAYING || state == STATE_PAUSE) {
//                Log.d(TAG, "doublClick [" + this.hashCode() + "] ");
//                startButton.performClick();
//            }
//            return super.onDoubleTap(e);
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!mChangePosition && !mChangeVolume) {
                onClickUiToggle();
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
    });

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
            }
            gestureDetector.onTouchEvent(event);
        } else if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
            }
        }
        return super.onTouch(v, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.poster) {
        } else if (i == R.id.surface_container) {
            clickSurfaceContainer();
        } else if (i == R.id.img_play_in_progress) {
            startButton.performClick();
        }
    }

    @Override
    protected void clickStart() {
        super.clickStart();
    }

    protected void clickSurfaceContainer() {
        startDismissControlViewTimer();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    public void onClickUiToggle() {//这是事件
        LogUtils.d();

        if (state == STATE_PREPARING) {
            changeUiToPreparing();
        } else if (state == STATE_PLAYING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (state == STATE_PAUSE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        }
    }


    //** 和onClickUiToggle重复，要干掉
    public void onCLickUiToggleToClear() {
        if (state == STATE_PREPARING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPreparing();
            } else {
            }
        } else if (state == STATE_PLAYING) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPlayingClear();
            } else {
            }
        } else if (state == STATE_PAUSE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToPauseClear();
            } else {
            }
        } else if (state == STATE_AUTO_COMPLETE) {
            if (bottomContainer.getVisibility() == View.VISIBLE) {
                changeUiToComplete();
            } else {
            }
        }
    }

    @Override
    public void onProgress(int progress, long position, long duration) {
        super.onProgress(progress, position, duration);
        //bottomProgressBar.setProgress(progress);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        super.setBufferProgress(bufferProgress);
        //bottomProgressBar.setSecondaryProgress(bufferProgress);
    }

    @Override
    public void resetProgressAndTime() {
        super.resetProgressAndTime();
        //bottomProgressBar.setProgress(0);
        //bottomProgressBar.setSecondaryProgress(0);
    }

    /**
     * 初始状态
     */
    public void changeUiToNormal() {
        hide(bottomContainer, DURATION);

        updateStartImage();
    }

    /**
     * 加载中...
     */
    public void changeUiToPreparing() {
//        setAllControlsVisibility(false, false, false, true, true);
//        loadingProgressBar.setVisibility(VISIBLE);
        posterImageView.setVisibility(VISIBLE);
//        setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
//                View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
        updateStartImage();
    }

    /**
     * 没走
     */
    public void changeUIToPreparingPlaying() {

//        setAllControlsVisibility(true, true, false, true, true);
////        setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.INVISIBLE,
////                View.VISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
//        updateStartImage();
    }

    public void changeUIToPreparingChangeUrl() {
//        setAllControlsVisibility(false, false, false, true, true);
////        setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
////                View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
//        updateStartImage();
    }

    /**
     * 播放显示框架ui
     */
    public void changeUiToPlayingShow() {
        setAllControlsVisibility(true, true, false, false, false);

//        setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE,
//                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
        updateStartImage();
    }

    /**
     * 播放隐藏框架ui
     */
    public void changeUiToPlayingClear() {
        setAllControlsVisibility(false, false, false, false, false);

//        setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
//                View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
    }

    public void changeUiToPauseShow() {
        setAllControlsVisibility(true, true, false, false, false);

//        setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE,
//                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
        updateStartImage();
    }

    public void changeUiToPauseClear() {
        setAllControlsVisibility(false, false, false, false, false);

//        setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
//                View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
    }

    public void changeUiToComplete() {
        setAllControlsVisibility(false, false, true, false, true);

//        setAllControlsVisibility(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
//                View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
        updateStartImage();
    }

    public void changeUiToError() {
        setAllControlsVisibility(true, false, true, false, false);

//        setAllControlsVisibility(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
//                View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
        updateStartImage();
    }

    /**
     * 控件显示隐藏 TODO 做出逐渐显示和隐藏
     */
    public void setAllControlsVisibility(boolean topCon, boolean bottomCon, boolean startBtn, boolean loadingPro, boolean posterImg) {
        LogUtils.d();
        if (hideAll) {
            bottomContainer.setVisibility(INVISIBLE);
            startButton.setVisibility(INVISIBLE);
            loadingProgressBar.setVisibility(INVISIBLE);
            posterImageView.setVisibility(INVISIBLE);
            return;
        }

//        if (topCon) show(topContainer);
//        else hide(topContainer);
        if (bottomCon) show(bottomContainer, DURATION);
        else hide(bottomContainer, DURATION);
        if (startBtn) show(startButton, DURATION);
        else hide(startButton, DURATION);
        if (loadingPro) show(loadingProgressBar, DURATION);
        else hide(loadingProgressBar, DURATION);
        if (posterImg) show(posterImageView, DURATION);
        else hide(posterImageView, DURATION);

        if (hidePlay) {
            startButton.setVisibility(INVISIBLE);
            return;
        }

        notifyStatus(bottomCon);
    }


    private void notifyStatus(boolean show) {
        if (listener != null) listener.onShown(state, show);
    }

    private void notifyStatus() {
        notifyStatus(bottomContainer.getVisibility() == VISIBLE);
    }

    public void hideProgress() {
        hide(bottomContainer, DURATION);
    }

    public interface OnShownStatusListener {

        void onShown(int state, boolean show);
    }

    private OnShownStatusListener listener;

    public void setListener(OnShownStatusListener listener) {
        this.listener = listener;
    }

    /**
     * 待播放状态 显示正常的播放按钮
     * 其他状态隐藏
     */
    public void updateStartImage() {
        LogUtils.d();
        if (state == STATE_PLAYING || state == STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
            smallPlayBtn.setImageResource(R.drawable.img_pause_in_progress);
        } else if (state == STATE_ERROR) {
            startButton.setVisibility(INVISIBLE);
        } else {
            startButton.setImageResource(R.drawable.img_play_high_quality);
            smallPlayBtn.setImageResource(R.drawable.img_play_in_progress);
        }
    }

    /**
     * 延时后隐藏界面控件
     */
    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        dismiss_controller_timer = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        dismiss_controller_timer.schedule(mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (dismiss_controller_timer != null) {
            dismiss_controller_timer.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        cancelDismissControlViewTimer();
    }

    @Override
    public void reset() {
        super.reset();
        cancelDismissControlViewTimer();
    }

    /**
     * 暂停
     */
    public void pause() {
        if (state == STATE_PLAYING) {
            mediaInterface.pause();
            onStatePause();
        }
    }

    public void dismissControlView() {
        LogUtils.d();
        if (state != STATE_NORMAL && state != STATE_ERROR && state != STATE_AUTO_COMPLETE) {
            post(() -> {
                bottomContainer.setVisibility(View.INVISIBLE);
                notifyStatus();
//                topContainer.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.INVISIBLE);
            });
        }
    }

    public class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            dismissControlView();
        }

    }

    @Override
    protected void touchActionMove(float x, float y) {
        // 屏蔽拖拽进度
    }

    @Override
    public void dismissProgressDialog() {
    }

    @Override
    public void showVolumeDialog(float deltaY, int volumePercent) {
    }

    @Override
    public void dismissVolumeDialog() {
    }

    @Override
    public void showBrightnessDialog(int brightnessPercent) {
    }

    @Override
    public void dismissBrightnessDialog() {
    }

    public Dialog createDialogWithView(View localView) {
        Dialog dialog = new Dialog(jzvdContext, R.style.jz_style_dialog_progress);
        dialog.setContentView(localView);
        Window window = dialog.getWindow();
        window.addFlags(Window.FEATURE_ACTION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        window.setLayout(-2, -2);
        WindowManager.LayoutParams localLayoutParams = window.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        window.setAttributes(localLayoutParams);
        return dialog;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public void showProgressDialog(float deltaX, String seekTime, long seekTimePosition, String totalTime, long totalTimeDuration) {
        // 这是拖拽进度 暂时不需要
        LogUtils.d("deltaX: " + deltaX + " seekTime:  " + seekTime + " seekTimePos : " + seekTimePosition + " total : " + totalTime + " total time duration : " + totalTimeDuration);
//        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration);
//        if (mProgressDialog == null) {
//            View localView = LayoutInflater.from(jzvdContext).inflate(R.layout.layout_dyvideoplayer_progress, null);
//            mDialogProgressBar = localView.findViewById(R.id.duration_progressbar);
//            mDialogSeekTime = localView.findViewById(R.id.tv_current);
//            mDialogTotalTime = localView.findViewById(R.id.tv_duration);
//            mDialogIcon = localView.findViewById(R.id.duration_image_tip);
//            mProgressDialog = createDialogWithView(localView);
//        }
//        if (!mProgressDialog.isShowing()) {
//            mProgressDialog.show();
//        }
//
//        mDialogSeekTime.setText(seekTime);
//        mDialogTotalTime.setText(" / " + totalTime);
//        mDialogProgressBar.setProgress(totalTimeDuration <= 0 ? 0 : (int) (seekTimePosition * 100 / totalTimeDuration));
//        onCLickUiToggleToClear();
    }

    /**
     * 初始化
     */
    public void setUp(String url1, int screen, Class mediaInterfaceClass) {

        if ((System.currentTimeMillis() - gobakFullscreenTime) < 200) {
            return;
        }

        if ((System.currentTimeMillis() - gotoFullscreenTime) < 200) {
            return;
        }
        super.setUp(new JZDataSource(url1), screen, mediaInterfaceClass);
    }

    /**
     * 初始化
     */
    public void setUp(String url) {
        if ((System.currentTimeMillis() - gobakFullscreenTime) < 200) {
            return;
        }

        if ((System.currentTimeMillis() - gotoFullscreenTime) < 200) {
            return;
        }
        super.setUp(new JZDataSource(url), Jzvd.SCREEN_FULLSCREEN, JZMediaSystem.class);
    }

    public boolean isHideAll() {
        return hideAll;
    }

    public void setHideAll(boolean hideAll) {
        this.hideAll = hideAll;

        if (hideAll) {
            setAllControlsVisibility(false, false, false, false, false);
        }
    }

    public void setHidePlay(boolean hidePlay) {
        if (hidePlay) {
            setAllControlsVisibility(false, false, false, false, false);
        }
    }

    public boolean isHidePlay() {
        return hidePlay;
    }

    @Override
    public int getLayoutId() {
        return R.layout.custom_jzvd;
    }


    // 方法1：静音不停止播放
    public void mute() {
        LogUtils.d();
        // 这会修改系统声音 暂时这么做
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) jzvdContext.getSystemService(Context.AUDIO_SERVICE);
        }
        streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    // 方法2：解除静音
    public void unmute() {
//        mediaInterface.setVolume(1, 1); // 这个接口无效
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) jzvdContext.getSystemService(Context.AUDIO_SERVICE);
        }
        if (streamVolume == 0) {
            streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
//        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, 0);
    }

    /**
     * 跳转进度到0
     */
    public void seekTo0() {
        seekToManulPosition = 0;
        mediaInterface.seekTo(0);
    }
}
