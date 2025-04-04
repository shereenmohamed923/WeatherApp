package com.example.weatherapp.data.repo

import android.content.Context
import com.example.weatherapp.utility.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SettingRepositoryImpl private constructor(private val context: Context): SettingRepository {

    private val sharedPref = context.getSharedPreferences("Settings_prefs", Context.MODE_PRIVATE)

    private val _TemperatureUnitFlow = MutableStateFlow(getSavedTemperatureUnit())
    override val temperatureUnitFlow: Flow<String> = _TemperatureUnitFlow

    private val _WindSpeedUnitFlow = MutableStateFlow(getSavedTemperatureUnit())
    override val windSpeedUnitFlow: Flow<String> = _WindSpeedUnitFlow

    override fun saveLanguage(languageCode: String) {
        sharedPref.edit().putString("language", languageCode).apply()
    }

    override fun getSavedLanguage(): String {
        return sharedPref.getString("language", "en") ?: "en"
    }

    override fun saveTemperatureUnit(unit: String) {
        sharedPref.edit().putString("temp_unit", unit).apply()
        _TemperatureUnitFlow.value = unit
    }

    override fun getSavedTemperatureUnit(): String {
        return sharedPref.getString("temp_unit", "Kelvin") ?: "Kelvin"
    }

    override fun saveWindSpeedUnit(unit: String) {
        sharedPref.edit().putString("wind_speed_unit", unit).apply()
        _WindSpeedUnitFlow.value = unit
    }

    override fun getSavedWindSpeedUnit(): String {
        return sharedPref.getString("wind_speed_unit", "meter/second") ?: "meter/second"
    }

    override fun checkNetworkConnection(): Boolean {
        return NetworkUtils.isNetworkAvailable(context)
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