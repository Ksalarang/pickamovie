package com.diyartaikenov.pickamovie.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.MovieDbNetwork
import com.diyartaikenov.pickamovie.network.PopularMovieContainer
import com.diyartaikenov.pickamovie.network.asDomainModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MovieViewModel(private val context: Context): ViewModel() {

    private var _popularMovies = MutableLiveData<List<Movie>>()

    val popularMovies: LiveData<List<Movie>> = _popularMovies

    var error = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            var container: PopularMovieContainer? = null

            try {
                container = MovieDbNetwork.service.getPopularMovies(1)

                _popularMovies.value = container.asDomainModel()
            } catch (e: HttpException) {
                error.value = e.response().toString()
            }
        }
    }
}