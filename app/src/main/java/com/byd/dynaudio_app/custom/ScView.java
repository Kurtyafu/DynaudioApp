package com.byd.dynaudio_app.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.byd.dynaudio_app.R;
import com.byd.dynaudio_app.utils.LogUtils;
import com.byd.dynaudio_app.utils.TouchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 声音特色增强的自定义view
 */
public class ScView extends View {

    private boolean hasDrawDefault;

    // 最外圆环
    private float mOuterRadius/*带宽度的半径*/, mOuterStrokeWidth/*外层圆宽度*/;
    private Paint mOuterPaint;

    // 绘制角落文字
    private Paint mTextPaintSoft, mTextPaintDynamic, mTextPaintOriginalSound;
    private int checkedColor;
    private int unCheckedColor;

    // 最小圆（带红点）
    private float cx, cy, cRadius;

    // 最里圆环
    private float mSmallCircleX;
    private float mSmallCircleY;
    private float mSmallCircleRadius, mSmallStrokeWidth;
    private Paint mSmallCirclePaint;

    // 可移动区域半径
    private float mMoveRadius;
    private float mMoveStrokeWidth;

    // 可移动内侧随着移动的圆环
    private float c1x, c1y, c1Radius;
    private float c2x, c2y, c2Radius, c3x, c3y, c3Radius, c4x, c4y, c4Radius;

    private float mCenterX, mCenterY;

    // 画笔渐变相关
    private float gradientX, gradientX1;
    private float gradientY, gradientY1;

    private float lastGradientX = -1;
    private float gradientRadius;
    private int[] gradientColors;
    private float[] gradientPos;


    // 波纹相关
    private List<WaveCircle> waveCircles = new ArrayList<>();
    private Paint wavePaint;

    // 单位1
    private float realOne;

    // 呼吸缩放
    private float factor1 = 1.f, factor2 = 1.f, factor3 = 1.f, factor4 = 1.f, smallFactor = 1.f, moveFactor = 1.f;

    public ScView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mCenterX = getWidth() / 2f; // 圆心x坐标
        mCenterY = getHeight() / 2f; // 圆心y坐标
        // 默认所有的圆都处于圆心位置
        c1x = c2x = c3x = c4x = mSmallCircleX = mCenterX;
        c1y = c2y = c3y = c4y = mSmallCircleY = mCenterY;

        mOuterRadius = getWidth() / 2.f;
        mOuterStrokeWidth = mOuterRadius / 120.f * 7.f;
        mOuterPaint = new Paint();
        mOuterPaint.setStrokeWidth(mOuterStrokeWidth);
        mOuterPaint.setStyle(Paint.Style.STROKE);
//        mOuterPaint.setColor(Color.parseColor(cx == mCenterX && cy == mCenterY ? "#73FFFFFF" : "#26FFFFFF"));// 0508需求 初始位置45透明度 移动后15

        cx = mCenterX;
        cy = mCenterY;

        realOne = getWidth() / 288.f;
        cRadius = realOne * 40 / 2.f;


        mMoveRadius = 110 * realOne;


        mMoveStrokeWidth = realOne;


        // 红色外一圈
        mSmallCircleRadius = 83 * realOne;
        mSmallCirclePaint = new Paint();
        mSmallStrokeWidth = realOne;
        mSmallCirclePaint.setStrokeWidth(mSmallStrokeWidth);
        mSmallCirclePaint.setStyle(Paint.Style.STROKE);
        mSmallCirclePaint.setColor(Color.parseColor("#626262"));

        c1Radius = 95 * realOne;

        c2Radius = 101 * realOne;
        c3Radius = 104 * realOne;
        c4Radius = 107 * realOne;

        gradientX = -1;
        gradientY = mCenterY;
        gradientRadius = mMoveRadius * 2;
        if (gradientColors == null) {
            gradientColors = new int[]{Color.parseColor("#CCFFFFFF"),
                    /*Color.parseColor("#373737"), *//*Color.parseColor("#1AFFFFFF"),*/ Color.parseColor("#1AFFFFFF")};
        }

        if (gradientPos == null) {
            gradientPos = new float[]{0.f, /*0.6f,*/ 1.f};
        }


        wavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setColor(Color.parseColor("#3b3738"));

        // 文字大小、颜色见下方
        int textSize = (int) (13 * realOne);

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

