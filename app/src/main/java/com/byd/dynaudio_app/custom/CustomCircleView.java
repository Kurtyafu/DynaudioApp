package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomCircleView extends View {
    private Paint outerPaint;
    private Paint middlePaint;
    private Paint innerPaint;
    private float centerX;
    private float centerY;
    private float radius;


    public CustomCircleView(Context context) {
        super(context);
        init();
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        outerPaint = new Paint();
        outerPaint.setColor(Color.parseColor("#FFFF2C35"));
        outerPaint.setStrokeWidth(dpToPx(1));
        outerPaint.setStyle(Paint.Style.STROKE);
        outerPaint.setAntiAlias(true);

        middlePaint = new Paint();
        middlePaint.setColor(Color.parseColor("#4DFF2C35"));
        middlePaint.setStyle(Paint.Style.FILL);
        middlePaint.setAntiAlias(true);

        innerPaint = new Paint();
        innerPaint.setColor(Color.parseColor("#FFCF022D"));
        innerPaint.setStyle(Paint.Style.FILL);
        innerPaint.setAntiAlias(true);

        radius = dpToPx(15.5f);
        centerX = dpToPx(15.5f);
        centerY = dpToPx(15.5f);
    }

    private float dpToPx(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) dpToPx(31);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerX, centerY, radius-dpToPx(1), outerPaint);
        canvas.drawCircle(centerX, centerY, radius - dpToPx(2), middlePaint);
        canvas.drawCircle(centerX, centerY, dpToPx(7), innerPaint);
    }
}
