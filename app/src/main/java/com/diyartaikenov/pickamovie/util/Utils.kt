package com.diyartaikenov.pickamovie.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object SortOrder {
    const val ASC = 0
    const val DESC = 1
}

/**
 * Returns ISO 8601 format of the given [Date]: yyyy-MM-dd.
 */
val Date.standardFormat: String
    @SuppressLint("SimpleDateFormat")
    get() = SimpleDateFormat("yyyy-MM-dd").format(this)