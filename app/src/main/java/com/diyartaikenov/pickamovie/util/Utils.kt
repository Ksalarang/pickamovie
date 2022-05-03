package com.diyartaikenov.pickamovie.util

/**
 *
 * **name** is used for database queries, **value** is used for web queries.
 */
enum class SortBy(val value: String) {
    POPULARITY_DESC("popularity.desc"),
    RELEASE_DATE_ASC("release_date.asc"),
    RELEASE_DATE_DESC("release_date.desc"),
    VOTE_AVERAGE_DESC("vote_average.desc"),
}

object SortOrder {
    const val ASC = 0
    const val DESC = 1
}