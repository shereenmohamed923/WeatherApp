package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey
    val cityId: Int,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    val temperature: Double,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val clouds: Int,
    val weatherDescription: String,
    val weatherIcon: String,
    val lastUpdatedDate: Double
)