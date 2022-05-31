package com.diyartaikenov.pickamovie.viewmodel

import android.util.Log
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
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.network.MovieList
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import com.diyartaikenov.pickamovie.repository.network.SortBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    val movieRepository: MovieRepository
): ViewModel() {

    var queryParams = QueryParams()
        private set

    val scope: CoroutineScope
        get() = viewModelScope

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> = _movies

    private var _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    private var _networkError = MutableLiveData(false)
    val networkError: LiveData<Boolean> = _networkError

    private var _isNetworkErrorShown = MutableLiveData(false)
    val isNetworkErrorShown: LiveData<Boolean> = _isNetworkErrorShown

    init {
        viewModelScope.launch {
            movieRepository.getMovies(queryParams)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
        // Get the updated list of all movie genres
        viewModelScope.launch {
            val result = movieRepository.getGenres()
            if (result.isSuccess) {
                result.getOrNull()!!.collectLatest {
                    _genres.value = it
                }
                _networkError.value = false
                _isNetworkErrorShown.value = false
            } else {
                _networkError.value = true
            }
        }
    }

    /**
     * todo
     */
    fun getMoviesWithQueryParams(
        sortBy: SortBy = queryParams.sortBy,
        releaseDateLte: LocalDate = queryParams.releaseDateLte,
        withGenres: List<Int> = queryParams.withGenres,
        withoutGenres: List<Int> = queryParams.withoutGenres,
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
            movieList = movieList,
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