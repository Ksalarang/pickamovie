package com.diyartaikenov.pickamovie.network

import com.diyartaikenov.pickamovie.model.Movie
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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

@JsonClass(generateAdapter = true)
data class PopularMovie(
    val id: Long,
    val title: String,
    val overview: String,
    @Json(name = "genre_ids")
    val genreIds: List<Int>,
    @Json(name = "release_date")
    val releaseDate: String,

    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,

    val popularity: Double,
    @Json(name = "vote_average")
    val voteAverage: Double,
    @Json(name = "vote_count")
    val voteCount: Int,

    @Json(name = "original_title")
    val originalTitle: String,
    @Json(name = "original_language")
    val originalLanguage: String,
    val video: Boolean,
    val adult: Boolean,
)

fun PopularMovieContainer.asDomainModel(): List<Movie> {
    return movies.map {
        Movie(
            id = it.id,
            title = it.title,
            posterPath = it.posterPath
        )
    }
}