package com.diyartaikenov.pickamovie.repository.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diyartaikenov.pickamovie.model.Movie
import java.time.LocalDate

@Entity(tableName = "movies")
@TypeConverters(GenreIdsConverter::class)
data class DbMovie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val overview: String?,
    @ColumnInfo(name = "release_date")
    val releaseDate: String,
    @ColumnInfo(name = "genre_ids")
    val genreIds: List<Int>,

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

fun DbMovie.asDomainModel(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        releaseDate = LocalDate.parse(this.releaseDate),
        genreIds = this.genreIds,
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,
        popularity = this.popularity,
        voteAverage = this.voteAverage,
        voteCount = this.voteCount
    )
}