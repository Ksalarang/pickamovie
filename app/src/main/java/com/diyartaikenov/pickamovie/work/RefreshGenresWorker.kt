package com.diyartaikenov.pickamovie.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.diyartaikenov.pickamovie.repository.MovieRepository
import com.diyartaikenov.pickamovie.repository.database.MovieDao
import com.diyartaikenov.pickamovie.repository.network.MoviesApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import retrofit2.HttpException
import java.io.IOException

@HiltWorker
class RefreshGenresWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val movieRepository: MovieRepository,
    ): CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            movieRepository.refreshGenres()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}