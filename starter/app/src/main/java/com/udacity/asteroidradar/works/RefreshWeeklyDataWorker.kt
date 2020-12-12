package com.udacity.asteroidradar.works

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.AsteroidsFilter
import com.udacity.asteroidradar.data.AsteroidsRepository
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import retrofit2.HttpException

class RefreshWeeklyDataWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    companion object{
        const val NAME = "RefreshWork"
    }
    override suspend fun doWork(): Result {
        val databaseAsteroid = AsteroidsDatabase.getInstance(applicationContext)
        val repository = AsteroidsRepository(databaseAsteroid)
        return try {
            repository.getAsteroidsWithRetrofit(AsteroidsFilter.SHOW_WEEK)
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}