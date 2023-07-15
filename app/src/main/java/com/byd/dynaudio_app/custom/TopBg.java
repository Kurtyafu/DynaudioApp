package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.base.BaseViewPagerAdapter;
import com.byd.dynaudio_app.bean.TopBgBean;
import com.byd.dynaudio_app.databinding.LayoutTopBarItemBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LogUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 顶部背景
 * 会被上拉手势收起
 * 有两页 可以翻页
 */
public class TopBg extends BaseView<LayoutViewTopBgBinding> {

    private BaseViewPagerAdapter<TopBgBean, LayoutTopBarItemBinding> adapter;

    public TopBg(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopBg(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void init(AttributeSet attrs) {
        adapter = new BaseViewPagerAdapter<>(new ArrayList<>(), R.layout.layout_top_bar_item, mContext) {
            @Override
            protected void bindItem(LayoutTopBarItemBinding binding, ViewGroup container, int position, TopBgBean bean) {
                binding.tvTopTitle.setText(bean.getTitle());
                binding.tvTopContent.setText(bean.getContent());
                Glide.with(mContext)
                        .setDefaultRequestOptions(ImageLoader.getOptions())
                        .load(bean.getBgUrl())
                        .into(binding.imgBg);
            }
        };
        mDataBinding.vpTopBar.setAdapter(adapter);
        mDataBinding.vpTopBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    Map<Integer, ImageView> map = new HashMap<>();

    public void setData(List<TopBgBean> data) {
        if (adapter != null) adapter.setData(data);

        if (data != null && data.size() > 0) {
            map.clear();

            for (int i = 0; i < data.size(); i++) {
                LogUtils.d("add22222 ///");

                ShapeableImageView imageView = new ShapeableImageView(mContext);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        DensityUtils.dp2Px(mContext, 6), DensityUtils.dp2Px(mContext, 6));
                layoutParams.leftMargin = DensityUtils.dp2Px(mContext, 15);
                imageView.setLayoutParams(layoutParams);

                // 创建新的ShapeAppearanceModel对象，并将所有角设置为圆角
                ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel
                        .Builder()
                        .setAllCornerSizes(DensityUtils.dp2Px(mContext, 3))
                        .build();

                // 将新的ShapeAppearanceModel对象设置为ShapeableImageView的形状外观
                imageView.setShapeAppearanceModel(shapeAppearanceModel);

                // 设置背景颜色为红色
                imageView.setBackgroundColor(i == 0 ? Color.parseColor("#FFCF022D")
                        : Color.parseColor("#73FFFFFF"));

                int finalI = i;
                imageView.setOnClickListener(v -> {
                    mDataBinding.vpTopBar.setCurrentItem(finalI, true);
                    setPoint(finalI);
                });

                map.put(i, imageView);
                mDataBinding.llPoint.addView(imageView);
            }
        }
    }

    public void setPoint(int position) {
        for (int pos : map.keySet()) {
            map.get(pos).setBackgroundColor(pos == position
                    ? Color.parseColor("#FFCF022D")
                    : Color.parseColor("#73FFFFFF"));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_top_bg;
    }
}
