package com.example.weatherapp.data.repo

import android.location.Location
import com.example.weatherapp.data.model.Coord
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    val locationFlow: StateFlow<Location?>
    fun saveLocation(lat: Double, lon: Double)
    fun getSavedLocation(): Coord
    fun saveLocationName(name: String)
    fun getSavedLocationName(): String
    fun saveLocationPreference(source: String)
    fun getSavedLocationPreference(): String
}