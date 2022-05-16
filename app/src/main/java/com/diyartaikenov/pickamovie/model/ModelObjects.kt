package com.diyartaikenov.pickamovie.model

import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.network.MoviesApi
import com.diyartaikenov.pickamovie.repository.network.ProductionCountry
import java.time.LocalDate

/**
 * Represents a preview of Movie objects.
 *
 * Should be used to display lists of movies.
 */
data class Movie(
    val id: Int,
    val title: String,
    val overview: String?,
    val releaseDate: LocalDate,
    val genreIds: List<Int>,
    val posterPath: String?,
    val backdropPath: String?,
    val popularity: Double,
    val voteAverage: Double,
    val voteCount: Int,
)

/**
 * Movie class used to get the details about a particular movie.
 */
data class DetailedMovie(
    val id: Int,
    val title: String,
    val overview: String?,
    val genres: List<Genre>,
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

/**
 * Use the [value] to filter movies by their status when fetching data from the [MoviesApi].
 *
 * The [MovieStatus.UNKNOWN] is not supported by the [MoviesApi], it's used in-app only.
 */
enum class MovieStatus(val value: String) {
    RUMORED("Rumored"),
    PLANNED("Planned"),
    IN_PRODUCTION("In Production"),
    POST_PRODUCTION("Post Production"),
    RELEASED("Released"),
    CANCELED("Canceled"),
    UNKNOWN(""),
}

fun String.asMovieStatus(): MovieStatus {
    return when (this) {
        MovieStatus.RUMORED.value -> MovieStatus.RUMORED
        MovieStatus.PLANNED.value -> MovieStatus.PLANNED
        MovieStatus.IN_PRODUCTION.value -> MovieStatus.IN_PRODUCTION
        MovieStatus.POST_PRODUCTION.value -> MovieStatus.POST_PRODUCTION
        MovieStatus.RELEASED.value -> MovieStatus.RELEASED
        MovieStatus.CANCELED.value -> MovieStatus.CANCELED
        else -> MovieStatus.UNKNOWN
    }
}