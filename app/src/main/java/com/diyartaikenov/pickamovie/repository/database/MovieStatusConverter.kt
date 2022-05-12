package com.diyartaikenov.pickamovie.repository.database

import androidx.room.TypeConverter
import com.diyartaikenov.pickamovie.model.MovieStatus

class MovieStatusConverter {
    @TypeConverter
    fun movieStatusToString(status: MovieStatus): String {
        return status.name
    }

    @TypeConverter
    fun stringToMovieStatus(statusName: String): MovieStatus {
        return when(statusName) {
            MovieStatus.RUMORED.name -> MovieStatus.RUMORED
            MovieStatus.PLANNED.name -> MovieStatus.PLANNED
            MovieStatus.IN_PRODUCTION.name -> MovieStatus.IN_PRODUCTION
            MovieStatus.POST_PRODUCTION.name -> MovieStatus.POST_PRODUCTION
            MovieStatus.RELEASED.name -> MovieStatus.RELEASED
            MovieStatus.CANCELED.name -> MovieStatus.CANCELED
            else -> MovieStatus.UNKNOWN
        }
    }
}