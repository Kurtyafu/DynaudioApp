package com.byd.dynaudio_app.bean.request;

public class BindPhoneBody {

    private String phone;
    private String code;
    private String userId;

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
