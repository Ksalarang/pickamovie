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
import com.diyartaikenov.pickamovie.repository.database.DbMovie
import com.diyartaikenov.pickamovie.repository.database.Genre
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

    // FIXME: add docs to fields and methods
    var queryParams = QueryParams()
        private set

    val scope: CoroutineScope
        get() = viewModelScope

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> = _movies

    private var _detailedMovie = MutableLiveData<DetailedMovie>()
    val detailedMovie: LiveData<DetailedMovie> = _detailedMovie

    private var _movie = MutableLiveData<Movie>()
    val movie: LiveData<Movie> = _movie

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

    fun getMoviesWithQueryParams(
        sortBy: SortBy = queryParams.sortBy,
        releaseDateLte: LocalDate = queryParams.releaseDateLte,
        withGenres: List<Int> = queryParams.withGenres,
        withoutGenres: List<Int> = queryParams.withoutGenres,
    ) {
        queryParams = QueryParams(
            sortBy,
            releaseDateLte,
            withGenres,
            withoutGenres,
        )
        viewModelScope.launch {
            movieRepository.getMovies(queryParams)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
    }

    /**
     * First get previously saved [DbMovie] as [Movie] and update the corresponding [movie] LiveData.
     * Then get [DetailedMovie] from the network and update the [detailedMovie] LiveData.
     * Listen to both LiveData variables and update the UI.
     *
     * This way part of a [Movie] object data is loaded instantly, reducing waiting time.
     */
    fun refreshMovieDetails(movieId: Int) {
        viewModelScope.launch {
            val result = movieRepository.getMovieById(movieId)
            if (result.isSuccess) {
                _movie.value = result.getOrNull()!!
            } else {
                Log.d("myTag", "refreshMovieDetails: " +
                        "${result.exceptionOrNull()!!.message}")
            }
        }
        viewModelScope.launch {
            val result = movieRepository.getDetailedMovieById(movieId)
            if (result.isSuccess) {
                _detailedMovie.value = result.getOrNull()!!
                _networkError.value = false
                _isNetworkErrorShown.value = false
            } else {
                _networkError.value = true
            }
        }
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