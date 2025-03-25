package com.example.weatherapp.utility

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.weatherapp.R

class LocalizationHelper(private val context: Context) {
    fun setLanguage(languageCode: String): String {
        //repository.saveLanguage(languageCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
        return languageCode
    }

}
class UnitHelper{
    fun convertTemperature(temp: Double, unit: String, context: Context): Pair<String, String>{
        return when(unit){
            "Celsius" -> Pair((temp - 273.15).toInt().toString(), context.getString(R.string.celsius))
            "Fahrenheit" -> Pair(((temp - 273.15) * 9 / 5 + 32).toInt().toString(), context.getString(R.string.fahrenheit))//
            else -> Pair(temp.toInt().toString(), context.getString(R.string.kelvin))//
        }
    }
}