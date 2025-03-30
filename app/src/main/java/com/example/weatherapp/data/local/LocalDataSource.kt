package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow


interface LocalDataSource {

    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)
    suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity>
    suspend fun deleteCurrentWeather(currentWeather: CurrentWeatherEntity)

    suspend fun insertForecast(forecast: List<ForecastEntity>)
    suspend fun getForecast(): Flow<List<ForecastEntity>>

    suspend fun insertFavoriteCity(city: FavoriteCityEntity)
    suspend fun getAllFavoriteCities(): Flow<List<FavoriteCityEntity>>
    suspend fun deleteFavoriteCity(city: FavoriteCityEntity)
}