package com.example.weatherapp.data.repo

import android.content.Context
import android.location.Location
import android.util.Log
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.utility._locationState
import kotlinx.coroutines.flow.StateFlow


class LocationRepositoryImpl private constructor (context: Context): LocationRepository {

    private val sharedPref = context.getSharedPreferences("Settings_prefs", Context.MODE_PRIVATE)
    val _locationFlow = _locationState
    override val locationFlow: StateFlow<Location?> = _locationFlow


    override fun saveLocation(lat: Double, lon: Double) {
        sharedPref.edit()
            .putFloat("lat", lat.toFloat())
            .putFloat("lon", lon.toFloat())
            .apply()
        //_locationFlow.value = Coord(lat, lon)
        Log.i("TAG", "saveLocation: ${_locationFlow.value}")
    }

    override fun getSavedLocation(): Coord {
        val lat = sharedPref.getFloat("lat", 0f).toDouble()
        val lon = sharedPref.getFloat("lon", 0f).toDouble()
        return Coord(lat, lon)
    }

    companion object{
        private var INSTANCE: LocationRepositoryImpl?= null
        fun getInstance(context: Context): LocationRepository {
            return INSTANCE ?: synchronized(this){
                val temp = LocationRepositoryImpl(context)
                INSTANCE =temp
                temp
            }
        }
    }
}
