package com.example.weatherapp.data.repo

import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    val unitFlow: Flow<String>
    fun saveLanguage(languageCode: String)
    fun getSavedLanguage(): String
    fun saveUnit(unit: String)
    fun getSavedUnit(): String
    fun checkNetworkConnection(): Boolean
}