package com.byd.dynaudio_app.custom;

import static androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.databinding.LayoutViewScCustomBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;

public class ScCustomView extends BaseView<LayoutViewScCustomBinding> {

    private int num1;
    private int num2;
    private int num3;
    private int num4;

    private float zeroY;

    public ScCustomView(@NonNull Context context) {
        super(context);
    }

    public ScCustomView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init(AttributeSet attrs) {

    }

    /**
     * 暴露给外接调用的设置 数值的方法
     */
    public void changeProgress(int who, int num) {
        // LogUtils.d("who : " + who + " num : " + num);
        changeProgress(getView(who), getImgView(who),
                num != 0 ? Math.abs(num) * DensityUtils.dp2Px(mContext, 10) : 0, num > 0);
        getTextView(who).setText(String.valueOf(num));
        dispatchNumChange(who, num);
    }

    private void changeProgress(@NonNull View view, ImageView imageView, int height, boolean isOverZero) {
        // 进度条高亮背景
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (layoutParams != null) {
            if (isOverZero) {
                layoutParams.topToBottom = UNSET;
                layoutParams.bottomToTop = R.id.v_0;
            } else {
                layoutParams.bottomToTop = UNSET;
                layoutParams.topToBottom = R.id.v_0;
            }

            layoutParams.height = height;

            view.setVisibility(height != 0 ? VISIBLE : INVISIBLE);
            view.setLayoutParams(layoutParams);
        }

        // 圆
        imageView.setTranslationY(isOverZero ? -height : height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        zeroY = (mDataBinding.v0.getTop() + mDataBinding.v0.getBottom()) / 2.f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        LogUtils.d("x : " + event.getX() + " y : " + event.getY() + "who : " + who + " event : " + event.getAction());
        float x = event.getX();
        float y = event.getY();

        if (y < mDataBinding.vBgPro1.getTop()) {
            y = mDataBinding.vBgPro1.getTop();
        }
        if (y > mDataBinding.vBgPro1.getBottom()) {
            y = mDataBinding.vBgPro1.getBottom();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (y < mDataBinding.vBgPro1.getTop() || y > mDataBinding.vBgPro1.getBottom()) {
                    return super.onTouchEvent(event);
                }
                // 从x方向分析应该是哪个线接收touch事件
                int index = getIndexOfMinValue((int) Math.abs((x - (mDataBinding.vBgPro1.getLeft() + mDataBinding.vBgPro1.getRight()) / 2.f)), (int) Math.abs((x - (mDataBinding.vBgPro2.getLeft() + mDataBinding.vBgPro2.getRight()) / 2.f)), (int) Math.abs((x - (mDataBinding.vBgPro3.getLeft() + mDataBinding.vBgPro3.getRight()) / 2.f)), (int) Math.abs((x - (mDataBinding.vBgPro4.getLeft() + mDataBinding.vBgPro4.getRight()) / 2.f)));

                who = index;

                changeProgress(getView(who), getImgView(who), (int) Math.abs(y - zeroY), y < zeroY);
                int num0 = calculateNumByY(y);
                getTextView(who).setText(String.valueOf(num0));
                dispatchNumChange(who, num0);

                break;
            case MotionEvent.ACTION_MOVE:  // 和move一样移动即可
                changeProgress(getView(who), getImgView(who), (int) Math.abs(y - zeroY), y < zeroY);

                // 定位当前数值
                int num = calculateNumByY(y);
                getTextView(who).setText(String.valueOf(num));
                dispatchNumChange(who, num);
                break;
            case MotionEvent.ACTION_UP:
                // 定位当前数值
                int num2 = calculateNumByY(y);
//                LogUtils.d("who : " + who + " num :" + num2);
//                changeProgress(who, num2);

                who = 0;
                break;
        }
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_sc_custom;
    }

    // touch事件谁来处理
    private int who = 0;

    /**
     * 根据y计算num
     *
     * @param y 介于y1-y2之间
     * @return 返回num
     */
    private int calculateNumByY(float y) {
        float dy = zeroY - y;
        // dy分布在-90dp到90dp之间

        int num = 0;

        float abs = Math.abs(dy);
        float delta = mDataBinding.vBgPro1.getHeight() / 2.f / 9.f;

        if (abs > 8 * delta) {
            num = 9;
        } else if (abs > 7 * delta) {
            num = 8;
        } else if (abs > 6 * delta) {
            num = 7;
        } else if (abs > 5 * delta) {
            num = 6;
        } else if (abs > 4 * delta) {
            num = 5;
        } else if (abs > 3 * delta) {
            num = 4;
        } else if (abs > 2 * delta) {
            num = 3;
        } else if (abs > 1 * delta) {
            num = 2;
        } else if (abs > DensityUtils.dp2Px(mContext, 4)) {
            num = 1;
        } else {
            num = 0;
        }

        int factor = dy >= 0 ? 1 : -1;
        return factor * num;
    }

    private int getIndexOfMinValue(int a, int b, int c, int d) {
        int min = Math.min(Math.min(Math.min(a, b), c), d);
//        LogUtils.d("a : " + a + " b : " + b + " c : " + c + " d: " + d + " min : " + min);
        if (min == a) {
            return 1;
        } else if (min == b) {
            return 2;
        } else if (min == c) {
            return 3;
        } else {
            return 4;
        }
    }


    public View getView(int who) {
        View view;  // 高亮背景进度条
        switch (who) {
            case 1:
                view = mDataBinding.progress1;
                break;
            case 2:
                view = mDataBinding.progress2;
                break;
            case 3:
                view = mDataBinding.progress3;
                break;
            default:
                view = mDataBinding.progress4;
                break;
        }
        return view;
    }

    public ImageView getImgView(int who) {
        ImageView imageView;  // 圆
        switch (who) {
            case 1:
                imageView = mDataBinding.img1;
                break;
            case 2:
                imageView = mDataBinding.img2;
                break;
            case 3:
                imageView = mDataBinding.img3;
                break;
            default:
                imageView = mDataBinding.img4;
                break;
        }
        return imageView;
    }

    private TextView getTextView(int who) {
        TextView textView;  // 圆
        switch (who) {
            case 1:
                textView = mDataBinding.tvNum1;
                break;
            case 2:
                textView = mDataBinding.tvNum2;
                break;
            case 3:
                textView = mDataBinding.tvNum3;
                break;
            default:
                textView = mDataBinding.tvNum4;
                break;
        }
        return textView;
    }

    /**
     * 分发
     */
    private void dispatchNumChange(int who, int num) {
        switch (who) {
            case 1:
                num1 = num;
                break;
            case 2:
                num2 = num;
                break;
            case 3:
                num3 = num;
                break;
            default:
                num4 = num;
                break;
        }
        if (touch != null) {
            touch.onTouch(who, num);
        }
    }

    /**
     * 是否发生变化
     */
    public boolean isChange() {
        return num1 != 0 || num2 != 0 || num3 != 0 || num4 != 0;
    }

    public void setTouch(Touch touch) {
        this.touch = touch;
    }

    private Touch touch;

    public interface Touch {
        void onTouch(int who, int num);
    }
}
