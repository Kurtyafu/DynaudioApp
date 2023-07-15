package com.byd.dynaudio_app.fragment;


import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.os.Environment;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.databinding.LayoutFragmentGuideBinding;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

public class GuideFragment extends BaseFragment<LayoutFragmentGuideBinding, BaseViewModel> {
    private CountDownTimer countDownTimer;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.txtSkip);

        String prefix = mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
        String videoPath = prefix + "/video/SalesDemo.mp4";
        mDataBinding.videoView.setVideoPath(videoPath);
        mDataBinding.videoView.start();

        countDownTimer = new CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                mDataBinding.txtSkip.setText("跳过(" + Math.round((double) l / 1000) + ")");
            }

            @Override
            public void onFinish() {
                toFragment(new NewMainFragment());
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void initListener() {
        mDataBinding.txtSkip.setOnClickListener(view -> {
            toFragment(new NewMainFragment());
            countDownTimer.cancel();
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            mDataBinding.videoView.pause();
        }
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
        return R.layout.layout_fragment_guide;
    }
}
