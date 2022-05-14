package com.diyartaikenov.pickamovie.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.database.MovieDao
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

    fun getMovies(queryParams: QueryParams): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviesPagingSource(moviesApi, queryParams) }
        ).flow
    }

    suspend fun getMovieDetails(movieId: Int): DetailedMovie {
        return moviesApi.getMovieDetails(movieId = movieId).asDomainModel()
    }

    suspend fun getGenres(): Flow<List<Genre>> {
        val genres = moviesApi.getGenres().genres
        movieDao.insertGenres(genres)

        return movieDao.getAllGenres()
    }

    fun getGenresById(ids: List<Int>): Flow<List<Genre>> {
        return movieDao.getGenresById(ids)
    }

    companion object {
        // The Movie Db API appears to return 24 items with each page
        const val NETWORK_PAGE_SIZE = 24
    }
}