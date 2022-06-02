package com.diyartaikenov.pickamovie

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.work.*
import com.diyartaikenov.pickamovie.work.RefreshGenresWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val REFRESH_GENRES_WORK_NAME = "com.diyartaikenov.pickamovie.work.RefreshGenresWorker"

@HiltAndroidApp
class MovieApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.Default).launch {
            setupRecurringWork()
        }
    }

    /**
     * Setup WorkManager background job to fetch new network data daily.
     */
    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }
            .build()

        val refreshGenresPeriodicRequest =
            PeriodicWorkRequestBuilder<RefreshGenresWorker>(1, TimeUnit.DAYS)
                .setConstraints(constraints)
                .build()

        val workManager = WorkManager.getInstance(applicationContext)
        val operation = workManager.enqueueUniquePeriodicWork(
            REFRESH_GENRES_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            refreshGenresPeriodicRequest
        )
    }
}