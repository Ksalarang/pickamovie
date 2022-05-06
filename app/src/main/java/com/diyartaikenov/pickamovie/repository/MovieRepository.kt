package com.diyartaikenov.pickamovie.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.diyartaikenov.pickamovie.database.MovieDao
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.MoviesApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val moviesApi: MoviesApi
) {

    fun getMovies(): LiveData<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { MoviesPagingSource(moviesApi) }
        ).liveData
    }

    companion object {
        // The Movie Db API appears to return 24 items with each page
        const val NETWORK_PAGE_SIZE = 24
    }
}