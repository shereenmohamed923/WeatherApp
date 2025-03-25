package com.example.weatherapp.data.repo

import android.content.Context

class SettingRepositoryImpl(context: Context): SettingRepository {

    private val sharedPref = context.getSharedPreferences("Settings_prefs", Context.MODE_PRIVATE)

    override fun saveLanguage(languageCode: String) {
        sharedPref.edit().putString("language", languageCode).apply()
    }

    override fun getSavedLanguage(): String {
        return sharedPref.getString("language", "en") ?: "en"
    }

    override fun saveTemperatureUnit(unit: String) {
        sharedPref.edit().putString("temp_unit", unit).apply()
    }

    override fun getTemperatureUnit(): String {
        return sharedPref.getString("temp_unit", "Kelvin") ?: "Kelvin"
    }

    companion object{
        private var INSTANCE: SettingRepositoryImpl?= null
        fun getInstance(context: Context): SettingRepository {
            return INSTANCE ?: synchronized(this){
                val temp = SettingRepositoryImpl(context)
                INSTANCE =temp
                temp
            }
        }
    }

}