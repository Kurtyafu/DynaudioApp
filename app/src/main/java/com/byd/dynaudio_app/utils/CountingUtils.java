package com.byd.dynaudio_app.utils;


import android.os.SystemClock;
import android.view.View;

public class CountingUtils {
    /**
     * 给控件设置多次点击触发的事件
     * @param v                     需要设置监听事件的View
     * @param clickTimes            触发时需要点击的次数
     * @param effectTime            触发的有效时间段(毫秒)
     * @param multipleClickListener 触发监听器
     */
    public static void setMultipleClickListener(View v, final int clickTimes, final long effectTime,
                                                final MultipleClickListener multipleClickListener) {
        final long[] mHits = new long[clickTimes];
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] > SystemClock.uptimeMillis() - effectTime) {
                    multipleClickListener.onClick(v, clickTimes, effectTime);
                }
            }
        });
    }

    /**
     * 多次单击触发监听器
     */
    public interface MultipleClickListener {
        void onClick(View v, int clickTimes, long effectTime);
    }

}