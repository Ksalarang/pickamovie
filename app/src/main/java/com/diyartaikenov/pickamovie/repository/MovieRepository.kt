package com.diyartaikenov.pickamovie.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.diyartaikenov.pickamovie.database.MovieDao
import com.diyartaikenov.pickamovie.database.asDomainModel
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.MoviesApi
import com.diyartaikenov.pickamovie.network.asDatabaseModel
import com.diyartaikenov.pickamovie.util.SortBy
import com.diyartaikenov.pickamovie.util.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val moviesApi: MoviesApi
) {

    val movies: LiveData<List<Movie>> =
        Transformations.map(movieDao.getMovies(
            SortBy.POPULARITY_DESC.name,
            SortOrder.DESC,
        )) {
            it.asDomainModel()
        }

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val movieContainer = moviesApi.getMoviesSortedAndFilteredWith()
            movieDao.insertAll(movieContainer.asDatabaseModel())
        }
    }
}