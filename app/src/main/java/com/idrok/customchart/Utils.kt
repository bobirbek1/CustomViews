package com.idrok.customchart

import android.util.DisplayMetrics
import android.util.TypedValue


fun Float.dpToPx(displayMetrics: DisplayMetrics): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displayMetrics)
}

fun Float.spToPx(displayMetrics: DisplayMetrics): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, displayMetrics)
}


data class PointsF(
        val x: Float,
        val y: Float
)