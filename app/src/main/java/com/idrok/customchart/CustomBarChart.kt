package com.idrok.customchart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class CustomBarChart @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attr, defStyle) {

    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()
    private var data: BarChartData? = null
    private var bottomTextSize = 12f
    private var barWidth = 48f
    private var leftTextSize = 24f
    private var titleTextSize = 24f
    private var horizontalLineWidth = 2f
    private var bottomTextColor: Int = Color.parseColor("#9E9BB2")
    private var leftTextColor: Int = Color.parseColor("#9E9BB2")
    private var horizontalLineColor: Int = Color.parseColor("#F5F5F5")
    private var startColor: Int = Color.parseColor("#6D5FF3")
    private var endColor: Int = Color.parseColor("#C0B4FE")
    private var titleTextColor = Color.parseColor("#000000")
    private var chartType: BarChartType? = null
    private var titleText = ""


    private val startPoints = arrayListOf<PointF>()
    private val endPoints = arrayListOf<PointF>()
    private var maxValue = 0
    private var maxValueOfdata = 0
    private var mBorder = 16f
    private var height = 0f
    private var width = 0f

    private var dp = 0f


    init {
        init(attr, defStyle)
    }

    private fun init(attr: AttributeSet?, defStyle: Int) {

        dp = resources.displayMetrics.density
        mBorder = 16 * dp
        barWidth = 24 * dp
        leftTextSize = 12 * dp
        bottomTextSize = 12 * dp
        titleTextSize = 16 * dp

        val a = context.obtainStyledAttributes(attr, R.styleable.CustomBarChart, defStyle, 0)
        titleText = a.getString(R.styleable.CustomBarChart_titleText) ?: ""
        bottomTextSize = a.getDimension(R.styleable.CustomBarChart_bottomTextSize, 12 * dp)
        barWidth = a.getDimension(R.styleable.CustomBarChart_barWidth, 32 * dp)
        leftTextSize = a.getDimension(R.styleable.CustomBarChart_leftTextSize, 12 * dp)
        titleTextSize = a.getDimension(R.styleable.CustomBarChart_titleTextSize, titleTextSize)
        horizontalLineWidth = a.getDimension(R.styleable.CustomBarChart_HorizontalLineWidth, dp)
        bottomTextColor = a.getColor(R.styleable.CustomBarChart_bottomTextColor, bottomTextColor)
        leftTextColor = a.getColor(R.styleable.CustomBarChart_leftTextColor, leftTextColor)
        horizontalLineColor =
            a.getColor(R.styleable.CustomBarChart_horizontalLineColor, horizontalLineColor)
        titleTextColor = a.getColor(R.styleable.CustomBarChart_titleTextColor, titleTextColor)
        startColor = a.getColor(R.styleable.CustomBarChart_startColor, startColor)
        endColor = a.getColor(R.styleable.CustomBarChart_endColor, endColor)
        a.recycle()

    }

    fun setChartType(chartType: BarChartType) {
        this.chartType = chartType
        invalidate()
    }

    fun setBottomTextColor(color: Int) {
        bottomTextColor = color
        invalidate()
    }

    fun setLeftTextColor(color: Int) {
        leftTextColor = color
        invalidate()
    }

    fun setHorizontalLineColor(color: Int) {
        horizontalLineColor = color
        invalidate()
    }

    fun setStartEndColor(startColor: Int, endColor: Int) {
        this.startColor = startColor
        this.endColor = endColor
        invalidate()
    }

    fun setData(data: BarChartData) {
        this.data = data
        maxValueOfdata = data.values.maxOf {
            it.toInt()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (data == null
            && data?.text.isNullOrEmpty()
            && data?.values.isNullOrEmpty()
            && chartType == null
        )
            return

        evaluateMaxValue()
        evaluatePoints()
        drawHorizontalLinesAndLeftText(canvas)
        drawBottomText(canvas)
        drawBarLine(canvas)
        drawTitleAndCircle(canvas)

    }

    private fun drawTitleAndCircle(canvas: Canvas?) {

        //Draw title text
        mPaint.color = titleTextColor
        mPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        mPaint.textSize = titleTextSize
        mPaint.textAlign = Paint.Align.LEFT

        canvas?.drawText(titleText, measuredWidth / 2f - 32 * dp, 32 * dp, mPaint)

        //draw indicator circle
        mPaint.style = Paint.Style.FILL
        mPaint.shader = LinearGradient(
            measuredHeight / 2 - 48 * dp,
            32 * dp,
            measuredHeight / 2 - 48 * dp,
            16 * dp,
            startColor,
            endColor,
            Shader.TileMode.MIRROR
        )
        canvas?.drawCircle(measuredWidth / 2 - 48 * dp, 24 * dp, titleTextSize / 2, mPaint)
        mPaint.shader = null

    }

    private fun drawBarLine(canvas: Canvas?) {

//        mPaint.style = Paint.Style.STROKE
//        mPaint.strokeCap = Paint.Cap.BUTT
//        mPaint.strokeWidth = barWidth
//        mPaint.pathEffect = CornerPathEffect(10f)
//        val rect = RectF()

        val rect = RectF()
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.isDither = true


        Log.d("CustomBarChart", "drawBarLine: ${startPoints.size}")
        (0 until startPoints.size).forEach {
            mPaint.shader = LinearGradient(
                startPoints[it].x,
                startPoints[it].y,
                endPoints[it].x,
                endPoints[it].y,
                startColor,
                endColor,
                Shader.TileMode.MIRROR
            )


            rect.set(
                startPoints[it].x,
                endPoints[it].y,
                endPoints[it].x,
                startPoints[it].y
            )

            mPath.addRoundRect(rect, 4 * dp, 4 * dp, Path.Direction.CW)

            canvas?.drawPath(mPath, mPaint)

        }
        mPaint.shader = null
    }

    private fun drawBottomText(canvas: Canvas?) {
        mPaint.textSize = bottomTextSize
        mPaint.color = bottomTextColor
        mPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val size = data!!.text.size
        (0 until size).forEach {
            canvas?.drawText(
                data!!.text[it],
                58 * dp + width * it / (size - 1),
                measuredHeight - 8 * dp,
                mPaint
            )
        }

    }

    private fun drawHorizontalLinesAndLeftText(canvas: Canvas?) {

        //Draw horizontal lines
        mPaint.style = Paint.Style.STROKE
        mPaint.color = horizontalLineColor
        mPaint.strokeWidth = horizontalLineWidth
        (0 until 5).forEach {
            val y = measuredHeight - 48 * dp - it * height / 4
            canvas?.drawLine(0f, y, measuredWidth.toFloat(), y, mPaint)
        }

        //draw left text
        mPaint.style = Paint.Style.FILL
        mPaint.color = leftTextColor
        mPaint.textSize = leftTextSize
        (0 until 5).forEach {
            canvas?.drawText(
                "${maxValue * it / 4} ${data?.measurement}",
                8 * dp,
                measuredHeight - 32 * dp - height * it / 4,
                mPaint
            )
        }


    }

    private fun evaluateMaxValue() {
        when (maxValueOfdata) {
            in 2..10 -> {
                maxValue = 10
                return
            }
            in 11..50 -> {
                maxValue = 50
                return
            }
            in 51..100 -> {
                maxValue = 100
                return
            }
            in 101..200 -> {
                maxValue = 200
                return
            }
            in 201..300 -> {
                maxValue = 300
                return
            }
            in 301..400 -> {
                maxValue = 400
                return
            }
            in 401..500 -> {
                maxValue = 500
                return
            }
            in 501..1000 -> {
                maxValue = 1000
                return
            }
            in 1001..2000 -> {
                maxValue = 2000
                return
            }
            in 2001..3000 -> {
                maxValue = 3000
                return
            }
            in 3001..4000 -> {
                maxValue = 4000
                return
            }
            in 4001..5000 -> {
                maxValue = 5000
                return
            }
            in 5001..10000 -> {
                maxValue = 10000
                return
            }
        }
    }

    private fun evaluatePoints() {
        width = measuredWidth - 3 * mBorder - 50 * dp
        height = measuredHeight - 96 * dp
        when (chartType) {

            BarChartType.Daily -> {
                return
            }
            BarChartType.Monthly -> {
                return
            }
            BarChartType.Weekly -> {

                (0 until 7).forEach {
                    startPoints.add(
                        PointF(
                            1 * mBorder + 50 * dp + it * width / 6 - barWidth / 2,
                            measuredHeight - 32 * dp
                        )
                    )
                    endPoints.add(
                        PointF(
                            startPoints[it].x + barWidth,
                            measuredHeight - 48 * dp - height * data!!.values[it] / maxValue
                        )
                    )
                }

            }
            else ->
                return

        }
    }
}

data class BarChartData(
    val text: ArrayList<String>,
    val values: ArrayList<Float>,
    val measurement: String
)

enum class BarChartType {
    Daily,
    Weekly,
    Monthly
}
