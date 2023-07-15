package com.byd.dynaudio_app.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于快速编写一个viewpager的adapter
 */
public abstract class BaseViewPagerAdapter<V extends Object, T extends ViewDataBinding> extends PagerAdapter {
    protected List<V> mData;
    protected int layoutId;

    private Context mContext;

    public BaseViewPagerAdapter(@NonNull List<V> mData, @LayoutRes int layoutId, @NonNull Context context) {
        this.mData = mData;
        this.layoutId = layoutId;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position){
        T binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), layoutId, container, false);
        if (mData != null)
            bindItem(binding, container, position, position < mData.size() ? mData.get(position) : null);
        container.addView(binding.getRoot());
        return binding.getRoot();
    }

    /**
     * 绑定子view的布局
     */
    protected abstract void bindItem(T binding, ViewGroup container, int position, V bean);

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (object instanceof View) {
            container.removeView((View) object);
        }
    }

    public void setData(@NonNull List<V> data) {
        if (mData == null) mData = new ArrayList<>();
        mData.clear();
        mData.addAll(data);

        notifyDataSetChanged();
    }
}
