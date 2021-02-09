package com.idrok.customchart

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap

class CustomSmallProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {


    private val mPaint = Paint()
    private val mRect = RectF()

    private var progressWidth: Float = 16f
    private var progressColor: Int = Color.parseColor("#FF5674")
    private var progressBackground: Int = Color.parseColor("#FFDDE3")
    private var mImage: Drawable? = null
    private var mProgress: Int = 0
    private var dp = resources.displayMetrics.density

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomSmallProgress, defStyle, 0)
        progressWidth = a.getDimension(R.styleable.CustomSmallProgress_progressWidth, 8 * dp)
        progressColor = a.getColor(R.styleable.CustomSmallProgress_progressColor, progressColor)
        progressBackground =
            a.getColor(R.styleable.CustomSmallProgress_progressBackground, progressBackground)
        mProgress = a.getInt(R.styleable.CustomSmallProgress_setProgress, 0)
        mImage = a.getDrawable(R.styleable.CustomSmallProgress_setImage)
        a.recycle()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawProgress(canvas)
        drawImage(canvas)

    }

    private fun drawImage(canvas: Canvas?) {
        mImage?.let {
            val bitmap = it.toBitmap(
                it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            canvas?.drawBitmap(
                bitmap,
                measuredWidth / 2f - bitmap.width/2,
                measuredHeight / 2f - bitmap.height/2,
                mPaint
            )
        }
    }

    private fun drawProgress(canvas: Canvas?) {
        val offset = progressWidth/2 * dp
        val progress = 360 * mProgress / 100f
        mRect[offset, offset, measuredWidth - offset] = measuredHeight - offset
        mPaint.style = Paint.Style.STROKE
        mPaint.color = progressColor
        mPaint.strokeWidth = progressWidth
        mPaint.strokeCap = Paint.Cap.ROUND
        canvas?.drawArc(mRect, -90f, progress, false, mPaint)
    }

    private fun drawBackground(canvas: Canvas?) {
        val offset = progressWidth/2 * dp
        mRect[offset, offset, measuredWidth - offset] = measuredHeight - offset
        mPaint.style = Paint.Style.STROKE
        mPaint.color = progressBackground
        mPaint.strokeWidth = progressWidth
        mPaint.strokeCap = Paint.Cap.ROUND
        canvas?.drawArc(mRect, -90f, 360f, false, mPaint)
    }

}