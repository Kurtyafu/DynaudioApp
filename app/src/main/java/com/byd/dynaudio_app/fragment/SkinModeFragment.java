package com.byd.dynaudio_app.fragment;


import android.view.View;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.databinding.LayoutFragmentSkinBinding;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.TouchUtils;

public class SkinModeFragment extends BaseFragment<LayoutFragmentSkinBinding, BaseViewModel> {
    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.btnBack);
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));
        mDataBinding.rlFollowSystem.setOnClickListener(view -> checkSystem());
        mDataBinding.rlDarkMode.setOnClickListener(view -> checkDark());
        mDataBinding.rlLightMode.setOnClickListener(view -> checkLight());
    }

    private void checkLight() {
        if (mDataBinding.viewCheckLight.getVisibility() == View.VISIBLE) return;
        mDataBinding.viewCheckSystem.setVisibility(View.GONE);
        mDataBinding.viewCheckDark.setVisibility(View.GONE);
        mDataBinding.viewCheckLight.setVisibility(View.VISIBLE);
    }

    private void checkDark() {
        if (mDataBinding.viewCheckDark.getVisibility() == View.VISIBLE) return;
        mDataBinding.viewCheckSystem.setVisibility(View.GONE);
        mDataBinding.viewCheckDark.setVisibility(View.VISIBLE);
        mDataBinding.viewCheckLight.setVisibility(View.GONE);
    }

    private void checkSystem() {
        if (mDataBinding.viewCheckSystem.getVisibility() == View.VISIBLE) return;
        mDataBinding.viewCheckSystem.setVisibility(View.VISIBLE);
        mDataBinding.viewCheckDark.setVisibility(View.GONE);
        mDataBinding.viewCheckLight.setVisibility(View.GONE);
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
        return R.layout.layout_fragment_skin;
    }

}
