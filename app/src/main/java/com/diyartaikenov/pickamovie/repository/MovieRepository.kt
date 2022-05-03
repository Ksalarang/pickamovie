package com.diyartaikenov.pickamovie.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.diyartaikenov.pickamovie.database.AppDatabase
import com.diyartaikenov.pickamovie.database.asDomainModel
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.MovieDbNetwork
import com.diyartaikenov.pickamovie.network.asDatabaseModel
import com.diyartaikenov.pickamovie.util.SortBy
import com.diyartaikenov.pickamovie.util.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val database: AppDatabase,
    private val network: MovieDbNetwork
) {

    val movies: LiveData<List<Movie>> =
        Transformations.map(database.movieDao.getMovies(
            SortBy.POPULARITY_DESC.name,
            SortOrder.DESC,
        )) {
            it.asDomainModel()
        }

    suspend fun refreshMovies() {
        withContext(Dispatchers.IO) {
            val movieContainer = network.service.getMoviesSortedAndFiltered()
            database.movieDao.insertAll(movieContainer.asDatabaseModel())
        }
    }
}