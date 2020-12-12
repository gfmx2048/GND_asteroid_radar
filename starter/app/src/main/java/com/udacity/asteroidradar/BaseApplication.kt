package com.udacity.asteroidradar

import android.app.Application
import android.os.Build
import androidx.work.*
import com.udacity.asteroidradar.works.DeleteOldDataWorker
import com.udacity.asteroidradar.works.RefreshWeeklyDataWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BaseApplication: Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        addDailyWorkToDeleteOldDAta()
        addDailyWorkToRefreshData()
    }

    private fun addDailyWorkToDeleteOldDAta() {
       applicationScope.launch {
        val periodicDeleteDataWork = PeriodicWorkRequestBuilder<DeleteOldDataWorker>(1,TimeUnit.DAYS).build()
           WorkManager.getInstance().enqueueUniquePeriodicWork(DeleteOldDataWorker.NAME,ExistingPeriodicWorkPolicy.KEEP,periodicDeleteDataWork)
       }
    }

    /**
     * Be able to cache the data of the asteroid by using a worker, so it downloads and saves today's asteroids in background once a day when the device is charging and wifi is enabled.
     */
    private fun addDailyWorkToRefreshData() {
        applicationScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresBatteryNotLow(true)
                .setRequiresCharging(true)
                .apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        setRequiresDeviceIdle(true)
                    }
                }.build()

            val periodicRefreshDataWork = PeriodicWorkRequestBuilder<RefreshWeeklyDataWorker>(1,TimeUnit.DAYS).setConstraints(constraints).build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(RefreshWeeklyDataWorker.NAME,ExistingPeriodicWorkPolicy.KEEP,periodicRefreshDataWork)
        }
    }
}