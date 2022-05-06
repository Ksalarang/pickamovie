package com.diyartaikenov.pickamovie.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diyartaikenov.pickamovie.TAG
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
): ViewModel() {

    val movies: LiveData<List<Movie>> = movieRepository.movies

    val moviesPagingData = movieRepository.getMoviesLiveData()

    private var _networkError = MutableLiveData<Boolean>()
    val networkError: LiveData<Boolean> = _networkError

    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            try {
                movieRepository.refreshMovies()
                _networkError.value = false
            } catch (networkError: IOException) {
                Log.d(TAG, networkError.message, networkError.cause)
                _networkError.value = true
            }
        }
    }
}