package com.example.weatherapp.data.repo

import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    val temperatureUnitFlow: Flow<String>
    val windSpeedUnitFlow: Flow<String>
    fun saveLanguage(languageCode: String)
    fun getSavedLanguage(): String
    fun saveTemperatureUnit(unit: String)
    fun getSavedTemperatureUnit(): String
    fun saveWindSpeedUnit(unit: String)
    fun getSavedWindSpeedUnit(): String
    fun checkNetworkConnection(): Boolean
}