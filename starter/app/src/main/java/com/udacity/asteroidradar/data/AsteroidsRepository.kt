package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.RetrofitClient
import com.udacity.asteroidradar.network.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import com.udacity.asteroidradar.data.database.asDomainModel
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

enum class AsteroidsApiStatus{
    LOADING,
    ERROR,
    DONE
}

enum class AsteroidsFilter(val value:String){
    SHOW_TODAY(""),
    SHOW_WEEK(""),
    SHOW_SAVED("")
}

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    //observer our database and get the results
    val asteroidsLD: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

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

    //the internal mutableLiveData
    private val _status = MutableLiveData<AsteroidsApiStatus?>()
    //the external immutable LiveData
    val status: LiveData<AsteroidsApiStatus?>
        get() = _status


    fun clearErrorResponse() {
        _errorLD.value =null
    }

    /**
     * used to get all the asteroids
     */
    suspend fun getAsteroidsWithRetrofit(asteroidsFilter: AsteroidsFilter) {
        withContext(Dispatchers.IO) {
            try {
                _status.postValue( AsteroidsApiStatus.LOADING)
                val formattedDatesList = getNextSevenDaysFormattedDates()
                val getResponseFromRetrofit = when(asteroidsFilter){
                    AsteroidsFilter.SHOW_WEEK ->RetrofitClient.instance.getAsteroidsBasedOnClosestApproachAsync(formattedDatesList.first(),formattedDatesList.last(),BuildConfig.API_KEY)
                    AsteroidsFilter.SHOW_TODAY -> RetrofitClient.instance.getAsteroidsBasedOnClosestApproachAsync(formattedDatesList.first(),formattedDatesList.first(),BuildConfig.API_KEY)
                    AsteroidsFilter.SHOW_SAVED -> RetrofitClient.instance.getAsteroidsBasedOnClosestApproachAsync(formattedDatesList.first(),formattedDatesList.last(),BuildConfig.API_KEY)
                }
                Timber.d("RESPONSE FROM GET ASTEROIDS ${getResponseFromRetrofit}}")
                val jsonResponse = JSONObject(getResponseFromRetrofit)
                val networkAsteroidsList = parseAsteroidsJsonResult(jsonResponse)
                database.asteroidDao.insertAll(*networkAsteroidsList.asDatabaseModel())
                _status.postValue(AsteroidsApiStatus.DONE)

            } catch (e: Exception) {
                Timber.d("\nerror.message ${e.message}\n error.localized ${e.localizedMessage} \n error.cause ${e.cause} \n error.stackTrace ${e.stackTrace} \n e.javaClass ${e.javaClass.name} and suppressed.size ${e.suppressed.size}")
                Timber.d("What type is error ${e.javaClass}")
                _status.postValue(AsteroidsApiStatus.ERROR)
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