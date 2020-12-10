package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    /**
     * used to get all the asteroids
     */
    suspend fun getAsteroidsWithRetrofit(startDate: String,endDate: String) {
        withContext(Dispatchers.IO) {
            try {
                val getResponseFromRetrofit = RetrofitClient.instance.getAsteroidsBasedOnClosestApproachAsync("2020-12-05","2020-12-10",BuildConfig.API_KEY)
                Timber.d("RESPONSE FROM GET ASTEROIDS ${getResponseFromRetrofit}}")
                val jsonResponse = JSONObject(getResponseFromRetrofit)
                val asteroidsList = parseAsteroidsJsonResult(jsonResponse)
                _asteroidsLD.postValue(asteroidsList)

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
            try {
                val getResponseFromRetrofit = RetrofitClient.instance.getImageOfTheDayAsync(BuildConfig.API_KEY)
                Timber.d("RESPONSE FROM GET PICTURE OF THE DAY ${getResponseFromRetrofit}}")
                _pictureOfTheDay.postValue(getResponseFromRetrofit)

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