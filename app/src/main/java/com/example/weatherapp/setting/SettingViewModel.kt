package com.example.weatherapp.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.SettingRepository
import kotlinx.coroutines.flow.Flow

class SettingViewModel(private val repository: SettingRepository, private val locationRepository: LocationRepository): ViewModel() {

    fun getSavedLanguage(): String {
        return repository.getSavedLanguage()
    }

    fun saveLanguage(languageCode: String) {
        repository.saveLanguage(languageCode)
    }

    fun setTemperatureUnit(unit: String){
        repository.saveUnit(unit)
    }

    fun getTemperatureUnit(): String{
        return repository.getSavedUnit()
    }

    fun saveLocation(lat: Double, lon: Double){
        locationRepository.saveLocation(lat, lon)
    }

    fun getSavedLocation(): Coord{
        return locationRepository.getSavedLocation()
    }
}

class SettingFactory(private val settingRepository: SettingRepository, private val locationRepository: LocationRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(settingRepository, locationRepository) as T
    }
}