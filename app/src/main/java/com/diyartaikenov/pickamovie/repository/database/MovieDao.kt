package com.diyartaikenov.pickamovie.repository.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.diyartaikenov.pickamovie.util.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

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

    @Query("select * from movies where id = :id")
    suspend fun getMovieById(id: Int): DbMovie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movies: List<DbMovie>)

    @Query("delete from movies ")
    suspend fun deleteAllMovies()

    //region Genres

    @Query("select * from genres")
    fun getAllGenres(): Flow<List<Genre>>

    @Query("select count(*) from genres")
    fun countGenres(): Flow<Int>

    @Query("select * from genres where id in (:ids)")
    fun getGenresById(ids: List<Int>): Flow<List<Genre>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<Genre>)

    //endregion

    @Query("select * from movie_certifications where country = 'US' order by ordinal asc")
    fun getUsCertifications(): Flow<List<Certification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCertifications(certifications: List<Certification>)

    @Query("select count(*) from movie_certifications")
    fun countCertifications(): Flow<Int>
}