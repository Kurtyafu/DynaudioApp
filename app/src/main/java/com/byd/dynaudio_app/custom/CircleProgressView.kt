package com.byd.dynaudio_app.custom

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.byd.dynaudio_app.R

class CircleProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mCurrent //当前进度
            = 0
    private val mPaintOut: Paint
    private val mPaintCurrent: Paint
    private val mPaintWidth //画笔宽度
            : Float
    private var mPaintColor = Color.RED //画笔颜色
    private val location //从哪个位置开始
            : Int
    private var startAngle //开始角度
            = 0f
    private var manimator = ValueAnimator.ofFloat(0f, 100f)

    private var mLoadingCompleteListener: OnLoadingCompleteListener? = null

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView)
        location = array.getInt(R.styleable.CircleProgressView_location, 1)
        mPaintWidth = array.getDimension(
            R.styleable.CircleProgressView_progress_paint_width,
            dip2px(context, 4f).toFloat()
        ) //默认4dp
        mPaintColor =
            array.getColor(R.styleable.CircleProgressView_progress_paint_color, mPaintColor)
        array.recycle()

        //画笔->背景圆弧
        mPaintOut = Paint()
        mPaintOut.isAntiAlias = true
        mPaintOut.strokeWidth = mPaintWidth
        mPaintOut.style = Paint.Style.STROKE
        mPaintOut.color = resources.getColor(R.color.transparent)
        mPaintOut.strokeCap = Paint.Cap.ROUND
        //画笔->进度圆弧
        mPaintCurrent = Paint()
        mPaintCurrent.isAntiAlias = true
        mPaintCurrent.strokeWidth = mPaintWidth
        mPaintCurrent.style = Paint.Style.STROKE
        mPaintCurrent.color = mPaintColor
        mPaintCurrent.strokeCap = Paint.Cap.ROUND
        if (location == 1) { //默认从左侧开始
            startAngle = -180f
        } else if (location == 2) {
            startAngle = -90f
        } else if (location == 3) {
            startAngle = 0f
        } else if (location == 4) {
            startAngle = 90f
        }
    }

    fun startLoading() {
        manimator.duration = 1000
        manimator.interpolator = LinearInterpolator();
        manimator.addUpdateListener { animation ->
            mCurrent = (animation.animatedValue as Float).toInt()
            invalidate()
        }
        manimator.start()
    }

    fun stopLoading() {
        manimator.cancel()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val size = if (width > height) height else width
        setMeasuredDimension(size, size)
    }

    // 然后调用onDraw 进行绘制 看看效果：
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //绘制背景圆弧,因为画笔有一定的宽度，所有画圆弧的范围要比View本身的大小稍微小一些，不然画笔画出来的东西会显示不完整
        val rectF = RectF(
            mPaintWidth / 2,
            mPaintWidth / 2,
            width - mPaintWidth / 2,
            height - mPaintWidth / 2
        )
        canvas.drawArc(rectF, 0f, 360f, false, mPaintOut)

        //绘制当前进度
        val sweepAngle = (360 * mCurrent / 100).toFloat()
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaintCurrent)
        if (mLoadingCompleteListener != null && mCurrent == 100) {
            mLoadingCompleteListener!!.complete()
        }
    }

    /**
     * 获取当前进度值
     *
     * @return
     */
    fun getmCurrent(): Int {
        return mCurrent
    }

    /**
     * 设置当前进度
     *
     * @param mCurrent
     */
    fun setmCurrent(mCurrent: Int) {
        this.mCurrent = mCurrent
        invalidate()
    }

    fun setOnLoadingCompleteListener(loadingCompleteListener: OnLoadingCompleteListener?) {
        mLoadingCompleteListener = loadingCompleteListener
    }

    interface OnLoadingCompleteListener {
        fun complete()
    }

    companion object {
        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dip2px(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}