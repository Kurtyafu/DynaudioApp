package com.byd.dynaudio_app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.byd.dynaudio_app.R;

public class ImageLoader {

    public static RequestOptions getOptions() {
        // 设置全局的loading和error占位图
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.img_loading)
                .fallback(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .centerInside();
        return requestOptions;
    }

    public static void load(Context context, @NonNull Integer resId, ImageView imageView) {
        Glide.with(context)
                .setDefaultRequestOptions(getOptions())
                .load(resId)
                .centerCrop()
                .into(imageView);
    }

    public static void load(Context context, @NonNull String url, ImageView imageView) {
        Glide.with(context)
                .setDefaultRequestOptions(getOptions())
                .load(url)
                .centerInside()
//                .transition(new DrawableTransitionOptions().crossFade()) // 0410 导致详情页图片加载异常
                .into(imageView);
    }

    public static void loadRound(Context context, @NonNull String url, ImageView imageView) {
        Glide.with(context)
                .setDefaultRequestOptions(getOptions())
                .load(url)
                .centerCrop()
                .apply(new RequestOptions().circleCrop())
                .into(imageView);
    }

    public static void loadRound(Context context, @NonNull String url, ImageView imageView, int radius) {
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        MultiTransformation<Bitmap> multiTransformation = new MultiTransformation<>(roundedCorners);
        RequestOptions requestOptions = RequestOptions.bitmapTransform(multiTransformation);

        Glide.with(context)
                .load(url)
                .centerCrop()
                .apply(requestOptions)
                .transition(new DrawableTransitionOptions().crossFade())
                .into(imageView);
    }

    public static void loadBase64(Context context, @NonNull String url, ImageView imageView) {
        byte[] decode = Base64.decode(url, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decode, 0, decode.length);

        Glide.with(context)
                .load(decodedByte)
                .centerCrop()
                .into(imageView);
    }
}
