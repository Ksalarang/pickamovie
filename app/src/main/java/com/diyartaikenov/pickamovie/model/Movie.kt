package com.diyartaikenov.pickamovie.model

/**
 * Represents a preview of Movie objects.
 *
 * Should be used in app to be displayed on screen i. e. in lists.
 */
data class Movie(
    val id: Long,
    val title: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
)

data class Genre(
    val id: Long,
    val name: String,
)

data class ProductionCountry(
    /**
     * ISO-3166-1 two-letter country code.
     */
    val isoCode: String,
    val name: String,
)