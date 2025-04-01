package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity

import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherResponse>
    suspend fun addCurrentWeather(currentWeather: CurrentWeatherEntity)
    suspend fun removeCurrentWeather()

    suspend fun getForecastWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<ForecastResponse>
    suspend fun addForecast(forecast: List<ForecastEntity>)

    suspend fun getAllFavoriteCities(): Flow<List<CurrentWeatherEntity>>
    suspend fun getFavoriteCityCurrent(cityId: Int): Flow<List<CurrentWeatherEntity>>
    suspend fun getFavoriteCityForecast(cityId: Int): Flow<List<ForecastEntity>>
    suspend fun addFavoriteCity(cityCurrentWeather: CurrentWeatherEntity)
    suspend fun removeFavoriteCity(cityId: Int)

}