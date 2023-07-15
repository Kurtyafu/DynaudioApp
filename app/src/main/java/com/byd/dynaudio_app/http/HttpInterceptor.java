package com.byd.dynaudio_app.http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.user.UserController;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpInterceptor {

    private static final String TAG = HttpInterceptor.class.getSimpleName();

    public static Interceptor tokenInterceptor() {
        return chain -> {
            String token = UserController.getInstance().getToken();
            Request originalRequest = chain.request();
            if (token.isEmpty()) {
                return chain.proceed(originalRequest);
            } else {
                Request updateRequest = originalRequest.newBuilder().header("token", token).build();
                return chain.proceed(updateRequest);
            }
        };
    }

    public static Interceptor headerInterceptor() {
        return chain -> chain.proceed(chain.request());
    }

    public static HttpLoggingInterceptor logInterceptor() {
        return new HttpLoggingInterceptor(message -> Log.e(TAG, message)).setLevel(HttpLoggingInterceptor.Level.BODY);
    }
}
