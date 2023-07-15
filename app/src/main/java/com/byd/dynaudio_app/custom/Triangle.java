package com.byd.dynaudio_app.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class Triangle extends View {

    private Paint mPaint;

    public Triangle(Context context) {
        super(context);
        init();
    }

    public Triangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Triangle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        Path path = new Path();
        path.moveTo(0, 0); // 左上角
        path.lineTo(width, 0); // 右上角
        path.lineTo(width / 2, height); // 中间正下方
        path.close(); // 封闭路径

        canvas.drawPath(path, mPaint);
    }

    public void setColor(int color) {
        mPaint.setColor(color);
    }

    /**
     * 翻转
     */
    public void reverse() {
        setRotation(180);
    }
}
