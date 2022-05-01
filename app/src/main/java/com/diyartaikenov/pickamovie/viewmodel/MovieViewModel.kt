package com.diyartaikenov.pickamovie.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diyartaikenov.pickamovie.model.Movie
import com.diyartaikenov.pickamovie.network.MovieDbNetwork
import com.diyartaikenov.pickamovie.network.NetworkMovieContainer
import com.diyartaikenov.pickamovie.network.asDomainModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieViewModel @Inject constructor(): ViewModel() {

    private var _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    val errorStatus = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            var container: NetworkMovieContainer? = null

            try {
                container = MovieDbNetwork.service.getPopularMovies(1)

                _popularMovies.value = container.asDomainModel()
            } catch (e: HttpException) {
                errorStatus.value = e.response().toString()
            }
        }
    }
}