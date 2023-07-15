package com.byd.dynaudio_app.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.byd.dynaudio_app.MainActivity;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.custom.DyVideoPlayer;
import com.byd.dynaudio_app.utils.DensityUtils;

public class VideoPlayerManager {
    private static VideoPlayerManager instance;
    private DyVideoPlayer dyVideoPlayer;

    private VideoPlayerManager() {
    }

    public static VideoPlayerManager getInstance() {
        if (instance == null) instance = new VideoPlayerManager();
        return instance;
    }

    private Context mContext;

    public void init(@NonNull Context context) {
        mContext = context;

        preloadSalesPresentation();

    }

    private void preloadSalesPresentation() {
        dyVideoPlayer = new DyVideoPlayer(mContext);
        dyVideoPlayer.setUp("https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218025702PSiVKDB5ap.mp4");
        dyVideoPlayer.setHideAll(true);
        ViewGroup.LayoutParams layoutParams = dyVideoPlayer.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            dyVideoPlayer.setLayoutParams(layoutParams);
        }
        dyVideoPlayer.startPreloading();
    }


    public DyVideoPlayer getDyVideoPlayer() {
        return dyVideoPlayer;
    }
}
