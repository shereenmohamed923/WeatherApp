package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlertsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val scheduledTime: Long,
    val isNotified: Boolean = false
)