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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// The Movie Db API appears to return 24 items with each page
const val NETWORK_PAGE_SIZE = 24

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val moviesApi: MoviesApi,
) {

    private val pagingConfig = PagingConfig(
        pageSize = NETWORK_PAGE_SIZE,
        enablePlaceholders = false,
    )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Ensure that this repository can always provide movie genres
            movieDao.countGenres().collectLatest { genresCount ->
                if (genresCount == 0) {
                    try {
                        refreshGenres()
                    } catch (e: Exception) {
                        Log.d("myTag", "init: refreshing genres: ${e.message}")
                    }
                }
            }
        }
    }

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

    fun getGenres(): Result<Flow<List<Genre>>> {
        return try {
            val genres = movieDao.getAllGenres()
            Result.success(genres)
        } catch (e: Exception) {
            Log.d("myTag", "getGenres: ${e.message}")
            Result.failure(e)
        }
    }

    fun getGenresById(ids: List<Int>): Flow<List<Genre>> {
        return movieDao.getGenresById(ids)
    }

    /**
     * Fetch latest genre data from network and store it in the database.
     * @throws Exception
     */
    suspend fun refreshGenres() {
        val genres = moviesApi.getAllGenres().genres
        movieDao.insertGenres(genres)
    }
}