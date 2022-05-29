package com.diyartaikenov.pickamovie.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.MovieRepository.Companion.NETWORK_PAGE_SIZE
import com.diyartaikenov.pickamovie.repository.database.MovieDao
import com.diyartaikenov.pickamovie.repository.network.*

private const val STARTING_PAGE_INDEX = 1

class MoviesPagingSource(
    private val moviesApi: MoviesApi,
    private val queryParams: QueryParams,
    private val moviesDao: MovieDao,
): PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            val networkResponse = when (queryParams.sortBy) {
                SortBy.POPULAR -> {
                    moviesApi.getPopularMovies(page = pageKey)
                }
                SortBy.TOP_RATED -> {
                    moviesApi.getTopRatedMovies(page = pageKey)
                }
                else -> {
                    // If withGenres value is null,
                    // query movie results without filtering by genres
                    moviesApi.getMovies(
                        page = pageKey,
                        sortBy = queryParams.sortBy.value,
                        releaseDateLte = queryParams.releaseDateLte.toString(),
                        withGenres = queryParams.withGenres.asString(),
                        withoutGenres = queryParams.withoutGenres.asString(),
                    )
                }
            }
            // Persist the movie data in the db to retrieve it later when getting movie details.
            moviesDao.insertAll(networkResponse.asDatabaseModel())

            val prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1

            val nextKey = if (networkResponse.movies.isEmpty()) null else {
                pageKey + (params.loadSize / NETWORK_PAGE_SIZE)
            }

            LoadResult.Page(
                data = networkResponse.asDomainModel(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            Log.d("myTag", "load: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val closestPageToAnchor = state.closestPageToPosition(anchorPosition)

            closestPageToAnchor?.prevKey?.plus(1)
                ?: closestPageToAnchor?.nextKey?.minus(1)
        }
    }
}