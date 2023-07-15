package com.byd.dynaudio_app.http;

import okhttp3.OkHttpClient;

public class OkHttpConfig {

    public static OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .hostnameVerifier((hostname, session) -> true)
                .addInterceptor(HttpInterceptor.headerInterceptor())
                .addInterceptor(HttpInterceptor.logInterceptor())
                .addInterceptor(HttpInterceptor.tokenInterceptor())
                .build();
    }
}
