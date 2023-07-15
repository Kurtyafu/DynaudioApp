package com.byd.dynaudio_app.fragment;

import static androidx.constraintlayout.widget.ConstraintProperties.PARENT_ID;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.hardware.bydauto.audio.BYDAutoAudioDevice;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseFragment;
import com.byd.dynaudio_app.base.BaseViewModel;
import com.byd.dynaudio_app.car.CarAudioListener;
import com.byd.dynaudio_app.databinding.LayoutSubfragmentSsfBinding;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

/**
 * 声场聚焦
 */
public class SsfFragment extends BaseFragment<LayoutSubfragmentSsfBinding, BaseViewModel> implements View.OnClickListener {
    private int topCheckedBgColor;
    private int topUncheckedColor;

    private int clickPos = 0;
    private LottieAnimationView leftTop;
    private LottieAnimationView rightTop;
    private LottieAnimationView leftBottom;
    private LottieAnimationView rightBottom;
    private long internal;
    private LottieAnimationView surroundLeft;
    private LottieAnimationView surroundTop;
    private LottieAnimationView surroundRight;
    private LottieAnimationView surroundBottom;
    private ImageView lottieCenter;

    // 圈圈相对于设计稿缩放
    private float factor = 1.1f, factorSurround = 1.2f;

    @Override
    protected void initView() {
        noNeedDisplayPlayer();
        TouchUtils.bindClickItem(mDataBinding.tvReset);

        topCheckedBgColor = Color.parseColor("#FFCF022D");
        topUncheckedColor = Color.parseColor("#FF272829");

        // 初始化车座位的四个动画
        int width = (int) (100 * factor);
        int height = (int) (100 * factor);
        leftTop = getLottie(157 + 50 - height / 2, 15 + 50 - width / 2, width, height);
        rightTop = getLottie(157 + 50 - height / 2, 109 + 50 - width / 2, width, height);
        leftBottom = getLottie(251 + 50 - height / 2, 15 + 50 - width / 2, width, height);
        rightBottom = getLottie(251 + 50 - height / 2, 109 + 50 - width / 2, width, height);
        // 环绕的四个动画
        surroundLeft = getSurroundLottie(0);
        surroundTop = getSurroundLottie(1);
        surroundRight = getSurroundLottie(2);
        surroundBottom = getSurroundLottie(3);
        // 初始化可以拖动的image
        lottieCenter = new ImageView(mContext);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(DensityUtils.dp2Px(mContext, 100), DensityUtils.dp2Px(mContext, 100));
        // 设置大小及位置
        int size = 40;
        layoutParams.width = DensityUtils.dp2Px(mContext, size);
        layoutParams.height = DensityUtils.dp2Px(mContext, size);
        layoutParams.topToTop = PARENT_ID;
        layoutParams.rightToRight = PARENT_ID;
        layoutParams.bottomToBottom = PARENT_ID;
        layoutParams.leftToLeft = PARENT_ID;
        lottieCenter.setLayoutParams(layoutParams);
        lottieCenter.setImageResource(R.drawable.img_center_point);
        mDataBinding.clSurround.addView(lottieCenter);

        surroundLeft.setTranslationX(DensityUtils.dp2Px(mContext, (int) (-50 * factorSurround)));
        surroundRight.setTranslationX(DensityUtils.dp2Px(mContext, (int) (50 * factorSurround)));
        surroundTop.setTranslationY(DensityUtils.dp2Px(mContext, (int) (-50 * factorSurround)));
        surroundBottom.setTranslationY(DensityUtils.dp2Px(mContext, (int) (50 * factorSurround)));


        // 相邻的两个动画的时间间隔
        internal = 300;

        CarManager.getInstance()
                .registerListener(new CarAudioListener() {
                    @Override
                    public void onDynaSoundFieldFocus(int status) {
                        super.onDynaSoundFieldFocus(status);
                        // LogUtils.d("声场聚焦回调：" + status);
                        if (status < 1) status = 1;
                        onLeftClick(status - 1);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        int dynaSoundFieldFocus = CarManager.getInstance()
                .getDynaSoundFieldFocus();
        LogUtils.d("声场聚焦 : " + dynaSoundFieldFocus);
        if (dynaSoundFieldFocus > 0 && dynaSoundFieldFocus < 7) {
            onLeftClick(dynaSoundFieldFocus - 1);
        }
    }

    @Override
    protected void initListener() {
        mDataBinding.tvAllCar.setOnClickListener(this);
        mDataBinding.tvMainDriver.setOnClickListener(this);
        mDataBinding.tvFirstOfficer.setOnClickListener(this);
        mDataBinding.tvBackRow.setOnClickListener(this);
        mDataBinding.tvSurround.setOnClickListener(this);
        mDataBinding.tvSsfRegulation.setOnClickListener(this);
        mDataBinding.tvReset.setOnClickListener(this);
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
        return R.layout.layout_subfragment_ssf;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_all_car:  // 全车
                onLeftClick(0);
                CarManager.getInstance()
                        .setDynaSoundFieldFocus(BYDAutoAudioDevice.DYNA_WHOLE_VEHICLE);
                break;
            case R.id.tv_main_driver:  // 主驾
                onLeftClick(1);
                LogUtils.d("click main driver...");
                CarManager.getInstance()
                        .setDynaSoundFieldFocus(BYDAutoAudioDevice.DYNA_MAIN_DRIVER);
                break;
            case R.id.tv_first_officer:  // 副驾
                onLeftClick(2);
                CarManager.getInstance()
                        .setDynaSoundFieldFocus(BYDAutoAudioDevice.DYNA_COPILOT);
                break;
            case R.id.tv_back_row:  // 后排
                onLeftClick(3);
                CarManager.getInstance()
                        .setDynaSoundFieldFocus(BYDAutoAudioDevice.DYNA_BACK_ROW);
                break;
            case R.id.tv_surround:  // 环绕
                onLeftClick(4);
                CarManager.getInstance()
                        .setDynaSoundFieldFocus(BYDAutoAudioDevice.DYNA_EMBRACE);
                break;
            case R.id.tv_ssf_regulation:  // 声场聚焦调节
                onLeftClick(5);
                CarManager.getInstance()
                        .setDynaSoundFieldFocus(BYDAutoAudioDevice.DYNA_FRONT_ROW);
                break;
            case R.id.tv_reset:
                resetSsf();
                CarManager.getInstance()
                        .setResetDynaSoundFieldFoucs(1);
                break;
        }
    }

    /**
     * 更改左边的ui
     */
    private void onLeftClick(int index) {
        for (int i = 0; i < mDataBinding.llLeftSsf.getChildCount(); i++) {
            TextView textView = (TextView) mDataBinding.llLeftSsf.getChildAt(i);
            textView.setBackgroundColor(index == i ? topUncheckedColor : 0);
            textView.setTextColor(mContext.getColor(index == i ? R.color.white : R.color.white_alpha_45));
        }

        mDataBinding.tvReset.setVisibility(View.INVISIBLE);
        mDataBinding.tvTipTop.setVisibility(View.INVISIBLE);
        mDataBinding.tvTips.setVisibility(View.VISIBLE);


        clearAll();
        switch (index) {
            case 0:
                showAllCar();
                break;
            case 1:
                showDriver();
                break;
            case 2:
                showFirstOfficer();
                break;
            case 3:
                showBackRow();
                break;
            case 4:
                showSurround();
                break;
            case 5:
                showSsf();
                break;
        }

        clickPos = index;
    }

    /**
     * 按照顺序 在四个座位的地方显示动画
     */
    private void showAllCar() {
        LogUtils.d();

        mDataBinding.getRoot().postDelayed(showAllCarRunnable, internal);
    }

    private Runnable showAllCarRunnable = new Runnable() {
        @Override
        public void run() {
            mDataBinding.getRoot().postDelayed(leftTopRunnable, internal * 0);
            mDataBinding.getRoot().postDelayed(rightTopRunnable, internal * 1);
            mDataBinding.getRoot().postDelayed(leftBottomRunnable, internal * 2);
            mDataBinding.getRoot().postDelayed(rightBottomRunnable, internal * 3);
        }
    };

    private Runnable leftTopRunnable = new Runnable() {
        @Override
        public void run() {
            leftTop.setVisibility(View.VISIBLE);
            leftTop.playAnimation();
        }
    };

    private Runnable leftBottomRunnable = new Runnable() {
        @Override
        public void run() {
            leftBottom.setVisibility(View.VISIBLE);
            leftBottom.playAnimation();
        }
    };

    private Runnable rightTopRunnable = new Runnable() {
        @Override
        public void run() {
            rightTop.setVisibility(View.VISIBLE);
            rightTop.playAnimation();
        }
    };

    private Runnable rightBottomRunnable = new Runnable() {
        @Override
        public void run() {
            rightBottom.setVisibility(View.VISIBLE);
            rightBottom.playAnimation();
        }
    };

    /**
     * 主驾
     */
    private void showDriver() {
        mDataBinding.getRoot().post(leftTopRunnable);
    }

    /**
     * 副驾
     */
    private void showFirstOfficer() {
        mDataBinding.getRoot().post(rightTopRunnable);
    }

    /**
     * 后排
     */
    private void showBackRow() {
        mDataBinding.getRoot().postDelayed(leftBottomRunnable, 0);
        mDataBinding.getRoot().postDelayed(rightBottomRunnable, internal);
    }

    /**
     * 环绕
     */
    private void showSurround() {
        mDataBinding.clSurround.setVisibility(View.VISIBLE);

        mDataBinding.llVertical.setVisibility(View.INVISIBLE);
        mDataBinding.llHorizontal.setVisibility(View.INVISIBLE);
        lottieCenter.setVisibility(View.INVISIBLE);

        surroundLeft.setVisibility(View.VISIBLE);
        surroundTop.setVisibility(View.VISIBLE);
        surroundRight.setVisibility(View.VISIBLE);
        surroundBottom.setVisibility(View.VISIBLE);

        mDataBinding.getRoot().postDelayed(() -> {
            surroundLeft.playAnimation();
            surroundTop.playAnimation();
            surroundRight.playAnimation();
            surroundBottom.playAnimation();
        }, 0);
    }

    /**
     * 记录触摸点位置的变量
     */
    private int x;

    private int y;

    /**
     * 声场聚焦调节
     */
    @SuppressLint("ClickableViewAccessibility")
    private void showSsf() {

        resetSsf();
        mDataBinding.clSurround.setVisibility(View.VISIBLE);
        mDataBinding.tvTipTop.setVisibility(View.VISIBLE);

        mDataBinding.llVertical.setVisibility(View.VISIBLE);
        mDataBinding.llHorizontal.setVisibility(View.VISIBLE);
        lottieCenter.setVisibility(View.VISIBLE);

        leftTop.setVisibility(View.INVISIBLE);
        leftBottom.setVisibility(View.INVISIBLE);
        rightTop.setVisibility(View.INVISIBLE);
        rightBottom.setVisibility(View.INVISIBLE);

        surroundLeft.setVisibility(View.INVISIBLE);
        surroundTop.setVisibility(View.INVISIBLE);
        surroundRight.setVisibility(View.INVISIBLE);
        surroundBottom.setVisibility(View.INVISIBLE);

        // 实现lottieCenter的拖动
        lottieCenter.setOnTouchListener((v, event) -> {
            setResetShow(true);
            int left, top, right, bottom;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) (event.getRawX() - x);
                    int dy = (int) (event.getRawY() - y);
                    // 更改imageView位置，原来View的四边距离

                    left = v.getLeft() + dx;
                    top = v.getTop() + dy;
                    right = v.getRight() + dx;
                    bottom = v.getBottom() + dy;

                    moveV(v, left, top, right, bottom);
                    //获取移动后的位置
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                default:
                    break;
            }
            return true;
        });

        mDataBinding.clSurround.setOnTouchListener((view, event) -> {
            setResetShow(true);
            int left, top, right, bottom;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        left = (int) (event.getX() - lottieCenter.getWidth() / 2.f);
                        right = (int) (event.getX() + lottieCenter.getWidth() / 2.f);
                        top = (int) (event.getY() - lottieCenter.getHeight() / 2.f);
                        bottom = (int) (event.getY() + lottieCenter.getHeight() / 2.f);
                        moveV(lottieCenter, left, top, right, bottom);
                    case MotionEvent.ACTION_UP:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (event.getRawX() - x);
                        int dy = (int) (event.getRawY() - y);
                        // 更改imageView位置，原来View的四边距离

                        left = lottieCenter.getLeft() + dx;
                        top = lottieCenter.getTop() + dy;
                        right = lottieCenter.getRight() + dx;
                        bottom = lottieCenter.getBottom() + dy;

                        moveV(lottieCenter, left, top, right, bottom);
                        //获取移动后的位置
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        break;
                    default:
                        break;
            }
            return true;
        });

