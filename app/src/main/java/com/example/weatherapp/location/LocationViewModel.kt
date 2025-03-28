package com.example.weatherapp.location

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.repo.LocationRepository

class LocationViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    fun saveLocation(lat: Double, lon:Double) {
        locationRepository.saveLocation(lon,lat)
    }

}

class LocationFactory(private val locationRepository: LocationRepository): ViewModelProvider.Factory{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(locationRepository) as T
    }
}