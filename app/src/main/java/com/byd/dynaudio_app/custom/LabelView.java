package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;

/**
 * 标签的自定义view，如：Hi-Res,Dolby等
 */
public class LabelView extends androidx.appcompat.widget.AppCompatImageView {

    public LabelView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private String path;

    /**
     * 这个是传入一个图片地址
     * 加载
     */
    public void setLabelImgPath(String url) {
        // LogUtils.d("url: " + url);
        if (!TextUtils.isEmpty(url)) {

            if (TextUtils.equals(url, path)) {
                setVisibility(VISIBLE);
                return;
            } else {
                path = url;
            }
            Glide.with(getContext()).asBitmap()
                    .load(url)
                    .error(R.color.transport)
                    .centerInside()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            ViewGroup.LayoutParams layoutParams = getLayoutParams();
                            int px = DensityUtils.dp2Px(getContext(), 16);
                            layoutParams.width = width / (height / px);
                            setLayoutParams(layoutParams);
                            setImageBitmap(resource);
                            if (listener != null) listener.onImgLoadEnd(LabelView.this);
                        }
                    });
            // 如果glide加载错误 就不显示这个图片
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }
    }

    private OnImgChangeListener listener;

    public void setListener(OnImgChangeListener listener) {
        this.listener = listener;
    }

    public interface OnImgChangeListener {

        void onImgLoadEnd(LabelView view);
    }
}
