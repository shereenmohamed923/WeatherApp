package com.example.weatherapp.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.data.repo.SettingRepository

class SettingViewModel(private val repository: SettingRepository): ViewModel() {

    fun getSavedLanguage(): String {
        return repository.getSavedLanguage()
    }

    fun saveLanguage(languageCode: String) {
        repository.saveLanguage(languageCode)
    }

    fun convertTemperature(temp: Double, unit: String): Double{
        return when(unit){
            "Celsius" -> temp - 273.15
            "Fahrenheit" -> (temp - 273.15) * 9 / 5 + 32
            else -> temp
        }
    }

    fun saveTemperatureUnit(unit: String){
        repository.saveTemperatureUnit(unit)
    }

    fun getTemperatureUnit(): String{
        return repository.getTemperatureUnit()
    }
}

class SettingFactory(private val repo: SettingRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(repo) as T
    }
}