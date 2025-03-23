package com.example.weatherapp.setting

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repo.SettingRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val repository: SettingRepository): ViewModel() {

    fun setLanguage(context: Context, languageCode: String) {
        viewModelScope.launch {
            repository.saveLanguage(languageCode)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.getSystemService(LocaleManager::class.java).applicationLocales =
                    LocaleList.forLanguageTags(languageCode)
            } else {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
            }
        }
    }

    fun getSavedLanguage(): String {
        return repository.getSavedLanguage()
    }
}

class SettingFactory(private val repo: SettingRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(repo) as T
    }
}