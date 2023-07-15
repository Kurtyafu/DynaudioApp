package com.byd.dynaudio_app.custom;

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.byd.dynaudio_app.R

/**
 * 自定义View 绘制 播放、暂停UI
 */
open class PlayStatusView(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
) :
    View(context, attrs, defStyleAttr, defStyleRes) {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0, 0)

    val TAG = "PlayPregressBar"
    var colorPuse: Int = resources.getColor(R.color.white_alpha_95)//暂停图标颜色
    var colorPlay: Int = resources.getColor(R.color.white_alpha_95)//播放图片颜色
    var colorCricle: Int = resources.getColor(R.color.white_alpha_95)//外环颜色
    var colorProgress: Int = resources.getColor(R.color.white_alpha_95)//已经走过的进度颜色

    var r_c = 0f//圆环半径
    var paintPause: Paint//暂停图标画笔
    var paintPlay: Paint//播放图标画笔
    var paintCricle: Paint//圆环画笔
    var paintProgress: Paint//已经走过的图标画笔
    var w = 0//view的宽
    var h = 0//wiew的高
    var ox = 0f//圆心坐标x
    var oy = 0f//圆心y
    var painWidth = 3f//画笔宽
    var proportion = 0.6f//图形占比，圆占view的宽度（高度，按较小的计算）比，该数值越大图片占view的比例就越大

    var max: Int = 100
    var progress: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var isPlaying = false
        set(value) {
            field = value
            invalidate()
        }

    //播放图标三个点的坐标
    val playIconArray: Array<Coordinate> by lazy {
        arrayOf(Coordinate(), Coordinate(), Coordinate())
    }
    val pauseIconArray: Array<Coordinate> by lazy {
        arrayOf(Coordinate(), Coordinate(), Coordinate(), Coordinate())
    }
    val progressArray: Array<Coordinate> by lazy {
        arrayOf(Coordinate(), Coordinate())
    }

    init {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PlayStatusView)
        painWidth = array.getDimension(
            R.styleable.PlayStatusView_paint_width,
            dip2px(context, painWidth).toFloat()
        )
        colorCricle = array.getColor(R.styleable.PlayStatusView_paint_color, colorCricle)
        colorPlay = array.getColor(R.styleable.PlayStatusView_paint_color, colorPlay)
        colorPuse = array.getColor(R.styleable.PlayStatusView_paint_color, colorPuse)
        proportion = array.getFloat(R.styleable.PlayStatusView_proportion, proportion)

        array.recycle()
        paintPause = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE//空心，FILL填充
            color = colorPuse
            strokeWidth = painWidth
        }
        paintPlay = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL//空心，FILL填充
            color = colorPlay
            strokeWidth = painWidth
        }
        paintCricle = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE//空心，FILL填充
            color = colorCricle
            strokeWidth = painWidth
        }
        paintProgress = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL//空心，FILL填充
            strokeWidth = painWidth + 2
        }
    }

    fun setbgColor(color: Int) {
        paintCricle.color = color
        invalidate()
    }

    companion object {
        /**
         * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
         */
        fun dip2px(context: Context, dpValue: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpValue, context.resources.displayMetrics
            ).toInt()
        }
    }

    /**
     * 设置最新状态 两种状态 播放、暂停随后绘制对应的UI
     */
    fun setPlayStatus(isPlaying: Boolean) {
        this.isPlaying = isPlaying;
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        //获取view宽高
        h = bottom - top
        w = right - left
        //计算圆心坐标和宽高，view内相对坐标
        ox = w * 0.5f
        oy = h * 0.5f
        if (w > h) {
            r_c = h * proportion * 0.5f
        } else {
            r_c = w * proportion * 0.5f
        }
//        LogUtils.i(TAG, Throwable(), "sqrt=" + Math.sqrt(4.0))
        //计算三角形位置坐标
//        3:4/5
//        13.2:17.6/22
        var rMin = r_c * 0.5f
        var temp = 3 * rMin / (2 * Math.sqrt(3.0))
        playIconArray[0].cx = (ox - Math.sqrt(rMin * rMin - temp * temp)).toFloat()
        playIconArray[0].cy = (oy - temp).toFloat()
        playIconArray[1].cx = playIconArray[0].cx
        playIconArray[1].cy = (oy + temp).toFloat()
        playIconArray[2].cx = ox + rMin
        playIconArray[2].cy = oy

        //计算暂停双竖线的坐标
        var temp2 = 0.25f * r_c
        var tempH = 0.35f * r_c
        pauseIconArray[0].cx = ox - temp2
        pauseIconArray[0].cy = oy - tempH
        pauseIconArray[1].cx = pauseIconArray[0].cx
        pauseIconArray[1].cy = oy + tempH
        pauseIconArray[2].cx = ox + temp2
        pauseIconArray[2].cy = pauseIconArray[0].cy
        pauseIconArray[3].cx = pauseIconArray[2].cx
        pauseIconArray[3].cy = pauseIconArray[1].cy
        //圆弧所在矩形坐标
        progressArray[0].cx = ox - r_c
        progressArray[0].cy = oy - r_c
        progressArray[1].cx = ox + r_c
        progressArray[1].cy = oy + r_c

//        progressArray[0].cx = ox - r_c-1
//        progressArray[0].cy = oy - r_c-1
//        progressArray[1].cx = ox + r_c+1
//        progressArray[1].cy = oy + r_c+1
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //画外层圆圈
        canvas?.drawCircle(ox, oy, r_c, paintCricle)
//        canvas?.drawCircle(ox, oy, 22f, paintCricle)
        if (isPlaying) {
            //画暂停双竖线
            var path1 = Path()
            path1.moveTo(pauseIconArray[0].cx, pauseIconArray[0].cy)
            path1.lineTo(pauseIconArray[1].cx, pauseIconArray[1].cy)
            path1.moveTo(pauseIconArray[2].cx, pauseIconArray[2].cy)
            path1.lineTo(pauseIconArray[3].cx, pauseIconArray[3].cy)
            path1.close()
            canvas?.drawPath(path1, paintPause)
        } else {
            //画内层正三角形
            var path = Path()
            path.moveTo(playIconArray[0].cx, playIconArray[0].cy)
            path.lineTo(playIconArray[1].cx, playIconArray[1].cy)
            path.lineTo(playIconArray[2].cx, playIconArray[2].cy)
            path.lineTo(playIconArray[0].cx, playIconArray[0].cy)
            path.close()
            canvas?.drawPath(path, paintPlay)
        }

//            RectF rect = new RectF(10, 10, 300, 400);
////        canvas.drawArc(rect, 参数一是RectF对象，一个矩形区域椭圆形的限用于定义在形状、大小、电弧
//        180, 参数二是起始角(度)在电弧的开始
//        180, 参数三扫描角(度)开始顺时针测量的
//        true, 参数四是如果这是真的话,包括椭圆中心的电弧,并关闭它,如果它是假这将是一个弧线,参数五是Paint对象；
//        mPaint);
        //画进度弧线
//        var rect = RectF(
//            progressArray[0].cx, progressArray[0].cy, progressArray[1].cx, progressArray[1].cy
//        )
//        canvas?.drawArc(rect, -90f, 360f * progress / max, false, paintProgress)
    }

    data class Coordinate(var cx: Float = 0f, var cy: Float = 0f) {
    }

}