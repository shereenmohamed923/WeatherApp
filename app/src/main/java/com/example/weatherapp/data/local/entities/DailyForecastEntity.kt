package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_forecast",
    foreignKeys = [ForeignKey(entity = CurrentWeatherEntity::class, parentColumns = ["cityId"], childColumns = ["homeCityId"], onDelete = ForeignKey.CASCADE)]
)
data class DailyForecastEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val homeCityId: Int,
    val date: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherDescription: String,
    val weatherIcon: String
)