package com.diyartaikenov.pickamovie.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class DbMovie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String,
    // fixme: how to store list of ints?
//    @ColumnInfo(name = "genre_ids")
//    val genreIds: List<Int>,

    @ColumnInfo(name = "poster_path")
    val posterPath: String?,
    @ColumnInfo(name = "backdrop_path")
    val backdropPath: String?,

    val popularity: Double,
    @ColumnInfo(name = "vote_average")
    val voteAverage: Double,
    @ColumnInfo(name = "vote_count")
    val voteCount: Int,
)