package com.byd.dynaudio_app.fragment;

import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;

import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.manager.PhoneManager;
import com.byd.dynaudio_app.manager.SalesPresentationManager;
import com.byd.dynaudio_app.utils.LogUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;

/**
 * 3d沉浸音效
 */
public class Immersion3dFragment extends BaseExperienceFragment {

    private static final String TAG = Immersion3dFragment.class.getSimpleName();

    private int currentType;//0,1,2
    private boolean isFirstCreate;

    private String thunderstormPath;
    private String thunderstormStereoPath;
    private String symphonyPath;
    private String symphonyStereoPath;
    private String helicopterPath;
    private String helicopterStereoPath;
    private final int originEffect = CarManager.getInstance().getDyna3DSoundEffect();
    private ProgressDrawable progressDrawable;

    @Override
    protected void initObserver() {
        String prefix = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/video/";

        thunderstormPath = prefix + "thunderstorm.mp4";// 雷雨
//        thunderstormStereoPath = prefix + "thunderstormStereo.mp4";// 雷雨(立体声)
        symphonyPath = prefix + "symphony.mp4";// 交响乐
//        symphonyStereoPath     = prefix + "symphonyStereo.mp4";// 交响乐(立体声)
        helicopterPath = prefix + "helicopter.mp4";// 直升机
//        helicopterStereoPath   = prefix + "helicopterStereo.mp4";// 直升机(立体声)

        mDataBinding.playerView.setOnErrorListener((mediaPlayer, i, i1) -> {
//            CustomToast.makeText(mContext, "无法播放该视频", Toast.LENGTH_SHORT).show();
            if (progressDrawable == null) {
                progressDrawable = new ProgressDrawable();
                mDataBinding.imgProgress.setImageDrawable(progressDrawable);
            }
            mDataBinding.llProgress.setVisibility(View.VISIBLE);
            progressDrawable.start();
            return true;
        });

        SalesPresentationManager.getInstance().setLoadListener(new SalesPresentationManager.FileLoadListener() {
            @Override
            public void progress(BaseDownloadTask task, int progress) {
                if (progressDrawable != null && currentType == (int) task.getTag()) {
                    mDataBinding.txtProgress.setText("正在加载中 " + progress + "%");
                }
            }

            @Override
            public void completed(BaseDownloadTask task) {
                if (progressDrawable != null && currentType == (int) task.getTag()) {
                    progressDrawable.stop();
                    mDataBinding.llProgress.setVisibility(View.GONE);
                    onTopClick(currentType);
                }
            }
        });

        LogUtils.i(TAG, "进入3D沉浸音效页面，当前系统值 >>> " + originEffect);
        // 需求调整，为什么这样我也不理解，fuck
        CarManager.getInstance().setDyna3DSoundEffect(4);
        setBottom(0);
        onTopClick(0);
        LogUtils.i(TAG, "ON按钮开");

        PhoneManager.getInstance().addPhoneListener(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                super.onCallStateChanged(state, phoneNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        // 电话停止，继续播放
                        if (PhoneManager.getInstance().isCallActive()) {
                            mDataBinding.playerView.start();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        // 来电或通话中，暂停播放
                        if (mDataBinding.playerView.isPlaying()) {
                            mDataBinding.playerView.pause();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CarManager.getInstance().setDyna3DSoundEffect(originEffect);
        LogUtils.i(TAG, "离开3D沉浸音效页面，当前系统值 >>> " + originEffect);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) mContext;
        // if (!(activity.getCurrentFragment() instanceof SalesPresentationFragment)) return;
        LogUtils.d("im3d : " + CarManager.getInstance().isPMode());

        if (isPressHome) {
//            if (beforePauseProgress < 100) beforePauseProgress = 100;
            mDataBinding.playerView.seekTo(beforePauseProgress);
            if (!CarManager.getInstance().isPMode()) {
                if (mDataBinding.playerView.isPlaying()) {
                    mDataBinding.playerView.pause();
                }
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                return;
            }
            if (!PhoneManager.getInstance().isCallActive()) {
                mDataBinding.playerView.start();
            }
        } else {
            if (!isFirstCreate) {
                isFirstCreate = true;
                return;
            }
            int effect = CarManager.getInstance().getDyna3DSoundEffect();
            if (effect > 1 && effect <= 6) {
                setBottom(0);
            } else {
                setBottom(1);
            }

            onTopClick(0);
        }
    }

    /**
     * 底部item点击
     */
    @Override
    protected void onBottomClick(int index) {
        // super.onBottomClick(index);
        // 如果Index = 0 开启3d沉浸音
        if (index == 0) {
            CarManager.getInstance().setDyna3DSoundEffect(4);
            LogUtils.i(TAG, "点击ON按钮，音效值 >>> 0x4");
        } else {
            CarManager.getInstance().setDyna3DSoundEffect(1);
            LogUtils.i(TAG, "点击OFF按钮，音效值 >>> 0x1");
        }

        switch (currentType) {
            case 0:
//                if (index == 0) {
                mDataBinding.playerView.setVideoPath(thunderstormPath);
//                } else {
//                    mDataBinding.playerView.setVideoPath(thunderstormStereoPath);
//                }
                break;
            case 1:
//                if (index == 0) {
                mDataBinding.playerView.setVideoPath(symphonyPath);
//                } else {
//                    mDataBinding.playerView.setVideoPath(symphonyStereoPath);
//                }
                break;
            case 2:
//                if (index == 0) {
                mDataBinding.playerView.setVideoPath(helicopterPath);
//                } else {
//                    mDataBinding.playerView.setVideoPath(helicopterStereoPath);
//                }
                break;
        }

        mDataBinding.playerView.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        LogUtils.d("im3d : " + hidden);
        super.onHiddenChanged(hidden);
        if (hidden) {
            mDataBinding.playerView.pause();
            mDataBinding.playerView.seekTo(0);
        }
    }

    private boolean isPressHome = false;// 实测按下home键才会走
    private int beforePauseProgress;// 用于解决home键 然后再返回后不能回到之前的进度播放

    @Override
    public void onPause() {
        super.onPause();
        isPressHome = true;
        mDataBinding.playerView.pause();
        beforePauseProgress = mDataBinding.playerView.getCurrentPosition();
        LogUtils.d("im3d : " + CarManager.getInstance().isPMode());
    }

    /**
     * 顶部item点击
     */
    @Override
    protected void onTopClick(int index) {
        LogUtils.d("index : " + index + " before : " + topIndex);
        if (progressDrawable != null) {
            progressDrawable.stop();
            mDataBinding.llProgress.setVisibility(View.GONE);
        }
        if (!CarManager.getInstance().isPMode() && topIndex != -1) {
            if (mDataBinding.playerView.isPlaying()) {
                mDataBinding.playerView.pause();
            }
            CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
            return;
        }

        super.onTopClick(index);
        switch (index) {
            case 0:
                currentType = 0;
                mDataBinding.playerView.setVideoPath(thunderstormPath);
                break;
            case 1:
                currentType = 1;
                mDataBinding.playerView.setVideoPath(symphonyPath);
                break;
            case 2:
                currentType = 2;
                mDataBinding.playerView.setVideoPath(helicopterPath);
                break;
        }

        mDataBinding.playerView.start();
    }

    @Override
    protected int getTitleText() {
        return R.string.immersion3d;
    }

    @Override
    protected int getLeftText() {
        return R.string.thunder_rain;
    }

    @Override
    protected int getMidText() {
        return R.string.symphony;
    }

    @Override
    protected int getRightText() {
        return R.string.helicopter;
    }

    @Override
    protected int getLeftCheckedImg() {
        return R.drawable.img_thunder;
    }

    @Override
    protected int getLeftUncheckedImg() {
        return R.drawable.img_thunder_unchecked;
    }

    @Override
    protected int getMidCheckedImg() {
        return R.drawable.img_symphony;
    }

    @Override
    protected int getMidUncheckedImg() {
        return R.drawable.img_symphony_unchecked;
    }

    @Override
    protected int getRightCheckedImg() {
        return R.drawable.img_helicopter;
    }

    @Override
    protected int getRightUncheckedImg() {
        return R.drawable.img_helicopter_unchecked;
    }

    @Override
    protected int getTipsText() {
        return R.string.tips_in_3d_immersion;
    }
}
