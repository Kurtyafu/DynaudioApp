package com.byd.dynaudio_app.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.databinding.LayoutFragmentSoundSettingsBinding;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

public class SoundSettingsFragment extends BaseFragment<LayoutFragmentSoundSettingsBinding, BaseViewModel> implements View.OnClickListener {

    private int topCheckedBgColor;
    private int topUncheckedColor;
    private SsfFragment ssfFragment;
    private ScFragment scFragment;

    @Override
    protected void initView() {
        // LogUtils.d("in22222222222....");
        TouchUtils.bindClickItem(
                mDataBinding.imgBack,
                mDataBinding.llSsf,
                mDataBinding.llSc);

        // ui无mini播放器 先直接隐藏
        noNeedDisplayPlayer();
        // PlayerVisionManager.getInstance().hideMiniPlayer();

        topCheckedBgColor = Color.parseColor("#FFCF022D");
        topUncheckedColor = Color.parseColor("#FF272829");

        ssfFragment = new SsfFragment();
        scFragment = new ScFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        Integer value = LiveDataBus.get().with(LiveDataBusConstants.sound_settings_tab, Integer.class)
                .getValue();
        boolean isSc = value != null && value == 1;
        toSubFragment(isSc ? scFragment : ssfFragment);
        changeTop(!isSc);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        Integer value = LiveDataBus.get().with(LiveDataBusConstants.sound_settings_tab, Integer.class)
                .getValue();
        boolean isSc = value != null && value == 1;
        toSubFragment(isSc ? scFragment : ssfFragment);
        changeTop(!isSc);
    }

    @Override
    protected void initListener() {
        mDataBinding.llSsf.setOnClickListener(this);
        mDataBinding.llSc.setOnClickListener(this);

        mDataBinding.imgBack.setOnClickListener(v ->
                LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));

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
        return R.layout.layout_fragment_sound_settings;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_ssf:  // 声场聚焦
                changeTop(true);
                toSubFragment(ssfFragment);
                break;
            case R.id.ll_sc:  // 声音特色
                changeTop(false);
                toSubFragment(scFragment);
                break;
        }
    }

    private void toSubFragment(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.ll_soundsetting, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * 改变顶部的ui
     */
    private void changeTop(boolean isSsf) {
        mDataBinding.llSsf.setBackgroundColor(isSsf ? topCheckedBgColor : topUncheckedColor);
        mDataBinding.llSc.setBackgroundColor(isSsf ? topUncheckedColor : topCheckedBgColor);
        mDataBinding.imgSsf.setImageResource(isSsf ? R.drawable.img_ssf_checked : R.drawable.img_ssf_unchecked);
        mDataBinding.imgSc.setImageResource(isSsf ? R.drawable.img_sc_unchecked : R.drawable.img_sc_checked);
        mDataBinding.tvSsf.setTextColor(mContext.getColor(isSsf ? R.color.white : R.color.white_alpha_45));
        mDataBinding.tvSc.setTextColor(mContext.getColor(isSsf ? R.color.white_alpha_45 : R.color.white));
        mDataBinding.tvSsf.setTypeface(null, isSsf ? Typeface.BOLD : Typeface.NORMAL);
        mDataBinding.tvSc.setTypeface(null, isSsf ? Typeface.NORMAL : Typeface.BOLD);


        LiveDataBus.get().with(LiveDataBusConstants.sound_settings_tab, Integer.class)
                .postValue(isSsf ? 0 : 1);
    }
}
