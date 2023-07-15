package com.byd.dynaudio_app.manager;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PhoneManager {
    private static PhoneManager instance;

    public static PhoneManager getInstance() {
        if (instance == null) {
            instance = new PhoneManager();
        }
        return instance;
    }

    private Context mContext;

    public PhoneManager init(@NonNull Context context) {
        mContext = context;

        registerPhoneStateListener();
        return this;
    }

    private void registerPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private List<PhoneStateListener> listeners = new ArrayList<>();

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (listeners != null) {
                for (PhoneStateListener listener : listeners) {
                    if (listener != null) listener.onCallStateChanged(state, incomingNumber);
                }
            }

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    isCallActive = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_RINGING:
                    isCallActive = true;
                    break;
            }
        }
    };

    private boolean isCallActive;

    public boolean isCallActive() {
        return isCallActive;
    }

    public PhoneManager addPhoneListener(@NonNull PhoneStateListener listener) {
        listeners.add(listener);
        return this;
    }
}
