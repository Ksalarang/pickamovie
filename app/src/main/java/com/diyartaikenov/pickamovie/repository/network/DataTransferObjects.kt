package com.diyartaikenov.pickamovie.repository.network

import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.database.DbMovie
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate

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
    val id: Int,
    val title: String,
    val overview: String?,
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
            overview = it.overview,
            releaseDate = LocalDate.parse(it.releaseDate),
            genreIds = it.genreIds,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            popularity = it.popularity,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount
        )
    }
}

fun NetworkMovieContainer.asDatabaseModel(): List<DbMovie> {
    return movies.map {
        DbMovie(
            id = it.id,
            title = it.title,
            overview = it.overview,
            releaseDate = it.releaseDate,
            genreIds = it.genreIds,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            popularity = it.popularity,
            voteAverage = it.voteAverage,
            voteCount = it.voteCount
        )
    }
}

@JsonClass(generateAdapter = true)
data class GenresNetworkResponse(
    val genres: List<Genre>
)