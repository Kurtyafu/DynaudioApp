package com.byd.dynaudio_app.custom.main;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;

import android.graphics.drawable.Drawable;

import android.util.AttributeSet;

import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class AutoAdjustHeightImageView extends ImageView {

    private int imageWidth;

    private int imageHeight;

    public AutoAdjustHeightImageView(Context context, AttributeSet attrs) {

        super(context, attrs);

        getImageSize();

    }

    private void getImageSize() {

        Drawable background = this.getBackground();

        if (background == null) return;

        Bitmap bitmap = ((BitmapDrawable) background).getBitmap();

        imageWidth = bitmap.getWidth();
        imageHeight = bitmap.getHeight();
    }

    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * imageHeight / imageWidth;
        this.setMeasuredDimension(width, height);

    }

}