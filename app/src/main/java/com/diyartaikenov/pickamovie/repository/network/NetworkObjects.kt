package com.diyartaikenov.pickamovie.repository.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

private const val BASE_URL = "https://api.themoviedb.org/3/"
private const val API_KEY = "ae79897238ffe8dff6aae654a4a07455"
private const val DEFAULT_LANGUAGE = "en"
private const val DEFAULT_REGION = "US"

@Singleton
/**
 * A class that holds a reference to an instance of [MoviesApi].
 */
class MoviesNetwork @Inject constructor() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val moviesApi: MoviesApi = retrofit.create(MoviesApi::class.java)
}

/**
 * A network API that queries *The Movie Db* to get movies related data.
 */
interface MoviesApi {

    /**
     * Get a list of sorted and filtered movies.
     */
    @GET("discover/movie?api_key=$API_KEY")
    suspend fun getMovies(
        /**
         * Specify a language to query translatable fields with.
         *
         * minLength: 2, pattern: ([a-z]{2})-([A-Z]{2}), default: en-US
         */
        @Query("language") language: String = DEFAULT_LANGUAGE,
        /**
         * Specify a ISO 3166-1 code to filter release dates. Must be uppercase.
         *
         * pattern: ^[A-Z]{2}$
         */
        @Query("region") region: String = DEFAULT_REGION,
        /**
         * Specify the page of results to query.
         * Min: 1, max: 1000, default: 1.
         */
        @Query("page") page: Int,
        /**
         * Choose from one of the many available sort options.
         *
         * Allowed Values: popularity.asc, popularity.desc, release_date.asc,
         * release_date.desc, revenue.asc, revenue.desc, primary_release_date.asc,
         * primary_release_date.desc, original_title.asc, original_title.desc,
         * vote_average.asc, vote_average.desc, vote_count.asc, vote_count.desc.
         * default: popularity.desc
         */
        @Query("sort_by") sortBy: String,
        // FIXME: rename variable
        /**
         * Filter and only include movies that have a release date
         * (looking at all release dates) that is less than or equal to the specified value.
         */
        @Query("release_date.lte") releaseDateLte: String,
        /**
         * Comma separated string of genre ids to include in the results.
         */
        @Query("with_genres") withGenres: String,
        /**
         * Comma separated string of genre ids to exclude from the results.
         */
        @Query("without_genres") withoutGenres: String,
        /**
         * Filter and only include movies that have a vote count that is greater than
         * or equal to the specified value.
         */
        @Query("vote_count.gte") minimumVoteCount: Int,
        /**
         * Filter and only include movies that have a vote count that is less than
         * or equal to the specified value.
         */
        @Query("vote_count.lte") maximumVoteCount: Int,
        /**
         * Filter and only include movies that have a rating that is greater
         * or equal to the specified value.
         */
        @Query("vote_average.gte") minimumVoteAverage: Float,
        /**
         * Filter and only include movies that have a rating that is less than
         * or equal to the specified value.
         */
        @Query("vote_average.lte") maximumVoteAverage: Float,
    ): NetworkMovieContainer

    /**
     * Get a predefined list of movies that are popular now.
     */
    @GET("movie/popular?api_key=$API_KEY")
    suspend fun getPopularMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("region") region: String = DEFAULT_REGION,
        @Query("page") page: Int,
    ): NetworkMovieContainer

    /**
     * Get a predefined list of top rated movies.
     */
    @GET("movie/top_rated?api_key=$API_KEY")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("region") region: String = DEFAULT_REGION,
        @Query("page") page: Int,
    ): NetworkMovieContainer

    /**
     * Get movie details with videos included.
     */
    @GET("movie/{movie_id}?api_key=$API_KEY&append_to_response=videos")
    suspend fun getDetailedMovie(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = DEFAULT_LANGUAGE,
        @Query("include_video_language") videoLanguages: String = "$DEFAULT_LANGUAGE,null"
    ): NetworkDetailedMovie

    /**
     * Get a list of all movie genres.
     */
    @GET("genre/movie/list?api_key=$API_KEY")
    suspend fun getAllGenres(
        @Query("language") language: String = DEFAULT_LANGUAGE,
    ): GenresNetworkResponse

    @GET("certification/movie/list?api_key=$API_KEY")
    suspend fun getCertifications(): CertificationsNetworkResponse
}

/**
 * Query parameters for sorting and filtering data through network requests.
 * @see MoviesApi.getMovies
 */
class QueryParams(
    val sortBy: SortBy = SortBy.POPULARITY_DESC,
    val releaseDateLte: LocalDate = LocalDate.now(),
    val withGenres: List<Int> = listOf(),
    val withoutGenres: List<Int> = listOf(),
    val minVoteCount: Int = 0,
    val maxVoteCount: Int = Int.MAX_VALUE,
    val minVoteAverage: Float = 0f,
    val maxVoteAverage: Float = 10f,
    val movieList: MovieList? = null,
)

/**
 * Sort options for network queries.
 *
 * Use [SortBy.value] for [MoviesApi.getMovies()]'s **sortBy** query parameter.
 * @see MoviesApi.getMovies
 */
enum class SortBy(val value: String) {
    POPULARITY_DESC("popularity.desc"),
    VOTE_AVERAGE_DESC("vote_average.desc"),
    RELEASE_DATE_DESC("release_date.desc"),
    RELEASE_DATE_ASC("release_date.asc"),
}

/**
 * Enumeration of predefined movie lists, fetched from [MoviesApi].
 * @see MoviesApi.getPopularMovies
 * @see MoviesApi.getTopRatedMovies
 */
enum class MovieList {
    POPULAR,
    TOP_RATED,
}

/**
 * Represent a list of integers as a string with values separated by a comma.
 */
fun List<Int>.asString(): String {
    val genreIdsJoiner = StringJoiner(",")
    forEach { genreId ->
        genreIdsJoiner.add(genreId.toString())
    }
    return genreIdsJoiner.toString()
}