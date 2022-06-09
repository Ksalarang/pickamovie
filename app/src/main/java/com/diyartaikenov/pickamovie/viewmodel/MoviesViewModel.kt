package com.diyartaikenov.pickamovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.model.MovieVideo
import com.diyartaikenov.pickamovie.repository.MovieRepository
import com.diyartaikenov.pickamovie.repository.database.Certification
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.network.MovieList
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import com.diyartaikenov.pickamovie.repository.network.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    val movieRepository: MovieRepository
): ViewModel() {

    /**
     * Any time this viewModel fetches movies with [QueryParams] it saves them in this field
     * to retrieve later when needed.
     */
    var queryParams = QueryParams()
        private set

    val scope: CoroutineScope
        get() = viewModelScope

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> = _movies

    private var _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean> = _networkError

    private var _isNetworkErrorShown = MutableLiveData(false)
    val isNetworkErrorShown: LiveData<Boolean> = _isNetworkErrorShown

    init {
        viewModelScope.launch {
            val popularMoviesQuery = QueryParams(movieList = MovieList.POPULAR)
            movieRepository.getMovies(popularMoviesQuery)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
    }

    /**
     * Update [_movies] paging data with data fetched from network.
     * Also store the given parameters in the [queryParams] field.
     */
    fun getMoviesWithQueryParams(
        sortBy: SortBy = queryParams.sortBy,
        releaseDateLte: LocalDate = queryParams.releaseDateLte,
        withGenres: List<Int> = queryParams.withGenres,
        withoutGenres: List<Int> = queryParams.withoutGenres,
        minVoteCount: Int = queryParams.minVoteCount,
        maxVoteCount: Int = queryParams.maxVoteCount,
        minVoteAverage: Float = queryParams.minVoteAverage,
        maxVoteAverage: Float = queryParams.maxVoteAverage,
        // If movieList wasn't assigned a value, it should be nulled here,
        // so that callers of this function which request a custom query
        // don't have to know about this and do nulling themselves.
        movieList: MovieList? = null,
    ) {
        queryParams = QueryParams(
            sortBy = sortBy,
            releaseDateLte = releaseDateLte,
            withGenres = withGenres,
            withoutGenres = withoutGenres,
            minVoteCount = minVoteCount,
            maxVoteCount = maxVoteCount,
            movieList = movieList,
            minVoteAverage = minVoteAverage,
            maxVoteAverage = maxVoteAverage,
        )
        viewModelScope.launch {
            movieRepository.getMovies(queryParams)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
    }

    suspend fun getMovie(movieId: Int): Movie? {
        return movieRepository.getMovieById(movieId).getOrNull()
    }

    suspend fun getDetailedMovie(movieId: Int): DetailedMovie? {
        return movieRepository.getDetailedMovieById(movieId).getOrNull()
    }

    fun getGenres(): Result<Flow<List<Genre>>> {
        return movieRepository.getGenres()
    }

    fun getCertifications(): Result<Flow<List<Certification>>> {
        return movieRepository.getCertifications()
    }

    /**
     * Change this flag when a network error is shown.
     */
    fun onNetworkErrorShown() {
        _isNetworkErrorShown.value = true
    }

    /**
     * Return only official YouTube trailers.
     */
    fun filterVideos(videos: List<MovieVideo>): List<MovieVideo> {
        return videos.filter {
            it.official && it.site == "YouTube" && it.type == "Trailer"
        }
    }
}