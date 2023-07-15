package com.byd.dynaudio_app.fragment;

import static com.byd.dynaudio_app.utils.TouchUtils.hide;
import static com.byd.dynaudio_app.utils.TouchUtils.show;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.car.PModeListener;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.databinding.LayoutFragmentSalesPresentationBinding;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.manager.PhoneManager;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.manager.SalesPresentationManager;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;
import com.king.zxing.util.CodeUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.SimpleCallback;

import java.lang.reflect.Field;

public class SalesPresentationFragment extends BaseFragment<LayoutFragmentSalesPresentationBinding, BaseViewModel> {

    //有声文件路径
    private String videoPathVoiced;
    //有声URL
    private String salesSalesUrlVoiced;

    //无声文件路径
    private String videoPathSilent;

    //无声URL
    private String salesSalesUrlSsilent;

    private String videoPath;

    //销售文件路径
    private Handler delayShowHandler;
    private Handler progressHandler;
    private boolean isDraggingSeekBar; // 是否拽进度条

    private boolean isDemoMode; // 演示模式，左上角UI替换(返回 > 进入主页)

    private boolean isInWithMusicPlay = false; // 进入该页面的时候 是否在播放音乐

    public SalesPresentationFragment() {
    }

    public SalesPresentationFragment(boolean isDemoMode) {
        this.isDemoMode = isDemoMode;
    }

