package com.diyartaikenov.pickamovie.network

import com.diyartaikenov.pickamovie.util.SortBy
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

const val BASE_URL = "https://api.themoviedb.org/3/"
const val API_KEY = "ae79897238ffe8dff6aae654a4a07455"
const val DEFAULT_LANGUAGE = "en-US"

@Singleton
class MoviesNetwork @Inject constructor() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val moviesApi: MoviesApi = retrofit.create(MoviesApi::class.java)
}

interface MoviesApi {

    @GET("movie/popular?api_key=$API_KEY")
    suspend fun getPopularMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = 1
    ): NetworkMovieContainer

    @GET("discover/movie?api_key=$API_KEY")
    suspend fun getMoviesSortedAndFilteredWith(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int = 1,
        @Query("sort_by") sortBy: String = SortBy.POPULARITY_DESC.value,
    ): NetworkMovieContainer
}

@Singleton
class MoviesRemoteDataSource @Inject constructor(
    private val moviesApi: MoviesApi
) {


}