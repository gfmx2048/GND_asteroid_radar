package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.data.AsteroidsRepository
import com.udacity.asteroidradar.data.database.AsteroidsDatabase
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    fun clearErrorResponse() {
        asteroidsRepository.clearErrorResponse()
    }

    fun clearAsteroidsResponse() {
        asteroidsRepository.clearAsteroidsResponse()
    }

    fun clearPictureOfTheDayResponse() {
        asteroidsRepository.clearPictureOfTheDay()
    }

    private val database = AsteroidsDatabase.getInstance(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    val asteroids = asteroidsRepository.asteroidsLD
    val pictureOfTheDAy = asteroidsRepository.pictureOfTheDay
    val error = asteroidsRepository.errorLD

    init {
        fetchAsteroids("","")
        fetchPictureOfTheDay()
    }

    fun fetchAsteroids(startDate:String,endDate:String){
        viewModelScope.launch {
            asteroidsRepository.getAsteroidsWithRetrofit(startDate,endDate)
        }
    }

    fun fetchPictureOfTheDay(){
        viewModelScope.launch {
            asteroidsRepository.getImageOfTheDay()
        }
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