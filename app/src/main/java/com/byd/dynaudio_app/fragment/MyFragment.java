package com.byd.dynaudio_app.fragment;

import android.view.View;
import android.widget.Toast;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.request.ChangeNameBody;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutFragmentMyBinding;
import com.byd.dynaudio_app.dialog.ChangeNameDialog;
import com.byd.dynaudio_app.dialog.LoginDialog;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.network.NetworkObserver;
import com.byd.dynaudio_app.user.IUserCallback;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.ToastUtis;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.List;

public class MyFragment extends BaseFragment<LayoutFragmentMyBinding, BaseViewModel> implements IUserCallback {

    private LoginDialog mLoginDialog;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(
                mDataBinding.btnBack,
                mDataBinding.imgEdit,
                mDataBinding.btnLogin);

        updateLoginState();
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> {
            LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
        });
        mDataBinding.imgEdit.setOnClickListener(view -> changeNikeName());
        mDataBinding.btnLogin.setOnClickListener(view -> {
            if (!NetworkObserver.getInstance().isConnectNormal(mContext)) {
                ToastUtis.noNetWorkToast(mContext);
                return;
            }
            if (mLoginDialog == null) {
                mLoginDialog = new LoginDialog(getContext());
                mLoginDialog.setPolicyClickListener(type -> {
                    toFragment(new PolicyFragment(type));
                    mLoginDialog.hide();
                });
                mLoginDialog.setOnDismissListener(dialogInterface -> mLoginDialog = null);
            }
            mLoginDialog.show();
        });
        mDataBinding.llSetting.setOnClickListener(view -> toFragment(new SettingFragment()));
        mDataBinding.llCollect.setOnClickListener(view -> toFragment(new MyCollectFragment()));
        mDataBinding.llRecord.setOnClickListener(view -> toFragment(new PlayRecordFragment()));
    }

    @Override
    protected void initObserver() {
        UserController.getInstance().addUserObserver(this);
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // 用户查看隐私服务页面后返回重新显示登录对话框
        if (!hidden) {
            if (mLoginDialog != null) {
                mLoginDialog.show();
            }
            updateLoginState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UserController.getInstance().removeUserObserver(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_my;
    }

    private void updateLoginState() {
        if (UserController.getInstance().isLoginStates()) {
            mDataBinding.llNotLogin.setVisibility(View.GONE);
            mDataBinding.rlUserInfo.setVisibility(View.VISIBLE);
            mDataBinding.imgBg.setImageResource(R.drawable.bg_my_login);
            requestUserInfo();
        } else {
            mDataBinding.llNotLogin.setVisibility(View.VISIBLE);
            mDataBinding.rlUserInfo.setVisibility(View.GONE);
            mDataBinding.imgBg.setImageResource(R.drawable.bg_my_not_login);
            List<MusicListBean> musicCollect = DBController.queryMusicCollect(mContext);
            List<MusicListBean> audioCollect = DBController.queryAudioCollect(mContext);
            List<MusicListBean> musicRecord = DBController.queryMusicRecord(mContext);
            List<MusicListBean> audioRecord = DBController.queryAudioRecord(mContext);
            mDataBinding.txtCollectNum.setText(String.valueOf(musicCollect.size() + audioCollect.size()));
            mDataBinding.txtRecordNum.setText(String.valueOf(musicRecord.size() + audioRecord.size()));
        }
    }

    public void setViewData(UserInfoBean bean) {
        mDataBinding.txtUserName.setText(bean.getUserName());
        mDataBinding.txtCollectNum.setText(String.valueOf(bean.getCollectNum()));
        mDataBinding.txtRecordNum.setText(String.valueOf(bean.getRecordNum()));
    }

    private void requestUserInfo() {
        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().getUserInfo(userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<UserInfoBean> bean) {
                UserInfoBean data = bean.getData();
                if (data != null) setViewData(bean.getData());
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void changeNikeName() {
        String beforeName = mDataBinding.txtUserName.getText().toString();
        ChangeNameDialog dialog = new ChangeNameDialog(mContext, beforeName);
        dialog.setClickListener(new ChangeNameDialog.IChangeNameClickListener() {
            @Override
            public void onSaveNameListener(String newName) {
                requestChangeName(newName);
            }
        });
        dialog.show();
    }

    private void requestChangeName(String newName) {
        ChangeNameBody body = new ChangeNameBody();
        body.setUserId(UserController.getInstance().getUserId());
        body.setUserName(newName);
        ApiClient.getInstance().changeName(body).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean bean) {
                mDataBinding.txtUserName.setText(newName);
                UserController.getInstance().getUserInfo().setUserName(newName);
                CustomToast.makeText(mContext, "用户名修改成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    @Override
    public void onLogin() {
        updateLoginState();
    }

    @Override
    public void onLogOut() {
        updateLoginState();
    }
}
