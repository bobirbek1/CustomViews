package com.idrok.customchart

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.core.graphics.drawable.toBitmap


class ArcProgressBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet,
    defStyle: Int = 0
) :
    ProgressBar(context, attrs, defStyle) {
    private var mRect: RectF? = null
    private var mProgressRect: RectF? = null

    private var mPaint: Paint? = null

    private var mBorderWidth = 20f
    private var mProgressColor = -0x10000
    private var mBackgroundColor = Color.parseColor("#E5E0FF")
    private var mDrawDirection = 0
    private var mStartAngle = -90
    private var mImage: Drawable? = null
    private var dp = 0f
    private var dashLieWidth = 8f
    private var titleSize = 32f
    private var subtitleSize = 24f
    private var titleText = ""
    private var subtitleText = ""


    private fun init(attrs: AttributeSet, defStyle: Int) {
        dp = resources.displayMetrics.density


        val a = context
            .obtainStyledAttributes(attrs, R.styleable.ArcProgressBar, defStyle, 0)
        mBorderWidth = a.getDimension(R.styleable.ArcProgressBar_borderWidth, mBorderWidth)
        mBackgroundColor = a.getColor(R.styleable.ArcProgressBar_backgroundColor, mBackgroundColor)
        mProgressColor = a.getColor(R.styleable.ArcProgressBar_progressColor, mProgressColor)
        mDrawDirection = a.getInteger(R.styleable.ArcProgressBar_drawDirection, mDrawDirection)
        mStartAngle = a.getInteger(R.styleable.ArcProgressBar_startAngle, mStartAngle)
        mImage = a.getDrawable(R.styleable.ArcProgressBar_setImage)
        dashLieWidth = a.getDimension(R.styleable.ArcProgressBar_dashLineWidth, 8f)
        titleSize = a.getDimension(R.styleable.ArcProgressBar_titleTextSize, 16 * dp)
        subtitleSize = a.getDimension(R.styleable.ArcProgressBar_subtitleTextSize, 12 * dp)
        titleText = a.getString(R.styleable.ArcProgressBar_titleText) ?: ""
        subtitleText = a.getString(R.styleable.ArcProgressBar_subtitleText) ?: ""



        a.recycle()
        mRect = RectF()
        mProgressRect = RectF()
        mPaint = Paint()
        mPaint!!.style = Paint.Style.STROKE
    }

    fun setTitle(title: String) {
        titleText = title
        invalidate()
    }

    fun setSubtitle(subtitle: String) {
        subtitleText = subtitle
        invalidate()
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTexts(canvas)
        drawBackground(canvas)
        drawProgress(canvas)
        drawDashedLine(canvas)
        drawImage(canvas)
    }

    private fun drawTexts(canvas: Canvas) {

        mPaint?.style = Paint.Style.FILL

        // Draw title text
        mPaint?.textSize = titleSize
        mPaint?.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        mPaint?.color = Color.parseColor("#000000")
        mPaint?.textAlign = Paint.Align.CENTER
        canvas.drawText(titleText, measuredWidth / 2f, measuredHeight / 2f + 24*dp, mPaint!!)

        // Draw subtitle text
        mPaint?.textSize = subtitleSize
        mPaint?.typeface = null
        mPaint?.color = Color.parseColor("#9E9BB2")
        canvas.drawText(subtitleText, measuredWidth / 2f, measuredHeight / 2 + 48 * dp, mPaint!!)

        mPaint?.style = Paint.Style.STROKE

    }

    private fun drawImage(canvas: Canvas) {
        if (mImage != null) {
            val bitmap = mImage?.toBitmap(
                mImage?.intrinsicWidth ?: 120,
                mImage?.intrinsicHeight ?: 140,
                Bitmap.Config.ARGB_8888
            )
            canvas.drawBitmap(
                bitmap!!,
                measuredWidth / 2f - bitmap.width / 2f,
                measuredHeight / 2f - bitmap.height / 2f - 32 * dp,
                mPaint
            )
        }
    }

    private fun drawDashedLine(canvas: Canvas) {

        mPaint!!.style = Paint.Style.STROKE
        mPaint?.pathEffect = DashPathEffect(floatArrayOf(1f, 15f), 0f)
        mPaint?.strokeWidth = dashLieWidth
        mPaint?.strokeCap = Paint.Cap.SQUARE
        mPaint?.color = Color.parseColor("#9E9BB2")
        mRect!![mBorderWidth + 12 * dp, mBorderWidth + 12 * dp, measuredWidth - 12 * dp - mBorderWidth] =
            measuredHeight - 12 * dp - mBorderWidth
        canvas.drawArc(mRect!!, 132f, 280f, false, mPaint!!)
        mPaint?.pathEffect = null
    }

    private fun drawProgress(canvas: Canvas) {
        if (progress > 0) {
            mPaint!!.style = Paint.Style.STROKE
            mPaint!!.color = mProgressColor
            mPaint!!.strokeWidth = mBorderWidth
            mPaint!!.strokeCap = Paint.Cap.ROUND


            var angle = 270 * progress.toFloat() / max
            angle = if (mDrawDirection == 0) -angle else angle
            canvas.drawArc(mRect!!, 135f, angle, false, mPaint!!)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        val offset = mBorderWidth / 2
        mRect!![offset, offset, measuredWidth - offset] = measuredHeight - offset
        mPaint!!.style = Paint.Style.STROKE
        mPaint?.color = mBackgroundColor
        mPaint?.strokeWidth = mBorderWidth
        mPaint?.strokeCap = Paint.Cap.ROUND
        canvas.drawArc(mRect!!, 135f, 270f, false, mPaint!!)
    }


    init {
        init(attrs, defStyle)
    }
}