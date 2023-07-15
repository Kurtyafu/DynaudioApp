package com.byd.dynaudio_app.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有上拉加载更多的adapter
 */
public abstract class BaseLoadingRecyclerViewAdapter<V extends Object, T extends ViewDataBinding> extends RecyclerView.Adapter {
    protected Context mContext;
    protected List<V> mData = new ArrayList<>();

    public BaseLoadingRecyclerViewAdapter(Context mContext, List<V> mData) {
        this.mContext = mContext;
        this.mData = mData;

        init();
    }

    protected void init() {

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        T inflate = DataBindingUtil.inflate(LayoutInflater.from(mContext), getLayoutId(), parent, false);
        return new RecyclerView.ViewHolder(inflate.getRoot()) {};
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T dataBinding = DataBindingUtil.findBinding(holder.itemView);
        V v = null;
        if (mData != null && position < mData.size()) v = mData.get(position);
        if (dataBinding != null && v != null) {
            bindItem(dataBinding, v, position);
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    protected abstract int getLayoutId();

    /**
     * 绑定子view
     */
    protected abstract void bindItem(T dataBinding, V v, int position);
}
