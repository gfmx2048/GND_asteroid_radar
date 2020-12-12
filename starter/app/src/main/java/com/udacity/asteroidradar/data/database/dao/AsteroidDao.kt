package com.udacity.asteroidradar.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.data.database.DatabaseAsteroid

@Dao
interface AsteroidDao {
    @Query("Select * from DatabaseAsteroid WHERE closeApproachDate >= :date ORDER BY closeApproachDate ASC")
    fun getAllAsteroidsFromTodayIgnorePreviousDays(date:String): LiveData<List<DatabaseAsteroid>>

    @Query("Select * from DatabaseAsteroid WHERE closeApproachDate like :date ORDER BY closeApproachDate ASC")
    fun getTodayAsteroids(date:String): LiveData<List<DatabaseAsteroid>>

    @Query("Select * from DatabaseAsteroid WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate ASC")
    fun getWeekAsteroids(startDate: String, endDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE from DatabaseAsteroid WHERE closeApproachDate < :date")
    fun deleteOldData(date:String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)
}