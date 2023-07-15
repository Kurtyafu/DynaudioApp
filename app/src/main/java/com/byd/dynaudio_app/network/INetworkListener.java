package com.byd.dynaudio_app.network;

public interface INetworkListener {
    void onNetworkChanged(boolean isConnected, NetworkType type);
}
