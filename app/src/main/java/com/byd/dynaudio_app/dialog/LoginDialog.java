package com.byd.dynaudio_app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.BuildConfig;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.request.PhoneLoginBody;
import com.byd.dynaudio_app.bean.request.SaveOrDeleteBody;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.MsCodeBean;
import com.byd.dynaudio_app.bean.response.QrcodeBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.controller.CollectController;
import com.byd.dynaudio_app.controller.PlayRecordController;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.database.DBController;
import com.byd.dynaudio_app.databinding.LayoutDialogLoginBinding;
import com.byd.dynaudio_app.fragment.PolicyFragment;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.user.ILoginCallBack;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.user.WxApiController;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginDialog extends Dialog {
    private boolean mCodeCorrect;
    private CountDownTimer mQrCodeCountDownTimer;
    private CountDownTimer mPhoneCodeCountDownTimer;
    private LayoutDialogLoginBinding mBinging;
    private IPolicyClickListener mPolicyClickListener;
    private ILoginCallBack mLoginCallback;

    public void setLoginCallback(ILoginCallBack loginCallback) {
        this.mLoginCallback = loginCallback;
    }

    public void setPolicyClickListener(IPolicyClickListener listener) {
        this.mPolicyClickListener = listener;
    }

    public LoginDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initPolicyText();
        selectWechat();
    }

    private void initView() {
        mBinging = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_dialog_login, null, false);
        setContentView(mBinging.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = 800;
        lp.height = 870;
        getWindow().setAttributes(lp);

        TouchUtils.bindClickItem(mBinging.imgClose, mBinging.txtSendCode, mBinging.txtLoginBtn);
    }

    private void initListener() {
        mBinging.imgClose.setOnClickListener(view -> dismiss());
        mBinging.llWechatLogin.setOnClickListener(view -> selectWechat());
        mBinging.llPhoneLogin.setOnClickListener(view -> selectPhone());
        mBinging.txtSendCode.setOnClickListener(view -> {
            if (!mBinging.txtSendCode.isSelected()) {
                return;
            }
            requestSendCode();
            sendCodeCountDownTimer();
        });
        mBinging.llRadioButtonPolicy.setOnClickListener(view -> {
            if (mBinging.viewRadioBtn.isSelected()) {
                mBinging.viewRadioBtn.setSelected(false);
                mBinging.txtLoginBtn.setSelected(false);
            } else {
                mBinging.viewRadioBtn.setSelected(true);
                if (mCodeCorrect) {
                    mBinging.txtLoginBtn.setSelected(true);
                }
            }
        });
        mBinging.txtLoginBtn.setOnClickListener(view -> {
            if (mBinging.txtLoginBtn.isSelected()) {
                postLogin();
            } else if (!mBinging.viewRadioBtn.isSelected()) {
                String toastStr = "请先阅读并同意“用户协议”和“隐私政策”";
                CustomToast.makeText(getContext(), toastStr, Toast.LENGTH_SHORT).show();
            }
        });
        mBinging.llQrCodeRefresh.setOnClickListener(view -> {
            mBinging.llQrCodeRefresh.setVisibility(View.GONE);
            requestQrCode();
        });
        initEditTextChangedListener();
    }

    private void initEditTextChangedListener() {
        mBinging.editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phoneNumber = mBinging.editPhoneNumber.getText().toString().trim();
                if (isPhoneNumber(phoneNumber)) {
                    mBinging.txtSendCode.setSelected(true);
                } else if (mBinging.txtSendCode.isSelected() && mPhoneCodeCountDownTimer == null) {
                    mBinging.txtSendCode.setSelected(false);
                }
            }
        });
        mBinging.editMessageCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mBinging.editMessageCode.getText().toString().trim().length() == 6) {
                    mCodeCorrect = true;
                    if (mBinging.viewRadioBtn.isSelected()) {
                        mBinging.txtLoginBtn.setSelected(true);
                    }
                } else {
                    mCodeCorrect = false;
                    if (mBinging.txtLoginBtn.isSelected()) {
                        mBinging.txtLoginBtn.setSelected(false);
                    }
                }
            }
        });
    }

    private void postLogin() {
        String phoneNumber = mBinging.editPhoneNumber.getText().toString().trim();
        String messageCode = mBinging.editMessageCode.getText().toString().trim();

        PhoneLoginBody loginBody = new PhoneLoginBody();
        loginBody.setPhone(phoneNumber);
        loginBody.setCode(messageCode);

        ApiClient.getInstance().phoneLogin(loginBody).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<UserInfoBean> bean) {
                if (bean.isSuccess()) {
                    loginSuccess(bean.getData());
                } else {
                    CustomToast.makeText(getContext(), bean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFail(Throwable e) {
                CustomToast.makeText(getContext(), "网络无法访问，请检查后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            String regex = "^1[3456789]\\d{9}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phoneNumber);
            boolean matches = matcher.matches();
            if (matches) {
                return true;
            } else {
                CustomToast.makeText(getContext(), "手机号码错误，请重新输入", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    private void requestSendCode() {
        String phoneNumber = mBinging.editPhoneNumber.getText().toString().trim();
        ApiClient.getInstance().sendMsCode(phoneNumber).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(MsCodeBean bean) {
                if (bean.getSuccess()) {
//                    String code = bean.getData();
//                    mBinging.editMessageCode.setText(code);
                } else {
                    CustomToast.makeText(getContext(), bean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFail(Throwable e) {
                CustomToast.makeText(getContext(), "网络无法访问，请检查后重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendCodeCountDownTimer() {
        if (mPhoneCodeCountDownTimer == null) {
            mPhoneCodeCountDownTimer = new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long l) {
                    mBinging.txtSendCode.setClickable(false);
                    mBinging.txtSendCode.setText(l / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    mBinging.txtSendCode.setText("发送验证码");
                    mBinging.txtSendCode.setClickable(true);
                    String phoneNumber = mBinging.editPhoneNumber.getText().toString().trim();
                    if (isPhoneNumber(phoneNumber)) {
                        mBinging.txtSendCode.setSelected(true);
                    } else if (mBinging.txtSendCode.isSelected()) {
                        mBinging.txtSendCode.setSelected(false);
                    }
                    mPhoneCodeCountDownTimer.cancel();
                    mPhoneCodeCountDownTimer = null;
                }
            };
        }
        mPhoneCodeCountDownTimer.start();
    }

    private void loginSuccess(UserInfoBean bean) {
        SPUtils.putValue(getContext(), SPUtils.SP_KEY_USER_ID, bean.getId());
        SPUtils.putValue(getContext(), SPUtils.SP_KEY_TOKEN, bean.getToken());
        // 同步本地收藏和播放记录
        syncLocalData();
        UserController.getInstance().setUserId(bean.getId());
        UserController.getInstance().setToken(bean.getToken());
        UserController.getInstance().setUserInfo(bean);
        UserController.getInstance().setLoginStates(true);
        UserController.getInstance().notifyLogin();
        CustomToast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
        if (mLoginCallback != null) {
            mLoginCallback.onLogin();
        }
        dismiss();
    }

    private void syncLocalData() {
        String userId = UserController.getInstance().getUserId();

        List<MusicListBean> musicCollect = DBController.queryMusicCollect(getContext());
        List<MusicListBean> audioCollect = DBController.queryAudioCollect(getContext());
        List<SaveOrDeleteBody> collectBodies = new ArrayList<>();
        for (MusicListBean bean : musicCollect) {
            SaveOrDeleteBody body = new SaveOrDeleteBody();
            body.setUserId(userId);
            body.setLibraryId(String.valueOf(bean.getLibraryId()));
            body.setLibraryType(bean.getLibraryType());
            collectBodies.add(body);
        }
        for (MusicListBean bean : audioCollect) {
            SaveOrDeleteBody body = new SaveOrDeleteBody();
            body.setUserId(userId);
            body.setLibraryId(String.valueOf(bean.getLibraryId()));
            body.setLibraryType(bean.getLibraryType());
            collectBodies.add(body);
        }
        CollectController.getInstance().requestSave(collectBodies);

        List<MusicListBean> musicRecord = DBController.queryMusicRecord(getContext());
        List<MusicListBean> audioRecord = DBController.queryAudioRecord(getContext());
        List<SaveOrDeleteBody> recordBodies = new ArrayList<>();
        for (MusicListBean bean : musicRecord) {
            SaveOrDeleteBody body = new SaveOrDeleteBody();
            body.setUserId(userId);
            body.setLibraryId(String.valueOf(bean.getLibraryId()));
            body.setLibraryType(bean.getLibraryType());
            recordBodies.add(body);
        }
        for (MusicListBean bean : audioRecord) {
            SaveOrDeleteBody body = new SaveOrDeleteBody();
            body.setUserId(userId);
            body.setLibraryId(String.valueOf(bean.getLibraryId()));
            body.setLibraryType(bean.getLibraryType());
            recordBodies.add(body);
        }
        PlayRecordController.getInstance().requestSave(recordBodies);
    }

    private void selectWechat() {
        if (mBinging.imgWechat.isSelected()) return;
        mBinging.imgWechat.setSelected(true);
        mBinging.imgPhone.setSelected(false);
        mBinging.txtWechatLogin.setSelected(true);
        mBinging.txtWechatLogin.getPaint().setFakeBoldText(true);
        mBinging.txtPhoneLogin.setSelected(false);
        mBinging.txtPhoneLogin.getPaint().setFakeBoldText(false);
        mBinging.viewWechatTab.setVisibility(View.VISIBLE);
        mBinging.viewPhoneTab.setVisibility(View.GONE);

        mBinging.llWechat.setVisibility(View.VISIBLE);
        mBinging.llPhone.setVisibility(View.GONE);

        if (BuildConfig.HOST.equals("prod")) {
            requestQrCode();
        }
    }

    private void selectPhone() {
        if (mBinging.imgPhone.isSelected()) return;
        mBinging.imgWechat.setSelected(false);
        mBinging.imgPhone.setSelected(true);
        mBinging.txtWechatLogin.setSelected(false);
        mBinging.txtWechatLogin.getPaint().setFakeBoldText(false);
        mBinging.txtPhoneLogin.setSelected(true);
        mBinging.txtPhoneLogin.getPaint().setFakeBoldText(true);
        mBinging.viewWechatTab.setVisibility(View.GONE);
        mBinging.viewPhoneTab.setVisibility(View.VISIBLE);

        mBinging.llWechat.setVisibility(View.GONE);
        mBinging.llPhone.setVisibility(View.VISIBLE);

        //从手机号登录Tab切换至微信登录Tab需要重新获取二维码
        if (mQrCodeCountDownTimer != null) {
            mQrCodeCountDownTimer.cancel();
        }
    }

    private void requestQrCode() {
        ApiClient.getInstance().getQrCode().subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<QrcodeBean> bean) {
                if (bean.getData() != null) {
                    QrcodeBean data = bean.getData();
                    Glide.with(getContext()).load(data.getQrcodeUrl()).into(mBinging.imgQrCode);
                    // 拿到二维码之后开始轮询状态
                    qrCodeCountDownTimer(data.getSceneStr());
                } else {
                    CustomToast.makeText(getContext(), bean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void pollingRequestStatus(String sceneStr) {
        ApiClient.getInstance().checkScanSuccess(sceneStr).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean<UserInfoBean> bean) {
                if (bean.getData() != null) {
                    loginSuccess(bean.getData());
                    if (mQrCodeCountDownTimer != null) {
                        mQrCodeCountDownTimer.cancel();
                    }
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void qrCodeCountDownTimer(String sceneStr) {
        //二维码加载完成之后开始计时
        if (mQrCodeCountDownTimer == null) {
            mQrCodeCountDownTimer = new CountDownTimer(120000, 2000) {
                @Override
                public void onTick(long l) {
                    pollingRequestStatus(sceneStr);
                }

                @Override
                public void onFinish() {
                    //两分钟未扫码，二维码过期，提示点击刷新
                    mBinging.llQrCodeRefresh.setVisibility(View.VISIBLE);
                    mQrCodeCountDownTimer.cancel();
                }
            };
        }
        mQrCodeCountDownTimer.start();
    }

    private void initPolicyText() {
        SpannableStringBuilder spanBuilderQr = new SpannableStringBuilder("扫码登录即代表同意");
        SpannableStringBuilder spanBuilderPhone = new SpannableStringBuilder("已阅读并同意");

        //用户协议
        SpannableString span = new SpannableString("“用户协议”");
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                if (mPolicyClickListener != null) {
                    mPolicyClickListener.onClick(PolicyFragment.PolicyType.userAgreement);
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        }, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置颜色
        span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanBuilderQr.append(span);
        spanBuilderPhone.append(span);

        spanBuilderQr.append(" 及 ");
        spanBuilderPhone.append(" 及 ");

        //隐私政策
        span = new SpannableString("“隐私政策”");
        span.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                if (mPolicyClickListener != null) {
                    mPolicyClickListener.onClick(PolicyFragment.PolicyType.privacyPolicy);
                }
            }

            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }

        }, 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.WHITE), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanBuilderQr.append(span);
        spanBuilderPhone.append(span);

        // 赋值给TextView
        mBinging.txtQrPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        mBinging.txtPhonePolicy.setMovementMethod(LinkMovementMethod.getInstance());
        mBinging.txtQrPolicy.setText(spanBuilderQr);
        mBinging.txtPhonePolicy.setText(spanBuilderPhone);
        // 设置高亮颜色透明，因为点击会变色
        mBinging.txtQrPolicy.setHighlightColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        mBinging.txtPhonePolicy.setHighlightColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
    }

    @Override
    public void dismiss() {
        if (mQrCodeCountDownTimer != null) {
            mQrCodeCountDownTimer.cancel();
            mQrCodeCountDownTimer = null;
        }
        if (mPhoneCodeCountDownTimer != null) {
            mPhoneCodeCountDownTimer.cancel();
            mPhoneCodeCountDownTimer = null;
        }
        if (mPolicyClickListener != null) {
            mPolicyClickListener = null;
        }
        if (mLoginCallback != null) {
            mLoginCallback = null;
        }
        WxApiController.getInstance().removeListener();
        super.dismiss();
    }

    public interface IPolicyClickListener {
        void onClick(PolicyFragment.PolicyType type);
    }
}