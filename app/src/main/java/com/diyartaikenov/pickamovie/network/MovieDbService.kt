package com.diyartaikenov.pickamovie.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

const val BASE_URL = "https://api.themoviedb.org/3/"
const val API_KEY = "ae79897238ffe8dff6aae654a4a07455"
const val DEFAULT_LANGUAGE = "en-US"

interface MovieDbService {

    @GET("movie/popular?api_key=$API_KEY&language=$DEFAULT_LANGUAGE")
    suspend fun getPopularMovies(@Query("page")page: Int = 1): NetworkMovieContainer
}

@Singleton
class MovieDbNetwork @Inject constructor() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: MovieDbService = retrofit.create(MovieDbService::class.java)
}