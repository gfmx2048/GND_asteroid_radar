package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.squareup.moshi.Moshi
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.RetrofitClient
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    //the internal mutableLiveData
    private val _asteroidsLD = MutableLiveData<List<Asteroid>?>()
    //the external immutable LiveData
    val asteroidsLD: LiveData<List<Asteroid>?>
        get() = _asteroidsLD

    //the internal mutableLiveData
    private val _pictureOfTheDay = MutableLiveData<PictureOfDay?>()
    //the external immutable LiveData
    val pictureOfTheDay: LiveData<PictureOfDay?>
        get() = _pictureOfTheDay

    //the internal mutableLiveData
    private val _errorLD = MutableLiveData<String>()
    //the external immutable LiveData
    val errorLD: LiveData<String?>
        get() = _errorLD

    fun clearErrorResponse() {
        _errorLD.value =null
    }

    fun clearAsteroidsResponse() {
        _asteroidsLD.value =null
    }

    fun clearPictureOfTheDay(){
        _pictureOfTheDay.value =null
    }

    /**
     * used to get all the asteroids
     */
    suspend fun getAsteroidsWithRetrofit(startDate: String,endDate: String) {
        withContext(Dispatchers.IO) {
            //Get the Deferred object for our retrofit request
            val getResponseFromRetrofit = RetrofitClient.instance.getAsteroidsBasedOnClosestApproach("2020-12-01","2020-12-07",BuildConfig.API_KEY)
            try {
                //await the deferred response
                val apiResponse = getResponseFromRetrofit.await()
                val jsonResponse = JSONObject(apiResponse.toString())
                Timber.d("RESPONSE FROM GET ASTEROIDS ${jsonResponse}}")

                val sortedList = parseAsteroidsJsonResult(jsonResponse)
                _asteroidsLD.postValue(sortedList)

            } catch (e: Exception) {
                Timber.d("\nerror.message ${e.message}\n error.localized ${e.localizedMessage} \n error.cause ${e.cause} \n error.stackTrace ${e.stackTrace} \n e.javaClass ${e.javaClass.name} and suppressed.size ${e.suppressed.size}")
                Timber.d("What type is error ${e.javaClass}")
                when (e) {
                    is HttpException ->{
                        _errorLD.postValue("Error")
                    }
                    is UnknownHostException -> {
                        _errorLD.postValue("No internet")
                    }
                    else -> {
                        _errorLD.postValue("Unknown error ${e.localizedMessage}")
                    }
                }
            }
        }
    }

    suspend fun getImageOfTheDay() {
        withContext(Dispatchers.IO) {
            //Get the Deferred object for our retrofit request
            val getResponseFromRetrofit = RetrofitClient.instance.getImageOfTheDay(BuildConfig.API_KEY)
            try {
                //await the deferred response
                val apiResponse = getResponseFromRetrofit.await()
                Timber.d("RESPONSE FROM GET PICTURE OF THE DAY ${apiResponse}}")
                _pictureOfTheDay.postValue(apiResponse)

            } catch (e: Exception) {
                Timber.d("\nerror.message ${e.message}\n error.localized ${e.localizedMessage} \n error.cause ${e.cause} \n error.stackTrace ${e.stackTrace} \n e.javaClass ${e.javaClass.name} and suppressed.size ${e.suppressed.size}")
                Timber.d("What type is error ${e.javaClass}")
                when (e) {
                    is HttpException ->{
                        _errorLD.postValue("Error")
                    }
                    is UnknownHostException -> {
                        _errorLD.postValue("No internet")
                    }
                    else -> {
                        _errorLD.postValue("Unknown error ${e.localizedMessage}")
                    }
                }
            }
        }
    }
}