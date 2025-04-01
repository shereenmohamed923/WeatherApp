package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow


interface LocalDataSource {

    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)
    suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity>
    suspend fun deleteCurrentWeather()

    suspend fun insertForecast(forecast: List<ForecastEntity>)
    suspend fun getForecast(): Flow<List<ForecastEntity>>

    suspend fun insertFavoriteCity(cityCurrentWeather: CurrentWeatherEntity)
    suspend fun getAllFavoriteCities(): Flow<List<CurrentWeatherEntity>>
    suspend fun getFavoriteCityCurrent(cityId: Int): Flow<List<CurrentWeatherEntity>>
    suspend fun getFavoriteCityForecast(cityId: Int): Flow<List<ForecastEntity>>
    suspend fun deleteFavoriteCity(cityId: Int)
}