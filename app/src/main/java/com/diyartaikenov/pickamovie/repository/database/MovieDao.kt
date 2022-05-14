package com.diyartaikenov.pickamovie.repository.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.diyartaikenov.pickamovie.util.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("select * from movies order by popularity desc")
    fun getPopularMovies(): LiveData<List<DbMovie>>

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
    ): LiveData<List<DbMovie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<DbMovie>)

    @Query("select * from genres")
    fun getAllGenres(): Flow<List<Genre>>

    @Query("select * from genres where id in (:ids)")
    fun getGenresById(ids: List<Int>): Flow<List<Genre>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenres(genres: List<Genre>)
}