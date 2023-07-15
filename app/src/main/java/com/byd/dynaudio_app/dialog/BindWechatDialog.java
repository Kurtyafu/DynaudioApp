package com.byd.dynaudio_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.BindQrCodeBean;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.TouchUtils;

public class BindWechatDialog extends Dialog {

    private IBindListener listener;
    private CountDownTimer countDownTimer;

    public void setListener(IBindListener listener) {
        this.listener = listener;
    }

    public BindWechatDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bind_wechat);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();
    }

    private void initView() {
        ImageView imgClose = findViewById(R.id.img_close);
        ImageView imgQrcode = findViewById(R.id.img_qr_code);
        TouchUtils.bindClickItem(imgClose);
        imgClose.setOnClickListener(view -> dismiss());

        String userId = UserController.getInstance().getUserId();
        ApiClient.getInstance().getBindQrCode(userId).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<BindQrCodeBean> bean) {
                if (bean.isSuccess()) {
                    Glide.with(getContext()).load(bean.getData().getQrcodeUrl()).into(imgQrcode);
                    qrCodeCountDownTimer(bean.getData().getSceneStr());
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void pollingRequestStatus(String sceneStr) {
        ApiClient.getInstance().checkBindSuccess(sceneStr).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean bean) {
                if (bean.isSuccess()) {
                    if (listener != null) {
                        listener.onBind();
                    }
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    dismiss();
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void qrCodeCountDownTimer(String sceneStr) {
        //二维码加载完成之后开始计时
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(120000, 2000) {
                @Override
                public void onTick(long l) {
                    pollingRequestStatus(sceneStr);
                }

                @Override
                public void onFinish() {

                }
            };
        }
        countDownTimer.start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    public interface IBindListener {
        void onBind();
    }
}
