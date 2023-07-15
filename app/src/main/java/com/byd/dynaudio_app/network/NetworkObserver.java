package com.byd.dynaudio_app.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <img width="650" height="440" src="https://ts1.cn.mm.bing.net/th/id/R-C.25f89d3d997dde85d66d9d74952c9a78?rik=UQ%2fdKosN6d5YCg&riu=http%3a%2f%2fpdan1.kuaiyunds.com%2fpdan1%2f2020%2f202012192_xiao.jpg&ehk=cgnxNct%2fnzG%2fSejZ%2b5hK6f9WrwbSPe1TTUPu136u6ys%3d&risl=&pid=ImgRaw&r=0">
 */

public class NetworkObserver {
    private static final String TAG = NetworkObserver.class.getSimpleName();
    private final List<INetworkListener> mNetworkObservers = new ArrayList<>();

    private static class LazierHolder {
        static NetworkObserver instance = new NetworkObserver();
    }

    public static NetworkObserver getInstance() {
        return LazierHolder.instance;
    }

    public synchronized void addNetworkObserver(@NonNull Context context, @NonNull INetworkListener listener) {
        if (mNetworkObservers.size() == 0) startObserveNetwork(context);
        mNetworkObservers.add(listener);
    }

    public synchronized void removeNetworkObserver(@NonNull Context context, @NonNull INetworkListener listener) {
        mNetworkObservers.remove(listener);
        if (mNetworkObservers.size() == 0) stopObserveNetwork(context);
    }

    private synchronized void notifyNetworkChanged(boolean isConnected, NetworkType type) {
        for (INetworkListener observer : mNetworkObservers) {
            if (observer != null) {
                observer.onNetworkChanged(isConnected, type);
            }
        }
    }

    private void startObserveNetwork(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filter);
    }

    private void stopObserveNetwork(Context context) {
        context.unregisterReceiver(mReceiver);
    }

    public boolean isConnectNormal(Context context) {
        return getNetworkState(context) != NetworkType.none;
    }

    public NetworkType getNetworkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {
                return NetworkType.wifi;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NetworkType.mobile;
            }
        } else {
            return NetworkType.none;
        }
        return NetworkType.none;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkType networkState = getNetworkState(context);
                notifyNetworkChanged(networkState != NetworkType.none, networkState);
                LogUtils.i(TAG, "网络连接状态 ---> " + networkState);
            }
        }
    };
}