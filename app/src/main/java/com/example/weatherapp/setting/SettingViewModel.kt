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

class SettingViewModel(private val settingsRepository: SettingRepository, private val locationRepository: LocationRepository): ViewModel() {

    fun getSavedLanguage(): String {
        return settingsRepository.getSavedLanguage()
    }

    fun saveLanguage(languageCode: String) {
        settingsRepository.saveLanguage(languageCode)
    }

    fun saveTemperatureUnit(unit: String){
        settingsRepository.saveTemperatureUnit(unit)
    }

    fun getSavedTemperatureUnit(): String{
        return settingsRepository.getSavedTemperatureUnit()
    }

    fun saveWindSpeedUnit(unit: String){
        settingsRepository.saveWindSpeedUnit(unit)
    }

    fun getSavedWindSpeedUnit(): String{
        return settingsRepository.getSavedWindSpeedUnit()
    }

    fun saveLocation(lat: Double, lon: Double){
        locationRepository.saveLocation(lat, lon)
    }

    fun saveLocationPreference(source: String){
        locationRepository.saveLocationPreference(source)
    }

    fun getSavedLocationPreference(): String{
        return locationRepository.getSavedLocationPreference()
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