        postDelayed(() -> startBreathe(), 300);

        dispatcherTouch(0, 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制不可动的
//        if (!hasDrawDefault) {
        drawDefault(canvas);
//            hasDrawDefault = true;
//        }

        // 绘制可移动的
        drawOther(canvas);

        // 绘制波纹
        drawWave(canvas);
    }

    private void drawDefault(Canvas canvas) {
        // 绘制最外圈的
        RectF rectF = new RectF(mCenterX - mOuterRadius + mOuterStrokeWidth,
                mCenterY - mOuterRadius + mOuterStrokeWidth,
                mCenterX + mOuterRadius - mOuterStrokeWidth,
                mCenterY + mOuterRadius - mOuterStrokeWidth);

        mOuterPaint.setColor(Color.parseColor(cx == mCenterX && cy == mCenterY ? "#73FFFFFF" : "#26FFFFFF"));// 0508需求 初始位置45透明度 移动后15

        canvas.drawArc(rectF, -20, 100, false, mOuterPaint);
        canvas.drawArc(rectF, 100, 100, false, mOuterPaint);
        canvas.drawArc(rectF, 220, 100, false, mOuterPaint);

        // 1.在下方圆环空的地方绘制 "原声"
        String text1 = getContext().getString(R.string.originalSound);
        float x1 = mCenterX - mTextPaintOriginalSound.measureText(text1) / 2;
        float y1 = mCenterY + mOuterRadius - mOuterStrokeWidth / 2;
        canvas.drawText(text1, x1, y1, mTextPaintOriginalSound);

        // 2.在左上角圆环空的地方绘制 "柔和" 要求文字底部朝向圆心
        String text2 = getContext().getString(R.string.soft);
        float x2 = mCenterX - mTextPaintSoft.measureText(text1) / 2;
        float y2 = mCenterY - mOuterRadius + mOuterStrokeWidth * 3.f / 2.f;
        canvas.save();
        canvas.rotate(300, mCenterX, mCenterY);
        canvas.drawText(text2, x2, y2, mTextPaintSoft);
        canvas.restore();

        // 3.在右上角圆环空的地方绘制 "动感" 要求文字底部朝向圆心
        String text3 = getContext().getString(R.string.dynamic);
        float x3 = mCenterX - mTextPaintDynamic.measureText(text1) / 2;
        float y3 = mCenterY - mOuterRadius + mOuterStrokeWidth * 3.f / 2.f;
        canvas.save();
        canvas.rotate(60, mCenterX, mCenterY);
        canvas.drawText(text3, x3, y3, mTextPaintDynamic);
        canvas.restore();

//        drawRing(mCenterX, mCenterY, mOuterRadius, mOuterStrokeWidth, mOuterPaint, canvas);
    }

