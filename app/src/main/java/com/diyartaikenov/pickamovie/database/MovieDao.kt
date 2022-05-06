package com.diyartaikenov.pickamovie.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.diyartaikenov.pickamovie.util.SortOrder

@Dao
interface MovieDao {

    @Query("select * from movies order by popularity desc")
    fun getPopularMovies(): LiveData<List<DatabaseMovie>>

    @Query("select * from movies " +
            "order by " +
            "case when :sortOrder = 0 then " +
            "case " +
            "when :sortBy = 'RELEASE_DATE_ASC' then release_date " +
            "end end asc, " +
            "case when :sortOrder = 1 then " +
            "case " +
            "when :sortBy = 'POPULARITY_DESC' then popularity " +
            "when :sortBy = 'RELEASE_DATE_DESC' then release_date " +
            "when :sortBy = 'VOTE_AVERAGE_DESC' then vote_average " +
            "end end desc ")
    /**
     * Get sorted and filtered movies list.
     * @param sortBy use [SortBy.name] field.
     * @param sortOrder use [SortOrder.ASC] or [SortOrder.DESC] constants.
     */
    fun getMovies(
        sortBy: String,
        sortOrder: Int,
    ): LiveData<List<DatabaseMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<DatabaseMovie>)
}