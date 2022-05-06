package com.diyartaikenov.pickamovie.model

/**
 * Represents a preview of Movie objects.
 *
 * Should be used to display lists of movies.
 */
data class Movie(
    val id: Long,
    val title: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val genreIds: List<Int>,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
)

/**
 * Movie class used to get the details about a particular movie.
 */
data class DetailedMovie(
    val id: Long,
    val title: String,
    val overview: String?,
    val genres: List<MovieGenre>,
    val releaseDate: String,
    /**
     * Counted in minutes
     */
    val runtime: Int?,
    /**
     * Length: 9,
     * pattern: ^tt[0-9]{7}
     */
    val imdbId: String?,

    val posterPath: String?,
    val backdropPath: String?,

    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,

    val hasVideo: Boolean,
    val status: MovieStatus,
    val productionCountries: List<ProductionCountry>,
    val originalLanguage: String,
    val originalTitle: String,
)

data class MovieGenre(
    val id: Int,
    val name: String,
)

data class ProductionCountry(
    /**
     * ISO-3166-1 two-letter country code.
     */
    val isoCode: String,
    val name: String,
)

enum class MovieStatus {
    RUMORED,
    PLANNED,
    IN_PRODUCTION,
    POST_PRODUCTION,
    RELEASED,
    CANCELED,
    UNKNOWN,
}