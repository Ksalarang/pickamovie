package com.diyartaikenov.pickamovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel() {

    val movies: LiveData<PagingData<Movie>> =
        movieRepository.getMovies().cachedIn(viewModelScope)
}