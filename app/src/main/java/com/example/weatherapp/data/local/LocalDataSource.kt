package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.DailyForecastEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
import com.example.weatherapp.data.local.entities.HourlyForecastEntity
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.flow.Flow


interface LocalDataSource {

    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)
    suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity>
    suspend fun deleteCurrentWeather(currentWeather: CurrentWeatherEntity)

    suspend fun insertHourlyForecast(forecast: List<HourlyForecastEntity>)
    suspend fun getHourlyForecast(): Flow<List<HourlyForecastEntity>>

    suspend fun insertDailyForecast(forecast: List<DailyForecastEntity>)
    suspend fun getDailyForecast(): Flow<List<DailyForecastEntity>>

    suspend fun insertFavoriteCity(city: FavoriteCityEntity)
    suspend fun getAllFavoriteCities(): Flow<List<FavoriteCityEntity>>
    suspend fun deleteFavoriteCity(city: FavoriteCityEntity)
}