package com.diyartaikenov.pickamovie.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY = "ae79897238ffe8dff6aae654a4a07455"
private const val DEFAULT_LANGUAGE = "ru-KZ"
private const val DEFAULT_REGION = "KZ"

@Singleton
class MoviesNetwork @Inject constructor() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val moviesApi: MoviesApi = retrofit.create(MoviesApi::class.java)
}

interface MoviesApi {

    @GET("discover/movie?api_key=$API_KEY")
    suspend fun getMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("page") page: Int,
        @Query("region") region: String = DEFAULT_REGION,
        @Query("sort_by") sortBy: String,
        @Query("release_date.lte") releaseDateLte: String,
    ): NetworkMovieContainer
}

/**
 * Query parameters for network queries.
 */
class QueryParams(
    val sortBy: SortBy = SortBy.POPULARITY_DESC,
    /**
     * Filter and only include movies that have a release date
     * (looking at all release dates) that is less than or equal to the specified value.
     */
    val releaseDateLte: Date = Date(),
)

/**
 * Sort options for network queries.
 *
 * Use [SortBy.value] for [MoviesApi.getMovies()]'s **sortBy** query parameter.
 */
enum class SortBy(val value: String) {
    POPULARITY_DESC("popularity.desc"),
    VOTE_AVERAGE_DESC("vote_average.desc"),
    RELEASE_DATE_DESC("release_date.desc"),
    RELEASE_DATE_ASC("release_date.asc"),
}