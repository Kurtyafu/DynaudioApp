package com.byd.dynaudio_app.base;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.byd.dynaudio_app.R;

public class StatusBarUtils {
    private Activity activity;

    public StatusBarUtils(Activity activity) {
        this.activity = activity;
    }

    /**
     * 进入应用后默认需要显示状态栏 初始化状态栏
     *
     * @param color 状态栏颜色
     */
    public void initStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.transport));
        }
    }

    //将状态栏设置为传入的color
    public void setColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View view = activity.getWindow().getDecorView();
            view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(color));
        }
    }

    //true隐藏，false显示
    public void hideStatusBar(boolean enable) {
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        if (enable) {
            attributes.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attributes);
        } else {
            attributes.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attributes);
        }
    }

    public void setTextColor(boolean isDarkBackground) {
        View decor = activity.getWindow().getDecorView();
        if (isDarkBackground) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
