package com.diyartaikenov.pickamovie.network

import com.diyartaikenov.pickamovie.model.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 200 code response returns this object that contains a list of movies.
 */
@JsonClass(generateAdapter = true)
data class NetworkMovieContainer(
    val page: Int,
    @Json(name = "results")
    val movies: List<NetworkMovie>,
    @Json(name = "total_results")
    val totalResults: Int,
    @Json(name = "total_pages")
    val totalPages: Int,
)

/**
 * Movie class retrieved from network.
 */
@JsonClass(generateAdapter = true)
data class NetworkMovie(
    val id: Long,
    val title: String,
    val overview: String,
    @Json(name = "release_date")
    val releaseDate: String,
    @Json(name = "genre_ids")
    val genreIds: List<Int>,

    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,

    val popularity: Double,
    @Json(name = "vote_average")
    val voteAverage: Double,
    @Json(name = "vote_count")
    val voteCount: Int,
)

fun NetworkMovieContainer.asDomainModel(): List<Movie> {
    return movies.map {
        Movie(
            id = it.id,
            title = it.title,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            genreIds = it.genreIds,
            popularity = it.popularity,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount
        )
    }
}