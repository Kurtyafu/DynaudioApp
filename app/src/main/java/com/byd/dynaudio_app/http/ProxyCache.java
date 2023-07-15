package com.byd.dynaudio_app.http;

import com.byd.dynaudio_app.DynaudioApplication;
import com.danikula.videocache.HttpProxyCacheServer;

public class ProxyCache {

    private static HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy() {
        return proxy == null ? proxy = new HttpProxyCacheServer(getContext()) : proxy;
    }

    private static DynaudioApplication getContext() {
        return DynaudioApplication.getContext();
    }

}
