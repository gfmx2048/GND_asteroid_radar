package com.udacity.asteroidradar.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.data.database.dao.AsteroidDao

//@Database(entities = [AsteroidsDatabase::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
//    abstract val asteroidDao: AsteroidDao
//
//    private lateinit var INSTANCE: AsteroidsDatabase
//
//    fun getDatabase(context: Context): AsteroidsDatabase {
//        synchronized(AsteroidsDatabase::class.java) {
//            if (!::INSTANCE.isInitialized) {
//                INSTANCE = Room.databaseBuilder(
//                    context.applicationContext,
//                    AsteroidsDatabase::class.java,
//                    "asteroidsDb"
//                ).build()
//            }
//        }
//        return INSTANCE
//    }
}