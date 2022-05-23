package com.diyartaikenov.pickamovie.repository.network

import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.MovieVideo
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
    @Json(name = "videos")
    val videosResponse: NetworkMovieVideosResponse,
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

@JsonClass(generateAdapter = true)
data class NetworkMovieVideosResponse(
    @Json(name = "results")
    val videos: List<NetworkMovieVideo>
)

@JsonClass(generateAdapter = true)
data class NetworkMovieVideo(
    @Json(name = "iso_639_1")
    /**
     * ISO-639-1 2-letter language code. Example: 'en' for English
     */
    val language: String,
    @Json(name = "iso_3166_1")
    /**
     * ISO-3166-1 2-letter region code. Example: 'US' for USA
     */
    val region: String,
    val name: String,
    val key: String,
    val site: String,
    val size: Int,
    val type: String,
    val official: Boolean,
    @Json(name = "published_at")
    val publishedAt: String,
    val id: String,
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
        originalTitle = originalTitle,
        videos = videosResponse.videos.asDomainModel(),
    )
}

fun List<NetworkMovieVideo>.asDomainModel(): List<MovieVideo> {
    return this.map {
        MovieVideo(
            language = it.language,
            region = it.region,
            name = it.name,
            key = it.key,
            site = it.site,
            size = it.size,
            type = it.type,
            official = it.official,
            publishedAt = it.publishedAt,
            id = it.id,
        )
    }
}