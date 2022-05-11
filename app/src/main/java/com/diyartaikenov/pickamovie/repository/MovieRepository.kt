package com.diyartaikenov.pickamovie.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.diyartaikenov.pickamovie.database.Genre
import com.diyartaikenov.pickamovie.database.MovieDao
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.MoviesApi
import com.diyartaikenov.pickamovie.network.QueryParams
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