    public boolean isDemoMode() {
        return isDemoMode;
    }

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.imgBack, mDataBinding.llToMain);

        if (isDemoMode) {
            mDataBinding.imgBack.setVisibility(View.GONE);
            mDataBinding.llToMain.setVisibility(View.VISIBLE);
        }

        delayShowHandler = new Handler();
        progressHandler = new Handler();
        // initPlayer();
        if (MusicPlayManager.getInstance().isPlaying()) {
            MusicPlayManager.getInstance().pauseMusic();
            setInWithMusicPlay(true);
        }
        noNeedDisplayPlayer();
        PlayerVisionManager.getInstance().hideMiniPlayer();

        String prefix = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/video";

        videoPath = prefix + "/" + "SalesDemo.mp4";
        mDataBinding.player.setVideoPath(videoPath);
        mDataBinding.player.setOnPreparedListener(mediaPlayer -> setMute(true));

        // 获取下载演示视频
        SalesPresentationManager.getInstance().init(mContext);
        SalesPresentationManager.getInstance().requestData();

        PhoneManager.getInstance().addPhoneListener(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        // 电话停止，继续播放
                        if (PhoneManager.getInstance().isCallActive()) {
                            mDataBinding.player.start();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        // 来电或通话中，暂停播放
                        if (mDataBinding.player.isPlaying()) {
                            if (inPlay) {
                                showPauseAndNotHide();
                            } else {
                                mDataBinding.player.pause();
                            }
                            mDataBinding.imgPlayInProgress.setImageResource(R.drawable.img_play_in_progress);
                        }
                        break;
                }
            }
        });
    }

    Runnable hideRunnable = () -> {
        hide(mDataBinding.imgBack);
        hide(mDataBinding.llShare);
        hide(mDataBinding.layoutBottom);
    };

    Runnable progressRunnable = () -> { // p1的进度
        long duration = mDataBinding.player.getDuration();
        long currentPosition = mDataBinding.player.getCurrentPosition();
        int progress = (int) (currentPosition * 100 / duration);

        String currentTimeString = SPUtils.formatTime(currentPosition);
        String durationString = SPUtils.formatTime(duration);

        if (!isDraggingSeekBar) {  // 没有拖拽就更新
            mDataBinding.bottomSeekProgress.setProgress(progress);
            mDataBinding.current.setText(currentTimeString);
            mDataBinding.total.setText(durationString);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initListener() {
        mDataBinding.player.setOnCompletionListener(mp -> {
            if (inPlay) {
                showPauseAndNotHide();
            } else {
                if (!CarManager.getInstance().isPMode()) {
                    CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                    return;
                }
                mp.start();
            }
        });

        mDataBinding.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CarManager.getInstance().isPMode()) {
                    CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                    return;
                }
                startPlayAnimator(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                        super.onAnimationEnd(animation, isReverse);
                        // 动画播放完成后 播放视频、切换左上角图片为x，显示分享
                        playVideo1();
                    }
                });
            }
        });

        mDataBinding.imgBack.setOnClickListener(v -> {
            if (inPlay) {
                stopVideo();
            } else {
                LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
                mDataBinding.player.pause();
            }
        });

        mDataBinding.llToMain.setOnClickListener(view -> toFragment(new NewMainFragment()));

        mDataBinding.getRoot().setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 处于播放p1过程 点击显示界面上按键及进度条 延迟后消失
                    if (inPlay && mDataBinding.player.isPlaying()) {
                        if (mDataBinding.layoutBottom.getVisibility() == View.INVISIBLE) {
                            show(mDataBinding.imgBack);
                            show(mDataBinding.llShare);
                            show(mDataBinding.layoutBottom);
                        }

                        resetShowDoc();
                    }
                    return true;
            }
            return false;
        });

        startRunnableForProgress();

        mDataBinding.bottomSeekProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDraggingSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDraggingSeekBar = false;

                int to = (int) (seekBar.getProgress() * 1.f / 100.f * mDataBinding.player.getDuration());
                mDataBinding.player.seekTo(to);
                resetShowDoc();
            }
        });
        mDataBinding.llShare.setOnClickListener(v -> {
            boolean playing = mDataBinding.player.isPlaying();
            mDataBinding.player.pause();
            showShareDialog(playing);
            resetShowDoc();
        });

        mDataBinding.clImmersion.setOnClickListener(v -> toFragment(new Immersion3dFragment()));

        mDataBinding.imgPlayInProgress.setOnClickListener(view -> {
            if (!CarManager.getInstance().isPMode()) {
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                return;
            }

            if (mDataBinding.player.isPlaying()) {
                // mDataBinding.player.pause();
                showPauseAndNotHide();
            } else {
                mDataBinding.player.start();
            }

            mDataBinding.getRoot().postDelayed(() -> mDataBinding.imgPlayInProgress.setImageResource(
                    mDataBinding.player.isPlaying()
                            ? R.drawable.img_pause_in_progress
                            : R.drawable.img_play_in_progress
            ), 100);
            resetShowDoc();
        });

        CarManager.getInstance().addPModeListener(new PModeListener() {
            @Override
            public void onPModeChange(boolean isPMode) {
                super.onPModeChange(isPMode);
                if (!isPMode) {
                    if (mDataBinding.player.isPlaying()) {
                        CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                        if (inPlay) {
                            showPauseAndNotHide();
                        } else {
                            mDataBinding.player.pause();
                        }
                        mDataBinding.imgPlayInProgress.setImageResource(R.drawable.img_play_in_progress);
                    }
                }

                mDataBinding.bottomSeekProgress.setEnabled(isPMode);
            }
        });
    }

    /**
     * 0530 暂停时候 界面的操作的东西不消失
     */
    private void showPauseAndNotHide() {
        mDataBinding.player.pause();
        mDataBinding.imgPlayInProgress.setImageResource(R.drawable.img_play_in_progress);
        show(mDataBinding.imgBack);
        show(mDataBinding.llShare);
        show(mDataBinding.layoutBottom);

        delayShowHandler.postDelayed(() ->
                delayShowHandler.removeCallbacks(hideRunnable), 1000);
    }

    /**
     * 重置显示控制栏的计时
     */
    private void resetShowDoc() {
        delayShowHandler.removeCallbacks(hideRunnable);
        delayShowHandler.postDelayed(hideRunnable, 5000);
    }

    private void startRunnableForProgress() {
        progressHandler.post(progressRunnable);
        mDataBinding.getRoot().postDelayed(() -> startRunnableForProgress(), 100);
    }

    @Override
    protected void initObserver() {

    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_sales_presentation;
    }

    /**
     * 开启播放按钮的动画 先缩小 然后放大
     */
    private void startPlayAnimator(Animator.AnimatorListener listener) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(mDataBinding.imgPlay, "scaleX", 1, 0.9f, 1.f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(mDataBinding.imgPlay, "scaleY", 1, 0.9f, 1.f);

        AnimatorSet set = new AnimatorSet();
        set.setDuration(330);
        set.playTogether(scaleX, scaleY);

        set.addListener(listener);

        set.start();
    }

    private boolean inPlay = false;

    public boolean isInPlay() {
        return inPlay;
    }

    private void playVideo1() {
        mDataBinding.player.seekTo(0);
        setMute(false);
        mDataBinding.player.start();

        // 修改左上角图标
        mDataBinding.imgBack.setImageResource(R.mipmap.ic_fork);
        mDataBinding.imgPlayInProgress.setImageResource(R.drawable.img_pause_in_progress);
        // 隐藏组件
        if (isDemoMode) {
            hide(mDataBinding.llToMain);
        }
        hide(mDataBinding.imgBack);
        hide(mDataBinding.tvTitle);
        hide(mDataBinding.tvSubtitle);
        hide(mDataBinding.imgPlay);
        hide(mDataBinding.tvClick);
        hide(mDataBinding.clImmersion);

        // 修改全局变量
        inPlay = true;
    }

    private void setMute(boolean mute) {
        // LogUtils.d("mute : " + mute);
//        if (getReflectMediaPlayer() != null) {
        MediaPlayer reflectMediaPlayer = getReflectMediaPlayer();
        reflectMediaPlayer.setVolume(mute ? 0 : 1.f, mute ? 0 : 1.f);
//        }
    }

    private MediaPlayer getReflectMediaPlayer() {
        try {
            Class clazz = Class.forName("android.widget.VideoView");
            Field field = clazz.getDeclaredField("mMediaPlayer");
            field.setAccessible(true);
            return (MediaPlayer) field.get(mDataBinding.player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void stopVideo() {
        if (isDemoMode) {
            mDataBinding.imgBack.setVisibility(View.GONE);
            show(mDataBinding.llToMain);
        } else {
            show(mDataBinding.imgBack);
        }
        show(mDataBinding.tvTitle);
        show(mDataBinding.tvSubtitle);
        show(mDataBinding.tvClick);
        show(mDataBinding.imgPlay);
        show(mDataBinding.clImmersion);

        hide(mDataBinding.llShare);
        hide(mDataBinding.layoutBottom);

        mDataBinding.player.seekTo(0);
        setMute(true);
        mDataBinding.getRoot().postDelayed(() -> {
            if (!CarManager.getInstance().isPMode()) {
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                return;
            }
            mDataBinding.player.start();
        }, 100);

        // 修改左上角图标 隐藏右上角分享
        mDataBinding.imgBack.setImageResource(R.mipmap.ic_back);
        hide(mDataBinding.llShare);
        // 修改全局变量
        inPlay = false;

        delayShowHandler.removeCallbacks(hideRunnable);
    }

    private void showShareDialog(boolean isPlayingBeforeShowDialog) {
        BasePopupView share = new XPopup.Builder(mContext).setPopupCallback(new SimpleCallback() {
                    @Override
                    public void onDismiss(BasePopupView popupView) {
                        super.onDismiss(popupView);

                        // 弹窗隐藏时候 如果之前是播放的就继续播放
                        if (isPlayingBeforeShowDialog && CarManager.getInstance().isPMode())
                            mDataBinding.player.start();
                    }
                })
                .hasStatusBar(false)
                .asLoading(getString(R.string.share_video), R.layout.layout_dialog_share_video, LoadingPopupView.Style.Spinner)
                .show();

        ImageView imageView = share.findViewById(R.id.img_qr);

        String sharePath = "https://dynfiles.s3.cn-northwest-1.amazonaws.com.cn/h5/index.html";
        Bitmap qrCode = CodeUtils.createQRCode(sharePath, 340, Color.BLACK);

        Glide.with(mContext).setDefaultRequestOptions(ImageLoader.getOptions()).load(qrCode).into(imageView);

        View imgFork = share.findViewById(R.id.img_fork);
        imgFork.setOnClickListener(view -> share.dismiss());

    }

    /**
     * 进入沉浸音和从沉浸音返回会走这里
     *
     * @param hidden True if the fragment is now hidden, false otherwise.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        MainActivity activity = (MainActivity) mContext;
        // if (!(activity.getCurrentFragment() instanceof SalesPresentationFragment)) return;
        LogUtils.d("sales hidden : " + hidden);

        // 这里只会影响外层视频
        if (hidden) {
            mDataBinding.player.pause();
            mDataBinding.player.seekTo(0);
        } else {
            if (!CarManager.getInstance().isPMode()) {
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                return;
            }
            mDataBinding.player.start();
        }
    }

    // 从首页来会走
    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) mContext;
        if (!(activity.getCurrentFragment() instanceof SalesPresentationFragment)) return;
        LogUtils.d("sales :  " + CarManager.getInstance().isPMode());
        if (beforePauseProgress < 100) beforePauseProgress = 100;
        mDataBinding.player.seekTo(beforePauseProgress);
        if (!CarManager.getInstance().isPMode()) {
            if (mDataBinding.player.isPlaying()) {
                mDataBinding.player.pause();
            }
            CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
            return;
        }

        if (!PhoneManager.getInstance().isCallActive()
        ) {
            if (inPlay && !beforePauseIsPlaying) {
                return;
            }
            mDataBinding.imgPlayInProgress.setImageResource(R.drawable.img_pause_in_progress);
            mDataBinding.player.start();
        }
    }

    private int beforePauseProgress;// 用于解决home键 然后再返回后不能回到之前的进度播放
    private boolean beforePauseIsPlaying = true;

    // 返回首页 home键会走
    @Override
    public void onPause() {
        super.onPause();
        beforePauseIsPlaying = mDataBinding.player.isPlaying();
        MainActivity activity = (MainActivity) mContext;
        if (!(activity.getCurrentFragment() instanceof SalesPresentationFragment)) return;
        LogUtils.d("sales");
        mDataBinding.player.pause();
        mDataBinding.imgPlayInProgress.setImageResource(R.drawable.img_play_in_progress);
        beforePauseProgress = mDataBinding.player.getCurrentPosition();
    }

    public boolean isInWithMusicPlay() {
        return isInWithMusicPlay;
    }

    public void setInWithMusicPlay(boolean inWithMusicPlay) {
        isInWithMusicPlay = inWithMusicPlay;
    }
}
