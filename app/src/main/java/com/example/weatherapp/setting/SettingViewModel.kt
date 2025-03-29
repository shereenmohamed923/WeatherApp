package com.example.weatherapp.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.SettingRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    fun saveGps(){
        viewModelScope.launch {
            val location = locationRepository.locationFlow.filterNotNull().first()
            locationRepository.saveLocation(lat = location.latitude, lon = location.longitude)
        }
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