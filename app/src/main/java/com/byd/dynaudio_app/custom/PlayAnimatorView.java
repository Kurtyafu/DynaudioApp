package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.bean.MusicPlayerBean;
import com.byd.dynaudio_app.manager.MusicPlayManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放的跳动view 所有东西不写固定值 按view的比例来
 */
public class PlayAnimatorView extends View implements LifecycleOwner {
    private LifecycleRegistry lifecycleRegistry;

    private int duration;
    private Context mContext;

    public PlayAnimatorView(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PlayAnimatorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private boolean isPlay = true;
    private int left, center, right;

    private int startX;
    private int lineWidth;

    private Paint paint;

    private int one;
    private int pos = 0;


    @SuppressLint("UseCompatLoadingForDrawables")
    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor("#FFFF3D46"));
        paint.setStyle(Paint.Style.STROKE);

        duration = (int) (1000.f / 30);
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.markState(Lifecycle.State.CREATED);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        one = getWidth() / 16;
        lineWidth = 2 * one;
        paint.setStrokeWidth(lineWidth);
        startX = 3 * one;
        LiveDataBus.get().with(LiveDataBusConstants.Player.CURRENT_PLAYER, MusicPlayerBean.class)
                .observe(this, player -> {
                    if (player != null) {
                        MusicPlayerBean.PlayStatus playStatus = player.getPlayStatus();
                        if (playStatus == MusicPlayerBean.PlayStatus.Playing && getVisibility() == VISIBLE) {
                            isPlay = true;
                        } else {
                            isPlay = false;
                        }
                    }
                });
    }

    List<Lines> data = new ArrayList<>() {{
        // 根据帧图 不稳定
        /*add(new Lines(0, 3, 11));
        add(new Lines(0, 2, 10));
        add(new Lines(0, 1, 9));
        add(new Lines(1, 0, 7));
        add(new Lines(2, 0, 5));
        add(new Lines(3, 0, 3));
        add(new Lines(5, 0, 3));
        add(new Lines(7, 0, 1));
        add(new Lines(9, 1, 0));
        add(new Lines(10, 2, 0));
        add(new Lines(11, 3, 0));
        add(new Lines(12, 4, 0));
        add(new Lines(11, 5, 0));
        add(new Lines(10, 6, 1));
        add(new Lines(9, 7, 2));
        add(new Lines(8, 8, 3));
        add(new Lines(7, 9, 5));
        add(new Lines(6, 9, 4));
        add(new Lines(4, 9, 6));
        add(new Lines(3, 9, 7));
        add(new Lines(2, 8, 8));
        add(new Lines(1, 7, 9));
        add(new Lines(0, 6, 9));
        add(new Lines(0, 5, 9));
        add(new Lines(0, 3, 7));
        add(new Lines(0, 2, 6));
        add(new Lines(0, 1, 5));
        add(new Lines(1, 0, 4));
        add(new Lines(2, 0, 3));
        add(new Lines(2, 0, 2));
        add(new Lines(4, 0, 1));
        add(new Lines(5, 1, 0));
        add(new Lines(6, 1, 0));
        add(new Lines(7, 2, 0));
        add(new Lines(8, 3, 0));
        add(new Lines(9, 4, 0));
        add(new Lines(9, 5, 1));
        add(new Lines(9, 7, 2));
        add(new Lines(8, 9, 3));
        add(new Lines(7, 9, 4));
        add(new Lines(5, 9, 5));
        add(new Lines(3, 9, 6));
        add(new Lines(2, 9, 8));
        add(new Lines(1, 8, 9));
        add(new Lines(1, 7, 9));
        add(new Lines(0, 5, 9));
        add(new Lines(0, 5, 9));
        add(new Lines(0, 3, 9));*/

        add(new Lines(3, 12, 8));
        add(new Lines(2, 11, 9));
        add(new Lines(1, 10, 10));
        add(new Lines(0, 9, 11));
        add(new Lines(1, 8, 12));
        add(new Lines(2, 7, 12));
        add(new Lines(3, 6, 11));
        add(new Lines(4, 5, 10));

        add(new Lines(5, 4, 9));
        add(new Lines(6, 3, 8));
        add(new Lines(7, 2, 7));
        add(new Lines(8, 1, 6));
        add(new Lines(9, 0, 5));
        add(new Lines(10, 1, 4));
        add(new Lines(11, 2, 3));

        add(new Lines(12, 3, 2));
        add(new Lines(12, 4, 1));
        add(new Lines(11, 5, 0));
        add(new Lines(10, 6, 1));
        add(new Lines(9, 7, 2));
        add(new Lines(8, 8, 3));
        add(new Lines(7, 9, 4));
        add(new Lines(6, 10, 5));
        add(new Lines(5, 11, 6));
        add(new Lines(4, 11, 7));


    }};

    private Runnable checkPosRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlay || (!isPlay && (left != data.get(0).l || center != data.get(0).c || right != data.get(0).r))) {
                // 变化
                pos++;

                if (pos > data.size() - 1) {
                    pos = 0;
                }

                left = data.get(pos).l;
                center = data.get(pos).c;
                right = data.get(pos).r;

                invalidate();
            }

            postDelayed(checkPosRunnable, duration);
        }
    };


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        LogUtils.d("l  ： " + left + " center : " + center + " r : " + right);
        canvas.drawLine(startX, 14 * one,
                startX, (14 - left) * one, paint);
        canvas.drawLine(startX + 4 * one, 14 * one,
                startX + 4 * one, (14 - center) * one, paint);
        canvas.drawLine(startX + 8 * one, 14 * one,
                startX + 8 * one, (14 - right) * one, paint);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        lifecycleRegistry.markState(Lifecycle.State.STARTED);
        isPlay = MusicPlayManager.getInstance().isPlaying();
        left = data.get(pos).l;
        center = data.get(pos).c;
        right = data.get(pos).r;
        invalidate();
        removeCallbacks(checkPosRunnable);
        post(checkPosRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED);
        isPlay = false;
    }

    class Lines {
        int l, c, r;

        public Lines(int l, int c, int r) {
            this.l = l;
            this.c = c;
            this.r = r;
        }
    }
}
