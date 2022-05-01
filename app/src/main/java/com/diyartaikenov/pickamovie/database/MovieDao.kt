package com.diyartaikenov.pickamovie.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("select * from movies order by popularity desc")
    fun getPopularMovies(): Flow<List<DatabaseMovie>>
}