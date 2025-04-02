package com.example.weatherapp.data.repo

import android.content.Context
import com.example.weatherapp.utility.NetworkUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SettingRepositoryImpl private constructor(context: Context): SettingRepository {

    private val sharedPref = context.getSharedPreferences("Settings_prefs", Context.MODE_PRIVATE)

    private val _unitFlow = MutableStateFlow(getSavedUnit())

    override val unitFlow: Flow<String> = _unitFlow
    val cntxt: Context = context

    override fun saveLanguage(languageCode: String) {
        sharedPref.edit().putString("language", languageCode).apply()
    }

    override fun getSavedLanguage(): String {
        return sharedPref.getString("language", "en") ?: "en"
    }

    override fun saveUnit(unit: String) {
        sharedPref.edit().putString("temp_unit", unit).apply()
        _unitFlow.value = unit
    }

    override fun getSavedUnit(): String {
        return sharedPref.getString("temp_unit", "Kelvin") ?: "Kelvin"
    }

    override fun checkNetworkConnection(): Boolean {
        return NetworkUtils.isNetworkAvailable(cntxt)
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