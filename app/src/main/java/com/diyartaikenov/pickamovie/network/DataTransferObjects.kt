package com.diyartaikenov.pickamovie.network

import com.diyartaikenov.pickamovie.model.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * 200 code response object that contains a list of popular movies.
 */
@JsonClass(generateAdapter = true)
data class PopularMovieContainer(
    val page: Int,
    @Json(name = "results")
    val movies: List<PopularMovie>,
    @Json(name = "total_results")
    val totalResults: Int,
    @Json(name = "total_pages")
    val totalPages: Int,
)

/**
 * Movie class retrieved with popular movies query.
 */
@JsonClass(generateAdapter = true)
data class PopularMovie(
    val id: Long,
    val title: String,
    val overview: String,

    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String,

    val popularity: Double,

    @Json(name = "vote_average")
    val voteAverage: Double,
    @Json(name = "vote_count")
    val voteCount: Int,
)

// todo: remove
fun PopularMovieContainer.asDomainModel(): List<Movie> {
    return movies.map {
        Movie(
            id = it.id,
            title = it.title,
            posterPath = it.posterPath
        )
    }
}