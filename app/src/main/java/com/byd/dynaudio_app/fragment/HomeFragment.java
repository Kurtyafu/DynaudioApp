package com.byd.dynaudio_app.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModelProvider;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.bean.ResponseData;
import com.byd.dynaudio_app.databinding.LayoutFragmentHomeBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TestUtils;
import com.byd.dynaudio_app.viewmodel.HomeViewModel;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class HomeFragment extends BaseFragment<LayoutFragmentHomeBinding, HomeViewModel> {
    // 顶部最小高度 顶部最大高度
    private int topClMinHeight, topClMaxHeight;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {


        /**
         * 设置有时间设置按钮的drawable left
         */
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = mContext.getResources().getDrawable(R.drawable.audio_settings);
        drawable.setBounds(DensityUtils.dp2Px(mContext, 5), 0, DensityUtils.dp2Px(mContext, 15), DensityUtils.dp2Px(mContext, 15));
        mDataBinding.btnAudioSettings.setCompoundDrawables(drawable, null, null, null);


//        /**
//         * 设置有立即体验按钮的drawable right
//         * 23.02.23 暂无切图
//         */
//        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable2 = mContext.getResources().getDrawable(R.drawable.arrow_right);
//        int padding2 = DensityUtils.dp2Px(mContext, 3);
//        drawable.setBounds(padding2, 0, DensityUtils.dp2Px(mContext, 6), DensityUtils.dp2Px(mContext, 6));
//        mDataBinding.btnImmediateExperience.setCompoundDrawables(null, null, drawable2, null);

        // 处理顶部区域
        // 顶部区域最小高度36dp 最大高度为cl高度
//        topClMinHeight = DensityUtils.dp2Px(mContext, 46);
//        mDataBinding.getRoot().postDelayed(() -> topClMaxHeight = mDataBinding.clTop.getHeight(), 100);

        // 获取拖动上下产生的位移量
//        mDataBinding.svHome.setOnTouchListener(new View.OnTouchListener() {
//            private float initialY;
//
//            private long timeStamp;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent motionEvent) {
//                LogUtils.d("touch : " + motionEvent);
//                switch (motionEvent.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        // 记录手指按下的位置
//                        initialY = motionEvent.getY();
//                        timeStamp = System.currentTimeMillis();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        // 计算手指移动的距离
//                        float dy = motionEvent.getY() - initialY;
//                        changeTopClHeight(dy);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        long delta = System.currentTimeMillis() - timeStamp;
//                        break;
//                }
//                return false;
//            }
//        });

        // 初始化下方栏目：黑胶专区，有声专栏，3d沉浸声
//        mDataBinding.cardBlackPlastic.setData(TestUtils.getTestBlackPlasticList());
//        mDataBinding.cardAudioColumn.setData(TestUtils.getTestBlackPlasticList());
//        mDataBinding.card3dImmersionSound.setData(TestUtils.getTestBlackPlasticList());

        initOnClickListener();
    }

    private void initOnClickListener() {
        mDataBinding.btnLogin.setOnClickListener(view -> {
            MyFragment fragment = new MyFragment();
            toFragment(fragment);
        });
    }


    private void changeTopClHeight(float dy) {
        // 如果下拉需要先展示完全scroll view 就把下发的放出来
//        if (dy > 0 && mDataBinding.svHome.getScrollY() > 0) {
//            return;
//        }

//        int newHeight = mDataBinding.clTop.getHeight() + (int) (dy);
////        // LogUtils.d("new : " + newHeight + " top min : " + topClMinHeight + " max : " + topClMaxHeight);
//        if (newHeight >= topClMinHeight && newHeight <= topClMaxHeight) {
//            mDataBinding.clTop.getLayoutParams().height = newHeight;
//            mDataBinding.clTop.requestLayout();
//        }


//        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mDataBinding.svHome.getLayoutParams();
//        if (layoutParams != null) {
//            float newMarginTop = layoutParams.topMargin + dy;
//            LogUtils.d("margin top : " + layoutParams.topMargin + " new margin top : " + newMarginTop);
//            if (newMarginTop >= topClMinHeight && newMarginTop <= topClMaxHeight) {
//                mDataBinding.svHome.getLayoutParams().height = (int) newMarginTop;
//                mDataBinding.svHome.requestLayout();
//            }
//        }

    }

    @Override
    protected void initListener() {

    }

    @SuppressLint("CheckResult")
    @Override
    protected void initObserver() {
        mViewModel.requestHifiInfo().subscribe(new Observer<ResponseData>() {
            @Override
            public void onSubscribe(Disposable d) {
                // todo 解析返回的data 并设置 hifi bean
                // 这里用假数据先展示效果
//                mDataBinding.cardHifi.setTitleText("甄选高品质HiFi金曲\n" + "专注声音的精彩细节，诠释细腻情感\n" + "等你来听");
//                mDataBinding.cardHifi.setContent1Text("In the Stillness / Singer Name");
//                mDataBinding.cardHifi.setContent2Text("Schumann:Papillons / Singer Name");
//                mDataBinding.cardHifi.setContent3Text("In the Bleak Midwinter (Arr. O. Gjei");
            }

            @Override
            public void onNext(ResponseData responseData) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    protected HomeViewModel getViewModel() {
        return new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_fragment_home;
    }
}
