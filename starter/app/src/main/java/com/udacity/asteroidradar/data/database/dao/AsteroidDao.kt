package com.udacity.asteroidradar.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
//    @Query("Select * from Asteroid")
//    fun getAsteroids(): LiveData<List<Asteroid>>
}