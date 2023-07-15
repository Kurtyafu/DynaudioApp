package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.DensityUtils;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.SPUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.List;

public class SCCircularView extends FrameLayout {

    private Paint mPaint;
    private int radius;
    private int strokeWidth;
    private Paint mTextPaintSoft, mTextPaintDynamic, mTextPaintOriginalSound;
    private View child;
    private int checkedColor;
    private int unCheckedColor;

    /**
     * 类型 ：
     * 0：智能调节，只画圆即可
     * 1：声音特色调节，画圆和文字
     */
    private int type;

    public SCCircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#737373"));
        mPaint.setTextSize(20);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);

        // 文字大小、颜色见下方
        int textSize = DensityUtils.dp2Px(getContext(), SPUtils.isPad() ? 12 : 20);

        checkedColor = Color.parseColor("#CCFFFFFF");
        unCheckedColor = Color.parseColor("#73FFFFFF");

        mTextPaintSoft = new Paint();
        mTextPaintSoft.setStyle(Paint.Style.FILL);
        mTextPaintSoft.setColor(unCheckedColor);
        mTextPaintSoft.setTextSize(textSize);

        mTextPaintDynamic = new Paint();
        mTextPaintDynamic.setStyle(Paint.Style.FILL);
        mTextPaintDynamic.setColor(unCheckedColor);
        mTextPaintDynamic.setTextSize(textSize);

        mTextPaintOriginalSound = new Paint();
        mTextPaintOriginalSound.setStyle(Paint.Style.FILL);
        mTextPaintOriginalSound.setColor(unCheckedColor);
        mTextPaintOriginalSound.setTextSize(textSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        child = getChildAt(0);

        child.setX(getWidth() / 2.f - child.getWidth() / 2.f);
        child.setY(getHeight() / 2.f - child.getHeight() / 2.f);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        switch (type) {
            case 0:
                drawCircleOnly(canvas);
                break;
            case 1:
                drawCircle(canvas);
                drawText(canvas);
                break;
        }
    }

    private void drawCircleOnly(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // 绘制多个圆环 不包括最外圈的
        List<Float> list = new ArrayList<Float>() {{
            add(32.f);
            add(47.f);
            add(56.f);
            add(63.f);
            add(66.f);
            add(69.f);
            add(72.f);
        }};

        for (int i = 0; i < list.size(); i++) {
            mPaint.setStrokeWidth(DensityUtils.dp2Px(getContext(), 1));
            int radius = DensityUtils.dp2Px(getContext(), list.get(i).intValue());
            canvas.drawCircle(centerX, centerY, radius, mPaint);
        }

        // 绘制最外圈的
        strokeWidth = DensityUtils.dp2Px(getContext(), 7);
        mPaint.setStrokeWidth(strokeWidth);

        radius = DensityUtils.dp2Px(getContext(), 116);

        canvas.drawCircle(centerX, centerY, radius, mPaint);
    }

    private void drawText(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;


        // 1.在下方圆环空的地方绘制 "原声"
        String text1 = getContext().getString(R.string.originalSound);
        float x1 = centerX - mTextPaintOriginalSound.measureText(text1) / 2;
        float y1 = centerY + radius + strokeWidth / 2;
        canvas.drawText(text1, x1, y1, mTextPaintOriginalSound);

        // 2.在左上角圆环空的地方绘制 "柔和" 要求文字底部朝向圆心
        String text2 = getContext().getString(R.string.soft);
        float x2 = centerX - mTextPaintSoft.measureText(text1) / 2;
        float y2 = centerY - radius + strokeWidth / 2;
        canvas.save();
        canvas.rotate(300, centerX, centerY);
        canvas.drawText(text2, x2, y2, mTextPaintSoft);
        canvas.restore();

        // 3.在右上角圆环空的地方绘制 "动感" 要求文字底部朝向圆心
        String text3 = getContext().getString(R.string.dynamic);
        float x3 = centerX - mTextPaintDynamic.measureText(text1) / 2;
        float y3 = centerY - radius + strokeWidth / 2;
        canvas.save();
        canvas.rotate(60, centerX, centerY);
        canvas.drawText(text3, x3, y3, mTextPaintDynamic);
        canvas.restore();
    }

    protected void drawCircle(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // 绘制多个圆环 不包括最外圈的
        List<Float> list = new ArrayList<Float>() {{
            add(32.f);
            add(47.f);
            add(56.f);
            add(63.f);
            add(66.f);
            add(69.f);
            add(72.f);
        }};

        for (int i = 0; i < list.size(); i++) {
            mPaint.setStrokeWidth(DensityUtils.dp2Px(getContext(), 1));
            int radius = DensityUtils.dp2Px(getContext(), list.get(i).intValue());
            canvas.drawCircle(centerX, centerY, radius, mPaint);
        }

        // 绘制最外圈的
        strokeWidth = DensityUtils.dp2Px(getContext(), SPUtils.isPad() ? 7 : 12);
        mPaint.setStrokeWidth(strokeWidth);

        int x = getWidth() / 2;
        int y = getHeight() / 2;
        radius = DensityUtils.dp2Px(getContext(), SPUtils.isPad() ? 100 : 175);
        RectF rectF = new RectF(x - radius, y - radius, x + radius, y + radius);
        canvas.drawArc(rectF, -20, 100, false, mPaint);
        canvas.drawArc(rectF, 100, 100, false, mPaint);
        canvas.drawArc(rectF, 220, 100, false, mPaint);
    }

    /**
     * 获取圆心x
     */
    public float getCenterX() {
        return getWidth() / 2.f;
    }

    /**
     * 获取圆心y
     */
    public float getCenterY() {
        return getHeight() / 2.f;
    }

    /**
     * 获取可以拖动的半径
     */
    public float getMoveRadius() {
        return DensityUtils.dp2Px(getContext(), (int) 97.5f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        dispatcherTouch();

        float distance = TouchUtils.getDistance(event.getX(), event.getY(), getCenterX(), getCenterY());
        // LogUtils.d("distance : " + distance + " rd : " + getMoveRadius());
        LogUtils.d("x : " + DensityUtils.px2Dp(getContext(), (int) event.getX()) + " y " + DensityUtils.px2Dp(getContext(), (int) event.getY()));

        if (distance < getMoveRadius()) { // 如果在圆内 就直接分发给子view处理
            return moveChild(event.getX(), event.getY());
        } else { // 不在园内 映射给子view
            float k = (event.getY() - getCenterY()) / (event.getX() - getCenterX());
            double cosTheta = 1 / Math.sqrt(1 + k * k);
            double sinTheta = k * cosTheta;
            if (event.getX() < getCenterX()) {
                cosTheta *= -1.f;
                sinTheta *= -1.f;
            }

            double toX = getCenterX() + getMoveRadius() * cosTheta;
            double toY = getCenterY() + getMoveRadius() * sinTheta;
            return moveChild((float) toX, (float) toY);
        }
    }

    /**
     * 移动子view到 中心点为x,y的地方
     */
    @SuppressLint("ResourceAsColor")
    public boolean moveChild(float x, float y) {
        child.setX(x - child.getWidth() / 2.f);
        child.setY(y - child.getHeight() / 2.f);
        dispatcherTouchXAndY(x - getCenterX(), y - getCenterY());


        if (x == getCenterX() && y == getCenterY()) {
            setMode(0);
        } else {
            double centerX = getCenterX();
            double centerY = getCenterY();

            // Calculate the angle
            double angle = Math.toDegrees(Math.atan2(y - centerY, x - centerX));
            angle = (angle + 360) % 360; // convert negative angles to positive

            // Convert to angle relative to positive x-axis (i.e., "right")
            angle = (angle) % 360;
//            LogUtils.d("angle " + angle);

            // 根据角度分类
            if (angle >= 30 && angle <= 150) {
                setMode(3);
            } else if (angle <= 270 && angle > 150) {
                setMode(1);
            } else {
                setMode(2);
            }
        }

        return true;
    }

    /**
     * todo 将x y 对应的位置映射到需要设置的结果 三角形外 映射到三角形的连线
     */
    private void dispatcherTouchXAndY(float x, float y) {
        LogUtils.d("circle radius :" + getMoveRadius());
        float xr, yr, xd, yd, xy, yy;

        // touch事件对应的坐标是-getMoveRadius()到+getMoveRadius() 需要映射到-15到15
        int xMin = -15, xMax = 15;
        float delta = 2.f * getMoveRadius() / ((xMax - xMin) * 1.f); // 每一个格子的边长
        int toX = Math.round(x / (2 * getMoveRadius()) * (xMax - xMin));
        int toY = Math.round(y / (2 * getMoveRadius()) * (xMax - xMin));

        LogUtils.d("x : " + x + " y : " + y);

        LogUtils.d(" to x: " + toX + " to y : " + toY);


        if (touch != null) {
            touch.onTouch(x, y);
        }
    }

    /**
     * 更新文字颜色 0默认，1柔和，2动感，3原声
     */
    @SuppressLint("ResourceAsColor")
    public void setMode(int index) {
        mTextPaintSoft.setColor(unCheckedColor);
        mTextPaintDynamic.setColor(unCheckedColor);
        mTextPaintOriginalSound.setColor(unCheckedColor);

        switch (index) {
            case 1:
                mTextPaintSoft.setColor(checkedColor);
                break;
            case 2:
                mTextPaintDynamic.setColor(checkedColor);
                break;
            case 3:
                mTextPaintOriginalSound.setColor(checkedColor);
                break;
        }
        if (touch != null) touch.onMode(index);

        invalidate();
    }

    /**
     * 让子view回到中心
     */
    public void reset() {
        postDelayed(() -> moveChild(getCenterX(), getCenterY()), 100);
    }

    public void setType(int type) {
        this.type = type;

        invalidate();
    }

    private void dispatcherTouch() {
        if (touch != null) {
            touch.onTouch(type);
        }
    }

    public void setTouch(Touch touch) {
        this.touch = touch;
    }

    private Touch touch;

    public interface Touch {
        void onTouch(int type);

        /**
         * 更新文字颜色 0默认，1柔和，2动感，3原声
         */
        void onMode(int mode);

        void onTouch(float x, float y);
    }
}
