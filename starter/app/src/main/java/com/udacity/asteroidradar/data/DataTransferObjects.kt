package com.udacity.asteroidradar.data

import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid


/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 * AsteroidHolder holds a list of asteroids.
 *
 * This is to parse first level of our network result which looks like
 *
 */
//@JsonClass(generateAdapter = true)
//data class NetworkAsteroidsContainer(val asteroids: List<Asteroid>)
//
///**
// * Videos represent a devbyte that can be played.
// */
//@JsonClass(generateAdapter = true)
//data class NetworkVideo(
//    val title: String,
//    val description: String,
//    val url: String,
//    val updated: String,
//    val thumbnail: String,
//    val closedCaptions: String?)

///**
// * Convert Network results to database objects
// */
//fun NetworkAsteroidsContainer.asDomainModel(): List<Asteroid> {
//    return asteroids.map {
//        Asteroid(
//            title = it.title,
//            description = it.description,
//            url = it.url,
//            updated = it.updated,
//            thumbnail = it.thumbnail)
//    }
//}
//
//fun NetworkAsteroidsContainer.asDatabaseModel(): Array<Asteroid> {
//    return asteroids.map {
//        DatabaseVideo(
//            title = it.title,
//            description = it.description,
//            url = it.url,
//            updated = it.updated,
//            thumbnail = it.thumbnail)
//    }.toTypedArray()
//}