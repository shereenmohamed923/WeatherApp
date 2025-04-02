package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast_table",
    foreignKeys = [ForeignKey(entity = CurrentWeatherEntity::class, parentColumns = ["cityId"], childColumns = ["homeCityId"], onDelete = ForeignKey.CASCADE)]
)
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val homeCityId: Int,
    val cityName: String,
    val lat: Double,
    val lon: Double,
    var dateTime: String,
    var temperature: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    var isFav: Boolean = false
)