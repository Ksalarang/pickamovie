package com.diyartaikenov.pickamovie.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.MovieRepository.Companion.NETWORK_PAGE_SIZE
import com.diyartaikenov.pickamovie.repository.network.MoviesApi
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import com.diyartaikenov.pickamovie.repository.network.SortBy
import com.diyartaikenov.pickamovie.repository.network.asDomainModel
import retrofit2.HttpException
import java.io.IOException
import java.time.format.DateTimeFormatter

private const val STARTING_PAGE_INDEX = 1

class MoviesPagingSource(
    private val moviesApi: MoviesApi,
    private val queryParams: QueryParams
): PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val pageKey = params.key ?: STARTING_PAGE_INDEX

        return try {
            // Use the predefined query to get top rated movie list
            val networkResponse = when (queryParams.sortBy) {
                SortBy.POPULAR -> {
                    moviesApi.getPopularMovies(page = pageKey)
                }
                SortBy.TOP_RATED -> {
                    moviesApi.getTopRatedMovies(page = pageKey)
                }
                else -> {
                    moviesApi.getMovies(
                        page = pageKey,
                        sortBy = queryParams.sortBy.value,
                        releaseDateLte = queryParams.releaseDateLte.format(DateTimeFormatter.ISO_DATE)
                    )
                }
            }

            val prevKey = if (pageKey == STARTING_PAGE_INDEX) null else pageKey - 1

            val nextKey = if (networkResponse.movies.isEmpty()) null else {
                pageKey + (params.loadSize / NETWORK_PAGE_SIZE)
            }

            LoadResult.Page(
                data = networkResponse.asDomainModel(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
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