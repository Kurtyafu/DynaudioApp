package com.byd.dynaudio_app.fragment;

import android.graphics.Color;

import androidx.annotation.CallSuper;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.car.PModeListener;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.databinding.LayoutFragmentExperienceBinding;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

public abstract class BaseExperienceFragment extends BaseFragment<LayoutFragmentExperienceBinding, BaseViewModel> {

    private int textCheckedColor;
    private int textUncheckedColor;

    @CallSuper
    @Override
    protected void initView() {
        noNeedDisplayPlayer();

        TouchUtils.bindClickItem(
                mDataBinding.imgBack,
                mDataBinding.imgLeft,
                mDataBinding.imgMid,
                mDataBinding.imgRight,
                mDataBinding.tvLeft,
                mDataBinding.tvMid,
                mDataBinding.tvRight);

        textCheckedColor = Color.parseColor("#FFFFFFFF");
        textUncheckedColor = Color.parseColor("#73FFFFFF");

        mDataBinding.tvTitle.setText(getTitleText());

        mDataBinding.tvTips.setText(getTipsText());

        mDataBinding.tvLeft.setText(getLeftText());
        mDataBinding.tvMid.setText(getMidText());
        mDataBinding.tvRight.setText(getRightText());

        setTop(-1);

        mDataBinding.playerView.setOnPreparedListener(mediaPlayer -> {
            if (!CarManager.getInstance().isPMode()) {
                mDataBinding.getRoot().postDelayed(() -> mDataBinding.playerView.pause(), 10);
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
            }
        });
        CarManager.getInstance().addPModeListener(new PModeListener() {
            @Override
            public void onPModeChange(boolean isPMode) {
                super.onPModeChange(isPMode);
                if (!isPMode && mDataBinding.playerView.isPlaying()) {
                    mDataBinding.playerView.pause();
                    CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                }
            }
        });
    }

    protected abstract int getTitleText();

    protected abstract int getLeftText();

    protected abstract int getMidText();

    protected abstract int getRightText();

    @Override
    protected void initListener() {
        mDataBinding.imgLeft.setOnClickListener(v -> onTopClick(0));
        mDataBinding.imgMid.setOnClickListener(v -> onTopClick(1));
        mDataBinding.imgRight.setOnClickListener(v -> onTopClick(2));

        mDataBinding.tvLeft.setOnClickListener(v -> onTopClick(0));
        mDataBinding.tvMid.setOnClickListener(v -> onTopClick(1));
        mDataBinding.tvRight.setOnClickListener(v -> onTopClick(2));

        mDataBinding.switchView.setOnCheckedChangeListener((sb) -> {
            if (!CarManager.getInstance().isPMode()) {
                if (mDataBinding.playerView.isPlaying()) {
                    mDataBinding.playerView.pause();
                }
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                return;
            }

            if (sb) {
                onBottomClick(0);
            } else {
                onBottomClick(1);
            }
        });

        // 返回
        mDataBinding.imgBack.setOnClickListener(v ->
                LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));

        mDataBinding.playerView.setOnCompletionListener(mediaPlayer -> {
            if (!CarManager.getInstance().isPMode()) {
                if (mDataBinding.playerView.isPlaying()) {
                    mDataBinding.playerView.pause();
                }
                CustomToast.makeText(mContext, getString(R.string.currentP_cantPlayVideo)).show();
                return;
            }
            mediaPlayer.start();
        });// 背景视频无线循环播放
    }

    protected int topIndex;

    /**
     * 设置头部选中
     */
    public void setTop(int index) {
        topIndex = index;

        mDataBinding.imgLeft.setImageResource(index == 0 ? getLeftCheckedImg() : getLeftUncheckedImg());
        mDataBinding.imgMid.setImageResource(index == 1 ? getMidCheckedImg() : getMidUncheckedImg());
        mDataBinding.imgRight.setImageResource(index == 2 ? getRightCheckedImg() : getRightUncheckedImg());

        mDataBinding.tvLeft.setTextColor(index == 0 ? textCheckedColor : textUncheckedColor);
        mDataBinding.tvMid.setTextColor(index == 1 ? textCheckedColor : textUncheckedColor);
        mDataBinding.tvRight.setTextColor(index == 2 ? textCheckedColor : textUncheckedColor);
    }

    public void setBottom(int index) {
        // 不要疑惑，正常的开关左为“关”右为“开”，这个傻B设计刚好相反
        LogUtils.d("index : " + index);
        mDataBinding.switchView.setChecked(index == 0);
        if (index == 0) {// ON
        } else {// OFF
            mDataBinding.switchView.setChecked(true);
        }
    }

    protected abstract int getLeftCheckedImg();

    protected abstract int getMidCheckedImg();

    protected abstract int getRightCheckedImg();

    protected abstract int getLeftUncheckedImg();

    protected abstract int getMidUncheckedImg();

    protected abstract int getRightUncheckedImg();

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_experience;
    }

    protected abstract int getTipsText();

    @CallSuper
    protected void onTopClick(int index) {
        setTop(index);
    }

    @CallSuper
    protected void onBottomClick(int index) {
        setBottom(index);
    }
}
