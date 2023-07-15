package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.bean.ItemBean;
import com.byd.dynaudio_app.databinding.LayoutViewBannerBinding;
import com.byd.dynaudio_app.databinding.LayoutViewItemBannerBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 先写死数据类型
 */
public class CustomBanner extends BaseView<LayoutViewBannerBinding> {
    private List<ItemBean> mData = new ArrayList<>();
    private BannerAdapter adapter;

    public CustomBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init(AttributeSet attrs) {
        adapter = new BannerAdapter();
        mDataBinding.vpBanner.setAdapter(adapter);
        mDataBinding.vpBanner.setPageTransformer(false,new BannerTransformer());
    }

    public void setData(@NonNull List<ItemBean> data) {
        mData.clear();
        mData.addAll(data);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_banner;
    }

    public class BannerAdapter extends PagerAdapter {

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
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutViewItemBannerBinding bannerBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_view_item_banner, container, false);
            // todo bind item
//            Glide.with(mContext).load(mData.get(position).getShowImg()).into(bannerBinding.imgBanner);
            ImageLoader.load(mContext,mData.get(position).getShowImg(),bannerBinding.imgBanner);

            container.addView(bannerBinding.getRoot());
//            bannerBinding.getRoot().setOnClickListener(v -> LogUtils.d("onclick : ..." + position));
            return bannerBinding.getRoot();
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View) {
                container.removeView((View) object);
            }
        }
    }

    public class BannerTransformer implements ViewPager.PageTransformer {

        private static final float MIN_SCALE = 0.8F;

        private float translation;

        public BannerTransformer() {
            translation = DensityUtils.dp2Px(mContext, -50);
        }

        @Override
        public void transformPage(@NonNull View view, float position) {
            float scale = Math.max(1 - Math.abs(position), MIN_SCALE);
            view.setScaleY(scale);
            view.setScaleX(scale);
        }
    }
}
