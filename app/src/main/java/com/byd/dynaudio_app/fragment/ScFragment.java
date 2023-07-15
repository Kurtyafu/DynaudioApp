package com.byd.dynaudio_app.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byd.dynaudio_app.LiveDataBusConstants;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.custom.ScView;
import com.byd.dynaudio_app.databinding.LayoutSubfragmentScBinding;
import com.byd.dynaudio_app.utils.LiveDataBus;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

/**
 * 声音特色
 */
public class ScFragment extends BaseFragment<LayoutSubfragmentScBinding, BaseViewModel> implements View.OnClickListener {
    private int topCheckedBgColor;
    private int topUncheckedColor;

    private int checkPos = 0;

    private boolean is2ShowReset = true, is3ShowReset = true;

    @Override
    protected void initView() {
        noNeedDisplayPlayer();
        TouchUtils.bindClickItem(mDataBinding.tvReset);

        topCheckedBgColor = Color.parseColor("#FFCF022D");
        topUncheckedColor = Color.parseColor("#FF272829");
    }

    @Override
    public void onResume() {
        super.onResume();
        Integer checkPos = LiveDataBus.get().with(LiveDataBusConstants.SC_POS, Integer.class).getValue();
        this.checkPos = checkPos != null ? checkPos : 0;
        onLeftClick(this.checkPos);

        // 选中tab获取数据
        int dynaSoundFeatures = CarManager.getInstance().getDynaSoundFeatures();
        if (dynaSoundFeatures != 0){
            int toX = CarManager.getInstance().getDynaSoundFeatureStatus(2);
            int toY = CarManager.getInstance().getDynaSoundFeatureStatus(1);
            switch (dynaSoundFeatures) {
                case 1: // 原声
                    toX = 16;
                    toY = 1;
                    break;
                case 2: // 动感
                    toX = 31;
                    toY = 27;
                    break;
                case 3: // 柔和
                    toX = 1;
                    toY = 27;
                    break;
//            case 5: // 自定义
//                break;
            }
            int finalToX = toX;
            int finalToY = toY;
            mDataBinding.getRoot().postDelayed(() ->
                    mDataBinding.scc.setXAndYPos(finalToX, finalToY), 200);
        }

        mDataBinding.scd.changeProgress(1, CarManager.getInstance().getDynaGamut(1));
        mDataBinding.scd.changeProgress(2, CarManager.getInstance().getDynaGamut(2));
        mDataBinding.scd.changeProgress(3, CarManager.getInstance().getDynaGamut(3));
        mDataBinding.scd.changeProgress(4, CarManager.getInstance().getDynaGamut(4));
    }