    private void drawOther(Canvas canvas) {
        // 修改画笔属性
        if (gradientX != -1) {
//            RadialGradient radialGradient = new RadialGradient(gradientX, gradientY, gradientRadius, gradientColors, gradientPos, Shader.TileMode.CLAMP);
            LinearGradient linearGradient
                    = new LinearGradient(gradientX, gradientY, gradientX1, gradientY1, gradientColors, gradientPos, Shader.TileMode.MIRROR);
            mSmallCirclePaint.setShader(linearGradient);
        }
        // reset后渐变归零
        if ((cx == mCenterX && cy == mCenterY)) {
            mSmallCirclePaint.setShader(null);
        }

        // 绘制红色圆
        if (type != 0) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_center_point);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                    new Rect((int) (cx - cRadius), (int) (cy - cRadius),
                            (int) (cx + cRadius), (int) (cy + cRadius)), null);
        }

        // 绘制小圆
        drawRing(mSmallCircleX, mSmallCircleY, mSmallCircleRadius * smallFactor, mSmallStrokeWidth, mSmallCirclePaint, canvas);
        // 绘制可移动区域圆
        drawRing(mCenterX, mCenterY, mMoveRadius * moveFactor, mMoveStrokeWidth, mSmallCirclePaint, canvas);
        // 绘制大于小圆 小于可移动区域圆
        drawRing(c1x, c1y, c1Radius * factor1, mSmallStrokeWidth / 2.f, mSmallCirclePaint, canvas);
        drawRing(c2x, c2y, c2Radius * factor2, mSmallStrokeWidth / 2.f, mSmallCirclePaint, canvas);
        drawRing(c3x, c3y, c3Radius * factor3, mSmallStrokeWidth / 2.f, mSmallCirclePaint, canvas);
        drawRing(c4x, c4y, c4Radius * factor4, mSmallStrokeWidth / 2.f, mSmallCirclePaint, canvas);


    }

    private void drawWave(Canvas canvas) {
        for (int i = 0; i < waveCircles.size(); i++) {
            WaveCircle waveCircle = waveCircles.get(i);
            wavePaint.setAlpha(waveCircle.alpha);
            wavePaint.setShader(mSmallCirclePaint.getShader());
            drawRing(mCenterX, mCenterY, waveCircle.radius, 1, wavePaint, canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (type != 0) {
            handleTouch(event.getX(), event.getY(), event.getAction());
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y, int action) {
        post(() -> {
            // 停止呼吸灯
            stopBreathe();

            // 计算最小圆心
            float[] xy0 = getMaxXy(x, y, mCenterX, mCenterY, mMoveRadius, cRadius, mMoveStrokeWidth, mSmallStrokeWidth * 5);
            cx = xy0[0];
            cy = xy0[1];

            float[] xy = getMaxXy(x, y, mCenterX, mCenterY, mMoveRadius, mSmallCircleRadius, mMoveStrokeWidth, mSmallStrokeWidth);
            mSmallCircleX = xy[0];
            mSmallCircleY = xy[1];

            float[] xy1 = getMaxXy(x, y,
                    mCenterX, mCenterY, mMoveRadius, c1Radius, mMoveStrokeWidth, mSmallStrokeWidth);
            c1x = xy1[0];
            c1y = xy1[1];

            float[] xy2 = getMaxXy(x, y,
                    mCenterX, mCenterY, mMoveRadius, c2Radius, mMoveStrokeWidth, mSmallStrokeWidth);
            c2x = xy2[0];
            c2y = xy2[1];

            float[] xy3 = getMaxXy(x, y,
                    mCenterX, mCenterY, mMoveRadius, c3Radius, mMoveStrokeWidth, mSmallStrokeWidth);
            c3x = xy3[0];
            c3y = xy3[1];

            float[] xy4 = getMaxXy(x, y,
                    mCenterX, mCenterY, mMoveRadius, c4Radius, mMoveStrokeWidth, mSmallStrokeWidth);
            c4x = xy4[0];
            c4y = xy4[1];

            lastGradientX = gradientX;

            gradientX = xy[2];
            gradientY = xy[3];

            gradientX1 = xy[4];
            gradientY1 = xy[5];

            if (/*lastGradientX != gradientX && gradientX != 0 && */MotionEvent.ACTION_UP == action) {
                // 移动到边缘
                startWave();
            }

//        float distance = TouchUtils.getDistance(mSmallCircleX, mSmallCircleY, mCenterX, mCenterY);
//        if (distance < 20) {
//            gradientColors = new int[]{Color.parseColor("#373737"),
//                    Color.parseColor("#373737"), Color.parseColor("#080808")};
//        } else {
//            gradientColors = new int[]{Color.parseColor("#c5405c"), Color.parseColor("#373737"), Color.parseColor("#080808")};
//        }

            // 1.文字高亮显示
            if (cx == mCenterX && cy == mCenterY) {
                setMode(0);
            } else {
                // Calculate the angle
                double angle = Math.toDegrees(Math.atan2(cy - mCenterY, cx - mCenterX));
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

            // 处理移动事件，更新小圆的坐标
            invalidate();

        });

        if (type != 0 && action != MotionEvent.ACTION_CANCEL) { // 正常touch不会出cancel 这里用于获取并设置数据
            dispatchTouch(cx, cy);
        }

    }

    /**
     * 分发touch事件
     */
    private void dispatchTouch(float x, float y) {
        // touch事件对应的坐标是-getMoveRadius()到+getMoveRadius() 需要映射到-15到15
        int xMin = -15, xMax = 15;
        int yMin = -13, yMax = 13;

        x -= mCenterX;
        y -= mCenterY - (mMoveRadius - cRadius) / 2.f;
        float delta = 2.f * mMoveRadius / ((xMax - xMin) * 1.f); // 每一个格子的边长
        int toX = 16 + (int) Math.round(x / (Math.sqrt(3) * (mMoveRadius - cRadius - realOne * 3)) * (xMax - xMin));
        int toY = 27 - (int) Math.round(y / (1.5f * (mMoveRadius - cRadius - realOne * 3)) * (yMax - yMin));

//        LogUtils.d("x : " + x + " y : " + y);

        // 转换在三角形外的情况
        int[] transitate = transitate(toX, toY);

        toX = transitate[0];
        toY = transitate[1];
//
//        LogUtils.d(" to x: " + toX + " to y : " + toY);

        // 2.和三角形的交互
//        judgePosWithTriangle(cx, cy);

        // 3.分发
        dispatcherTouch(toX, toY);
    }

    private int[] transitate(int toX, int toY) {
        toX -= 16;
        toY -= 14;

        int[] res = new int[2];

        if (toY > 13) {
            // 三角形上方 求解该点到圆心直线(y=13)
            res[0] = (int) (13.f * toX / (toY * 1.f));
            res[1] = 13;
            LogUtils.d("top");
        } else if (toY < 26.f / 15.f * toX - 13) {
            // 在右侧边的下方 求解该点到圆心直线 与y=26.f/15.f * x -13交点
            res[0] = (int) (13.f / ((26.f / 15.f) - (toY * 1.f / toX)));
            res[1] = (int) (res[0] * (toY * 1.f / toX));
            if ((res[0] * (toY * 1.f / toX)) == 0.f) {
                res[1] = -13;
            }
            LogUtils.d("right ");
        } else if (toY < -26.f / 15.f * toX - 13) {
            // 在左侧边的下方 求解该点到圆心直线 与y=-26.f/15.f * x -13交点
            res[0] = (int) (13.f / (-(26.f / 15.f) - (toY * 1.f / toX)));
            res[1] = (int) (res[0] * (toY * 1.f / toX));
            if ((res[0] * (toY * 1.f / toX)) == 0.f) {
                res[1] = -13;
            }
            LogUtils.d("left");
        } else {
            // 在三角形内部
            res[0] = toX;
            res[1] = toY;
            LogUtils.d("in");
        }
        res[0] += 16;
        res[1] += 14;
        return res;
    }

    public void setXAndYPos(int toX, int toY) {
        postDelayed(() -> {
            int xMin = -15, xMax = 15;
            int yMin = -13, yMax = 13;
            float x = (float) (mCenterX + (toX - 16) * (Math.sqrt(3) * (mMoveRadius - cRadius - realOne * 3)) / (xMax - xMin));
            float y = mCenterY - (mMoveRadius - cRadius) / 2.f + (27 - toY) * 1.5f * (mMoveRadius - cRadius - realOne * 3) / (yMax - yMin);
//            LogUtils.d("to x : " + toX + " to y : " + toY
//                    + " x : " + x + " y : " + y);
            handleTouch(x, y, MotionEvent.ACTION_CANCEL);
            if (touch != null) {
                touch.onTouch(-100, -100, x == mCenterX && y == mCenterY);
            }
        }, 200);
    }

    private void judgePosWithTriangle(float x, float y) {
        // 柔和 动感 原声的坐标
        float x1 = (float) (mCenterX + mOuterRadius * Math.cos((150.f / 180) * Math.PI));
        float y1 = (float) (mCenterY + mOuterRadius * Math.sin((150.f / 180) * Math.PI));
        float x2 = (float) (mCenterX + mOuterRadius * Math.cos((30.f / 180) * Math.PI));
        float y2 = (float) (mCenterY + mOuterRadius * Math.sin((30.f / 180) * Math.PI));
        float x3 = (float) (mCenterX);
        float y3 = (float) (mCenterY + mOuterRadius);
        LogUtils.d("x1 : " + x1 + " y1 : " + y1
                + " x2 : " + x2 + " y2 : " + y2
                + " x3 : " + x3 + " y3 : " + y3);


        LogUtils.d(" x : " + x + " y : " + y + " is in tri : " + isPointInTriangle(
                x1, y1, x2, y2, x3, y3, x, y));

    }

    private float cross(float x1, float y1, float x2, float y2, float x3, float y3) {
        return (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
    }

    private boolean toLeft(float x1, float y1, float x2, float y2, float x3, float y3) {
        return cross(x1, y1, x2, y2, x3, y3) < 0;
    }

    public boolean isPointInTriangle(float x1, float y1, float x2, float y2, float x3, float y3, float x, float y) {
//        MyPoint2D point2D = new MyPoint2D(x, y);
//        MyPoint2D.MyTriangle2D myTriangle2D = new MyPoint2D.MyTriangle2D(
//                new MyPoint2D(x1, y1),
//                new MyPoint2D(x2, y2),
//                new MyPoint2D(x3, y3));
//        return myTriangle2D.contains(point2D);
        boolean isA = isObtuseAngle(x1, y1, x, y, x2, y2);
        boolean isB = isObtuseAngle(x1, y1, x, y, x3, y3);
        boolean isC = isObtuseAngle(x3, y3, x, y, x2, y2);
        return isA && isB && isC;
    }

    public static boolean panduan(float x1, float y1, float x2, float y2, float x3, float y3, float x, float y) {
        double abc = triangleArea(x1, y1, x2, y2, x3, y3);
        double abp = triangleArea(x1, y1, x2, y2, x, y);
        double acp = triangleArea(x1, y1, x3, y3, x, y);
        double bcp = triangleArea(x2, y2, x3, y3, x, y);
        LogUtils.d("abc : " + abc
                + " abp : " + abp
                + " acp : " + acp
                + " bcp : " + bcp);
        if (abc == abp + acp + bcp) {
            return true;
        } else {
            return false;
        }
    }

    private static double triangleArea(float x1, float y1, float x2, float y2, float x3, float y3) {// 返回三个点组成三角形的面积
        double area = 0.5 * Math.abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
        if ((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1) < 0) {
            area = -area;
        }
        return area;
    }

    public static boolean isObtuseAngle(float x1, float y1, float x2, float y2, float x3, float y3) {
        float ax = x2 - x1;
        float ay = y2 - y1;
        float bx = x3 - x1;
        float by = y3 - y1;
        float dotProduct = ax * bx + ay * by;
        return dotProduct < 0;
    }


    public static float getTriangleArea(float x1, float y1, float x2, float y2, float x3, float y3) {
        float a = TouchUtils.getDistance(x1, y1, x2, y2);
        float b = TouchUtils.getDistance(x1, y1, x3, y3);
        float c = TouchUtils.getDistance(x2, y2, x3, y3);
        float s = (a + b + c) / 2;
        float S = (float) Math.sqrt(s * (s - a) * (s - b) * (s - c));
        return S;
    }


    /**
     * 更新文字颜色 0默认，1柔和，2动感，3原声
     */
    @SuppressLint("ResourceAsColor")
    public void setMode(int index) {// 0508 边界文字不变化
        if (mTextPaintSoft != null
                && mTextPaintDynamic != null
                && mTextPaintOriginalSound != null) {

            mTextPaintSoft.setTypeface(Typeface.DEFAULT);
            mTextPaintDynamic.setTypeface(Typeface.DEFAULT);
            mTextPaintOriginalSound.setTypeface(Typeface.DEFAULT);

            mTextPaintSoft.setColor(unCheckedColor);
            mTextPaintDynamic.setColor(unCheckedColor);
            mTextPaintOriginalSound.setColor(unCheckedColor);


            switch (index) {
                case 1:
                    if (type != 0) { // 智能调节不要高亮文字
                        mTextPaintSoft.setTypeface(Typeface.DEFAULT_BOLD);
                        mTextPaintSoft.setColor(checkedColor);
                    }
                    break;
                case 2:
                    if (type != 0) { // 智能调节不要高亮文字
                        mTextPaintDynamic.setColor(checkedColor);
                        mTextPaintDynamic.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    break;
                case 3:
                    if (type != 0) { // 智能调节不要高亮文字
                        mTextPaintOriginalSound.setColor(checkedColor);
                        mTextPaintOriginalSound.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                    break;
            }
            if (touch != null) touch.onMode(index);

            invalidate();
        }
    }

    private int waveFlag, stopFlag = 0;

    private void startWave() {
        stopWave();

        waveFlag++;
        post(waveRunnable);
        postDelayed(() -> {
            stopFlag++;
            if (waveFlag == stopFlag) {
                stopWave();
            }
        }, 900);
    }

    private void stopWave() {
        removeCallbacks(waveRunnable);
        waveCircles.clear();
        invalidate();
    }

    private Runnable waveRunnable = new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < waveCircles.size(); i++) {
                waveCircles.get(i).radius += 5;
                waveCircles.get(i).alpha -= 30;

                if (waveCircles.get(i).alpha < 0) {
                    waveCircles.remove(i);
                }
            }

            if (waveCircles.size() < 4) {
                waveCircles.add(new WaveCircle(mMoveRadius, 255));
            }

            postInvalidate();
            ScView.this.postDelayed(waveRunnable, 100);
        }
    };

    private void startBreathe() {
        stopBreathe();

        if (cx == mCenterX && cy == mCenterY) {
            post(breatheRunnable);
        }
    }

    private void stopBreathe() {
        removeCallbacks(breatheRunnable);
        smallFactor = moveFactor = factor1 = factor2 = factor3 = factor4 = 1.f;
        invalidate();
    }

    private boolean up = false;
    private int currentScale = 0;
    public static final float maxFactor = 1.0f;
    public static final float minFactor = 0.99f;
    public static final float delta = 0.00045f;
    public static final long scaleDuration = 10;


    // 缩：里先缩 放：外先放
    private Runnable breatheRunnable = new Runnable() {
        @Override
        public void run() {

            if (moveFactor >= maxFactor) {
                up = false;
            } else if (moveFactor <= minFactor) {
                up = true;
            }


//            LogUtils.d("is up : " + up + " current : " + currentScale
//                    + " move factor : " + moveFactor + " small factor : " + smallFactor);
            if (up) {
                switch (currentScale) {
                    case 5:
                        moveFactor += delta;
                    case 4:
                        factor4 += delta;
                    case 3:
                        factor3 += delta;
                    case 2:
                        factor2 += delta;
                    case 1:
                        factor1 += delta;
                    case 0:
                        smallFactor += delta;
                        break;
                }
                currentScale++;
                if (currentScale > 5) {
                    currentScale = 0;
                }
//                breatheScaleFactor += delta;
            } else {
                switch (currentScale) {
                    case 5:
                        moveFactor -= delta;
                    case 4:
                        factor4 -= delta;
                    case 3:
                        factor3 -= delta;
                    case 2:
                        factor2 -= delta;
                    case 1:
                        factor1 -= delta;
                    case 0:
                        smallFactor -= delta;
                        break;
                }
                currentScale++;
                if (currentScale > 5) {
                    currentScale = 0;
                }
//                breatheScaleFactor -= delta;
            }
            postInvalidate();

            postDelayed(breatheRunnable, scaleDuration);
        }
    };


    /**
     * 绘制一个圆环
     *
     * @param cx          圆心x
     * @param cy          圆心y
     * @param radius      半径(外圈半径)
     * @param strokeWidth 宽度
     * @param paint       paint
     * @param canvas      画布
     */
    private void drawRing(float cx, float cy, float radius, float strokeWidth, Paint paint, Canvas canvas) {
        try {
            RectF rectF = new RectF(cx - radius + strokeWidth, cy - radius + strokeWidth,
                    cx + radius - strokeWidth, cy + radius - strokeWidth);
            canvas.drawArc(rectF, 0, 360, false, paint);
        } catch (IllegalArgumentException e) { // 0505 这里会偶现异常 只有一次 不浮现了
            LogUtils.d(e.toString());
        }
    }

    /**
     * 获取大圆内移动的小圆的最大x y坐标
     *
     * @param x                目标x
     * @param y                目标y
     * @param centerX          大圆心x
     * @param centerY          大圆心y
     * @param bigRadius        大圆半径
     * @param smallRadius      小圆半径
     * @param bigStrokeWidth   大圆宽
     * @param smallStrokeWidth 小圆宽
     */
    private float[] getMaxXy(float x, float y, float centerX, float centerY,
                             float bigRadius, float smallRadius,
                             float bigStrokeWidth, float smallStrokeWidth) {
        float[] res = new float[6];
        float distance = TouchUtils.getDistance(x, y, centerX, centerY);
        float maxLength = bigRadius - smallRadius - bigStrokeWidth - smallStrokeWidth;
//        Log.d("getMaxXy", "distance : " + distance + " max : " + maxLength);
        if (distance > maxLength) {
            double cosTheta = 0;
            double sinTheta = 0;
            // 不在园内 映射给子view
            if (x == centerX) {
                if (y > centerY) {
                    // 上方
                    cosTheta = 0;
                    sinTheta = 1;
                } else if (y < centerY) {
                    // 下方
                    cosTheta = 0;
                    sinTheta = -1;
                }
            } else {
                float k = (y - centerY) / (x - centerX);
                cosTheta = 1 / Math.sqrt(1 + k * k);
                sinTheta = k * cosTheta;
                if (x < centerX) {
                    cosTheta *= -1.f;
                    sinTheta *= -1.f;
                }
            }


            res[0] = (float) (centerX + maxLength * cosTheta);
            res[1] = (float) (centerY + maxLength * sinTheta);

            res[2] = (float) (centerX + mMoveRadius * cosTheta);
            res[3] = (float) (centerY + mMoveRadius * sinTheta);

            res[4] = (float) (centerX - mMoveRadius * cosTheta);
            res[5] = (float) (centerY - mMoveRadius * sinTheta);

        } else {
            res[0] = x;
            res[1] = y;
        }
        return res;
    }

    /**
     * 重置回到初始状态 并开启呼吸效果
     */
    public void reset() {
        cx = c1x = c2x = c3x = c4x = mSmallCircleX = mCenterX;
        cy = c1y = c2y = c3y = c4y = mSmallCircleY = mCenterY;
        factor1 = factor2 = factor3 = factor4 = smallFactor = moveFactor = 1.f;
        mTextPaintSoft.setTypeface(Typeface.DEFAULT);
        mTextPaintDynamic.setTypeface(Typeface.DEFAULT);
        mTextPaintOriginalSound.setTypeface(Typeface.DEFAULT);
        mTextPaintSoft.setColor(unCheckedColor);
        mTextPaintDynamic.setColor(unCheckedColor);
        mTextPaintOriginalSound.setColor(unCheckedColor);
        invalidate();

        postDelayed(() -> startBreathe(), 100);
    }

    class WaveCircle {
        float radius;
        int alpha;

        public WaveCircle(float radius, int alpha) {
            this.radius = radius;
            this.alpha = alpha;
        }
    }

    private int type = -1;

    public void setType(int type) {

        if (type != this.type && type == 0) {  // 智能调节
            setIntel();


        }
        this.type = type;

    }

    private void setIntel() {
        // 1.修改渐变颜色
        gradientColors = new int[]{Color.parseColor("#CF022D"),
                Color.parseColor("#CCFFFFFF"),
                Color.parseColor("#1AFFFFFF"),};
        gradientPos = new float[]{0.f, 0.15f, 1.f};
        // invalidate();
        // 2.开启自动运动
        // 从210度开始 顺时针旋转触发点击
        post(intelRunnable);
    }

    private int intelDegree = 210;//
    private final int deltaDegree = 5;
    private Runnable intelRunnable = new Runnable() {
        @Override
        public void run() {
            if (intelDegree == 210 - deltaDegree * 3
                    || intelDegree == 330 - deltaDegree * 3
                    || intelDegree == 90 - deltaDegree * 3) {
                touchDegree(intelDegree, MotionEvent.ACTION_UP);
            } else {
                touchDegree(intelDegree, MotionEvent.ACTION_MOVE);
            }

            intelDegree += deltaDegree;

            if (intelDegree >= 360) {
                intelDegree -= 360;
            }
            postDelayed(intelRunnable, 100);
        }
    };

    /**
     * 模拟点击圈外
     *
     * @param degree 角度
     * @param action 事件类型 up会触发波纹
     */
    private void touchDegree(int degree, int action) {
        int toX;
        int toY;
        switch (degree) {
            case 90:
                toX = (int) mCenterX;
                toY = (int) (mCenterY + mOuterRadius);
                handleTouch(toX, toY, action);
                postInvalidate();
                break;
            default:
                toX = (int) (mCenterX + mOuterRadius * Math.cos((degree / 180.f) * Math.PI));
                toY = (int) (mCenterY + mOuterRadius * Math.sin((degree / 180.f) * Math.PI));
                handleTouch(toX, toY, action);
                break;
        }
//        Log.d("touchDegree", "degree : " + degree + " action : " + action
//                + " to X: " + toX + " to Y: " + toY);
    }

    private void dispatcherTouch(int toX, int toY) {
        if (touch != null) {
            touch.onTouch(toX, toY, cx == mCenterX && cy == mCenterY);
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

        void onTouch(int x, int y, boolean inCenter);
    }
}
