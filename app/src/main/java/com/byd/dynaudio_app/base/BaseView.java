package com.byd.dynaudio_app.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseView<T extends ViewDataBinding> extends FrameLayout {
    protected T mDataBinding;
    protected Context mContext;

    public BaseView(@NonNull Context context) {
        this(context,null);
    }

    public BaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), this, false);
        addView(mDataBinding.getRoot());
        init(attrs);
    }

    public T getDataBinding() {
        return mDataBinding;
    }

    protected abstract void init(AttributeSet attrs);

    protected abstract int getLayoutId();

}