        // 获取声场相关的信息 设置界面
        int frontAndBack = CarManager.getInstance().getDynaSoundField(1); // 前后声场value -9到9
        int leftAndRight = CarManager.getInstance().getDynaSoundField(2); // 左右声场value

        setPosBySoundEffectFocus(leftAndRight, frontAndBack);
    }

    private void moveV(View v, int left, int top, int right, int bottom) {
        int dw = v.getWidth() / 2;
        int dh = v.getHeight() / 2;

        // 是否超出边界
        if (left < -dw) {
            left = -dw;
            right = left + v.getWidth();
        } else if (right > mDataBinding.clSurround.getWidth() + dw) {
            right = mDataBinding.clSurround.getWidth() + dw;
            left = right - v.getWidth();
        }

        if (top < -dh) {
            top = -dh;
            bottom = top + v.getHeight();
        } else if (bottom > mDataBinding.clSurround.getHeight() + dh) {
            bottom = mDataBinding.clSurround.getHeight() + dh;
            top = bottom - v.getHeight();
        }

        v.layout(left, top, right, bottom);

        // 移动虚线
        int realDx = (left + right) / 2 - mDataBinding.clSurround.getWidth() / 2;
        int realDy = (top + bottom) / 2 - mDataBinding.clSurround.getHeight() / 2;
        mDataBinding.llVertical.setTranslationX(realDx);
        mDataBinding.llHorizontal.setTranslationY(realDy);


        int centerX = (int) (v.getX() + v.getWidth() / 2);
        int centerY = (int) (v.getY() + v.getHeight() / 2);
        setSoundEffectFocusByPos(centerX, centerY);
    }

    /**
     * 根据拖到的x和y坐标来设置声场
     *
     * @param centerX 拖动的圆心x
     * @param centerY 拖到的圆心y
     */
    private void setSoundEffectFocusByPos(int centerX, int centerY) {
//        LogUtils.d("drag center x : " + centerX
//                + " center y : " + centerY);
        int xMax = mDataBinding.clSurround.getWidth();
        int yMax = mDataBinding.clSurround.getHeight();
//        LogUtils.d("x max : " + xMax + " y max : " + yMax);

        // 将(0,0)到(xMax,yMax)分成9*9的区域 映射对应的坐标
        float xPos = centerX * 1.f / (xMax * 1.f / 9 / 2);
        float yPos = centerY * 1.f / (yMax * 1.f / 9 / 2);
//        LogUtils.d("x pos : " + xPos + " y pos : " + yPos);

        int toXPos = Math.round(xPos) - 9;
        int toYPos = Math.round(yPos) - 9;
//        LogUtils.d("to x pos : " + toXPos + " to y pos : " + toYPos);

        CarManager.getInstance().setDynaSoundField(2, toXPos);
        CarManager.getInstance().setDynaSoundField(1, toYPos);
    }

    /**
     * 根据声场的值来设置x和y坐标
     *
     * @param xPos x对应的值
     * @param yPos y对应的值
     */
    private void setPosBySoundEffectFocus(int xPos, int yPos) {
        LogUtils.d("x pos : " + xPos + " y pos : " + yPos);
        mDataBinding.tvReset.setVisibility(xPos == 0 && yPos == 0
                ? View.INVISIBLE : View.VISIBLE);

        int xMax = mDataBinding.clSurround.getWidth();
        int yMax = mDataBinding.clSurround.getHeight();

        // 如果两个max为0  说明是进来就执行了 但是此时ui还没渲染出来 延时处理
        if (xMax == 0 || yMax == 0) {
            mDataBinding.getRoot().postDelayed(() -> setPosBySoundEffectFocus(xPos, yPos), 100);
            return;
        }

        int toXPos = (int) ((xMax * 1.f / 9 / 2) * (xPos + 9));
        int toYPos = (int) ((yMax * 1.f / 9 / 2) * (yPos + 9));
//        LogUtils.d("to x pos : " + toXPos + " to y pos : " + toYPos);

        int left = toXPos - lottieCenter.getWidth() / 2;
        int right = toXPos + lottieCenter.getWidth() / 2;
        int top = toYPos - lottieCenter.getHeight() / 2;
        int bottom = toYPos + lottieCenter.getHeight() / 2;

        // 移动动画
        mDataBinding.getRoot().post(new Runnable() {
            @Override
            public void run() {
                lottieCenter.layout(left, top, right, bottom);
            }
        });
        // 移动虚线
        int realDx = (left + right) / 2 - mDataBinding.clSurround.getWidth() / 2;
        int realDy = (top + bottom) / 2 - mDataBinding.clSurround.getHeight() / 2;
        mDataBinding.llVertical.setTranslationX(realDx);
        mDataBinding.llHorizontal.setTranslationY(realDy);
    }

    private void setResetShow(boolean show) {
        mDataBinding.tvReset.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private LottieAnimationView getLottie(int topMargin, int leftMargin, int width, int height) {
        LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(DensityUtils.dp2Px(mContext, 100), DensityUtils.dp2Px(mContext, 100));
        // 设置大小及位置
        layoutParams.width = DensityUtils.dp2Px(mContext, width);
        layoutParams.height = DensityUtils.dp2Px(mContext, height);
        layoutParams.topToTop = PARENT_ID;
        layoutParams.leftToLeft = PARENT_ID;
        layoutParams.topMargin = DensityUtils.dp2Px(mContext, topMargin);
        layoutParams.leftMargin = DensityUtils.dp2Px(mContext, leftMargin);
        lottieAnimationView.setLayoutParams(layoutParams);
        // 设置动画资源
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("sound_focus.json");
        lottieAnimationView.loop(true);
        // 添加到界面
        mDataBinding.clCar.addView(lottieAnimationView);
        return lottieAnimationView;
    }

    /**
     * 环绕的lottie
     *
     * @param position 0：左 1：上 2：右 3：下 4中心
     */
    private LottieAnimationView getSurroundLottie(int position) {
        LottieAnimationView lottieAnimationView = new LottieAnimationView(mContext);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                DensityUtils.dp2Px(mContext, 100), DensityUtils.dp2Px(mContext, 100));
        // 设置大小及位置
        layoutParams.width = DensityUtils.dp2Px(mContext, (int) (100 * factorSurround));
        layoutParams.height = DensityUtils.dp2Px(mContext, (int) (100 * factorSurround));

        switch (position) {
            case 0:
                layoutParams.topToTop = PARENT_ID;
                layoutParams.leftToLeft = PARENT_ID;
                layoutParams.bottomToBottom = PARENT_ID;
                break;
            case 1:
                layoutParams.topToTop = PARENT_ID;
                layoutParams.leftToLeft = PARENT_ID;
                layoutParams.rightToRight = PARENT_ID;
                break;
            case 2:
                layoutParams.topToTop = PARENT_ID;
                layoutParams.rightToRight = PARENT_ID;
                layoutParams.bottomToBottom = PARENT_ID;
                break;
            case 3:
                layoutParams.rightToRight = PARENT_ID;
                layoutParams.leftToLeft = PARENT_ID;
                layoutParams.bottomToBottom = PARENT_ID;
                break;
            case 4:
                layoutParams.topToTop = PARENT_ID;
                layoutParams.rightToRight = PARENT_ID;
                layoutParams.bottomToBottom = PARENT_ID;
                layoutParams.leftToLeft = PARENT_ID;
                break;
        }

        lottieAnimationView.setLayoutParams(layoutParams);
        // 设置动画资源
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setAnimation("sound_focus.json");
        lottieAnimationView.loop(true);
        // 添加到界面
        mDataBinding.clSurround.addView(lottieAnimationView);
        return lottieAnimationView;
    }

    /**
     * 清除所有的动画对象
     */
    private void clearAll() {
        for (int i = 0; i < mDataBinding.clCar.getChildCount(); i++) {
            View child = mDataBinding.clCar.getChildAt(i);
            child.setVisibility(View.GONE);
        }
        mDataBinding.getRoot().removeCallbacks(leftTopRunnable);
        mDataBinding.getRoot().removeCallbacks(rightTopRunnable);
        mDataBinding.getRoot().removeCallbacks(leftBottomRunnable);
        mDataBinding.getRoot().removeCallbacks(rightBottomRunnable);
        mDataBinding.getRoot().removeCallbacks(showAllCarRunnable);
    }

    private void resetSsf() {
        int width = mDataBinding.clSurround.getWidth();
        int height = mDataBinding.clSurround.getHeight();
        lottieCenter.layout(width / 2 - lottieCenter.getWidth() / 2,
                height / 2 - lottieCenter.getHeight() / 2,
                width / 2 + lottieCenter.getWidth() / 2,
                height / 2 + lottieCenter.getHeight() / 2);

        mDataBinding.llHorizontal.setTranslationY(0);
        mDataBinding.llVertical.setTranslationX(0);
        setResetShow(false);
    }
}
