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
    fun convertTemperature(temp: Double, unit: String, context: Context): Pair<Int, String>{
        return when(unit){
            "Celsius degree" -> Pair((temp - 273.15).toInt(), context.getString(R.string.celsius))
            "درجة مئوية" -> Pair((temp - 273.15).toInt(), context.getString(R.string.celsius))
            "Fahrenheit degree" -> Pair(((temp - 273.15) * 9 / 5 + 32).toInt(), context.getString(R.string.fahrenheit))
            "درجة فهرنهايت" -> Pair(((temp - 273.15) * 9 / 5 + 32).toInt(), context.getString(R.string.fahrenheit))
            else -> Pair(temp.toInt(), context.getString(R.string.kelvin))
        }
    }
    fun convertWindSpeed(windSpeed: Double, unit: String, context: Context): Pair<Int, String>{
        return when(unit){
            "miles/hour" -> Pair((windSpeed* 2.236936).toInt(), context.getString(R.string.mile_per_hour))
            "ميل/الساعة" ->  Pair((windSpeed* 2.236936).toInt(), context.getString(R.string.mile_per_hour))
            else -> Pair(windSpeed.toInt(), context.getString(R.string.meter_per_sec))
        }
    }
}