package com.diyartaikenov.pickamovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.QueryParams
import com.diyartaikenov.pickamovie.network.SortBy
import com.diyartaikenov.pickamovie.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel() {

    private var _movies = MutableLiveData<PagingData<Movie>>()
    val movies: LiveData<PagingData<Movie>> = _movies

    init {
        viewModelScope.launch {
            movieRepository.getMovies(QueryParams())
                .cachedIn(viewModelScope).collect {
                _movies.value = it
            }
        }
    }

    fun getMoviesWithQuery(queryParams: QueryParams) {
        viewModelScope.launch {
            movieRepository.getMovies(queryParams)
                .cachedIn(viewModelScope).collect {
                    _movies.value = it
                }
        }
    }
}