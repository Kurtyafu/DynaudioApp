package com.byd.dynaudio_app.user;

public interface IWxLoginCallback {
    void GotQrcode(byte[] imgBuf);

    void GotUserInfo(WxUserInfo info);
}
