package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "hourly_forecast",
    foreignKeys = [ForeignKey(entity = CurrentWeatherEntity::class, parentColumns = ["cityId"], childColumns = ["homeCityId"], onDelete = ForeignKey.CASCADE)]
)
data class HourlyForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val homeCityId: Int,
    val dateTime: Long,
    val temperature: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    val windSpeed: Double,
    val clouds: Int
)