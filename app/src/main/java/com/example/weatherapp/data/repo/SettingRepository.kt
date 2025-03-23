package com.example.weatherapp.data.repo

interface SettingRepository {
    fun saveLanguage(languageCode: String)
    fun getSavedLanguage(): String
}