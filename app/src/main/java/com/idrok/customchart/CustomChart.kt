package com.idrok.customchart

import android.content.Context
import android.graphics.*
import android.graphics.Color.WHITE
import android.graphics.Color.parseColor
import android.util.AttributeSet
import android.util.Log
import android.view.View


class SmoothLineChartEquallySpaced @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private val mPaint: Paint
    private val mPath: Path
    private val mCircleSize: Float
    private val mStrokeSize: Float
    private val mBorder: Float
    private var mValues = floatArrayOf()
    private val mMinY = 0f
    private var mMaxY = 0f
//    private val points = arrayListOf<PointF>()
//    private var size = 0


    fun setData(values: FloatArray) {
        mValues = values
        if (values.isNotEmpty()) {
            mMaxY = values[0]
            //mMinY = values[0].y;
            for (y in values) {
                if (y > mMaxY) mMaxY = y
                /*if (y < mMinY)
					mMinY = y;*/
            }
        }
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (mValues.isEmpty()) return
        val size = mValues.size
        val height = measuredHeight - 14 * mBorder
        val width = measuredWidth - 8 * mBorder
        Log.d("CustomView", "draw: $width")
        val dX = if (mValues.size > 1) (mValues.size - 1).toFloat() else 2f
        val dY: Float = if (mMaxY - mMinY > 0) mMaxY - mMinY else 2f
        mPath.reset()

        // calculate point coordinates
        val points: MutableList<PointF> = ArrayList(size)
        for (i in 0 until size) {
            val x = 4 * mBorder + i * width / dX
            val y = 8 * mBorder + height - (mValues[i] - mMinY) * height / dY
            points.add(PointF(x, y))
        }

        mPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.color = parseColor("#FFFFFF")
        mPaint.textSize = 2 * mBorder
        canvas.drawText("Ежедневные результат", width / 2 + 4 * mBorder, 4 * mBorder, mPaint)
        mPaint.typeface = null

        //draw gradient vertical lines

        mPaint.strokeWidth = mStrokeSize / 3
        for (point in points) {
            mPaint.shader = LinearGradient(points[0].x, 8 * mBorder, points[0].x, 8 * mBorder + height,
                    intArrayOf(parseColor("#7165E3"),
                            parseColor("#ACA7DF"),
                            parseColor("#7165E3")),
                    floatArrayOf(0f, 0.5f, 1f),
                    Shader.TileMode.CLAMP)
            canvas.drawLine(point.x, 8 * mBorder, point.x, height + 8 * mBorder, mPaint)
        }
        mPaint.shader = null
        mPaint.strokeWidth = mStrokeSize


        // Draw text week name
        mPaint.color = parseColor("#FFFFFF")
        mPaint.textSize = mBorder + 8
        (0 until 7).forEach {
            canvas.drawText("Пн", points[it].x, height + 10 * mBorder, mPaint)
        }

        // calculate smooth path
        var lX = 0f
        var lY = 0f
        mPath.moveTo(points[0].x, points[0].y)
        for (i in 1 until size) {
            val p = points[i] // current point

            // first control point
            val p0 = points[i - 1] // previous point
            val x1 = p0.x + lX
            val y1 = p0.y + lY

            // second control point
            val p1 = points[if (i + 1 < size) i + 1 else i] // next point
            lX = (p1.x - p0.x) / 2 * SMOOTHNESS // (lX,lY) is the slope of the reference line
            lY = (p1.y - p0.y) / 2 * SMOOTHNESS
            val x2 = p.x - lX
            val y2 = p.y - lY

            // add line
            mPath.cubicTo(x1, y1, x2, y2, p.x, p.y)
        }


        // draw path
        mPaint.color = CHART_COLOR
        mPaint.style = Paint.Style.STROKE
        canvas.drawPath(mPath, mPaint)

        // draw area
//        if (size > 0) {
//            mPaint.style = Paint.Style.FILL
//            mPaint.color = CHART_COLOR and 0xFFFFFF or 0x10000000
//            mPath.lineTo(points[size - 1].x, height + mBorder)
//            mPath.lineTo(points[0].x, height + mBorder)
//            mPath.close()
//            canvas.drawPath(mPath, mPaint)
//        }

        // draw circles
        mPaint.color = CHART_COLOR
        mPaint.style = Paint.Style.FILL_AND_STROKE
        for (point in points) {
            if (point == points[points.size - 1]) {
                canvas.drawCircle(point.x, point.y, mCircleSize, mPaint)
                continue
            }
            canvas.drawCircle(point.x, point.y, mCircleSize / 2, mPaint)
            if (point == points[0]) {
                mPaint.color = WHITE
            }
        }
        mPaint.style = Paint.Style.FILL
        mPaint.color = CHART_COLOR
        for (point in points) {
            if (point == points[0])
                continue
            if (point == points[points.size - 1]) {
                mPaint.color = parseColor("#7165E3")
                canvas.drawCircle(point.x, point.y, mStrokeSize, mPaint)
                continue
            }
            canvas.drawCircle(point.x, point.y,
//                    (mCircleSize - mStrokeSize) / 2,
                    mStrokeSize / 2 + 4,
                    mPaint)
        }
    }

    companion object {
        private val CHART_COLOR = parseColor("#1AF8EF")

        //                -0xff6634
        private const val CIRCLE_SIZE = 8
        private const val STROKE_SIZE = 4
        private const val SMOOTHNESS = 0.45f // the higher the smoother, but don't go over 0.5
    }

    init {
        val scale = context.resources.displayMetrics.density
        mCircleSize = scale * CIRCLE_SIZE
        mStrokeSize = scale * STROKE_SIZE
        mBorder = mCircleSize
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPath = Path()
    }
}