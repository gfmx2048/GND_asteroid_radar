package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.models.PictureOfDay
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Using Retrofit / moshi / and retrofit coroutines support
 *
 */
interface AsteroidApi {

    /**
     * Retrieve a list of Asteroids based on their closest approach date to Earth. GET https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY
     * start_date	YYYY-MM-DD	    none	                Starting date for asteroid search
     * end_date	    YYYY-MM-DD	    7 days after start_date	Ending date for asteroid search
     * api_key	    string	        DEMO_KEY	            api.nasa.gov key for expanded usage
     */
    @GET("neo/rest/v1/feed")
    suspend fun getAsteroidsBasedOnClosestApproachAsync(@Query("start_date")startDate:String,@Query("end_date") endDate: String,@Query("api_key") apiKey: String): String // with the suspend function we don't need deferred<>

    /**
     * Image of the day
     */
    @GET("planetary/apod")
    suspend fun getImageOfTheDayAsync( @Query("api_key") apiKey: String): PictureOfDay
}