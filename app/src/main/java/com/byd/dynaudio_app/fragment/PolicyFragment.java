package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.webkit.WebViewClient;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.databinding.LayoutFragmentPolicyBinding;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.network.NetworkType;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.ToastUtis;
import com.byd.dynaudio_app.utils.TouchUtils;

public class PolicyFragment extends BaseFragment<LayoutFragmentPolicyBinding, BaseViewModel> {

    public enum PolicyType {
        userAgreement, privacyPolicy
    }

    private final PolicyType mPolicyType;

    public PolicyFragment(PolicyType policyType) {
        this.mPolicyType = policyType;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.btnBack);
        if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网
            showError();
        }
        loadWebView();
    }

    private void loadWebView() {
        mDataBinding.webView.getSettings().setJavaScriptEnabled(true);
        mDataBinding.webView.setInitialScale(100);
        mDataBinding.webView.setWebViewClient(new WebViewClient());
        if (mPolicyType == PolicyType.userAgreement) {
            mDataBinding.txtTitle.setText(getString(R.string.user_agreement));
//            mDataBinding.webView.loadUrl("http://123.58.5.243:8004/agreement/index.html?tab=user#/pages/userAgreement/userAgreement");
            mDataBinding.webView.loadUrl("http://cms.goerdyna.com/agreement?tab=user");
        } else {
            mDataBinding.txtTitle.setText(getString(R.string.privacy_policy));
//            mDataBinding.webView.loadUrl("http://123.58.5.243:8004/agreement/index.html#/pages/privacyAgreement/privacyAgreement");
            mDataBinding.webView.loadUrl("http://cms.goerdyna.com/agreement");
        }
    }


    @Override
    public void onNetworkChanged(boolean isConnected, NetworkType type) {
        super.onNetworkChanged(isConnected, type);
        if (isConnected) {
//            showContentView();
//            loadWebView();
        }
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        if (!NetworkObserver.getInstance().isConnectNormal(mContext)) { // 没网
            ToastUtis.noNetWorkToast(mContext);
            showError();
        } else {
            showContentView();
            loadWebView();
        }
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> {
            LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
        });
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
        return R.layout.layout_fragment_policy;
    }
}
