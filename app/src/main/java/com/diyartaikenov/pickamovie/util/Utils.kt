package com.diyartaikenov.pickamovie.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import java.util.*
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

/**
 * Take first [n] items and separate them with a comma.
 * @param n A number of first items to return.
 * @param capitalizeSentence Capitalize the first letter of the resulting string if true.
 * Default value is false.
 * @param defaultReturnValue A value to return if the list is empty.
 * @return A string containing the first [n] items, separated by a comma.
 */
    fun List<String>.join(
    n: Int = Int.MAX_VALUE,
    capitalizeSentence: Boolean = false,
    defaultReturnValue: String = "",
): String {
    val joiner = StringJoiner(", ")
    this.take(n).forEach { joiner.add(it) }

    var items = joiner.toString()
    if (capitalizeSentence && items.isNotEmpty()) {
        items = items.substring(0, 1).uppercase() +
                items.substring(1).lowercase()
    }
    return items.ifEmpty { defaultReturnValue }
}

fun getAttributeResource(attributeResId: Int, context: Context): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(
        attributeResId,
        typedValue,
        true
    )
    return typedValue.data
}