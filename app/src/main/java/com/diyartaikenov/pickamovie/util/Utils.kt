package com.diyartaikenov.pickamovie.util

import android.content.Context
import android.util.DisplayMetrics
import kotlin.math.roundToInt

object SortOrder {
    const val ASC = 0
    const val DESC = 1
}

/**
 * Convert dp unit to equivalent pixels, depending on on device density.
 * The result is calculated in Float values, then rounded to the nearest Int.
 *
 * @param dp A value in dp (density independent pixels) unit which is to be converted into pixels.
 * @param context Context to get resources and device specific display metrics.
 * @return An Int value to represent pixel equivalent to dp depending on device density.
 */
fun convertDpToPixels(dp: Int, context: Context): Int {
    val px = dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    return px.roundToInt()
}