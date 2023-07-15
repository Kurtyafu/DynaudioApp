package com.byd.dynaudio_app;

import android.content.Intent;
import android.os.Handler;

import com.byd.dynaudio_app.base.BaseActivity;
import com.byd.dynaudio_app.base.BaseViewModel;

public class SplashActivity extends BaseActivity {

    private volatile boolean isStop;

    Runnable runnable = () -> {
        if (isStop) {
            return;
        }
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    };

    @Override
    protected void initView() {
        new Handler().postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        isStop = true;
        runnable = null;
        super.onDestroy();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initObserver() {

    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }
}
