package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseViewPagerAdapter;
import com.byd.dynaudio_app.bean.MusicListBean;
import com.byd.dynaudio_app.bean.response.BannerBean;
import com.byd.dynaudio_app.bean.response.BaseBean;
import com.byd.dynaudio_app.bean.response.UserInfoBean;
import com.byd.dynaudio_app.custom.main.MainRecyclerAdapter;
import com.byd.dynaudio_app.databinding.LayoutTopBarItemBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBarBinding;
import com.byd.dynaudio_app.databinding.LayoutViewTopBgBinding;
import com.byd.dynaudio_app.http.ApiClient;
import com.byd.dynaudio_app.http.BaseObserver;
import com.byd.dynaudio_app.manager.PlayerVisionManager;
import com.byd.dynaudio_app.user.UserController;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.ImageLoader;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TestUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

/**
 * 首页
 */
public class MainFragment extends TopShrinkFragment<LayoutViewTopBarBinding, LayoutViewTopBgBinding> {
    private BaseViewPagerAdapter<BannerBean, LayoutTopBarItemBinding> topBgAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        LogUtils.d();
        super.initView();

        initTopBg();
        initRecycler();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d();
    }

    private void initRecycler() {
        mDataBinding.recycler.setBackgroundColor(Color.parseColor("#FF000000"));
        mDataBinding.recycler.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.VERTICAL, false));
        mDataBinding.recycler.setAdapter(new MainRecyclerAdapter(
                Objects.requireNonNull(TestUtils.getMainTestList()), mContext));
//        mDataBinding.tvLoading.setVisibility(View.GONE);
    }

    private void initTopBg() {
        mDataBinding.imgBack.setVisibility(View.INVISIBLE);
        topBgAdapter = new BaseViewPagerAdapter<>(new ArrayList<>(), R.layout.layout_top_bar_item, mContext) {
            @Override
            protected void bindItem(LayoutTopBarItemBinding binding, ViewGroup container, int position, BannerBean bean) {
                binding.tvTopTitle.setText(bean.getTitle());
                binding.tvTopContent.setText(bean.getAssistantTitle());

                Glide.with(mContext)
                        .load(bean.getImgUrl())
                        .into(binding.imgBg);

//                ImageLoader.load(mContext, bean.getImgUrl(), binding.imgBg);

                binding.getRoot().setOnClickListener(v -> {
                    switch (position) {
                        case 0:
                            // 跳转到销售演示首页
                            toFragment(new SalesPresentationFragment());
                            break;
                        default:
                            // 跳转到声音设置
                            toFragment(new SoundSettingsFragment());
                            break;
                    }
                });
            }
        };
        mTopBgBinding.vpTopBar.setAdapter(topBgAdapter);
        mTopBgBinding.vpTopBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        ApiClient.getInstance().getTopBanner().subscribe(new BaseObserver<BaseBean<List<BannerBean>>>() {
            @Override
            protected void onSuccess(BaseBean<List<BannerBean>> responseData) {
                if (responseData == null) {
                    LogUtils.e("responseData null...");
                    return;
                }
                List<BannerBean> data = responseData.getData();
                if (data == null || data.size() < 2) {
                    data = new ArrayList<BannerBean>() {{
                        add(new BannerBean());
                        add(new BannerBean());
                    }};
                }
                setTopBannerData(data);
            }

            @Override
            protected void onFail(Throwable e) {
                LogUtils.e(e.toString());
                setTopBannerData(new ArrayList<>() {{
                    add(new BannerBean());
                    add(new BannerBean());
                }});
            }
        });
    }

    @Override
    protected boolean isMainPage() {
        return true;
    }

    Map<Integer, ImageView> map = new HashMap<>();

    public void setTopBannerData(List<BannerBean> data) {
        if (topBgAdapter != null) topBgAdapter.setData(data);

        if (data != null && data.size() > 0) {
            map.clear();

            for (int i = 0; i < data.size(); i++) {

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
                    mTopBgBinding.vpTopBar.setCurrentItem(finalI, true);
                    setPoint(finalI);
                });

                map.put(i, imageView);
                mTopBgBinding.llPoint.addView(imageView);
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
    protected View getTopBg() {
        mTopBgBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bg,
                        null, false);
        return mTopBgBinding.getRoot();
    }

    @Override
    protected View getTopBar() {
        mTopBarBinding = DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.layout_view_top_bar,
                        null, false);
        return mTopBarBinding.getRoot();
    }

    @Override
    protected void initListener() {
//        mTopBarBinding.tvAudioSettings.setOnClickListener(v -> toFragment(new SoundSettingsFragment()));
//        mTopBarBinding.imgAudioSettings.setOnClickListener(v -> toFragment(new SoundSettingsFragment()));
//
//        mTopBarBinding.imgUser.setOnClickListener(v -> toFragment(new MyFragment()));
//        mTopBarBinding.tvUserName.setOnClickListener(v -> toFragment(new MyFragment()));
    }

    @Override
    protected void initObserver() {
    }

    @Override
    protected boolean showLoadingMode() {
        return true;
    }
}
