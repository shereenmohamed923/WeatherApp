package com.example.weatherapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val cityId: Int,
    val cityName: String,
    val weatherIcon: String,
    val lat: String,
    val lon: String
)
