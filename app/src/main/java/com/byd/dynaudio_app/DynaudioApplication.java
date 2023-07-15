package com.byd.dynaudio_app;

import android.app.Application;
import android.graphics.Typeface;

import com.byd.dynaudio_app.utils.LogUtils;
import com.liulishuo.filedownloader.FileDownloader;

import java.lang.reflect.Field;

public class DynaudioApplication extends Application {

    private static DynaudioApplication mApplication;//全局上下文

    public static DynaudioApplication getContext() {
        return mApplication;
    }

    public static Typeface customTypeface;

    @Override
    public void onCreate() {
        super.onCreate();
        // LogUtils.d();

        mApplication = this;
        // 初始化音乐播放器

//        VideoViewManager.setConfig(VideoViewConfig.newBuilder()
//                //使用使用IjkPlayer解码
//                .setPlayerFactory(IjkPlayerFactory.create())
//                //使用ExoPlayer解码
////                .setPlayerFactory(ExoMediaPlayerFactory.create())
//                //使用MediaPlayer解码
//                .setPlayerFactory(AndroidMediaPlayerFactory.create())
//                .build());

        FileDownloader.setupOnApplicationOnCreate(this);
        FileDownloader.setup(this);

        // 初始化字体属性
//        customTypeface = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
//        // 全局替换英文字体
//        replaceFont();
    }

    private void replaceFont() {
        try {
            Field field = Typeface.class.getDeclaredField("DEFAULT");
            field.setAccessible(true);
            field.set(null, customTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
