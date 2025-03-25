package com.example.weatherapp.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.data.repo.SettingRepository
import kotlinx.coroutines.flow.Flow

class SettingViewModel(private val repository: SettingRepository): ViewModel() {

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
}

class SettingFactory(private val repo: SettingRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(repo) as T
    }
}