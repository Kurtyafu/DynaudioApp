package com.byd.dynaudio_app.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.bean.request.BindPhoneBody;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.custom.CustomToast;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BindPhoneDialog extends Dialog {

    private EditText editPhoneNumber;
    private EditText editMessageCode;
    private TextView txtSendCode;
    private TextView btnBind;

    private CountDownTimer countDownTimer;
    private IBindListener listener;

    public void setListener(IBindListener listener) {
        this.listener = listener;
    }

    public BindPhoneDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_bind_phone);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initView();
    }

    private void initView() {
        ImageView imgClose = findViewById(R.id.img_close);
        editPhoneNumber = findViewById(R.id.edit_phone_number);
        editMessageCode = findViewById(R.id.edit_message_code);
        txtSendCode = findViewById(R.id.txt_send_code);
        btnBind = findViewById(R.id.btn_bind);

        TouchUtils.bindClickItem(imgClose, btnBind);

        imgClose.setOnClickListener(view -> dismiss());
        initEditTextChangedListener();
        txtSendCode.setOnClickListener(view -> {
            if (!txtSendCode.isSelected()) return;
            requestSendCode();
            sendCodeCountDownTimer();
        });
        btnBind.setOnClickListener(view -> {
            if (!btnBind.isSelected()) return;
            requestBind();
        });
    }

    private void initEditTextChangedListener() {
        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String phoneNumber = editPhoneNumber.getText().toString().trim();
                if (isPhoneNumber(phoneNumber)) {
                    txtSendCode.setSelected(true);
                } else if (txtSendCode.isSelected() && countDownTimer == null) {
                    txtSendCode.setSelected(false);
                }
            }
        });
        editMessageCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnBind.setSelected(editMessageCode.getText().toString().trim().length() == 6);
            }
        });
    }

    private void requestSendCode() {
        String phoneNumber = editPhoneNumber.getText().toString().trim();
        ApiClient.getInstance().sendMsCodeForBind(phoneNumber).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean bean) {
                if (!bean.isSuccess()) {
                    CustomToast.makeText(getContext(), bean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void requestBind() {
        BindPhoneBody body = new BindPhoneBody();
        body.setPhone(editPhoneNumber.getText().toString().trim());
        body.setCode(editMessageCode.getText().toString().trim());
        body.setUserId(UserController.getInstance().getUserId());
        ApiClient.getInstance().bindPhone(body).subscribe(new BaseObserver<>() {
            @Override
            protected void onSuccess(BaseBean bean) {
                if (bean.isSuccess()) {
                    if (listener != null) {
                        listener.onBind();
                    }
                    dismiss();
                } else {
                    CustomToast.makeText(getContext(), bean.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onFail(Throwable e) {

            }
        });
    }

    private void sendCodeCountDownTimer() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(60000, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long l) {
                    txtSendCode.setClickable(false);
                    txtSendCode.setText(l / 1000 + "s");
                }

                @Override
                public void onFinish() {
                    txtSendCode.setText("发送验证码");
                    txtSendCode.setClickable(true);
                    String phoneNumber = editPhoneNumber.getText().toString().trim();
                    if (isPhoneNumber(phoneNumber)) {
                        txtSendCode.setSelected(true);
                    } else if (txtSendCode.isSelected()) {
                        txtSendCode.setSelected(false);
                    }
                    countDownTimer.cancel();
                    countDownTimer = null;
                }
            };
        }
        countDownTimer.start();
    }

    private boolean isPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 11) {
            String regex = "^1[3456789]\\d{9}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.matches();
        }
        return false;
    }

    public interface IBindListener {
        void onBind();
    }
}
