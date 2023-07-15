package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;
import com.lxj.xpopup.impl.LoadingPopupView;

public class CustomToast extends Toast {

    private Context mContext;
    private View mRootView;
    private View mContainer;
    private TextView mTextView;
    private ImageView mIconView;
    private LoadingPopupView toast;

    private String text;
    private Drawable icon;
    private WindowManager windowManager;
    private View contentView;
    private long duration = 3_000;

    public CustomToast(Context context) {
        super(context);
        this.mContext = context;
        this.generateView();
    }

    public static CustomToast makeText(Context context, int resId, int duration) throws NotFoundException {
        return makeText(context, context.getResources().getString(resId), duration);
    }

    public static CustomToast makeText(Context context, String text, int duration) {
        return makeText(context, null, text, duration);
    }

    public static CustomToast makeText(Context context, int iconId, String text, int duration) {
        return makeText(context, context.getResources().getDrawable(iconId), text, duration);
    }

    public static CustomToast makeText(Context context, Drawable icon, String text, int duration) {
        CustomToast result = new CustomToast(context);
        result.setIcon(icon);
        result.setText(text);
        result.setDuration(duration);
        result.text = text;
        result.icon = icon;

        return result;
    }

    public static CustomToast makeText(Context context, String text) {
        CustomToast result = new CustomToast(context);
        result.setText(text);
        result.text = text;

        return result;
    }

    public void setText(CharSequence s) {
        if (this.mTextView != null) {
            this.mTextView.setText(s.toString());
        }
    }

    public void setText(int resId) {
        this.setText(this.mContext.getResources().getString(resId));
    }

    public void setIcon(Drawable icon) {
        if (this.mIconView != null) {
            this.mIconView.setBackground(icon);
            this.mIconView.setVisibility(icon != null ? View.VISIBLE : View.GONE);
        }
    }

    public void setIcon(int drawableId) {
        this.setIcon(this.mContext.getResources().getDrawable(drawableId));
    }

    public CustomToast setGravity(int gravity) {
        setGravity(gravity, 0, 0);
        return this;
    }

    public CustomToast setBackground(Drawable drawable) {
        if (this.mContainer != null) {
            this.mContainer.setBackground(drawable);
        }
        return this;
    }

    public CustomToast setBackground(int resId) {
        this.setBackground(this.mContext.getResources().getDrawable(resId));
        return this;
    }

    public CustomToast setTextSize(float textSize) {
        this.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        return this;
    }

    public CustomToast setTextSize(int dimenId) {
        float dimension = this.mContext.getResources().getDimension(dimenId);
        this.mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimension);
        return this;
    }

    public void show() {
        if (contentView == null) {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_toast, null);
        }
        TextView tv = contentView.findViewById(R.id.tv_title);
        if (tv != null) tv.setText(text);

        ImageView iv = contentView.findViewById(R.id.iv_icon);
        if (iv != null) {
            iv.setImageDrawable(icon);
            iv.setVisibility(icon != null ? View.VISIBLE : View.GONE);
        }

        if (windowManager == null) {
            windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |    //不拦截页面点击事件
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.gravity = Gravity.TOP;
        layoutParams.y = DensityUtils.dp2Px(mContext, 1);// 此距离是弹窗距离顶部状态栏的距离
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        layoutParams.windowAnimations = R.style.topToastAnimation;    //自定义动画
        windowManager.addView(contentView, layoutParams);

        new Handler().postDelayed(() -> {
            if (windowManager != null) {
                windowManager.removeViewImmediate(contentView);
                windowManager = null;
            }
        }, duration);

    }

    private void generateView() {
        int layoutId = R.layout.layout_toast;
        this.mRootView = LayoutInflater.from(this.mContext).inflate(layoutId, null);
        this.mContainer = this.mRootView.findViewById(R.id.ll_container);
        this.mTextView = this.mRootView.findViewById(R.id.tv_title);
        this.mIconView = this.mRootView.findViewById(R.id.iv_icon);
    }

    public View getView() {
        return this.mRootView;
    }
}
