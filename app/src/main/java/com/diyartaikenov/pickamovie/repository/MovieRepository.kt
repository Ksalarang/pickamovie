package com.diyartaikenov.pickamovie.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.database.MovieDao
import com.diyartaikenov.pickamovie.repository.database.asDomainModel
import com.diyartaikenov.pickamovie.repository.network.MoviesApi
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import com.diyartaikenov.pickamovie.repository.network.asDomainModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val moviesApi: MoviesApi
) {

    private val pagingConfig = PagingConfig(
        pageSize =  NETWORK_PAGE_SIZE,
        enablePlaceholders = false,
    )

    fun getMovies(queryParams: QueryParams): Flow<PagingData<Movie>> {
        return Pager(
            config = pagingConfig,
            pagingSourceFactory = { MoviesPagingSource(moviesApi, queryParams, movieDao) }
        ).flow
    }

    suspend fun getDetailedMovieById(id: Int): Result<DetailedMovie> {
        return try {
            val movie = moviesApi.getDetailedMovie(movieId = id).asDomainModel()
            Result.success(movie)
        } catch (e: Exception) {
            Log.d("myTag", "getMovieDetails: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getMovieById(id: Int): Result<Movie> {
        return try {
            val movie = movieDao.getMovieById(id).asDomainModel()
            Result.success(movie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGenres(): Result<Flow<List<Genre>>> {
        return try {
            val genres = moviesApi.getAllGenres().genres
            movieDao.insertGenres(genres)
            Result.success(movieDao.getAllGenres())
        } catch (e: Exception) {
            Log.d("myTag", "getGenres: ${e.message}")
            Result.failure(e)
        }
    }

    fun getGenresById(ids: List<Int>): Flow<List<Genre>> {
        return movieDao.getGenresById(ids)
    }

    companion object {
        // The Movie Db API appears to return 24 items with each page
        const val NETWORK_PAGE_SIZE = 24
    }
}