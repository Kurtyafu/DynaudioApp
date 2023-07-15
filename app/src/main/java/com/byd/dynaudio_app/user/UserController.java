package com.byd.dynaudio_app.user;

import static android.content.Context.TELEPHONY_SERVICE;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

import com.byd.dynaudio_app.DynaudioApplication;
import com.byd.dynaudio_app.bean.response.UserInfoBean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    private static volatile UserController mInstance;

    private boolean mLoginStates;

    private final List<IUserCallback> mObserverList = new ArrayList<>();

    public static UserController getInstance() {
        if (mInstance == null) {
            synchronized (UserController.class) {
                if (mInstance == null) {
                    mInstance = new UserController();
                }
            }
        }
        return mInstance;
    }

    private UserInfoBean mUserInfo;

    private String mUserId = "";
    private String mToken = "";

    public UserInfoBean getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.mUserInfo = userInfo;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public boolean isLoginStates() {
        return mLoginStates;
    }

    public void setLoginStates(boolean loginStates) {
        this.mLoginStates = loginStates;
    }

    public void addUserObserver(@Nullable IUserCallback callback) {
        mObserverList.add(callback);
    }

    public void removeUserObserver(@Nullable IUserCallback callback) {
        mObserverList.remove(callback);
    }

    public void notifyLogin() {
        for (IUserCallback callback : mObserverList) {
            callback.onLogin();
        }
    }

    public void notifyLogOut() {
        for (IUserCallback callback : mObserverList) {
            callback.onLogOut();
        }
    }

    /**
     * 获取设备号
     * 0328暂定为IMEI
     */
    public String getEquipmentId() {
        String imei = "equipmentNo";
        try {
            Context context = DynaudioApplication.getContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            Method method = tm.getClass().getMethod("getImei");
            imei = (String) method.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }
}
