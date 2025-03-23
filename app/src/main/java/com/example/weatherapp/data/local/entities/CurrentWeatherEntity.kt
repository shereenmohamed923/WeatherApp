package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_weather")
data class CurrentWeatherEntity(
    @PrimaryKey
    val cityId: Int,
    val cityName: String,
    val country: String,
    val temperature: Double,
    val minTemperature: Double,
    val maxTemperature: Double,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val clouds: Int,
    val sunrise: Long,
    val sunset: Long,
    val weatherDescription: String,
    val weatherIcon: String
)