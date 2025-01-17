package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.data.AsteroidsFilter
import com.udacity.asteroidradar.data.AsteroidsRepository
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidsDatabase.getInstance(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    val asteroids = asteroidsRepository.asteroidsLD // always observe our single source of truth, the db
    val pictureOfTheDAy = asteroidsRepository.pictureOfTheDay
    val error = asteroidsRepository.errorLD
    val status = asteroidsRepository.status
    val pictureStatus = asteroidsRepository.pictureStatus

    //the internal mutableLiveData
    private val _selectedAsteroid = MutableLiveData<Asteroid?>()
    //the external immutable LiveData
    val selectedAsteroid: LiveData<Asteroid?>
        get() = _selectedAsteroid

    init {
       //The first time try to refresh the data
        viewModelScope.launch {
            asteroidsRepository.getAsteroidsWithRetrofit(AsteroidsFilter.SHOW_WEEK)
        }
        updateFilter(AsteroidsFilter.SHOW_SAVED)
        fetchPictureOfTheDay()
    }

    fun updateFilter(asteroidsFilter: AsteroidsFilter){
        viewModelScope.launch {
            asteroidsRepository.updateFilter(asteroidsFilter)
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid){
        _selectedAsteroid.value = asteroid
    }

    private fun fetchPictureOfTheDay(){
        viewModelScope.launch {
            asteroidsRepository.getImageOfTheDay()
        }
    }

    fun clearErrorResponse() {
        asteroidsRepository.clearErrorResponse()
    }

    fun clearSelectedAsteroid() {
        _selectedAsteroid.value = null
    }


    /**
     * Factory for constructing MainViewModel with parameter
     */
    class MainViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}