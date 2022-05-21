package com.diyartaikenov.pickamovie.repository.network

import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.asMovieStatus
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
/**
 * Network object that contains the primary information about the movie.
 */
data class NetworkDetailedMovie(
    val id: Int,
    val title: String,
    val overview: String?,
    val genres: List<Genre>,
    @Json(name = "release_date")
    val releaseDate: String,
    /**
     * Counted in minutes
     */
    val runtime: Int?,
    @Json(name = "imdb_id")
    /**
     * Length: 9,
     * pattern: ^tt[0-9]{7}
     */
    val imdbId: String?,

    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,

    val popularity: Double,
    @Json(name = "vote_average")
    val voteAverage: Double,
    @Json(name = "vote_count")
    val voteCount: Int,

    @Json(name = "video")
    val hasVideo: Boolean,
    val status: String,
    @Json(name = "production_countries")
    val productionCountries: List<ProductionCountry>,
    @Json(name = "original_language")
    val originalLanguage: String,
    @Json(name = "original_title")
    val originalTitle: String,
)

@JsonClass(generateAdapter = true)
data class ProductionCountry(
    @Json(name = "iso_3166_1")
    /**
     * ISO-3166-1 two-letter country code.
     */
    val isoCode: String,
    val name: String,
)

fun NetworkDetailedMovie.asDomainModel(): DetailedMovie {
    return DetailedMovie(
        id = id,
        title = title,
        overview = overview,
        genres = genres,
        releaseDate = LocalDate.parse(releaseDate),
        runtime = runtime,
        imdbId = imdbId,
        posterPath = posterPath,
        backdropPath = backdropPath,
        popularity = popularity,
        voteAverage = voteAverage,
        voteCount = voteCount,
        hasVideo = hasVideo,
        status = status.asMovieStatus(),
        productionCountries = productionCountries,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle
    )
}