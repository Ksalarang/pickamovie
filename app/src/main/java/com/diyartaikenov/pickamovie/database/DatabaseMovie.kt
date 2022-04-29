package com.diyartaikenov.pickamovie.database

import com.diyartaikenov.pickamovie.model.Genre
import com.squareup.moshi.Json

data class DatabaseMovie(
    val adult: Boolean,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    @Json(name = "belongs_to_collection")
    val belongsToCollection: Any?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String?,
    val id: Long,
    @Json(name = "imdb_id")
    val imdbId: String?,
    @Json(name = "original_language")
    val originalLanguage: String,
    @Json(name = "original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @Json(name = "poster_path")
    val posterPath: String?,
)