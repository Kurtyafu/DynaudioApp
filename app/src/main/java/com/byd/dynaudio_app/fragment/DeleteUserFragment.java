package com.byd.dynaudio_app.fragment;


import android.view.View;
import android.widget.Toast;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.bean.request.DeleteUserBody;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.databinding.LayoutFragmentDeleteUserBinding;
import com.byd.dynaudio_app.dialog.CustomDialog;
import com.byd.dynaudio_app.dialog.IOnClickListener;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

public class DeleteUserFragment extends BaseFragment<LayoutFragmentDeleteUserBinding, BaseViewModel> {
    private int chooseIndex;

    @Override
    protected void initView() {
        TouchUtils.bindClickItem(mDataBinding.btnBack, mDataBinding.btnNext, mDataBinding.btnApply);
    }

    @Override
    protected void initListener() {
        mDataBinding.btnBack.setOnClickListener(view -> goBack());
        mDataBinding.llReadTips.setOnClickListener(view -> {
            boolean selected = mDataBinding.viewRadioBtn.isSelected();
            mDataBinding.viewRadioBtn.setSelected(!selected);
            mDataBinding.btnNext.setSelected(!selected);
        });
        mDataBinding.btnNext.setOnClickListener(view -> nextStep());
        mDataBinding.llRadioBtn1.setOnClickListener(view -> choose(1));
        mDataBinding.llRadioBtn2.setOnClickListener(view -> choose(2));
        mDataBinding.llRadioBtn3.setOnClickListener(view -> choose(3));
        mDataBinding.llRadioBtn4.setOnClickListener(view -> choose(4));
        mDataBinding.btnApply.setOnClickListener(view -> deleteUser());
    }

    private void choose(int index) {
        chooseIndex = index;
        switch (index) {
            case 1:
                boolean selected1 = mDataBinding.viewRadioBtn1.isSelected();
                mDataBinding.btnApply.setSelected(!selected1);
                mDataBinding.viewRadioBtn1.setSelected(!selected1);
                mDataBinding.viewRadioBtn2.setSelected(false);
                mDataBinding.viewRadioBtn3.setSelected(false);
                mDataBinding.viewRadioBtn4.setSelected(false);
                break;
            case 2:
                boolean selected2 = mDataBinding.viewRadioBtn2.isSelected();
                mDataBinding.btnApply.setSelected(!selected2);
                mDataBinding.viewRadioBtn2.setSelected(!selected2);
                mDataBinding.viewRadioBtn1.setSelected(false);
                mDataBinding.viewRadioBtn3.setSelected(false);
                mDataBinding.viewRadioBtn4.setSelected(false);
                break;
            case 3:
                boolean selected3 = mDataBinding.viewRadioBtn3.isSelected();
                mDataBinding.btnApply.setSelected(!selected3);
                mDataBinding.viewRadioBtn3.setSelected(!selected3);
                mDataBinding.viewRadioBtn1.setSelected(false);
                mDataBinding.viewRadioBtn2.setSelected(false);
                mDataBinding.viewRadioBtn4.setSelected(false);
                break;
            case 4:
                boolean selected4 = mDataBinding.viewRadioBtn4.isSelected();
                mDataBinding.btnApply.setSelected(!selected4);
                mDataBinding.viewRadioBtn4.setSelected(!selected4);
                mDataBinding.viewRadioBtn1.setSelected(false);
                mDataBinding.viewRadioBtn2.setSelected(false);
                mDataBinding.viewRadioBtn3.setSelected(false);
                break;
        }
    }

    private void goBack() {
        LiveDataBus.get().with(LiveDataBusConstants.go_back).postValue(1);
    }

    private void nextStep() {
        if (!mDataBinding.btnNext.isSelected()) return;
        mDataBinding.llNotification.setVisibility(View.GONE);
        mDataBinding.llApply.setVisibility(View.VISIBLE);
    }

    private void deleteUser() {
        if (!mDataBinding.btnApply.isSelected()) return;
        CustomDialog dialog = new CustomDialog(mContext);
        dialog.setTitle("注销账号");
        dialog.setContent("账号注销后将不可恢复\n确定注销吗?");
        dialog.setClickListener(new IOnClickListener() {
            @Override
            public void onPositiveButtonListener() {
                requestDelete();
            }
        });
        dialog.show();
    }

    private void requestDelete() {
        DeleteUserBody body = new DeleteUserBody();
        body.setUserId(UserController.getInstance().getUserId());
        switch (chooseIndex) {
            case 1:
                body.setDestroyReason(mDataBinding.txtContent1.getText().toString());
                break;
            case 2:
                body.setDestroyReason(mDataBinding.txtContent2.getText().toString());
                break;
            case 3:
                body.setDestroyReason(mDataBinding.txtContent3.getText().toString());
                break;
            case 4:
                body.setDestroyReason(mDataBinding.txtContent4.getText().toString());
                body.setDestroyRemark(mDataBinding.editText.getText().toString());
                break;
        }
        ApiClient.getInstance().destroyAccount(body).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean bean) {
                if (bean.isSuccess()) deleteSuccess();
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void deleteSuccess() {
        UserController.getInstance().setLoginStates(false);
        SPUtils.removeSP(mContext, SPUtils.SP_KEY_USER_ID);
        // 切换游客登陆
        String equipmentId = UserController.getInstance().getEquipmentId();// 设备号
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
        toFragment(new MyFragment());
        CustomToast.makeText(mContext, "注销成功", Toast.LENGTH_SHORT).show();
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
        return R.layout.layout_fragment_delete_user;
    }
}