    @Override
    protected void initListener() {
        mDataBinding.ll0.setOnClickListener(this);
        mDataBinding.ll1.setOnClickListener(this);
        mDataBinding.ll2.setOnClickListener(this);

        mDataBinding.tvReset.setOnClickListener(this);

        mDataBinding.scc.setTouch(new ScView.Touch() {
            @Override
            public void onTouch(int type) {
            }

            @Override
            public void onMode(int mode) {// 0默认，1柔和，2动感，3原声
            }

            @Override
            public void onTouch(int x, int y, boolean isCenter) {
                is2ShowReset = !isCenter;
                mDataBinding.tvReset.setVisibility(is2ShowReset ? View.VISIBLE : View.INVISIBLE);

                // 这里-100是无效值 防止重复设置
                if (x != -100 && y != -100) {
                    //  设置车机的xy

                    int delta = 3;// 允许触发的误差范围

                    if (x <= 1 + delta && y >= 27 - delta) { // 柔和
                        LogUtils.d("touch 柔和...");
                        CarManager.getInstance().setDynaSoundFeatures(3);
                    } else if (x >= 31 - delta && y >= 27 - delta) { // 动感
                        LogUtils.d("touch 动感...");
                        CarManager.getInstance().setDynaSoundFeatures(2); // 原声
                    } else if (x >= 16 - delta && x <= 16 + delta && y <= 1 + delta) {
                        LogUtils.d("touch 原声...");
                        CarManager.getInstance().setDynaSoundFeatures(1);
                    } else {
                        LogUtils.d("touch 自定义 x : " + x + " y : " + y);
                        CarManager.getInstance().setDynaSoundFeatures(5);
                        CarManager.getInstance().setDynaSoundFeatureStatus(2, x);// 设置左右声音特色
                        CarManager.getInstance().setDynaSoundFeatureStatus(1, y);// 设置前后声音特色
                    }
                }
            }
        });

        mDataBinding.scd.setTouch((who, num) -> {
            boolean isChange = mDataBinding.scd.isChange();
            // 当前是自定义调节 && 数值发生改变
            is3ShowReset = isChange;
            mDataBinding.tvReset.setVisibility(is3ShowReset ? View.VISIBLE : View.INVISIBLE);
//            mDataBinding.tvTipTop.setVisibility(!is3ShowReset ? View.VISIBLE : View.INVISIBLE);

            CarManager.getInstance().setDynaGamut(who, num);
        });
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
        return R.layout.layout_subfragment_sc;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_0: // 智能调节
                onLeftClick(0);
                break;
            case R.id.ll_1: // 声音特色增强
                onLeftClick(1);
                break;
            case R.id.ll_2: // 自定义
                onLeftClick(2);
                break;
            case R.id.tv_reset:  // 重置
                switch (checkPos) {
                    case 1:
                        mDataBinding.scc.reset();
                        CarManager.getInstance().setResetDynaSoundFeature(1);
                        CarManager.getInstance().setDynaSoundFeatureStatus(2, 0);
                        CarManager.getInstance().setDynaSoundFeatureStatus(1, 0);
                        is2ShowReset = false;
                        break;
                    case 2:
                        is3ShowReset = false;
                        mDataBinding.scd.changeProgress(1, 0);
                        mDataBinding.scd.changeProgress(2, 0);
                        mDataBinding.scd.changeProgress(3, 0);
                        mDataBinding.scd.changeProgress(4, 0);
                        mDataBinding.tvTipTop.setVisibility(View.VISIBLE);
                        break;
                }
                mDataBinding.tvReset.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /**
     * 更改左边的ui
     */
    private void onLeftClick(int index) {
        checkPos = index;
        LiveDataBus.get().with(LiveDataBusConstants.SC_POS).postValue(index);

        for (int i = 0; i < mDataBinding.llLeftSc.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) mDataBinding.llLeftSc.getChildAt(i);
            TextView tvTitle = (TextView) layout.getChildAt(0);
            TextView tvContent = (TextView) layout.getChildAt(1);
            layout.setBackgroundColor(index == i ? topUncheckedColor : 0);
            tvTitle.setTextColor(mContext.getColor(index == i ? R.color.white : R.color.white_alpha_45));
            tvContent.setTextColor(mContext.getColor(index == i ? R.color.white : R.color.white_alpha_45));
        }

        switch (index) {
            case 0:
                mDataBinding.intelAdjustment.setVisibility(View.VISIBLE);
                mDataBinding.intelAdjustment.setType(0);
//                    mDataBinding.imgBgForIntel.setVisibility(View.VISIBLE);
                mDataBinding.scc.setVisibility(View.INVISIBLE);
                mDataBinding.scd.setVisibility(View.INVISIBLE);
                // 如果是智能调节 直接文字是一直隐藏的
                mDataBinding.tvTipTop.setVisibility(View.INVISIBLE);
                // 重置也隐藏
                mDataBinding.tvReset.setVisibility(View.INVISIBLE);
                break;
            case 1:
                mDataBinding.intelAdjustment.setVisibility(View.INVISIBLE);
//                    mDataBinding.imgBgForIntel.setVisibility(View.INVISIBLE);
                mDataBinding.scc.setVisibility(View.VISIBLE);
                mDataBinding.scd.setVisibility(View.INVISIBLE);
                mDataBinding.tvReset.setVisibility(is2ShowReset ? View.VISIBLE : View.INVISIBLE);
                // 0508需求 顶部提示一直显示
                mDataBinding.tvTipTop.setVisibility(View.VISIBLE);
                break;
            case 2:
                mDataBinding.intelAdjustment.setVisibility(View.INVISIBLE);
//                    mDataBinding.imgBgForIntel.setVisibility(View.INVISIBLE);
                mDataBinding.scc.setVisibility(View.INVISIBLE);
                mDataBinding.scd.setVisibility(View.VISIBLE);

                mDataBinding.tvReset.setVisibility(is3ShowReset ? View.VISIBLE : View.INVISIBLE);
                mDataBinding.tvTipTop.setVisibility(View.VISIBLE);
                break;
        }

    }
}
