package com.example.weatherapp.data.repo

import android.content.Context


class SettingRepositoryImpl(private val context: Context): SettingRepository {

    private val sharedPref = context.getSharedPreferences("Settings_prefs", Context.MODE_PRIVATE)

    override fun saveLanguage(languageCode: String) {
        sharedPref.edit().putString("language", languageCode).apply()
    }

    override fun getSavedLanguage(): String {
        return sharedPref.getString("language", "en") ?: "en"
    }

}