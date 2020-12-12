package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import com.udacity.asteroidradar.data.database.asDomainModel
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.network.RetrofitClient
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.getNextSevenDaysFormattedDates
import com.udacity.asteroidradar.network.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber

enum class AsteroidsApiStatus{
    LOADING,
    ERROR,
    DONE
}

enum class PotDApiStatus{
    LOADING,
    ERROR,
    DONE
}

enum class AsteroidsFilter{
    SHOW_TODAY,
    SHOW_WEEK,
    SHOW_SAVED
}

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    /**
     *  can you help me an explain me if this approach was right?. This was not working btw, with a null pointer exception. I had also added the filter as a liveData
     */
//    //observer the filter, when we have a change, check our db
//    var asteroidsLD: LiveData<List<Asteroid>> = Transformations.switchMap(filter) {astFilter->
//      when(astFilter){
//          AsteroidsFilter.SHOW_TODAY -> {
//              Transformations.map(database.asteroidDao.getAsteroids()){
//                  it.asDomainModel()
//              }
//          }
//          AsteroidsFilter.SHOW_WEEK ->{
//              Transformations.map(database.asteroidDao.getAsteroids()){
//                  it.asDomainModel()
//              }
//          }
//          else -> {
//              Transformations.map(database.asteroidDao.getAsteroids()){
//                  it.asDomainModel()
//              }
//          }
//      }
//    }

    private var todayAsteroids: LiveData<List<Asteroid>>
    private var weekAsteroids: LiveData<List<Asteroid>>
    private var allAsteroids: LiveData<List<Asteroid>>
    var asteroidsLD = MediatorLiveData<List<Asteroid>>()
    private var filter = AsteroidsFilter.SHOW_WEEK

    init {
        val dates = getNextSevenDaysFormattedDates()
        todayAsteroids = Transformations.map(database.asteroidDao.getTodayAsteroids(dates.first())){
            it.asDomainModel()
        }
        weekAsteroids  = Transformations.map(database.asteroidDao.getWeekAsteroids(dates.first(),dates.last())){
            it.asDomainModel()
        }
        allAsteroids =  Transformations.map(database.asteroidDao.getAllAsteroidsFromTodayIgnorePreviousDays(dates.first())){
            it.asDomainModel()
        }
        //Source for today's asteroids
        asteroidsLD.addSource(todayAsteroids){
            if(filter == AsteroidsFilter.SHOW_TODAY){
                it?.let { asteroidsLD.value = it}
            }
        }
        //Source for week's asteroids
        asteroidsLD.addSource(weekAsteroids){
            if(filter == AsteroidsFilter.SHOW_WEEK){
                it?.let {
                    asteroidsLD.value = it
                }
            }
        }
        //Source for all asteroids
        asteroidsLD.addSource(allAsteroids){
            if(filter == AsteroidsFilter.SHOW_SAVED){
                it?.let { asteroidsLD.value = it}
            }
        }
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

    //the internal mutableLiveData
    private val _pictureStatus = MutableLiveData<PotDApiStatus?>()
    //the external immutable LiveData
    val pictureStatus: LiveData<PotDApiStatus?>
        get() = _pictureStatus

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
                    else -> RetrofitClient.instance.getAsteroidsBasedOnClosestApproachAsync(formattedDatesList.first(),formattedDatesList.first(),BuildConfig.API_KEY)
                }
                val jsonResponse = JSONObject(getResponseFromRetrofit)
                val networkAsteroidsList = parseAsteroidsJsonResult(jsonResponse)
                database.asteroidDao.insertAll(*networkAsteroidsList.asDatabaseModel())
                _status.postValue(AsteroidsApiStatus.DONE)
            } catch (e: Exception) {
                _status.postValue(AsteroidsApiStatus.ERROR)
            }
        }
    }

   suspend fun updateFilter(asteroidsFilter: AsteroidsFilter) {
       when(asteroidsFilter){
           AsteroidsFilter.SHOW_TODAY -> {
               asteroidsLD.removeSource(todayAsteroids) //Renew the source with a new query with a new date. Maybe we changed the date
               val dates = getNextSevenDaysFormattedDates()
               todayAsteroids  = Transformations.map(database.asteroidDao.getTodayAsteroids(dates.first())){ it.asDomainModel() }
               asteroidsLD.addSource(todayAsteroids){
                   Timber.d("query found ${it.size} for today ${dates.first()}") // this will be triggered twice, one from the db and one when the retrofit query update the db
                   it?.let { asteroidsLD.value = it }
               }
           }
           AsteroidsFilter.SHOW_WEEK -> {
               asteroidsLD.removeSource(weekAsteroids) //Renew the source with a new query with a new date. Maybe we changed the date
               val dates = getNextSevenDaysFormattedDates()
               weekAsteroids  = Transformations.map(database.asteroidDao.getWeekAsteroids(dates.first(), dates.last())){ it.asDomainModel() }
               asteroidsLD.addSource(weekAsteroids){
                   Timber.d("query found ${it.size} for today ${dates.first()} until ${dates.last()}") // this will be triggered twice, one from the db and one when the retrofit query update the db
                   it?.let { asteroidsLD.value = it }
               }
           }
           else -> {
               asteroidsLD.removeSource(allAsteroids) //Renew the source with a new query with a new date. Maybe we changed the date
               val dates = getNextSevenDaysFormattedDates()
               allAsteroids  = Transformations.map(database.asteroidDao.getAllAsteroidsFromTodayIgnorePreviousDays(dates.first())){ it.asDomainModel() }
               asteroidsLD.addSource(allAsteroids){
                   Timber.d("query found ${it.size} for today ${dates.first()} ignoring the previous days}") // this will be triggered twice, one from the db and one when the retrofit query update the db
                   it?.let { asteroidsLD.value = it }
               }
           }
       }.also {
           filter = asteroidsFilter
           if(asteroidsFilter!=AsteroidsFilter.SHOW_SAVED) // do a refresh in the data
           getAsteroidsWithRetrofit(asteroidsFilter)
       }
    }

    suspend fun getImageOfTheDay() {
        withContext(Dispatchers.IO) {
            try {
                _pictureStatus.postValue( PotDApiStatus.LOADING)
                val getResponseFromRetrofit = RetrofitClient.instance.getImageOfTheDayAsync(BuildConfig.API_KEY)
                _pictureStatus.postValue( PotDApiStatus.DONE)
                _pictureOfTheDay.postValue(getResponseFromRetrofit)
            } catch (e: Exception) {
                _pictureStatus.postValue( PotDApiStatus.ERROR)
            }
        }
    }
}