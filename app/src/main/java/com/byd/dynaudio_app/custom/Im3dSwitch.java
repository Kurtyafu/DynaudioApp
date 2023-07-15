package com.byd.dynaudio_app.custom;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.base.BaseView;
import com.byd.dynaudio_app.car.CarManager;
import com.byd.dynaudio_app.databinding.LayoutViewIm3dSwitchBinding;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;

public class Im3dSwitch extends BaseView<LayoutViewIm3dSwitchBinding> {
    private long duration = 300;

    private boolean checked = true;


    private Runnable offRunnable = () -> {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mDataBinding.tv, "translationX",
                        mDataBinding.tv.getTranslationX(), DensityUtils.dp2Px(mContext, 168))
                .setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                mDataBinding.tv.setText("OFF");
//                mDataBinding.tv.setTypeface(null, Typeface.NORMAL);
//                mDataBinding.tv.setTextColor(Color.parseColor("#73FFFFFF"));
                checked = false;
                if (listener != null) listener.onCheckChange(checked);
            }
        });
        animator.addUpdateListener(valueAnimator -> {
            setTvBgByTranslationX(mDataBinding.tv.getTranslationX());
        });
        animator.start();
    };

    private Runnable onRunnable = () -> {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mDataBinding.tv, "translationX",
                        mDataBinding.tv.getTranslationX(), DensityUtils.dp2Px(mContext, 0))
                .setDuration(duration);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                mDataBinding.tv.setText("ON");
//                mDataBinding.tv.setTextColor(Color.parseColor("#FFFFFFFF"));
//                mDataBinding.tv.setTypeface(null, Typeface.BOLD);
                checked = true;
                if (listener != null) listener.onCheckChange(checked);
            }
        });
        animator.addUpdateListener(valueAnimator -> {
            setTvBgByTranslationX(mDataBinding.tv.getTranslationX());
        });
        animator.start();
    };

    private void setTvBgByTranslationX(float translationX) {
        // 使用 ArgbEvaluator 计算渐变颜色
        int colorA = Color.parseColor("#D0022D");
        int colorB = Color.parseColor("#474848");
        ArgbEvaluator evaluator = new ArgbEvaluator();
        float percentage = translationX / 336.f;
        int gradientColor = (int) evaluator.evaluate(percentage, colorA, colorB);
        mDataBinding.tv.setBackgroundColor(gradientColor);

        int offTextColor = Color.parseColor("#73FFFFFF");
        int onTextColor = Color.parseColor("#FFFFFFFF");
        int textColor = (int) evaluator.evaluate(percentage, onTextColor, offTextColor);

        String text = percentage >= .5f ? "OFF" : "ON";
        mDataBinding.tv.setTypeface(null, percentage >= .5f ? Typeface.NORMAL : Typeface.BOLD);
        mDataBinding.tv.setText(text);
        mDataBinding.tv.setTextColor(textColor);
    }

    public Im3dSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void init(AttributeSet attrs) {
        mDataBinding.tvOff.setOnClickListener(view -> setChecked(false));
        mDataBinding.tvOn.setOnClickListener(view -> setChecked(true));

        mDataBinding.tv.setOnTouchListener((view, event) -> {
            // LogUtils.d("action : " + event.getAction());
            if (!CarManager.getInstance().isPMode()) {
                CustomToast.makeText(mContext, mContext.getString(R.string.currentP_cantPlayVideo)).show();
                return false;
            }

            removeCallbacks(onRunnable);
            removeCallbacks(offRunnable);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = (event.getRawX() - x);

                    float toX = mDataBinding.tv.getTranslationX() + dx;

                    if (toX < 0) {
                        toX = 0;
                    } else if (toX > getWidth() / 2.f) {
                        toX = getWidth() / 2.f;
                    }
                    mDataBinding.tv.setTranslationX(toX);
                    mDataBinding.tv.setText("");
                    setTvBgByTranslationX(toX);
                    x = (int) event.getRawX();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    float x1 = mDataBinding.tv.getTranslationX() + mDataBinding.tv.getWidth() / 2.f;
                    float x2 = getWidth() / 2.f;
                    //  LogUtils.d("x1 : " + x1 + " x2 : " + x2);
                    removeCallbacks(onRunnable);
                    removeCallbacks(offRunnable);
                    post(x1 < x2 ? onRunnable : offRunnable);
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_view_im3d_switch;
    }

    public void setOnCheckedChangeListener(OnCheckChangeListener listener) {
        this.listener = listener;
    }

    private OnCheckChangeListener listener;

    public interface OnCheckChangeListener {
        void onCheckChange(boolean check);
    }

    public void setChecked(boolean flag) {
        // LogUtils.d("flag : " + flag);
        if (!CarManager.getInstance().isPMode()) {
            CustomToast.makeText(mContext, mContext.getString(R.string.currentP_cantPlayVideo)).show();
            return;
        }
        if (flag == checked) return;
        removeCallbacks(onRunnable);
        removeCallbacks(offRunnable);
        post(flag ? onRunnable : offRunnable);
    }

    private int x;

}
