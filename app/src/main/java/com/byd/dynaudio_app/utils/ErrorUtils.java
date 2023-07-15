package com.byd.dynaudio_app.utils;

import android.content.Context;

/**
 * 异常处理 是否需要处理。。
 */
public class ErrorUtils {
    public static void handleError(Context context, Throwable e) {
        LogUtils.e(e.toString());
    }
}
