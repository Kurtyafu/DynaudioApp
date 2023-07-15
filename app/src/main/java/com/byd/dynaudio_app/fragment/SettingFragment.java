package com.byd.dynaudio_app.fragment;

import android.view.View;
import android.widget.Toast;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutFragmentSettingBinding;
import com.byd.dynaudio_app.dialog.CustomDialog;
import com.byd.dynaudio_app.dialog.FeedBackDialog;
import com.byd.dynaudio_app.dialog.IOnClickListener;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.CacheDataManager;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

public class SettingFragment extends BaseFragment<LayoutFragmentSettingBinding, BaseViewModel> {

    enum SkinMode {
        system, dark, light
    }

    private SkinMode mSkinMode = SkinMode.system;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.btnBack);

        if (UserController.getInstance().isLoginStates()) {
            mDataBinding.txtLogOut.setVisibility(View.VISIBLE);
        } else {
            mDataBinding.rlUserManagement.setVisibility(View.GONE);
        }
        try {
            String cacheSize = CacheDataManager.getTotalCacheSize(mContext);
            mDataBinding.txtCacheSize.setText(cacheSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void initListener() {
        initOnClickListener();
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
        return R.layout.layout_fragment_setting;
    }

    private void initOnClickListener() {
        mDataBinding.btnBack.setOnClickListener(view -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));

        mDataBinding.rlUserManagement.setOnClickListener(view -> toFragment(new UserManagementFragment()));
        mDataBinding.rlSkinSetting.setOnClickListener(view -> toFragment(new SkinModeFragment()));

        mDataBinding.rlClear.setOnClickListener(view -> clearCache());
        mDataBinding.rlFeedBack.setOnClickListener(view -> new FeedBackDialog(mContext).show());
        mDataBinding.rlPrivacyPolicy.setOnClickListener(view -> toFragment(new PolicyFragment(PolicyFragment.PolicyType.privacyPolicy)));
        mDataBinding.rlUserAgreement.setOnClickListener(view -> toFragment(new PolicyFragment(PolicyFragment.PolicyType.userAgreement)));
        mDataBinding.rlAbout.setOnClickListener(view -> toFragment(new AboutFragment()));

        mDataBinding.txtLogOut.setOnClickListener(view -> logOut());
    }

    private void clearCache() {
        CacheDataManager.clearAllCache(mContext);
        try {
            String cacheSize = CacheDataManager.getTotalCacheSize(mContext);
            mDataBinding.txtCacheSize.setText(cacheSize);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        DBController.clearAll(mContext);
        SPUtils.clearSP(mContext);
        CustomToast.makeText(mContext, "清除成功", Toast.LENGTH_SHORT).show();
    }

    private void logOut() {
        CustomDialog dialog = new CustomDialog(mContext);
        dialog.setTitle("退出登录");
        dialog.setContent("退出账号后，将不能同步收藏及播放记录，确定退出吗？");
        dialog.setClickListener(new IOnClickListener() {
            @Override
            public void onPositiveButtonListener() {
                UserController.getInstance().setLoginStates(false);
                UserController.getInstance().notifyLogOut();
                CustomToast.makeText(mContext, "退出登录成功", Toast.LENGTH_SHORT).show();
                SPUtils.removeSP(mContext, SPUtils.SP_KEY_USER_ID);
                DBController.clearTimeRemaining(mContext);
                LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
                // 游客登录
                String equipmentId = UserController.getInstance().getEquipmentId();// 设备号
                LogUtils.d("进入了设备号" + equipmentId);
                ApiClient.getInstance().visitorLogin(equipmentId).subscribe(new BaseObserver<>() {
                    @Override
                    protected void onSuccess(BaseBean<UserInfoBean> bean) {
                        if (bean.isSuccess()) {
                            UserInfoBean data = bean.getData();
                            UserController.getInstance().setToken(data.getToken());
                            SPUtils.putValue(mContext, SPUtils.SP_KEY_TOKEN, data.getToken());
                        }
                    }

                    @Override
                    protected void onFail(Throwable e) {

                    }
                });
            }
        });
        dialog.show();
    }
}
