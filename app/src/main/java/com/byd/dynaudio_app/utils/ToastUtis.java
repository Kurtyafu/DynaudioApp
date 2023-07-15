package com.byd.dynaudio_app.utils;

import android.content.Context;
import android.widget.Toast;

import com.byd.dynaudio_app.R;

public class ToastUtis {

    public static void shortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void longToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    /**
     * 针对没有网络连接提示
     */
    public static void noNetWorkToast(Context context) {
        Toast.makeText(context, context.getString(R.string.nonetwork), Toast.LENGTH_SHORT).show();
    }

}
