package com.diyartaikenov.pickamovie.database

import androidx.room.PrimaryKey
import com.diyartaikenov.pickamovie.model.Genre
import com.squareup.moshi.Json

data class DatabaseMovie(
    @PrimaryKey
    val id: Long,
    val title: String,
    val overview: String? = null,
    val genres: List<Genre>,

    @Json(name = "release_date")
    val releaseDate: String,

    /**
     * Counted in minutes
     */
    val runtime: Int? = null,

    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "backdrop_path")
    val backdropPath: String?,

    @Json(name = "imdb_id")
    val imdbId: String?,

    @Json(name = "original_language")
    val originalLanguage: String,
    @Json(name = "original_title")
    val originalTitle: String,

    val popularity: Double,

    val hasVideo: Boolean,
)