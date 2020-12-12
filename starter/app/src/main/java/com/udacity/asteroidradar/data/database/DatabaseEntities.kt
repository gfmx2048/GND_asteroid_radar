package com.udacity.asteroidradar.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.models.Asteroid

@Entity
data class DatabaseAsteroid constructor(
        @PrimaryKey (autoGenerate = false)
        var id: Long,
        val codename: String,
        val closeApproachDate: String,
        val absoluteMagnitude: Double,
        val estimatedDiameter: Double,
        val relativeVelocity: Double,
        val distanceFromEarth: Double,
        val isPotentiallyHazardous: Boolean)

/**
 * CAn we use .copy() here for the objects?
 */
fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
            id = it.id,
            codename= it.codename,
            closeApproachDate= it.closeApproachDate,
            absoluteMagnitude= it.absoluteMagnitude,
            estimatedDiameter= it.estimatedDiameter,
            relativeVelocity= it.relativeVelocity,
            distanceFromEarth= it.distanceFromEarth,
            isPotentiallyHazardous= it.isPotentiallyHazardous
        )
    }
}