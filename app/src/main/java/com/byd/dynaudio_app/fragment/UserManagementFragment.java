package com.byd.dynaudio_app.fragment;


import android.text.TextUtils;
import android.widget.Toast;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.databinding.LayoutFragmentUserManagementBinding;
import com.byd.dynaudio_app.dialog.BindPhoneDialog;
import com.byd.dynaudio_app.dialog.BindWechatDialog;
import com.byd.dynaudio_app.dialog.CustomDialog;
import com.byd.dynaudio_app.dialog.IOnClickListener;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.TouchUtils;

public class UserManagementFragment extends BaseFragment<LayoutFragmentUserManagementBinding, BaseViewModel> {
    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.btnBack, mDataBinding.txtDelUser);

        UserInfoBean userInfo = UserController.getInstance().getUserInfo();
        if (!TextUtils.isEmpty(userInfo.getWxId())) {
            mDataBinding.bindWechat.setSelected(true);
        }
        if (!TextUtils.isEmpty(userInfo.getPhone())) {
            mDataBinding.bindPhone.setSelected(true);
        }
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1));
        mDataBinding.txtDelUser.setOnClickListener(view -> toFragment(new DeleteUserFragment()));
        mDataBinding.bindWechat.setOnClickListener(view -> {
            if (mDataBinding.bindWechat.isSelected()) {
                unBindWechat();
            } else {
                bindWechat();
            }
        });
        mDataBinding.bindPhone.setOnClickListener(view -> {
            if (mDataBinding.bindPhone.isSelected()) {
                unBindPhone();
            } else {
                bindPhone();
            }
        });
    }

    private void bindPhone() {
        BindPhoneDialog dialog = new BindPhoneDialog(mContext);
        dialog.setListener(() -> {
            CustomToast.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
            mDataBinding.bindPhone.setSelected(true);
        });
        dialog.show();
    }

    private void unBindPhone() {
        if (mDataBinding.bindWechat.isSelected() && mDataBinding.bindPhone.isSelected()) {
            CustomDialog dialog = new CustomDialog(mContext);
            dialog.setTitle("解除手机号绑定");
            dialog.setContent("解绑手机号后将\n无法继续使用它登录丹拿之声确定解绑吗?");
            dialog.setPositiveText("解除绑定");
            dialog.setClickListener(new IOnClickListener() {
                @Override
                public void onPositiveButtonListener() {
                    String userId = UserController.getInstance().getUserId();
                    ApiClient.getInstance().unbindPhone(userId).subscribe(new BaseObserver<>() {
                        @Override
                        protected void onSuccess(BaseBean bean) {
                            if (bean.isSuccess()) {
                                CustomToast.makeText(mContext, "解绑成功", Toast.LENGTH_SHORT).show();
                                mDataBinding.bindPhone.setSelected(false);
                            }
                        }

                        @Override
                        protected void onFail(Throwable e) {

                        }
                    });
                }
            });
            dialog.show();
        } else {
            CustomDialog dialog = new CustomDialog(mContext);
            dialog.setTitle("更改手机号绑定");
            dialog.setContent("更改绑定手机号后将\n无法继续使用此手机号登录丹拿之声，确定更改吗?");
            dialog.setPositiveText("更改绑定");
            dialog.setClickListener(new IOnClickListener() {
                @Override
                public void onPositiveButtonListener() {
                    bindPhone();
                }
            });
            dialog.show();
        }
    }

    private void bindWechat() {
        BindWechatDialog dialog = new BindWechatDialog(mContext);
        dialog.setListener(() -> {
            CustomToast.makeText(mContext, "绑定成功", Toast.LENGTH_SHORT).show();
            mDataBinding.bindWechat.setSelected(true);
        });
        dialog.show();
    }

    private void unBindWechat() {
        if (mDataBinding.bindWechat.isSelected() && mDataBinding.bindPhone.isSelected()) {
            CustomDialog dialog = new CustomDialog(mContext);
            dialog.setTitle("解除微信号绑定");
            dialog.setContent("解绑微信账号后将\n无法继续使用它登录丹拿之声，确定解绑吗?");
            dialog.setPositiveText("解除绑定");
            dialog.setClickListener(new IOnClickListener() {
                @Override
                public void onPositiveButtonListener() {
                    String userId = UserController.getInstance().getUserId();
                    ApiClient.getInstance().unbindWx(userId).subscribe(new BaseObserver<>() {
                        @Override
                        protected void onSuccess(BaseBean bean) {
                            if (bean.isSuccess()) {
                                CustomToast.makeText(mContext, "解绑成功", Toast.LENGTH_SHORT).show();
                                mDataBinding.bindWechat.setSelected(false);
                            }
                        }

                        @Override
                        protected void onFail(Throwable e) {

                        }
                    });
                }
            });
            dialog.show();
        } else {
            CustomDialog dialog = new CustomDialog(mContext);
            dialog.setTitle("请先绑定手机号");
            dialog.setContent("当前账号为唯一账号，不可解绑\n您可以绑定手机号后再解绑当前账号。");
            dialog.setPositiveText("去绑定");
            dialog.setClickListener(new IOnClickListener() {
                @Override
                public void onPositiveButtonListener() {
                    bindPhone();
                }
            });
            dialog.show();
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
        return R.layout.layout_fragment_user_management;
    }
}
