package com.udacity.asteroidradar.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import com.udacity.asteroidradar.network.getNextSevenDaysFormattedDates
import timber.log.Timber

class DeleteOldDataWorker(appContext: Context, params: WorkerParameters):CoroutineWorker(appContext,params) {
    companion object{
        const val NAME = "DeleteWork"
    }
    override suspend fun doWork(): Result {
        Timber.d("DELETE work is running")
        val databaseAsteroid = AsteroidsDatabase.getInstance(applicationContext)
        databaseAsteroid.asteroidDao.deleteOldData(getNextSevenDaysFormattedDates().first())
        return Result.success()
    }
}