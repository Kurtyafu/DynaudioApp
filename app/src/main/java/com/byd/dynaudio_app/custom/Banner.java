package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.bean.ItemBean;
import com.byd.dynaudio_app.databinding.LayoutBannerBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TestUtils;
import com.stx.xhb.androidx.XBanner;
import com.stx.xhb.androidx.transformers.BasePageTransformer;
import com.stx.xhb.androidx.transformers.ScalePageTransformer;
import com.stx.xhb.androidx.transformers.Transformer;

public class Banner extends BaseView<LayoutBannerBinding> {
    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(AttributeSet attrs) {

        mDataBinding.banner.setPageTransformer(Transformer.OverLap);
        mDataBinding.banner.setViewPagerMargin(-416);
        mDataBinding.banner.setBannerData(R.layout.layout_view_item_banner, TestUtils.getBannerTestList());
        mDataBinding.banner.loadImage((banner, model, view, position) -> {
            ImageView imageView = view.findViewById(R.id.img_banner);
            mDataBinding.getRoot().postDelayed(() -> LogUtils.d("width : " + DensityUtils.px2Dp(mContext, view.getWidth()) + " height : " + DensityUtils.px2Dp(mContext, view.getHeight())), 100);
            ItemBean bean = (ItemBean) model;
//            Glide.with(mContext).load(bean.getShowImg()).into(imageView);
            ImageLoader.load(mContext,bean.getShowImg(),imageView);
        });
        mDataBinding.banner.setCanClickSide(true);
        mDataBinding.banner.setOnItemClickListener((banner, model, view, position) -> {
            LogUtils.d("on click : " + position);
            mDataBinding.getRoot().postDelayed(() -> LogUtils.d("width : " + DensityUtils.px2Dp(mContext, view.getWidth()) + " height : " + DensityUtils.px2Dp(mContext, view.getHeight())), 100);
        });
        mDataBinding.banner.setIsClipChildrenMode(true);

        mDataBinding.banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
//        page.setZ(position > -2 && position < 2 ? 1 : 0);


        mDataBinding.banner.getViewPager().setPageTransformer(true, new ScalePageTransformer());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_banner;
    }
}
