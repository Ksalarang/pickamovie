package com.diyartaikenov.pickamovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diyartaikenov.pickamovie.model.DetailedMovie
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.MovieRepository
import com.diyartaikenov.pickamovie.repository.database.Genre
import com.diyartaikenov.pickamovie.repository.network.QueryParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    val movieRepository: MovieRepository
): ViewModel() {

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> = _movies

    private var _detailedMovie = MutableLiveData<DetailedMovie>()
    val detailedMovie: LiveData<DetailedMovie> = _detailedMovie

    private var _genres = MutableLiveData<List<Genre>>()
    val genres: LiveData<List<Genre>> = _genres

    var queryParams = QueryParams()
        private set

    val scope: CoroutineScope
        get() = viewModelScope

    init {
        viewModelScope.launch {
            movieRepository.getMovies(queryParams)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
        viewModelScope.launch {
            movieRepository.getGenres().collectLatest {
                _genres.value = it
            }
        }
    }

    fun getMoviesWithQuery(queryParams: QueryParams) {
        this.queryParams = queryParams

        viewModelScope.launch {
            movieRepository.getMovies(queryParams)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
    }

    /**
     * Get [DetailedMovie] by id from the network and store it in the [detailedMovie] LiveData variable.
     */
    fun refreshMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _detailedMovie.value = movieRepository.getMovieDetails(movieId)
        }
    }
}