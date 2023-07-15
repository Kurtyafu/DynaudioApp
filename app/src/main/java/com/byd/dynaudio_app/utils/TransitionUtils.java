package com.byd.dynaudio_app.utils;

import android.view.Gravity;

import androidx.transition.Fade;
import androidx.transition.Slide;

/**
 * 页面切换相关的工具类
 */
public class TransitionUtils {
    public static final int DURATION = 800;

    public static Object getLeftSlideTransition() {
        Slide slide = new Slide();
        slide.setDuration(DURATION);
        slide.setSlideEdge(Gravity.LEFT);
        return slide;
    }

    public static Object getRightSlideTransition() {
        Slide slide = new Slide();
        slide.setDuration(DURATION);
        slide.setSlideEdge(Gravity.RIGHT);
        return slide;
    }

    public static Object getEnterFadeTransition() {
        Fade fade = new Fade();
        fade.setDuration(DURATION);
        return fade;
    }
}
