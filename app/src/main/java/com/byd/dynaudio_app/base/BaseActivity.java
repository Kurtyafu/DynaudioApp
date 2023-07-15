package com.byd.dynaudio_app.base;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.byd.dynaudio_app.R;

public abstract class BaseActivity<T extends ViewDataBinding, V extends BaseViewModel> extends AppCompatActivity {
    protected T mDataBinding;
    protected V mViewModel;
    protected StatusBarUtils statusBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 透明状态栏
        statusBar = new StatusBarUtils(this);
        statusBar.initStatusBar(getResources().getColor(R.color.transport));
        statusBar.setTextColor(true);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        statusBar.setColor(R.color.transparent);
        mDataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mViewModel = getViewModel();
        initView();
        initListener();
        initObserver();
    }

    /**
     * 初始化界面组件
     */
    protected abstract void initView();

    /**
     * 初始化监听之类
     */
    protected abstract void initListener();

    /**
     * 初始化 观察者监听
     */
    protected abstract void initObserver();

    /**
     * 使用view model provider 构造view model
     */
    protected abstract V getViewModel();

    /**
     * 返回布局id
     */
    protected abstract int getLayoutId();
}
